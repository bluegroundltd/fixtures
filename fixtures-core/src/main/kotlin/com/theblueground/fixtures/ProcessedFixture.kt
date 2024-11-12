package com.theblueground.fixtures

import com.squareup.kotlinpoet.ClassName

/**
 * Keeps all the needed information that is processed for a data class which was annotated with the
 * [Fixture] annotation. The information will be used in order to generate a helper function which
 * will create test data.
 */
data class ProcessedFixture(
    val parentName: String,
    val classType: ClassName,
    val parameters: List<ProcessedFixtureParameter>,
)

val ProcessedFixture.simpleName
    get() = this.classType.simpleName

val ProcessedFixture.qualifiedName
    get() = this.classType.canonicalName

val ProcessedFixture.packageName
    get() = this.classType.packageName
