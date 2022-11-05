package com.theblueground.fixtures

import kotlin.annotation.Retention

/**
 * Defines a data class from which we can generate a function to help us build test data.
 * For example, if we had the following data class:
 *
 * ```
 * @Fixture
 * data class TestClass(
 *      val stringValue: String,
 *      val doubleValue: Double,
 *      val floatValue: Float,
 *      val booleanValue: Boolean,
 *      val intValue: Int
 * )
 * ```
 *
 * after kotlin symbol processing the following function would be generated:
 *
 * ```kotlin
 *
 * fun createTestClass(
 *      stringValue: String = "JX",
 *      doubleValue: Double = 4.535699617969113,
 *      floatValue: Float = 0.58258104f,
 *      booleanValue: Boolean = false,
 *      intValue: Int = 17
 * ) : TestClass = TestClass(
 *      stringValue = stringValue,
 *      doubleValue = doubleValue,
 *      floatValue = floatValue,
 *      booleanValue = booleanValue,
 *      intValue = intValue
 * )
 * ```
 *
 * As we can see, the naming convention for the generated function is the class' name with the
 * `create` prefix. So, from `TestClass` the `createTestClass` function will be generated.
 *
 * &nbsp;
 *
 * Check also [supported field types](SupportedFieldTypes).
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class Fixture
