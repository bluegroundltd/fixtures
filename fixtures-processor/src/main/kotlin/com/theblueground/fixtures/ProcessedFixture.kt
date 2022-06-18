package com.theblueground.fixtures

import com.google.devtools.ksp.symbol.KSFile
import com.squareup.kotlinpoet.ClassName

/**
 * Keeps all the needed information that is processed for a data class which was annotated with the
 * [Fixture] annotation. The information will be used in order to generate a helper function which
 * will create test data.
 */
internal data class ProcessedFixture(
    val classType: ClassName,
    val containingFile: KSFile,
    val parameters: List<ProcessedFixtureParameter>,
)

internal val ProcessedFixture.simpleName
    get() = this.classType.simpleName

internal val ProcessedFixture.qualifiedName
    get() = this.classType.canonicalName

internal val ProcessedFixture.packageName
    get() = this.classType.packageName