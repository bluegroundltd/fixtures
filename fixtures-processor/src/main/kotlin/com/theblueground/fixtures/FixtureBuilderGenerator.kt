package com.theblueground.fixtures

import com.google.devtools.ksp.processing.CodeGenerator
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.ksp.addOriginatingKSFile
import com.squareup.kotlinpoet.ksp.writeTo

/**
 * It uses the information that was extracted from [FixtureVisitor] to generate a file that contains
 * a helper function which will create test data.
 */
internal class FixtureBuilderGenerator(
    private val codeGenerator: CodeGenerator
) {

    companion object {

        const val OUTPUT_FIXTURE_FILENAME_SUFFIX = "Fixture"
    }

    private val valueGenerator = ParameterValueGenerator()

    fun generate(
        randomize: Boolean,
        processed: ProcessedFixture,
        fixtureAdapters: Map<TypeName, ProcessedFixtureAdapter>
    ) {
        val filename = processed.simpleName + OUTPUT_FIXTURE_FILENAME_SUFFIX

        FileSpec.builder(packageName = processed.packageName, fileName = filename)
//            .addOriginatingKSFile(ksFile = processed.containingFile) // TODO Check if we need smth like that
            .addFunction(
                funSpec = processed.toFunSpec(
                    randomize = randomize,
                    fixtureAdapters = fixtureAdapters
                )
            )
            .ensureNestedImports(processedFixture = processed)
            .build()
            .writeTo(codeGenerator = codeGenerator, aggregating = true)
    }

    private fun ProcessedFixture.toFunSpec(
        randomize: Boolean,
        fixtureAdapters: Map<TypeName, ProcessedFixtureAdapter>
    ): FunSpec {
        val functionName = "create${simpleName.replaceFirstChar { it.uppercaseChar() }}"

        val funSpec = FunSpec.builder(name = functionName)

        parameters.forEach {
            funSpec.addParameter(
                parameterSpec = it.toParameterSpec(
                    randomize = randomize,
                    fixtureAdapters = fixtureAdapters
                )
            )
        }

        return funSpec.addStatement(format = buildFunctionStatement())
            .returns(returnType = classType).build()
    }

    private fun ProcessedFixtureParameter.toParameterSpec(
        randomize: Boolean,
        fixtureAdapters: Map<TypeName, ProcessedFixtureAdapter>
    ): ParameterSpec {
        val defaultValue = valueGenerator.generateDefaultValue(
            randomize = randomize,
            parameter = this,
            fixtureAdapters = fixtureAdapters
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
        processedFixture: ProcessedFixture
    ): FileSpec.Builder {
        processedFixture.parameters
            .find { it.typeName == "ZonedDateTime" }
            ?.let { addImport(it.packageName, "ZoneId") }

        return this
    }
}
