package com.theblueground.fixtures

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.validate
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.ksp.KotlinPoetKspPreview

/**
 * Responsible for processing all data classes that were annotated with the [Fixture] annotation
 * and generating the corresponding helper functions. It will use an [FixtureVisitor] to extract the
 * necessary information from the data class declaration and then will use an [FixtureBuilderGenerator]
 * to generate the functions.
 */
@KotlinPoetKspPreview
internal class FixtureProcessor(
    options: Map<String, String>,
    codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
) : SymbolProcessor {

    companion object {

        private val FIXTURE_ADAPTER_ANNOTATION_FULLY_QUALIFIED_NAME =
            FixtureAdapter::class.java.name

        private val FIXTURE_ANNOTATION_FULLY_QUALIFIED_NAME = Fixture::class.java.name
    }

    private val fixtureBuilderGenerator = FixtureBuilderGenerator(codeGenerator = codeGenerator)

    private val processedFixtureAdapters = mutableMapOf<TypeName, ProcessedFixtureAdapter>()

    private val fixtureAdapterVisitor = FixtureAdapterVisitor(
        processedFixtureAdapters = processedFixtureAdapters,
    )

    private val processedFixtures = mutableMapOf<KSFile, List<ProcessedFixture>>()

    private val fixtureVisitor = FixtureVisitor(
        processedFixtureAdapters = processedFixtureAdapters,
        processedFixtures = processedFixtures,
    )

    private val kspArguments = KspArguments(
        randomize = options["fixtures.randomize"]?.let { it.equals("true", true) } ?: false,
        prefix = options["fixtures.prefix"] ?: "create",
    )

    private val runFixtures = options["fixtures.run"]?.let { it.equals("true", true) } ?: true

    override fun process(resolver: Resolver): List<KSAnnotated> {
        if (!runFixtures) {
            return emptyList()
        }

        visitFixtureAdapters(resolver = resolver)
        visitFixtures(resolver = resolver)
        return emptyList()
    }

    private fun visitFixtureAdapters(resolver: Resolver) {
        val symbols = resolver.getSymbolsWithAnnotation(
            annotationName = FIXTURE_ADAPTER_ANNOTATION_FULLY_QUALIFIED_NAME,
        )

        val annotationName = FixtureAdapter::class.simpleName // For logging purposes
        symbols.filterIsInstance<KSFunctionDeclaration>()
            .filter { kSFunctionDeclaration -> kSFunctionDeclaration.validate() }
            .forEach { kSFunctionDeclaration ->
                // look browse class information via FixtureAdapterVisitor
                kSFunctionDeclaration.accept(fixtureAdapterVisitor, Unit)
                val className = kSFunctionDeclaration.simpleName.asString()
                logger.logging(
                    message = "The class $className with $annotationName annotation was processed",
                )
            }
    }

    private fun visitFixtures(resolver: Resolver) {
        val symbols = resolver.getSymbolsWithAnnotation(
            annotationName = FIXTURE_ANNOTATION_FULLY_QUALIFIED_NAME,
        )

        val annotationName = Fixture::class.simpleName // For logging purposes
        symbols.filterIsInstance<KSClassDeclaration>()
            .filter { kSClassDeclaration -> kSClassDeclaration.validate() }
            .forEach { kSClassDeclaration ->
                // look browse class information via FixtureVisitor
                kSClassDeclaration.accept(fixtureVisitor, Unit)
                val className = kSClassDeclaration.simpleName.asString()
                logger.logging(
                    message = "The class $className with $annotationName annotation was processed",
                )
            }
    }

    override fun finish() {
        if (!runFixtures) {
            return
        }

        processedFixtures.forEach { (containingFile, processedFixtures) ->
            fixtureBuilderGenerator.generate(
                kspArguments = kspArguments,
                containingFile = containingFile,
                processedFixtures = processedFixtures,
                fixtureAdapters = processedFixtureAdapters,
            )
        }
    }

    override fun onError() {
        super.onError()
        logger.error("Failed to process fixtures")
    }
}
