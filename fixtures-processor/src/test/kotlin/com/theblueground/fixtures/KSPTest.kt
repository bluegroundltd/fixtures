package com.theblueground.fixtures

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.kspArgs
import com.tschuchort.compiletesting.kspWithCompilation
import com.tschuchort.compiletesting.symbolProcessorProviders
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import java.io.File

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
        sourceFiles: List<SourceFile>
    ): KotlinCompilation = KotlinCompilation()
        .apply {
            kspArgs = arguments.toMutableMap()
            workingDir = temporaryFolder.root
            inheritClassPath = true
            symbolProcessorProviders = listOf(FixtureProcessorProvider())
            sources = sourceFiles
            verbose = false
            kspWithCompilation = true
        }

    internal fun compile(
        arguments: Map<String, String> = emptyMap(),
        sourceFiles: List<SourceFile>
    ): KotlinCompilation.Result =
        prepareCompilation(arguments = arguments, sourceFiles = sourceFiles).compile()
}
