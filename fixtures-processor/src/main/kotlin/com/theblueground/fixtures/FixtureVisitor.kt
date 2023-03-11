package com.theblueground.fixtures

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.ksp.KotlinPoetKspPreview
import com.squareup.kotlinpoet.ksp.toClassName

/**
 * A visitor that extracts all the needed information from a data class that was annotated with
 * the [Fixture] annotation. This information will be used by [FixtureBuilderGenerator] in order to
 * generate a helper function which will create test data.
 */
@KotlinPoetKspPreview
internal class FixtureVisitor(
    processedFixtureAdapters: Map<TypeName, ProcessedFixtureAdapter>,
    private val processedFixtures: MutableMap<KSFile, List<ProcessedFixture>>
) : KSVisitorVoid() {

    private val processedParameterMapper = ProcessedParameterMapper(
        processedFixtureAdapters = processedFixtureAdapters
    )

    override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
        if (!classDeclaration.isDataClass) {
            throw IllegalStateException(
                "${Fixture::class.simpleName} can be used only in a data class."
            )
        }

        val containingFile = classDeclaration.containingFile!!

        val processedFixture = ProcessedFixture(
            classType = classDeclaration.toClassName(),
            parameters = extractParameters(classDeclaration = classDeclaration)
        )

        processedFixtures[containingFile] = processedFixtures.getOrDefault(containingFile, emptyList())
            .toMutableList()
            .apply { add(processedFixture) }
    }

    private fun extractParameters(
        classDeclaration: KSClassDeclaration
    ): List<ProcessedFixtureParameter> = classDeclaration.primaryConstructor!!
        .parameters
        .map { processedParameterMapper.mapParameter(parameterValue = it) }
}
