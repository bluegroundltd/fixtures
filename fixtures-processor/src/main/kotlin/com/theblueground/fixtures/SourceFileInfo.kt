package com.theblueground.fixtures

import com.google.devtools.ksp.symbol.KSFile

/**
 * Holds eagerly-extracted properties from a [KSFile] so they can be safely accessed
 * after the KSP analysis session ends (e.g. in [com.google.devtools.ksp.processing.SymbolProcessor.finish]).
 */
internal data class SourceFileInfo(
    val fileName: String,
    val packageName: String,
    val ksFile: KSFile,
)
