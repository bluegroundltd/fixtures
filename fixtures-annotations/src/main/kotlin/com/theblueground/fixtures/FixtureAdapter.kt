package com.theblueground.fixtures

/**
 * This annotations help us to provide values for non [supported field types](SupportedFieldTypes).
 *
 * Let's assume that we have the following classes that do not belong to our domain:
 *
 * ```kotlin
 * class Bar
 *
 * class Foo(val value: Bar)
 * ```
 *
 * Then we have a class that depends on one from the previous classes:
 *
 * ```kotlin
 * data class TestClass(
 *      val stringValue: String,
 *      val doubleValue: Double,
 *      val fooValue: Foo
 * )
 * ```
 *
 * If we add the [Fixture] annotation, then it will be impossible to generate a function that
 * helps to generate test data. This happens because we can not process classes outside from
 * our domain. So here comes the [FixtureAdapter] to rescue us.
 *
 * We can create the following function wherever we want in our main sourcesets:
 *
 * ```kotlin
 * @FixtureAdapter
 * fun fooFixtureProvider(): Foo = Foo()
 * ```
 *
 * Now, if we add the [Fixture] annotation the following helper function will be generated:
 *
 * ```kotlin
 * fun createTestClass(
 *      stringValue: String = "JX",
 *      doubleValue: Double = 4.535699617969113,
 *      fooValue: Foo = fooFixtureProvider()
 * ) : TestClass = TestClass(
 *      stringValue = stringValue,
 *      doubleValue = doubleValue,
 *      fooValue = fooValue
 * )
 * ```
 *
 * The only problem with this approach, is that the [FixtureAdapter] implementation should be
 * placed in the main sourcesets and not in the test sourcesets. This will be solved with the
 * release of [this issue](https://github.com/google/ksp/issues/962#issuecomment-1163538824)
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class FixtureAdapter
