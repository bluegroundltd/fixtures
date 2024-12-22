package com.theblueground.fixtures

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.symbol.KSFile
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeName
import java.io.File
import java.util.Locale

/**
 * It uses the information that was extracted from [FixtureVisitor] to generate a file that contains
 * a helper function which will create test data.
 */
internal class FixtureBuilderGroovyGenerator(
    private val codeGenerator: CodeGenerator,
) : FixtureBuilderGenerator {

    companion object {

        private const val TAB = "\t"

        const val OUTPUT_FIXTURE_FILENAME_SUFFIX = "Fixture"
    }

    private val valueGenerator = ParameterGroovyValueGenerator()

    override fun generate(
        randomize: Boolean,
        containingFile: KSFile,
        processedFixtures: List<ProcessedFixture>,
        fixtureAdapters: Map<TypeName, ProcessedFixtureAdapter>,
    ) {
        val fileNameWithoutExtension = File(containingFile.fileName).nameWithoutExtension
        val filename = fileNameWithoutExtension + OUTPUT_FIXTURE_FILENAME_SUFFIX
        val dependencies = Dependencies(aggregating = true, containingFile)
        val packageName = containingFile.packageName.asString()
        val output = codeGenerator.createNewFile(
            dependencies = dependencies,
            packageName = packageName,
            fileName = filename,
            extensionName = "groovy",
        )

        output.write("package $packageName\n\n".toByteArray())
        output.write(buildImports(processedFixtures = processedFixtures).toByteArray())
        processedFixtures.forEach {
            val builderClass = it.buildBuilderClass(
                randomize = randomize,
                fixtureAdapters = fixtureAdapters,
            )
            output.write(builderClass.toByteArray())
        }

        output.close()
    }

    private fun buildImports(
        processedFixtures: List<ProcessedFixture>,
    ): String = processedFixtures
        .flatMap { it.parameters }
        .filterNot { it is ProcessedFixtureParameter.PrimitiveParameter }
        .distinct()
        .joinToString("\n") {
            // To support default value assignment of ZonedDateTime
            if (it.javaTypeName == "ZonedDateTime") {
                "import ${it.packageName}.${it.javaTypeName}\n" +
                    "import ${it.packageName}.ZoneId"
            } else {
                "import ${it.packageName}.${it.javaTypeName}"
            }
        } + "\n"

    private fun ProcessedFixture.buildBuilderClass(
        randomize: Boolean,
        fixtureAdapters: Map<TypeName, ProcessedFixtureAdapter>,
    ): String {
        val className = "$parentName${simpleName.replaceFirstChar { it.uppercaseChar() }}Builder"
        val parametersPart = buildParametersPart(
            randomize = randomize,
            fixtureAdapters = fixtureAdapters,
            parameters = parameters,
        )
        val functionsPart = buildFunctionsPart(
            className = className,
            parameters = parameters,
        )
        val instantiationPart = buildInstantiationPart(processedFixture = this)

        return """
            |
            |class $className {
            |
            |$parametersPart
            |
            |${TAB}private $className() { }
            |
            |${TAB}static $className a$className() {
            |$TAB${TAB}new $className()
            |$TAB}
            $functionsPart
            |
            $instantiationPart
            |}
            |
        """.trimMargin()
    }

    private fun buildParametersPart(
        randomize: Boolean,
        fixtureAdapters: Map<TypeName, ProcessedFixtureAdapter>,
        parameters: List<ProcessedFixtureParameter>,
    ): String = parameters.joinToString("\n") {
        val value = valueGenerator.generateDefaultValue(
            randomize = randomize,
            parameter = it,
            fixtureAdapters = fixtureAdapters,
        )

        "${TAB}private ${it.javaTypeName} ${it.name} = $value"
    }

    private fun buildFunctionsPart(
        className: String,
        parameters: List<ProcessedFixtureParameter>,
    ): String = parameters.joinToString("\n") {
        val type = it.javaTypeName
        val name = it.name
        val capitalizeName = name.replaceFirstChar { char -> char.titlecase(Locale.getDefault()) }
        """|
           |$TAB$className with$capitalizeName($type $name) {
           |$TAB${TAB}this.$name = $name
           |$TAB${TAB}this
           |$TAB}"""
    }

    private fun buildInstantiationPart(
        processedFixture: ProcessedFixture,
    ): String {
        val parameters = processedFixture.parameters.joinToString("\n") {
            "|$TAB$TAB${TAB}this.${it.name},"
        }

        val parent = if (processedFixture.parentName.isNotBlank()) {
            "${processedFixture.parentName}."
        } else {
            ""
        }

        val name = processedFixture.simpleName
        return """|$TAB$parent$name build() {
                  |$TAB${TAB}new $parent$name(
                  $parameters
                  |$TAB$TAB)
                  |$TAB}"""
    }

    private val ProcessedFixtureParameter.javaTypeName: String
        get() = with(type) {
            if (this is ClassName && this.simpleName == "Int") {
                "Integer"
            } else {
                typeName
            }
        }
}
