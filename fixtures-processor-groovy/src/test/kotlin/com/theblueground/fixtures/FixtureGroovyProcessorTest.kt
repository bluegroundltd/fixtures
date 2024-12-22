package com.theblueground.fixtures

import com.google.common.truth.Truth.assertThat
import com.squareup.kotlinpoet.ksp.KotlinPoetKspPreview
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile.Companion.kotlin
import org.junit.Test

@KotlinPoetKspPreview
class FixtureGroovyProcessorTest : KSPTest(
    generatedSourcesPathPrefix = "ksp/sources/resources/",
    fixtureProcessorProvider = FixtureGroovyProcessorProvider(),
) {

    private val packageName = "somefixture"

    private val fixtureName = "TestClass"

    private val fixtureSource = """
                    package $packageName

                    import com.theblueground.fixtures.Fixture

                    import java.math.BigDecimal
                    import java.math.BigInteger
                    import java.util.*

                    @Fixture
                    data class $fixtureName(
                        val stringValue: String,
                        val doubleValue: Double,
                        val floatValue: Float,
                        val booleanValue: Boolean,
                        val intValue: Int,
                        val longValue: Long,
                        val nestedTestValue: TestSubClass,
                        val dateValue: Date,
                        val uuidValue: UUID,
                        val bigDecimalValue: BigDecimal,
                        val bigIntegerValue: BigInteger,
                        val testEnumValue: TestEnum,
                        val collectionValue: Map<Int, String>,
                        val testSealedValue: TestSealed
                    )

                    enum class TestEnum {
                        FIRST_ENUM, SECOND_ENUM
                    }

                    sealed class TestSealed {

                        object First : TestSealed()

                        object Second : TestSealed()

                        @Fixture
                        data class Third(val name: String) : TestSealed()
                    }

                    @Fixture
                    data class TestSubClass(
                        val stringValue: String,
                        val doubleValue: Double,
                        val floatValue: Float,
                        val booleanValue: Boolean,
                        val intValue: Int
                    )
    """.trimIndent()

    @Test
    fun `should generate a builder function with standard data while running fixtures`() {
        // Given
        val fixtureFile = kotlin(name = "$fixtureName.kt", contents = fixtureSource)

        // When
        val result = compile(
            arguments = mapOf("fixtures.run" to "true"),
            sourceFiles = listOf(fixtureFile),
        )
        val generatedContent = getGeneratedContent(
            packageName = packageName,
            filename = "${fixtureName}Fixture.groovy",
        )

        // Then
        assertThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.OK)
        val expected = """
            package somefixture

            import somefixture.TestSubClass
            import java.util.Date
            import java.util.UUID
            import java.math.BigDecimal
            import java.math.BigInteger
            import somefixture.TestEnum
            import kotlin.collections.Map
            import somefixture.TestSealed

            class TestClassBuilder {

              private String stringValue = "stringValue"
              private Double doubleValue = 0.0
              private Float floatValue = 0f
              private Boolean booleanValue = false
              private Integer intValue = 0
              private Long longValue = 0L
              private TestSubClass nestedTestValue = somefixture.TestSubClassBuilder.aTestSubClassBuilder().build()
              private Date dateValue = Date(0)
              private UUID uuidValue = UUID.fromString("00000000-0000-0000-0000-000000000000")
              private BigDecimal bigDecimalValue = BigDecimal.ZERO
              private BigInteger bigIntegerValue = BigInteger.ZERO
              private TestEnum testEnumValue = TestEnum.FIRST_ENUM
              private Map collectionValue = [:]
              private TestSealed testSealedValue = TestSealed.First

              private TestClassBuilder() { }

              static TestClassBuilder aTestClassBuilder() {
                new TestClassBuilder()
              }

              TestClassBuilder withStringValue(String stringValue) {
                this.stringValue = stringValue
                this
              }

              TestClassBuilder withDoubleValue(Double doubleValue) {
                this.doubleValue = doubleValue
                this
              }

              TestClassBuilder withFloatValue(Float floatValue) {
                this.floatValue = floatValue
                this
              }

              TestClassBuilder withBooleanValue(Boolean booleanValue) {
                this.booleanValue = booleanValue
                this
              }

              TestClassBuilder withIntValue(Integer intValue) {
                this.intValue = intValue
                this
              }

              TestClassBuilder withLongValue(Long longValue) {
                this.longValue = longValue
                this
              }

              TestClassBuilder withNestedTestValue(TestSubClass nestedTestValue) {
                this.nestedTestValue = nestedTestValue
                this
              }

              TestClassBuilder withDateValue(Date dateValue) {
                this.dateValue = dateValue
                this
              }

              TestClassBuilder withUuidValue(UUID uuidValue) {
                this.uuidValue = uuidValue
                this
              }

              TestClassBuilder withBigDecimalValue(BigDecimal bigDecimalValue) {
                this.bigDecimalValue = bigDecimalValue
                this
              }

              TestClassBuilder withBigIntegerValue(BigInteger bigIntegerValue) {
                this.bigIntegerValue = bigIntegerValue
                this
              }

              TestClassBuilder withTestEnumValue(TestEnum testEnumValue) {
                this.testEnumValue = testEnumValue
                this
              }

              TestClassBuilder withCollectionValue(Map collectionValue) {
                this.collectionValue = collectionValue
                this
              }

              TestClassBuilder withTestSealedValue(TestSealed testSealedValue) {
                this.testSealedValue = testSealedValue
                this
              }

              TestClass build() {
                new TestClass(
                  this.stringValue,
                  this.doubleValue,
                  this.floatValue,
                  this.booleanValue,
                  this.intValue,
                  this.longValue,
                  this.nestedTestValue,
                  this.dateValue,
                  this.uuidValue,
                  this.bigDecimalValue,
                  this.bigIntegerValue,
                  this.testEnumValue,
                  this.collectionValue,
                  this.testSealedValue,
                )
              }
            }

            class TestSealedThirdBuilder {

              private String name = "name"

              private TestSealedThirdBuilder() { }

              static TestSealedThirdBuilder aTestSealedThirdBuilder() {
                new TestSealedThirdBuilder()
              }

              TestSealedThirdBuilder withName(String name) {
                this.name = name
                this
              }

              TestSealed.Third build() {
                new TestSealed.Third(
                  this.name,
                )
              }
            }

            class TestSubClassBuilder {

              private String stringValue = "stringValue"
              private Double doubleValue = 0.0
              private Float floatValue = 0f
              private Boolean booleanValue = false
              private Integer intValue = 0

              private TestSubClassBuilder() { }

              static TestSubClassBuilder aTestSubClassBuilder() {
                new TestSubClassBuilder()
              }

              TestSubClassBuilder withStringValue(String stringValue) {
                this.stringValue = stringValue
                this
              }

              TestSubClassBuilder withDoubleValue(Double doubleValue) {
                this.doubleValue = doubleValue
                this
              }

              TestSubClassBuilder withFloatValue(Float floatValue) {
                this.floatValue = floatValue
                this
              }

              TestSubClassBuilder withBooleanValue(Boolean booleanValue) {
                this.booleanValue = booleanValue
                this
              }

              TestSubClassBuilder withIntValue(Integer intValue) {
                this.intValue = intValue
                this
              }

              TestSubClass build() {
                new TestSubClass(
                  this.stringValue,
                  this.doubleValue,
                  this.floatValue,
                  this.booleanValue,
                  this.intValue,
                )
              }
            }
        """.trimMargin()
        assertThat(generatedContent.removeWhitespaces()).isEqualTo(expected.removeWhitespaces())
    }

    @Test
    fun `should generate a builder function with standard data when no options are defined`() {
        // Given
        val fixtureFile = kotlin(name = "$fixtureName.kt", contents = fixtureSource)

        // When
        val result = compile(sourceFiles = listOf(fixtureFile))
        val generatedContent = getGeneratedContent(
            packageName = packageName,
            filename = "${fixtureName}Fixture.groovy",
        )

        // Then
        assertThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.OK)
        val expected = """
            package somefixture

            import somefixture.TestSubClass
            import java.util.Date
            import java.util.UUID
            import java.math.BigDecimal
            import java.math.BigInteger
            import somefixture.TestEnum
            import kotlin.collections.Map
            import somefixture.TestSealed

            class TestClassBuilder {

              private String stringValue = "stringValue"
              private Double doubleValue = 0.0
              private Float floatValue = 0f
              private Boolean booleanValue = false
              private Integer intValue = 0
              private Long longValue = 0L
              private TestSubClass nestedTestValue = somefixture.TestSubClassBuilder.aTestSubClassBuilder().build()
              private Date dateValue = Date(0)
              private UUID uuidValue = UUID.fromString("00000000-0000-0000-0000-000000000000")
              private BigDecimal bigDecimalValue = BigDecimal.ZERO
              private BigInteger bigIntegerValue = BigInteger.ZERO
              private TestEnum testEnumValue = TestEnum.FIRST_ENUM
              private Map collectionValue = [:]
              private TestSealed testSealedValue = TestSealed.First

              private TestClassBuilder() { }

              static TestClassBuilder aTestClassBuilder() {
                new TestClassBuilder()
              }

              TestClassBuilder withStringValue(String stringValue) {
                this.stringValue = stringValue
                this
              }

              TestClassBuilder withDoubleValue(Double doubleValue) {
                this.doubleValue = doubleValue
                this
              }

              TestClassBuilder withFloatValue(Float floatValue) {
                this.floatValue = floatValue
                this
              }

              TestClassBuilder withBooleanValue(Boolean booleanValue) {
                this.booleanValue = booleanValue
                this
              }

              TestClassBuilder withIntValue(Integer intValue) {
                this.intValue = intValue
                this
              }

              TestClassBuilder withLongValue(Long longValue) {
                this.longValue = longValue
                this
              }

              TestClassBuilder withNestedTestValue(TestSubClass nestedTestValue) {
                this.nestedTestValue = nestedTestValue
                this
              }

              TestClassBuilder withDateValue(Date dateValue) {
                this.dateValue = dateValue
                this
              }

              TestClassBuilder withUuidValue(UUID uuidValue) {
                this.uuidValue = uuidValue
                this
              }

              TestClassBuilder withBigDecimalValue(BigDecimal bigDecimalValue) {
                this.bigDecimalValue = bigDecimalValue
                this
              }

              TestClassBuilder withBigIntegerValue(BigInteger bigIntegerValue) {
                this.bigIntegerValue = bigIntegerValue
                this
              }

              TestClassBuilder withTestEnumValue(TestEnum testEnumValue) {
                this.testEnumValue = testEnumValue
                this
              }

              TestClassBuilder withCollectionValue(Map collectionValue) {
                this.collectionValue = collectionValue
                this
              }

              TestClassBuilder withTestSealedValue(TestSealed testSealedValue) {
                this.testSealedValue = testSealedValue
                this
              }

              TestClass build() {
                new TestClass(
                  this.stringValue,
                  this.doubleValue,
                  this.floatValue,
                  this.booleanValue,
                  this.intValue,
                  this.longValue,
                  this.nestedTestValue,
                  this.dateValue,
                  this.uuidValue,
                  this.bigDecimalValue,
                  this.bigIntegerValue,
                  this.testEnumValue,
                  this.collectionValue,
                  this.testSealedValue,
                )
              }
            }

            class TestSealedThirdBuilder {

              private String name = "name"

              private TestSealedThirdBuilder() { }

              static TestSealedThirdBuilder aTestSealedThirdBuilder() {
                new TestSealedThirdBuilder()
              }

              TestSealedThirdBuilder withName(String name) {
                this.name = name
                this
              }

              TestSealed.Third build() {
                new TestSealed.Third(
                  this.name,
                )
              }
            }

            class TestSubClassBuilder {

              private String stringValue = "stringValue"
              private Double doubleValue = 0.0
              private Float floatValue = 0f
              private Boolean booleanValue = false
              private Integer intValue = 0

              private TestSubClassBuilder() { }

              static TestSubClassBuilder aTestSubClassBuilder() {
                new TestSubClassBuilder()
              }

              TestSubClassBuilder withStringValue(String stringValue) {
                this.stringValue = stringValue
                this
              }

              TestSubClassBuilder withDoubleValue(Double doubleValue) {
                this.doubleValue = doubleValue
                this
              }

              TestSubClassBuilder withFloatValue(Float floatValue) {
                this.floatValue = floatValue
                this
              }

              TestSubClassBuilder withBooleanValue(Boolean booleanValue) {
                this.booleanValue = booleanValue
                this
              }

              TestSubClassBuilder withIntValue(Integer intValue) {
                this.intValue = intValue
                this
              }

              TestSubClass build() {
                new TestSubClass(
                  this.stringValue,
                  this.doubleValue,
                  this.floatValue,
                  this.booleanValue,
                  this.intValue,
                )
              }
            }
        """.trimIndent()
        assertThat(generatedContent.removeWhitespaces()).isEqualTo(expected.removeWhitespaces())
    }

    @Test
    fun `should generate a builder function with randomized data while running fixtures`() {
        // Given
        val fixtureFile = kotlin(name = "$fixtureName.kt", contents = fixtureSource)

        // When
        val result1 = compile(
            arguments = mapOf(
                "fixtures.run" to "true",
                "fixtures.randomize" to "true",
            ),
            sourceFiles = listOf(fixtureFile),
        )
        val firstTimeGeneratedContent = getGeneratedContent(
            packageName = packageName,
            filename = "${fixtureName}Fixture.groovy",
        )

        val result2 = compile(
            arguments = mapOf(
                "fixtures.run" to "true",
                "fixtures.randomize" to "true",
            ),
            sourceFiles = listOf(fixtureFile),
        )
        val secondTimeGeneratedContent = getGeneratedContent(
            packageName = packageName,
            filename = "${fixtureName}Fixture.groovy",
        )

        // Then
        assertThat(result1.exitCode).isEqualTo(KotlinCompilation.ExitCode.OK)
        assertThat(result2.exitCode).isEqualTo(KotlinCompilation.ExitCode.OK)
        assertThat(secondTimeGeneratedContent).isNotEqualTo(firstTimeGeneratedContent)
    }

    @Test
    fun `should generate a builder function with resolved type for typealias`() {
        // Given
        val fixtureSource = """
                    package $packageName

                    import com.theblueground.fixtures.Fixture

                    import java.math.BigDecimal

                    @Fixture
                    data class $fixtureName(
                        val bigDecimalAliasValue: BigDecimalAlias,
                    )

                    typealias BigDecimalAlias = BigDecimal
        """.trimIndent()
        val fixtureFile = kotlin(name = "$fixtureName.kt", contents = fixtureSource)

        // When
        val result = compile(
            arguments = mapOf("fixtures.run" to "true"),
            sourceFiles = listOf(fixtureFile),
        )
        val generatedContent = getGeneratedContent(
            packageName = packageName,
            filename = "${fixtureName}Fixture.groovy",
        )

        // Then
        assertThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.OK)
        val expected = """
            package somefixture

            import java.math.BigDecimal

            class TestClassBuilder {

              private BigDecimal bigDecimalAliasValue = BigDecimal.ZERO

              private TestClassBuilder() { }

              static TestClassBuilder aTestClassBuilder() {
                new TestClassBuilder()
              }

              TestClassBuilder withBigDecimalAliasValue(BigDecimal bigDecimalAliasValue) {
                this.bigDecimalAliasValue = bigDecimalAliasValue
                this
              }

              TestClass build() {
                new TestClass(
                  this.bigDecimalAliasValue,
                )
              }
            }
        """.trimIndent()
        assertThat(generatedContent.removeWhitespaces()).isEqualTo(expected.removeWhitespaces())
    }

    @Test
    fun `should not generate a builder function while not running tests`() {
        // Given
        val fixtureFile = kotlin(name = "$fixtureName.kt", contents = fixtureSource)

        // When
        val result = compile(
            arguments = mapOf("fixtures.run" to "false"),
            sourceFiles = listOf(fixtureFile),
        )
        val generatedFile = getGeneratedFile(
            packageName = packageName,
            filename = "${fixtureName}Fixture.kt",
        )

        // Then
        assertThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.OK)
        assertThat(generatedFile.exists()).isEqualTo(false)
    }

    @Test
    fun `should add import for ZoneId when has ZonedDateTime parameter`() {
        // Given
        val fixtureSource = """
                    package $packageName

                    import com.theblueground.fixtures.Fixture

                    import java.time.ZonedDateTime

                    @Fixture
                    data class $fixtureName(
                        val zonedDateTimeValue: ZonedDateTime,
                    )
        """.trimIndent()

        val fixtureFile = kotlin(name = "$fixtureName.kt", contents = fixtureSource)

        // When
        val result = compile(
            arguments = mapOf("fixtures.run" to "true"),
            sourceFiles = listOf(fixtureFile),
        )
        val generatedContent = getGeneratedContent(
            packageName = packageName,
            filename = "${fixtureName}Fixture.groovy",
        )

        // Then
        assertThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.OK)
        val expected = """
            package somefixture

            import java.time.ZonedDateTime
            import java.time.ZoneId

            class TestClassBuilder {

              private ZonedDateTime zonedDateTimeValue = ZonedDateTime.of(1989,1,23,0,0,0,0, ZoneId.of("UTC"))

              private TestClassBuilder() { }

              static TestClassBuilder aTestClassBuilder() {
                new TestClassBuilder()
              }

              TestClassBuilder withZonedDateTimeValue(ZonedDateTime zonedDateTimeValue) {
                this.zonedDateTimeValue = zonedDateTimeValue
                this
              }

              TestClass build() {
                new TestClass(
                  this.zonedDateTimeValue,
                )
              }
            }
        """.trimIndent()

        assertThat(generatedContent.removeWhitespaces()).isEqualTo(expected.removeWhitespaces())
    }

    @Test
    fun `should generate a builder function with standard data and nullable arguments`() {
        // Given
        val fixtureSource = """
                    package $packageName

                    import com.theblueground.fixtures.Fixture

                    import java.math.BigDecimal
                    import java.math.BigInteger
                    import java.util.*

                    @Fixture
                    data class $fixtureName(
                        val stringValue: String?,
                        val doubleValue: Double?,
                        val floatValue: Float?,
                        val booleanValue: Boolean?,
                        val intValue: Int?,
                        val longValue: Long?,
                        val nestedTestValue: TestSubClass?,
                        val dateValue: Date?,
                        val uuidValue: UUID?,
                        val bigDecimalValue: BigDecimal?,
                        val bigIntegerValue: BigInteger?,
                        val testEnumValue: TestEnum?,
                        val collectionValue: Map<Int, String>?,
                        val testSealedValue: TestSealed?
                    )

                    enum class TestEnum {
                        FIRST_ENUM, SECOND_ENUM
                    }

                    sealed class TestSealed {

                        object First : TestSealed()

                        object Second : TestSealed()

                        @Fixture
                        data class Third(val name: String) : TestSealed()
                    }

                    @Fixture
                    data class TestSubClass(
                        val stringValue: String,
                        val doubleValue: Double,
                        val floatValue: Float,
                        val booleanValue: Boolean,
                        val intValue: Int
                    )
        """.trimIndent()
        val fixtureFile = kotlin(name = "$fixtureName.kt", contents = fixtureSource)

        // When
        val result = compile(sourceFiles = listOf(fixtureFile))
        val generatedContent = getGeneratedContent(
            packageName = packageName,
            filename = "${fixtureName}Fixture.groovy",
        )

        // Then
        assertThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.OK)
        val expected = """
            package somefixture

            import somefixture.TestSubClass
            import java.util.Date
            import java.util.UUID
            import java.math.BigDecimal
            import java.math.BigInteger
            import somefixture.TestEnum
            import kotlin.collections.Map
            import somefixture.TestSealed

            class TestClassBuilder {

              private String stringValue = "stringValue"
              private Double doubleValue = 0.0
              private Float floatValue = 0f
              private Boolean booleanValue = false
              private Integer intValue = 0
              private Long longValue = 0L
              private TestSubClass nestedTestValue = somefixture.TestSubClassBuilder.aTestSubClassBuilder().build()
              private Date dateValue = Date(0)
              private UUID uuidValue = UUID.fromString("00000000-0000-0000-0000-000000000000")
              private BigDecimal bigDecimalValue = BigDecimal.ZERO
              private BigInteger bigIntegerValue = BigInteger.ZERO
              private TestEnum testEnumValue = TestEnum.FIRST_ENUM
              private Map collectionValue = [:]
              private TestSealed testSealedValue = TestSealed.First

              private TestClassBuilder() { }

              static TestClassBuilder aTestClassBuilder() {
                new TestClassBuilder()
              }

              TestClassBuilder withStringValue(String stringValue) {
                this.stringValue = stringValue
                this
              }

              TestClassBuilder withDoubleValue(Double doubleValue) {
                this.doubleValue = doubleValue
                this
              }

              TestClassBuilder withFloatValue(Float floatValue) {
                this.floatValue = floatValue
                this
              }

              TestClassBuilder withBooleanValue(Boolean booleanValue) {
                this.booleanValue = booleanValue
                this
              }

              TestClassBuilder withIntValue(Integer intValue) {
                this.intValue = intValue
                this
              }

              TestClassBuilder withLongValue(Long longValue) {
                this.longValue = longValue
                this
              }

              TestClassBuilder withNestedTestValue(TestSubClass nestedTestValue) {
                this.nestedTestValue = nestedTestValue
                this
              }

              TestClassBuilder withDateValue(Date dateValue) {
                this.dateValue = dateValue
                this
              }

              TestClassBuilder withUuidValue(UUID uuidValue) {
                this.uuidValue = uuidValue
                this
              }

              TestClassBuilder withBigDecimalValue(BigDecimal bigDecimalValue) {
                this.bigDecimalValue = bigDecimalValue
                this
              }

              TestClassBuilder withBigIntegerValue(BigInteger bigIntegerValue) {
                this.bigIntegerValue = bigIntegerValue
                this
              }

              TestClassBuilder withTestEnumValue(TestEnum testEnumValue) {
                this.testEnumValue = testEnumValue
                this
              }

              TestClassBuilder withCollectionValue(Map collectionValue) {
                this.collectionValue = collectionValue
                this
              }

              TestClassBuilder withTestSealedValue(TestSealed testSealedValue) {
                this.testSealedValue = testSealedValue
                this
              }

              TestClass build() {
                new TestClass(
                  this.stringValue,
                  this.doubleValue,
                  this.floatValue,
                  this.booleanValue,
                  this.intValue,
                  this.longValue,
                  this.nestedTestValue,
                  this.dateValue,
                  this.uuidValue,
                  this.bigDecimalValue,
                  this.bigIntegerValue,
                  this.testEnumValue,
                  this.collectionValue,
                  this.testSealedValue,
                )
              }
            }

            class TestSealedThirdBuilder {

              private String name = "name"

              private TestSealedThirdBuilder() { }

              static TestSealedThirdBuilder aTestSealedThirdBuilder() {
                new TestSealedThirdBuilder()
              }

              TestSealedThirdBuilder withName(String name) {
                this.name = name
                this
              }

              TestSealed.Third build() {
                new TestSealed.Third(
                  this.name,
                )
              }
            }

            class TestSubClassBuilder {

              private String stringValue = "stringValue"
              private Double doubleValue = 0.0
              private Float floatValue = 0f
              private Boolean booleanValue = false
              private Integer intValue = 0

              private TestSubClassBuilder() { }

              static TestSubClassBuilder aTestSubClassBuilder() {
                new TestSubClassBuilder()
              }

              TestSubClassBuilder withStringValue(String stringValue) {
                this.stringValue = stringValue
                this
              }

              TestSubClassBuilder withDoubleValue(Double doubleValue) {
                this.doubleValue = doubleValue
                this
              }

              TestSubClassBuilder withFloatValue(Float floatValue) {
                this.floatValue = floatValue
                this
              }

              TestSubClassBuilder withBooleanValue(Boolean booleanValue) {
                this.booleanValue = booleanValue
                this
              }

              TestSubClassBuilder withIntValue(Integer intValue) {
                this.intValue = intValue
                this
              }

              TestSubClass build() {
                new TestSubClass(
                  this.stringValue,
                  this.doubleValue,
                  this.floatValue,
                  this.booleanValue,
                  this.intValue,
                )
              }
            }
        """.trimIndent()
        assertThat(generatedContent.removeWhitespaces()).isEqualTo(expected.removeWhitespaces())
    }

    @Test
    fun `should generate a builder function with the provided fixture adapter`() {
        // Given
        val fixtureSource = """
                    package $packageName

                    import com.theblueground.fixtures.Fixture
                    import com.theblueground.fixtures.FixtureAdapter

                    @Fixture
                    data class $fixtureName(val stringValue: String)

                    @FixtureAdapter
                    fun stringFixtureProvider(): String = "A string"
        """.trimIndent()
        val fixtureFile = kotlin(name = "$fixtureName.kt", contents = fixtureSource)

        // When
        val result = compile(sourceFiles = listOf(fixtureFile))
        val generatedContent = getGeneratedContent(
            packageName = packageName,
            filename = "${fixtureName}Fixture.groovy",
        )

        // Then
        assertThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.OK)
        val expected = """
            package somefixture

            import kotlin.String

            class TestClassBuilder {

              private String stringValue = somefixture.stringFixtureProvider()

              private TestClassBuilder() { }

              static TestClassBuilder aTestClassBuilder() {
                new TestClassBuilder()
              }

              TestClassBuilder withStringValue(String stringValue) {
                this.stringValue = stringValue
                this
              }

              TestClass build() {
                new TestClass(
                  this.stringValue,
                )
              }
            }
        """.trimIndent()
        assertThat(generatedContent.removeWhitespaces()).isEqualTo(expected.removeWhitespaces())
    }

    @Test
    fun `should generate builder functions for inner classes with the same name`() {
        // Given
        val fixtureSource = """
                    package $packageName

                    import com.theblueground.fixtures.Fixture
                    import com.theblueground.fixtures.FixtureAdapter

                    @Fixture
                    data class Foo(val baz: Baz) {
                        @Fixture
                        data class Baz(val text: String)
                    }

                    @Fixture
                    data class Bar(val baz: Baz) {
                        @Fixture
                        data class Baz(val number: Int)
                    }
        """.trimIndent()
        val fixtureFile = kotlin(name = "$fixtureName.kt", contents = fixtureSource)

        // When
        val result = compile(sourceFiles = listOf(fixtureFile))
        val generatedContent = getGeneratedContent(
            packageName = packageName,
            filename = "${fixtureName}Fixture.groovy",
        )

        // Then
        assertThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.OK)
        val expected = """
            package somefixture

            import somefixture.FooBaz
            import somefixture.BarBaz

            class FooBuilder {

              private FooBaz baz = somefixture.FooBazBuilder.aFooBazBuilder().build()

              private FooBuilder() { }

              static FooBuilder aFooBuilder() {
                new FooBuilder()
              }

              FooBuilder withBaz(FooBaz baz) {
                this.baz = baz
                this
              }

              Foo build() {
                new Foo(
                  this.baz,
                )
              }
            }

            class FooBazBuilder {

              private String text = "text"

              private FooBazBuilder() { }

              static FooBazBuilder aFooBazBuilder() {
                new FooBazBuilder()
              }

              FooBazBuilder withText(String text) {
                this.text = text
                this
              }

              Foo.Baz build() {
                new Foo.Baz(
                  this.text,
                )
              }
            }

            class BarBuilder {

              private BarBaz baz = somefixture.BarBazBuilder.aBarBazBuilder().build()

              private BarBuilder() { }

              static BarBuilder aBarBuilder() {
                new BarBuilder()
              }

              BarBuilder withBaz(BarBaz baz) {
                this.baz = baz
                this
              }

              Bar build() {
                new Bar(
                  this.baz,
                )
              }
            }

            class BarBazBuilder {

              private Integer number = 0

              private BarBazBuilder() { }

              static BarBazBuilder aBarBazBuilder() {
                new BarBazBuilder()
              }

              BarBazBuilder withNumber(Integer number) {
                this.number = number
                this
              }

              Bar.Baz build() {
                new Bar.Baz(
                  this.number,
                )
              }
            }
        """.trimIndent()
        assertThat(generatedContent.removeWhitespaces()).isEqualTo(expected.removeWhitespaces())
    }
}
