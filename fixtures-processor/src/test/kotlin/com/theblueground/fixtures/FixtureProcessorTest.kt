package com.theblueground.fixtures

import com.google.common.truth.Truth.assertThat
import com.squareup.kotlinpoet.ksp.KotlinPoetKspPreview
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile.Companion.kotlin
import org.junit.Test

@KotlinPoetKspPreview
class FixtureProcessorTest : KSPTest() {

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
                        val testSealedObjectValue: TestSealedObject,
                        val testSealedDataClassValue: TestSealedDataClass,
                    )

                    enum class TestEnum {
                        FIRST_ENUM, SECOND_ENUM
                    }

                    sealed class TestSealedObject {

                        data class First(val name: String) : TestSealedObject()

                        object Second : TestSealedObject()

                        object Third : TestSealedObject()
                    }

                    sealed class TestSealedDataClass {

                        data class First(val name: String) : TestSealedDataClass()

                        @Fixture
                        data class Second(val name: String) : TestSealedDataClass()
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
            filename = "${fixtureName}Fixture.kt",
        )

        // Then
        assertThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.OK)
        val expected = """
            package $packageName

            import java.math.BigDecimal
            import java.math.BigInteger
            import java.util.Date
            import java.util.UUID
            import kotlin.Boolean
            import kotlin.Double
            import kotlin.Float
            import kotlin.Int
            import kotlin.Long
            import kotlin.String
            import kotlin.collections.Map

            public fun create$fixtureName(
              stringValue: String = "stringValue",
              doubleValue: Double = 0.0,
              floatValue: Float = 0f,
              booleanValue: Boolean = false,
              intValue: Int = 0,
              longValue: Long = 0L,
              nestedTestValue: TestSubClass = $packageName.createTestSubClass(),
              dateValue: Date = Date(0),
              uuidValue: UUID = UUID.fromString("00000000-0000-0000-0000-000000000000"),
              bigDecimalValue: BigDecimal = BigDecimal.ZERO,
              bigIntegerValue: BigInteger = BigInteger.ZERO,
              testEnumValue: TestEnum = TestEnum.FIRST_ENUM,
              collectionValue: Map<Int, String> = emptyMap(),
              testSealedObjectValue: TestSealedObject = TestSealedObject.Second,
              testSealedDataClassValue: TestSealedDataClass = createTestSealedDataClassSecond(),
            ): TestClass = $packageName.$fixtureName(
            	stringValue = stringValue,
            	doubleValue = doubleValue,
            	floatValue = floatValue,
            	booleanValue = booleanValue,
            	intValue = intValue,
            	longValue = longValue,
            	nestedTestValue = nestedTestValue,
            	dateValue = dateValue,
            	uuidValue = uuidValue,
            	bigDecimalValue = bigDecimalValue,
            	bigIntegerValue = bigIntegerValue,
            	testEnumValue = testEnumValue,
            	collectionValue = collectionValue,
            	testSealedObjectValue = testSealedObjectValue,
            	testSealedDataClassValue = testSealedDataClassValue
            )

            public fun createTestSealedDataClassSecond(name: String = "name"): TestSealedDataClass.Second =
                $packageName.TestSealedDataClass.Second(
            	name = name
            )

            public fun createTestSubClass(
              stringValue: String = "stringValue",
              doubleValue: Double = 0.0,
              floatValue: Float = 0f,
              booleanValue: Boolean = false,
              intValue: Int = 0,
            ): TestSubClass = $packageName.TestSubClass(
            	stringValue = stringValue,
            	doubleValue = doubleValue,
            	floatValue = floatValue,
            	booleanValue = booleanValue,
            	intValue = intValue
            )

        """.trimIndent()
        assertThat(generatedContent).isEqualTo(expected)
    }

    @Test
    fun `should generate a builder function with standard data when no options are defined`() {
        // Given
        val fixtureFile = kotlin(name = "$fixtureName.kt", contents = fixtureSource)

        // When
        val result = compile(sourceFiles = listOf(fixtureFile))
        val generatedContent = getGeneratedContent(
            packageName = packageName,
            filename = "${fixtureName}Fixture.kt",
        )

        // Then
        assertThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.OK)
        val expected = """
            package $packageName

            import java.math.BigDecimal
            import java.math.BigInteger
            import java.util.Date
            import java.util.UUID
            import kotlin.Boolean
            import kotlin.Double
            import kotlin.Float
            import kotlin.Int
            import kotlin.Long
            import kotlin.String
            import kotlin.collections.Map

            public fun create$fixtureName(
              stringValue: String = "stringValue",
              doubleValue: Double = 0.0,
              floatValue: Float = 0f,
              booleanValue: Boolean = false,
              intValue: Int = 0,
              longValue: Long = 0L,
              nestedTestValue: TestSubClass = $packageName.createTestSubClass(),
              dateValue: Date = Date(0),
              uuidValue: UUID = UUID.fromString("00000000-0000-0000-0000-000000000000"),
              bigDecimalValue: BigDecimal = BigDecimal.ZERO,
              bigIntegerValue: BigInteger = BigInteger.ZERO,
              testEnumValue: TestEnum = TestEnum.FIRST_ENUM,
              collectionValue: Map<Int, String> = emptyMap(),
              testSealedObjectValue: TestSealedObject = TestSealedObject.Second,
              testSealedDataClassValue: TestSealedDataClass = createTestSealedDataClassSecond(),
            ): TestClass = $packageName.$fixtureName(
            	stringValue = stringValue,
            	doubleValue = doubleValue,
            	floatValue = floatValue,
            	booleanValue = booleanValue,
            	intValue = intValue,
            	longValue = longValue,
            	nestedTestValue = nestedTestValue,
            	dateValue = dateValue,
            	uuidValue = uuidValue,
            	bigDecimalValue = bigDecimalValue,
            	bigIntegerValue = bigIntegerValue,
            	testEnumValue = testEnumValue,
            	collectionValue = collectionValue,
            	testSealedObjectValue = testSealedObjectValue,
            	testSealedDataClassValue = testSealedDataClassValue
            )

            public fun createTestSealedDataClassSecond(name: String = "name"): TestSealedDataClass.Second =
                $packageName.TestSealedDataClass.Second(
            	name = name
            )

            public fun createTestSubClass(
              stringValue: String = "stringValue",
              doubleValue: Double = 0.0,
              floatValue: Float = 0f,
              booleanValue: Boolean = false,
              intValue: Int = 0,
            ): TestSubClass = $packageName.TestSubClass(
            	stringValue = stringValue,
            	doubleValue = doubleValue,
            	floatValue = floatValue,
            	booleanValue = booleanValue,
            	intValue = intValue
            )

        """.trimIndent()
        assertThat(generatedContent).isEqualTo(expected)
    }

    @Test
    fun `should generate a builder function with custom prefix when defined`() {
        // Given
        val fixtureFile = kotlin(name = "$fixtureName.kt", contents = fixtureSource)

        // When
        val result = compile(
            arguments = mapOf("fixtures.prefix" to "new"),
            sourceFiles = listOf(fixtureFile),
        )
        val generatedContent = getGeneratedContent(
            packageName = packageName,
            filename = "${fixtureName}Fixture.kt",
        )

        // Then
        assertThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.OK)
        val expected = """
            package $packageName

            import java.math.BigDecimal
            import java.math.BigInteger
            import java.util.Date
            import java.util.UUID
            import kotlin.Boolean
            import kotlin.Double
            import kotlin.Float
            import kotlin.Int
            import kotlin.Long
            import kotlin.String
            import kotlin.collections.Map

            public fun new$fixtureName(
              stringValue: String = "stringValue",
              doubleValue: Double = 0.0,
              floatValue: Float = 0f,
              booleanValue: Boolean = false,
              intValue: Int = 0,
              longValue: Long = 0L,
              nestedTestValue: TestSubClass = $packageName.newTestSubClass(),
              dateValue: Date = Date(0),
              uuidValue: UUID = UUID.fromString("00000000-0000-0000-0000-000000000000"),
              bigDecimalValue: BigDecimal = BigDecimal.ZERO,
              bigIntegerValue: BigInteger = BigInteger.ZERO,
              testEnumValue: TestEnum = TestEnum.FIRST_ENUM,
              collectionValue: Map<Int, String> = emptyMap(),
              testSealedObjectValue: TestSealedObject = TestSealedObject.Second,
              testSealedDataClassValue: TestSealedDataClass = newTestSealedDataClassSecond(),
            ): TestClass = $packageName.$fixtureName(
            	stringValue = stringValue,
            	doubleValue = doubleValue,
            	floatValue = floatValue,
            	booleanValue = booleanValue,
            	intValue = intValue,
            	longValue = longValue,
            	nestedTestValue = nestedTestValue,
            	dateValue = dateValue,
            	uuidValue = uuidValue,
            	bigDecimalValue = bigDecimalValue,
            	bigIntegerValue = bigIntegerValue,
            	testEnumValue = testEnumValue,
            	collectionValue = collectionValue,
            	testSealedObjectValue = testSealedObjectValue,
            	testSealedDataClassValue = testSealedDataClassValue
            )

            public fun newTestSealedDataClassSecond(name: String = "name"): TestSealedDataClass.Second =
                $packageName.TestSealedDataClass.Second(
            	name = name
            )

            public fun newTestSubClass(
              stringValue: String = "stringValue",
              doubleValue: Double = 0.0,
              floatValue: Float = 0f,
              booleanValue: Boolean = false,
              intValue: Int = 0,
            ): TestSubClass = $packageName.TestSubClass(
            	stringValue = stringValue,
            	doubleValue = doubleValue,
            	floatValue = floatValue,
            	booleanValue = booleanValue,
            	intValue = intValue
            )

        """.trimIndent()
        assertThat(generatedContent).isEqualTo(expected)
    }

    @Test
    fun `should generate a builder function with no prefix when defined`() {
        // Given
        val fixtureFile = kotlin(name = "$fixtureName.kt", contents = fixtureSource)

        // When
        val result = compile(
            arguments = mapOf("fixtures.prefix" to ""),
            sourceFiles = listOf(fixtureFile),
        )
        val generatedContent = getGeneratedContent(
            packageName = packageName,
            filename = "${fixtureName}Fixture.kt",
        )

        // Then
        assertThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.OK)
        val expected = """
            package $packageName

            import java.math.BigDecimal
            import java.math.BigInteger
            import java.util.Date
            import java.util.UUID
            import kotlin.Boolean
            import kotlin.Double
            import kotlin.Float
            import kotlin.Int
            import kotlin.Long
            import kotlin.String
            import kotlin.collections.Map

            public fun ${fixtureName.replaceFirstChar { it.lowercase() }}(
              stringValue: String = "stringValue",
              doubleValue: Double = 0.0,
              floatValue: Float = 0f,
              booleanValue: Boolean = false,
              intValue: Int = 0,
              longValue: Long = 0L,
              nestedTestValue: TestSubClass = $packageName.testSubClass(),
              dateValue: Date = Date(0),
              uuidValue: UUID = UUID.fromString("00000000-0000-0000-0000-000000000000"),
              bigDecimalValue: BigDecimal = BigDecimal.ZERO,
              bigIntegerValue: BigInteger = BigInteger.ZERO,
              testEnumValue: TestEnum = TestEnum.FIRST_ENUM,
              collectionValue: Map<Int, String> = emptyMap(),
              testSealedObjectValue: TestSealedObject = TestSealedObject.Second,
              testSealedDataClassValue: TestSealedDataClass = testSealedDataClassSecond(),
            ): TestClass = $packageName.$fixtureName(
            	stringValue = stringValue,
            	doubleValue = doubleValue,
            	floatValue = floatValue,
            	booleanValue = booleanValue,
            	intValue = intValue,
            	longValue = longValue,
            	nestedTestValue = nestedTestValue,
            	dateValue = dateValue,
            	uuidValue = uuidValue,
            	bigDecimalValue = bigDecimalValue,
            	bigIntegerValue = bigIntegerValue,
            	testEnumValue = testEnumValue,
            	collectionValue = collectionValue,
            	testSealedObjectValue = testSealedObjectValue,
            	testSealedDataClassValue = testSealedDataClassValue
            )

            public fun testSealedDataClassSecond(name: String = "name"): TestSealedDataClass.Second =
                $packageName.TestSealedDataClass.Second(
            	name = name
            )

            public fun testSubClass(
              stringValue: String = "stringValue",
              doubleValue: Double = 0.0,
              floatValue: Float = 0f,
              booleanValue: Boolean = false,
              intValue: Int = 0,
            ): TestSubClass = $packageName.TestSubClass(
            	stringValue = stringValue,
            	doubleValue = doubleValue,
            	floatValue = floatValue,
            	booleanValue = booleanValue,
            	intValue = intValue
            )

        """.trimIndent()
        assertThat(generatedContent).isEqualTo(expected)
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
            filename = "${fixtureName}Fixture.kt",
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
            filename = "${fixtureName}Fixture.kt",
        )

        // Then
        assertThat(result1.exitCode).isEqualTo(KotlinCompilation.ExitCode.OK)
        assertThat(result2.exitCode).isEqualTo(KotlinCompilation.ExitCode.OK)
        assertThat(secondTimeGeneratedContent).doesNotMatch(firstTimeGeneratedContent)
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
            filename = "${fixtureName}Fixture.kt",
        )

        // Then
        assertThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.OK)
        val expected = """
            package $packageName

            import java.math.BigDecimal

            public fun create$fixtureName(bigDecimalAliasValue: BigDecimal = BigDecimal.ZERO): TestClass =
                $packageName.$fixtureName(
            	bigDecimalAliasValue = bigDecimalAliasValue
            )

        """.trimIndent()
        assertThat(generatedContent).isEqualTo(expected)
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
            filename = "${fixtureName}Fixture.kt",
        )

        // Then
        assertThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.OK)
        val expected = """
            package $packageName

            import java.time.ZoneId
            import java.time.ZonedDateTime

            public fun create$fixtureName(zonedDateTimeValue: ZonedDateTime = ZonedDateTime.of(1989,1,23,0,0,0,0,
                ZoneId.of("UTC"))): TestClass = $packageName.$fixtureName(
            	zonedDateTimeValue = zonedDateTimeValue
            )

        """.trimIndent()

        assertThat(generatedContent).isEqualTo(expected)
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
            filename = "${fixtureName}Fixture.kt",
        )

        // Then
        assertThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.OK)
        val expected = """
            package $packageName

            import java.math.BigDecimal
            import java.math.BigInteger
            import java.util.Date
            import java.util.UUID
            import kotlin.Boolean
            import kotlin.Double
            import kotlin.Float
            import kotlin.Int
            import kotlin.Long
            import kotlin.String
            import kotlin.collections.Map

            public fun create$fixtureName(
              stringValue: String? = "stringValue",
              doubleValue: Double? = 0.0,
              floatValue: Float? = 0f,
              booleanValue: Boolean? = false,
              intValue: Int? = 0,
              longValue: Long? = 0L,
              nestedTestValue: TestSubClass? = $packageName.createTestSubClass(),
              dateValue: Date? = Date(0),
              uuidValue: UUID? = UUID.fromString("00000000-0000-0000-0000-000000000000"),
              bigDecimalValue: BigDecimal? = BigDecimal.ZERO,
              bigIntegerValue: BigInteger? = BigInteger.ZERO,
              testEnumValue: TestEnum? = TestEnum.FIRST_ENUM,
              collectionValue: Map<Int, String>? = emptyMap(),
              testSealedValue: TestSealed? = TestSealed.First,
            ): TestClass = $packageName.$fixtureName(
            	stringValue = stringValue,
            	doubleValue = doubleValue,
            	floatValue = floatValue,
            	booleanValue = booleanValue,
            	intValue = intValue,
            	longValue = longValue,
            	nestedTestValue = nestedTestValue,
            	dateValue = dateValue,
            	uuidValue = uuidValue,
            	bigDecimalValue = bigDecimalValue,
            	bigIntegerValue = bigIntegerValue,
            	testEnumValue = testEnumValue,
            	collectionValue = collectionValue,
            	testSealedValue = testSealedValue
            )

            public fun createTestSealedThird(name: String = "name"): TestSealed.Third =
                $packageName.TestSealed.Third(
            	name = name
            )

            public fun createTestSubClass(
              stringValue: String = "stringValue",
              doubleValue: Double = 0.0,
              floatValue: Float = 0f,
              booleanValue: Boolean = false,
              intValue: Int = 0,
            ): TestSubClass = $packageName.TestSubClass(
            	stringValue = stringValue,
            	doubleValue = doubleValue,
            	floatValue = floatValue,
            	booleanValue = booleanValue,
            	intValue = intValue
            )

        """.trimIndent()
        assertThat(generatedContent).isEqualTo(expected)
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
            filename = "${fixtureName}Fixture.kt",
        )

        // Then
        assertThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.OK)
        val expected = """
            package somefixture

            import kotlin.String

            public fun createTestClass(stringValue: String = somefixture.stringFixtureProvider()): TestClass =
                somefixture.TestClass(
            	stringValue = stringValue
            )

        """.trimIndent()
        assertThat(generatedContent).isEqualTo(expected)
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
            filename = "${fixtureName}Fixture.kt",
        )

        // Then
        assertThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.OK)
        val expected = """
                    package somefixture

                    import kotlin.Int
                    import kotlin.String

                    public fun createFoo(baz: Foo.Baz = somefixture.createFooBaz()): Foo = somefixture.Foo(
                    	baz = baz
                    )

                    public fun createFooBaz(text: String = "text"): Foo.Baz = somefixture.Foo.Baz(
                    	text = text
                    )

                    public fun createBar(baz: Bar.Baz = somefixture.createBarBaz()): Bar = somefixture.Bar(
                    	baz = baz
                    )

                    public fun createBarBaz(number: Int = 0): Bar.Baz = somefixture.Bar.Baz(
                    	number = number
                    )

        """.trimIndent()
        assertThat(generatedContent).isEqualTo(expected)
    }
}
