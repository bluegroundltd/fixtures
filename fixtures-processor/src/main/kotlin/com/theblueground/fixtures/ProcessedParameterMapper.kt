package com.theblueground.fixtures

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeAlias
import com.google.devtools.ksp.symbol.KSValueParameter
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName

/**
 * Maps KS nodes into a [ProcessedFixtureParameter]
 */
internal class ProcessedParameterMapper(
    private val processedFixtureAdapters: Map<TypeName, ProcessedFixtureAdapter>,
) {

    fun mapParameter(parameterValue: KSValueParameter): ProcessedFixtureParameter {
        val resolvedType = parameterValue.type.resolve()
        return when (val declaration = resolvedType.declaration) {
            is KSTypeAlias -> mapParameter(parameterValue, declaration.type.resolve())
            else -> mapParameter(parameterValue, resolvedType)
        }
    }

    private fun mapParameter(
        parameterValue: KSValueParameter,
        parameterType: KSType,
    ): ProcessedFixtureParameter {
        val name = parameterValue.name!!.asString()
        val parameterClassDeclaration = parameterType.declaration as KSClassDeclaration

        return when {
            parameterType.hasFixtureAdapter(processedFixtureAdapters) -> mapFixtureAdapterParameter(
                name = name,
                parameterType = parameterType,
            )
            parameterClassDeclaration.isPrimitive -> mapPrimitiveParameter(
                name = name,
                parameterType = parameterType,
            )
            parameterClassDeclaration.isKnownType -> mapKnownTypeParameter(
                name = name,
                parameterType = parameterType,
            )
            parameterClassDeclaration.isFixture -> mapFixtureParameter(
                name = name,
                parameterType = parameterType,
            )
            parameterValue.isFixtureInOtherModule -> mapFixtureParameter(
                name = name,
                parameterType = parameterType,
            )
            parameterClassDeclaration.isEnum -> mapEnumParameter(
                name = name,
                parameterType = parameterType,
                parameterClassDeclaration = parameterClassDeclaration,
            )
            parameterClassDeclaration.isSealed -> mapSealedParameter(
                name = name,
                parameterType = parameterType,
                parameterClassDeclaration = parameterClassDeclaration,
            )
            parameterClassDeclaration.isCollection -> mapCollectionParameter(
                name = name,
                parameterType = parameterType,
            )
            else -> throw IllegalArgumentException(
                "${parameterType.toClassName().simpleName} is not a known type and no related @FixtureAdapter was found",
            )
        }
    }

    private fun mapPrimitiveParameter(
        name: String,
        parameterType: KSType,
    ): ProcessedFixtureParameter.PrimitiveParameter = ProcessedFixtureParameter.PrimitiveParameter(
        name = name,
        type = parameterType.toTypeName(),
    )

    private fun mapKnownTypeParameter(
        name: String,
        parameterType: KSType,
    ): ProcessedFixtureParameter.KnownTypeParameter = ProcessedFixtureParameter.KnownTypeParameter(
        name = name,
        type = parameterType.toTypeName(),
    )

    private fun mapFixtureParameter(
        name: String,
        parameterType: KSType,
    ): ProcessedFixtureParameter.FixtureParameter = ProcessedFixtureParameter.FixtureParameter(
        name = name,
        type = parameterType.toTypeName(),
    )

    private fun mapEnumParameter(
        name: String,
        parameterType: KSType,
        parameterClassDeclaration: KSClassDeclaration,
    ): ProcessedFixtureParameter.EnumParameter = ProcessedFixtureParameter.EnumParameter(
        name = name,
        type = parameterType.toTypeName(),
        entries = parameterClassDeclaration.mapEnumEntries(),
    )

    private fun KSDeclaration.mapEnumEntries(): List<String> = (this as KSClassDeclaration)
        .declarations
        .filterIsInstance<KSClassDeclaration>()
        .map { it.simpleName.asString() }
        .toList()

    private fun mapSealedParameter(
        name: String,
        parameterType: KSType,
        parameterClassDeclaration: KSClassDeclaration,
    ): ProcessedFixtureParameter.SealedParameter = ProcessedFixtureParameter.SealedParameter(
        name = name,
        type = parameterType.toTypeName(),
        entries = parameterClassDeclaration.mapSealedEntries(),
    )

    private fun KSDeclaration.mapSealedEntries(): List<ProcessedFixtureParameter.SealedParameter.SealedEntry> =
        (this as KSClassDeclaration)
            .declarations
            .filterIsInstance<KSClassDeclaration>()
            .map {
                ProcessedFixtureParameter.SealedParameter.SealedEntry(
                    isObject = it.isObject,
                    isFixture = it.isFixture,
                    name = it.simpleName.asString(),
                )
            }
            .toList()

    private fun mapCollectionParameter(
        name: String,
        parameterType: KSType,
    ): ProcessedFixtureParameter.CollectionParameter {
        val parameterizedType = parameterType.toClassName().parameterizedBy(
            typeArguments = parameterType.arguments.map { it.toTypeName() }.toTypedArray(),
        ).copy(nullable = parameterType.isMarkedNullable)

        return ProcessedFixtureParameter.CollectionParameter(
            name = name,
            type = parameterizedType,
        )
    }

    private fun mapFixtureAdapterParameter(
        name: String,
        parameterType: KSType,
    ): ProcessedFixtureParameter.FixtureAdapter {
        val type = parameterType.toTypeName()

        return ProcessedFixtureParameter.FixtureAdapter(
            name = name,
            type = type,
        )
    }
}
