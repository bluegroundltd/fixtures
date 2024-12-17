package com.theblueground.fixtures

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.symbol.KSFile
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.ksp.KotlinPoetKspPreview
import com.squareup.kotlinpoet.ksp.addOriginatingKSFile
import com.squareup.kotlinpoet.ksp.writeTo
import java.io.File

/**
 * It uses the information that was extracted from [FixtureVisitor] to generate a file that contains
 * a helper function which will create test data.
 */
@KotlinPoetKspPreview
internal class FixtureBuilderGenerator(
    private val codeGenerator: CodeGenerator,
) {

    companion object {

        const val OUTPUT_FIXTURE_FILENAME_SUFFIX = "Fixture"
    }

    private val valueGenerator = ParameterValueGenerator()

    fun generate(
        kspArguments: KspArguments,
        containingFile: KSFile,
        processedFixtures: List<ProcessedFixture>,
        fixtureAdapters: Map<TypeName, ProcessedFixtureAdapter>,
    ) {
        val fileNameWithoutExtension = File(containingFile.fileName).nameWithoutExtension
        val filename = fileNameWithoutExtension + OUTPUT_FIXTURE_FILENAME_SUFFIX
        val fileSpec = FileSpec.builder(packageName = containingFile.packageName.asString(), fileName = filename)

        processedFixtures.forEach {
            fileSpec.addFunction(
                funSpec = it.toFunSpec(
                    kspArguments = kspArguments,
                    containingFile = containingFile,
                    fixtureAdapters = fixtureAdapters,
                ),
            )
                .ensureNestedImports(processedFixture = it)
        }

        fileSpec.build()
            .writeTo(codeGenerator = codeGenerator, aggregating = true)
    }

    private fun ProcessedFixture.toFunSpec(
        kspArguments: KspArguments,
        containingFile: KSFile,
        fixtureAdapters: Map<TypeName, ProcessedFixtureAdapter>,
    ): FunSpec {
        val functionName = "${kspArguments.prefix}$parentName${simpleName.replaceFirstChar { it.uppercaseChar() }}"
            .replaceFirstChar { it.lowercaseChar() }

        val funSpec = FunSpec.builder(name = functionName)
            .addOriginatingKSFile(containingFile)

        parameters.forEach {
            funSpec.addParameter(
                parameterSpec = it.toParameterSpec(
                    kspArguments = kspArguments,
                    fixtureAdapters = fixtureAdapters,
                ),
            )
        }

        return funSpec.addStatement(format = buildFunctionStatement())
            .returns(returnType = classType).build()
    }

    private fun ProcessedFixtureParameter.toParameterSpec(
        kspArguments: KspArguments,
        fixtureAdapters: Map<TypeName, ProcessedFixtureAdapter>,
    ): ParameterSpec {
        val defaultValue = valueGenerator.generateDefaultValue(
            kspArguments = kspArguments,
            parameter = this,
            fixtureAdapters = fixtureAdapters,
        )
        return ParameterSpec.builder(name = name, type = type)
            .defaultValue("%L", defaultValue)
            .build()
    }

    private fun ProcessedFixture.buildFunctionStatement(): String =
        // Usage of qualified name ensures that everything will work, even with enclosing classes.
        "return $qualifiedName(\n${parameters.joinToString(",\n") { "\t${it.name} = ${it.name}" }}\n)"

    // This function is just a workaround for this problem:
    // https://github.com/square/kotlinpoet/issues/1406
    // Maybe in the future they support these things.
    private fun FileSpec.Builder.ensureNestedImports(
        processedFixture: ProcessedFixture,
    ): FileSpec.Builder {
        processedFixture.parameters
            .find { it.typeName == "ZonedDateTime" }
            ?.let { addImport(it.packageName, "ZoneId") }

        return this
    }
}
