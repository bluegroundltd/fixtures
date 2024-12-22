package com.theblueground.fixtures

import com.google.devtools.ksp.symbol.KSFile
import com.squareup.kotlinpoet.TypeName

/**
 * It uses the information that was extracted from [FixtureVisitor] to generate a file that contains
 * a helper function which will create test data.
 */
interface FixtureBuilderGenerator {

    fun generate(
        randomize: Boolean,
        containingFile: KSFile,
        processedFixtures: List<ProcessedFixture>,
        fixtureAdapters: Map<TypeName, ProcessedFixtureAdapter>,
    )
}
