package com.theblueground.fixtures

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.Dynamic
import com.squareup.kotlinpoet.LambdaTypeName
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.WildcardTypeName

/**
 * Keeps all the needed information that is processed for a data class' parameter. The data class
 * should be annotated with the [Fixture] annotation. The information will be used in order to
 * generate a helper function which will create test data.
 */
internal sealed class ProcessedFixtureParameter(
    open val name: String,
    open val type: TypeName,
) {

    /**
     * Keeps all the processed information for a primitive type field.
     */
    data class PrimitiveParameter(
        override val name: String,
        override val type: TypeName,
    ) : ProcessedFixtureParameter(
        name = name,
        type = type,
    )

    /**
     * Keeps all the processed information for a non primitive class type field which can be handled
     * by our kotlin symbol processor, without annotating it with the [Fixture] annotation.
     * Supported classes are:
     * - Date
     * - TimeZone
     * - UUID
     * - LocalDate
     * - LocalTime
     * - LocalDateTime
     * - ZonedDateTime
     * - Instant
     * - OffsetTime
     * - OffsetDateTime
     * - ZoneId
     * - BigDecimal
     * - BigInteger
     */
    data class KnownTypeParameter(
        override val name: String,
        override val type: TypeName,
    ) : ProcessedFixtureParameter(
        name = name,
        type = type,
    )

    /**
     * Keeps all the processed information for a field which has a type that is also annotated with
     * the [Fixture] annotation.
     */
    data class FixtureParameter(
        override val name: String,
        override val type: TypeName,
    ) : ProcessedFixtureParameter(
        name = name,
        type = type,
    )

    /**
     * Keeps all the processed information for an enum type field.
     */
    data class EnumParameter(
        override val name: String,
        override val type: TypeName,
        val entries: List<String>,
    ) : ProcessedFixtureParameter(
        name = name,
        type = type,
    )

    /**
     * Keeps all the processed information for a collection type field. Supported collections are:
     * - Array
     * - List
     * - Set
     * - Map
     */
    data class CollectionParameter(
        override val name: String,
        override val type: TypeName,
    ) : ProcessedFixtureParameter(
        name = name,
        type = type,
    )

    /**
     * Keeps all the processed information for a sealed class type field.
     */
    data class SealedParameter(
        override val name: String,
        override val type: TypeName,
        val entries: List<SealedEntry>,
    ) : ProcessedFixtureParameter(
        name = name,
        type = type,
    ) {
        data class SealedEntry(
            val isObject: Boolean,
            val isFixture: Boolean,
            val name: String,
        )
    }

    data class FixtureAdapter(
        override val name: String,
        override val type: TypeName,
    ) : ProcessedFixtureParameter(
        name = name,
        type = type,
    )
}

internal val ProcessedFixtureParameter.packageName: String
    get() = when (val type = this.type) {
        is ClassName -> type.packageName
        is ParameterizedTypeName -> type.rawType.packageName
        Dynamic,
        is LambdaTypeName,
        is TypeVariableName,
        is WildcardTypeName,
        -> ""
    }

internal val ProcessedFixtureParameter.typeName: String
    get() = when (val type = this.type) {
        is ClassName -> type.canonicalName.removePrefix(type.packageName).replace(".", "")
        is ParameterizedTypeName -> type.rawType.simpleName
        Dynamic,
        is LambdaTypeName,
        is TypeVariableName,
        is WildcardTypeName,
        -> ""
    }
