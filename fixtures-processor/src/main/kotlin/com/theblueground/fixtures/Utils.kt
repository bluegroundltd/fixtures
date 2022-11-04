package com.theblueground.fixtures

import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.Modifier

private val primitives: Set<String> = setOf(
    "String",
    "Char",
    "Boolean",
    "Int",
    "Long",
    "Float",
    "Double"
)

internal fun String.isPrimitive(): Boolean = primitives.contains(this)

internal val KSDeclaration.isPrimitive: Boolean
    get() = simpleName.asString().isPrimitive()

private val knownTypes: Set<String> = setOf(
    "Date", "TimeZone", "UUID", "LocalDate", "LocalTime", "LocalDateTime", "ZonedDateTime",
    "Instant", "OffsetTime", "OffsetDateTime", "ZoneId", "BigDecimal", "BigInteger"
)

internal val KSDeclaration.isKnownType: Boolean
    get() = simpleName.asString().isKnownType()

internal fun String.isKnownType(): Boolean = knownTypes.contains(this)

internal val KSDeclaration.isDataClass: Boolean
    // Unfortunately, this will work only if the data class is on tha same gradle module
    // We can not take a similar approach to enum. Check here: https://github.com/google/ksp/issues/736
    get() = modifiers.contains(Modifier.DATA)

internal val KSDeclaration.isEnum: Boolean
    // We do not use modifiers.contains(Modifier.ENUM) to make it possible
    // to detect enums in different gradle modules.
    get() = this is KSClassDeclaration && classKind == ClassKind.ENUM_CLASS

internal val KSDeclaration.isSealed: Boolean
    // Unfortunately, this will work only if the data class is on tha same gradle module
    // We can not take a similar approach to enum. Check here: https://github.com/google/ksp/issues/736
    get() = modifiers.contains(Modifier.SEALED)

internal val KSDeclaration.isFixture: Boolean
    get() = annotations.contains(Fixture::class.simpleName!!)

internal val KSClassDeclaration.isObject: Boolean
    get() = classKind == ClassKind.OBJECT

internal fun Sequence<KSAnnotation>.contains(annotationName: String): Boolean =
    any { it.shortName.asString() == annotationName }

private val collections: Set<String> = setOf(
    "Array",
    "List",
    "Set",
    "Map"
)

internal fun String.isCollection(): Boolean = collections.contains(this)

internal val KSDeclaration.isCollection: Boolean
    get() = simpleName.asString().isCollection()
