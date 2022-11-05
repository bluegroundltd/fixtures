package com.theblueground.fixtures

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName

/**
 * Keeps all the needed information that is processed for a data class' parameter. The data class
 * should be annotated with the [Fixture] annotation. The information will be used in order to
 * generate a helper function which will create test data.
 */
internal sealed class ProcessedFixtureParameter(
    open val name: String,
    open val classType: ClassName
) {

    /**
     * Keeps all the processed information for a primitive type field.
     */
    data class PrimitiveParameter(
        override val name: String,
        override val classType: ClassName
    ) : ProcessedFixtureParameter(
        name = name,
        classType = classType
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
        override val classType: ClassName
    ) : ProcessedFixtureParameter(
        name = name,
        classType = classType
    )

    /**
     * Keeps all the processed information for a field which has a type that is also annotated with
     * the [Fixture] annotation.
     */
    data class FixtureParameter(
        override val name: String,
        override val classType: ClassName
    ) : ProcessedFixtureParameter(
        name = name,
        classType = classType
    )

    /**
     * Keeps all the processed information for an enum type field.
     */
    data class EnumParameter(
        override val name: String,
        override val classType: ClassName,
        val entries: List<String>
    ) : ProcessedFixtureParameter(
        name = name,
        classType = classType
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
        override val classType: ClassName,
        val parameterizedType: ParameterizedTypeName
    ) : ProcessedFixtureParameter(
        name = name,
        classType = classType
    )

    /**
     * Keeps all the processed information for a sealed class type field.
     */
    data class SealedParameter(
        override val name: String,
        override val classType: ClassName,
        val entries: List<SealedEntry>
    ) : ProcessedFixtureParameter(
        name = name,
        classType = classType
    ) {
        data class SealedEntry(
            val isObject: Boolean,
            val isFixture: Boolean,
            val name: String
        )
    }

    data class FixtureAdapter(
        override val name: String,
        override val classType: ClassName
    ) : ProcessedFixtureParameter(
        name = name,
        classType = classType
    )
}

internal val ProcessedFixtureParameter.typeName
    get() = this.classType.simpleName
