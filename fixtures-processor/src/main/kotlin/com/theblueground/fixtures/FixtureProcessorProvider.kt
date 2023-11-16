package com.theblueground.fixtures

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

internal class FixtureProcessorProvider : SymbolProcessorProvider {

    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor =
        FixtureProcessor(
            options = environment.options,
            codeGenerator = environment.codeGenerator,
            logger = environment.logger,
        )
}
