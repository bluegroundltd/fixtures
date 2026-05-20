package com.theblueground.fixtures

import com.tschuchort.compiletesting.JvmCompilationResult
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.configureKsp
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import java.io.File

@OptIn(ExperimentalCompilerApi::class)
abstract class KSPTest {

    private val generatedSourcesPathPrefix = "ksp/sources/kotlin/"

    @Rule
    @JvmField
    internal val temporaryFolder: TemporaryFolder = TemporaryFolder()

    internal fun getGeneratedFile(packageName: String, filename: String): File {
        val generatedSourcesPath = "$generatedSourcesPathPrefix$packageName"
        val path = temporaryFolder.root.resolve(generatedSourcesPath)
        return File(path, filename)
    }

    internal fun getGeneratedContent(packageName: String, filename: String): String =
        getGeneratedFile(packageName = packageName, filename = filename).bufferedReader().readText()

    private fun prepareCompilation(
        arguments: Map<String, String>,
        sourceFiles: List<SourceFile>,
    ): KotlinCompilation = KotlinCompilation()
        .apply {
            configureKsp {
                symbolProcessorProviders += FixtureProcessorProvider()
                processorOptions.putAll(arguments)
                withCompilation = true
            }

            workingDir = temporaryFolder.root
            inheritClassPath = true
            sources = sourceFiles
            verbose = false
        }

    internal fun compile(
        arguments: Map<String, String> = emptyMap(),
        sourceFiles: List<SourceFile>,
    ): JvmCompilationResult = prepareCompilation(
        arguments = arguments,
        sourceFiles = sourceFiles,
    ).compile()
}
