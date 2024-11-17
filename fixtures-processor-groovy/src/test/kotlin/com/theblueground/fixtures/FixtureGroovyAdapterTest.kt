package com.theblueground.fixtures

import com.google.common.truth.Truth.assertThat
import com.squareup.kotlinpoet.ksp.KotlinPoetKspPreview
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import org.junit.Test

@KotlinPoetKspPreview
class FixtureGroovyAdapterTest : KSPTest(
    generatedSourcesPathPrefix = "ksp/sources/resources/",
    fixtureProcessorProvider = FixtureGroovyProcessorProvider(),
) {

    private val externalClassPackageName = "external"

    private val externalClassName = "ExternalClass"

    private val externalClassSource = """
                    package $externalClassPackageName

                    class $externalClassName
    """.trimIndent()

    private val fixturePackageName = "somefixture"

    private val fixtureName = "TestClass"

    private val fixtureSource = """
                    package $fixturePackageName

                    import  $externalClassPackageName.$externalClassName
                    import com.theblueground.fixtures.Fixture

                    @Fixture
                    data class $fixtureName(
                        val stringValue: String,
                        val doubleValue: Double,
                        val externalClassValue: $externalClassName,
                    )
    """.trimIndent()

    private val adapterClassPackageName = "adapter"

    private val adapterSource = """
                    package $adapterClassPackageName

                    import  $externalClassPackageName.$externalClassName
                    import com.theblueground.fixtures.FixtureAdapter

                    @FixtureAdapter
                    fun fixtureProvider(): $externalClassName = $externalClassName()
    """.trimIndent()

    @Test
    fun `should generate a builder function while running fixtures`() {
        // Given
        val externalClassFile = SourceFile.kotlin(
            name = "$externalClassName.kt",
            contents = externalClassSource,
        )
        val fixtureFile = SourceFile.kotlin(
            name = "$fixtureName.kt",
            contents = fixtureSource,
        )
        val fixtureAdapterFile = SourceFile.kotlin(
            name = "FixtureAdapter.kt",
            contents = adapterSource,
        )

        // When
        val result = compile(
            arguments = mapOf("fixtures.run" to "true"),
            sourceFiles = listOf(externalClassFile, fixtureFile, fixtureAdapterFile),
        )
        val generatedContent = getGeneratedContent(
            packageName = fixturePackageName,
            filename = "${fixtureName}Fixture.groovy",
        )

        // Then
        assertThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.OK)
        val expected = """
            package somefixture

            import external.ExternalClass

            class TestClassBuilder {

            	private String stringValue = "stringValue"
            	private Double doubleValue = 0.0
            	private ExternalClass externalClassValue = adapter.fixtureProvider()

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

            	TestClassBuilder withExternalClassValue(ExternalClass externalClassValue) {
            		this.externalClassValue = externalClassValue
            		this
            	}

            	TestClass build() {
            		new TestClass(
            			this.stringValue,
            			this.doubleValue,
            			this.externalClassValue,
            		)
            	}
            }
        """.trimIndent()
        assertThat(generatedContent.removeWhitespaces()).isEqualTo(expected.removeWhitespaces())
    }

    @Test
    fun `should generate a builder function when no options are defined`() {
        // Given
        val externalClassFile = SourceFile.kotlin(
            name = "$externalClassName.kt",
            contents = externalClassSource,
        )
        val fixtureFile = SourceFile.kotlin(
            name = "$fixtureName.kt",
            contents = fixtureSource,
        )
        val fixtureAdapterFile = SourceFile.kotlin(
            name = "FixtureAdapter.kt",
            contents = adapterSource,
        )

        // When
        val result = compile(
            sourceFiles = listOf(externalClassFile, fixtureFile, fixtureAdapterFile),
        )
        val generatedContent = getGeneratedContent(
            packageName = fixturePackageName,
            filename = "${fixtureName}Fixture.groovy",
        )

        // Then
        assertThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.OK)
        val expected = """
            package somefixture

            import external.ExternalClass

            class TestClassBuilder {

            	private String stringValue = "stringValue"
            	private Double doubleValue = 0.0
            	private ExternalClass externalClassValue = adapter.fixtureProvider()

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

            	TestClassBuilder withExternalClassValue(ExternalClass externalClassValue) {
            		this.externalClassValue = externalClassValue
            		this
            	}

            	TestClass build() {
            		new TestClass(
            			this.stringValue,
            			this.doubleValue,
            			this.externalClassValue,
            		)
            	}
            }
        """.trimIndent()
        assertThat(generatedContent.removeWhitespaces()).isEqualTo(expected.removeWhitespaces())
    }

    @Test
    fun `should not generate a builder function while not running fixtures`() {
        // Given
        val externalClassFile = SourceFile.kotlin(
            name = "$externalClassName.kt",
            contents = externalClassSource,
        )
        val fixtureFile = SourceFile.kotlin(
            name = "$fixtureName.kt",
            contents = fixtureSource,
        )
        val fixtureAdapterFile = SourceFile.kotlin(
            name = "FixtureAdapter.kt",
            contents = adapterSource,
        )

        // When
        val result = compile(
            arguments = mapOf("fixtures.run" to "false"),
            sourceFiles = listOf(externalClassFile, fixtureFile, fixtureAdapterFile),
        )
        val generatedFile = getGeneratedFile(
            packageName = fixturePackageName,
            filename = "${fixtureName}Fixture.kt",
        )

        // Then
        assertThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.OK)
        assertThat(generatedFile.exists()).isEqualTo(false)
    }
}
