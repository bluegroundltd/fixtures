package com.theblueground.fixtures

import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.ksp.toTypeName

/**
 * A visitor that extracts all the needed information from a function that was annotated with
 * the [FixtureAdapter] annotation. This information will be used by [FixtureBuilderGenerator] in
 * order to generate a helper function which will create test data with data classes that contain
 * non [supported field types](SupportedFieldTypes).
 */
internal class FixtureAdapterVisitor(
    private val processedFixtureAdapters: MutableMap<TypeName, ProcessedFixtureAdapter>
) : KSVisitorVoid() {

    override fun visitFunctionDeclaration(function: KSFunctionDeclaration, data: Unit) {
        val functionName = function.simpleName.asString()
        if (function.parameters.isNotEmpty()) {
            throw IllegalStateException("$functionName should not contain parameters.")
        }

        val functionReturnType = function.returnType
            ?: throw IllegalStateException("$functionName should return something.")

        val returnTypeName = functionReturnType.toTypeName()
        val processedFixtureAdapter = ProcessedFixtureAdapter(
            packageName = function.packageName.asString(),
            functionName = function.simpleName.asString()
        )
        processedFixtureAdapters[returnTypeName] = processedFixtureAdapter
    }
}
