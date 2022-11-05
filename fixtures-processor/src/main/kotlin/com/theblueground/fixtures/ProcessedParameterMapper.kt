package com.theblueground.fixtures

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSValueParameter
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.ksp.KotlinPoetKspPreview
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName

/**
 * Maps KS nodes into a [ProcessedFixtureParameter]
 */
@KotlinPoetKspPreview
internal class ProcessedParameterMapper(
    private val processedFixtureAdapters: Map<TypeName, ProcessedFixtureAdapter>
) {

    fun mapParameter(parameterValue: KSValueParameter): ProcessedFixtureParameter {
        val resolvedType = parameterValue.type.resolve()
        return mapParameter(
            parameterValue = parameterValue,
            parameterType = resolvedType,
            parameterClassDeclaration = (resolvedType.declaration as KSClassDeclaration)
        )
    }

    private fun mapParameter(
        parameterValue: KSValueParameter,
        parameterType: KSType,
        parameterClassDeclaration: KSClassDeclaration
    ): ProcessedFixtureParameter {
        val name = parameterValue.name!!.asString()

        return when {
            parameterClassDeclaration.isPrimitive -> mapPrimitiveParameter(
                name = name,
                parameterClassDeclaration = parameterClassDeclaration
            )
            parameterClassDeclaration.isKnownType -> mapKnownTypeParameter(
                name = name,
                parameterClassDeclaration = parameterClassDeclaration
            )
            parameterClassDeclaration.isFixture -> mapFixtureParameter(
                name = name,
                parameterClassDeclaration = parameterClassDeclaration
            )
            parameterClassDeclaration.isEnum -> mapEnumParameter(
                name = name,
                parameterClassDeclaration = parameterClassDeclaration
            )
            parameterClassDeclaration.isSealed -> mapSealedParameter(
                name = name,
                parameterClassDeclaration = parameterClassDeclaration
            )
            parameterClassDeclaration.isCollection -> mapCollectionParameter(
                name = name,
                parameterType = parameterType,
                parameterClassDeclaration = parameterClassDeclaration
            )
            else -> mapFixtureAdapterParameter(
                name = name,
                parameterClassDeclaration = parameterClassDeclaration
            )
        }
    }

    private fun mapPrimitiveParameter(
        name: String,
        parameterClassDeclaration: KSClassDeclaration
    ): ProcessedFixtureParameter.PrimitiveParameter = ProcessedFixtureParameter.PrimitiveParameter(
        name = name,
        classType = parameterClassDeclaration.toClassName()
    )

    private fun mapKnownTypeParameter(
        name: String,
        parameterClassDeclaration: KSClassDeclaration
    ): ProcessedFixtureParameter.KnownTypeParameter = ProcessedFixtureParameter.KnownTypeParameter(
        name = name,
        classType = parameterClassDeclaration.toClassName()
    )

    private fun mapFixtureParameter(
        name: String,
        parameterClassDeclaration: KSClassDeclaration
    ): ProcessedFixtureParameter.FixtureParameter = ProcessedFixtureParameter.FixtureParameter(
        name = name,
        classType = parameterClassDeclaration.toClassName()
    )

    private fun mapEnumParameter(
        name: String,
        parameterClassDeclaration: KSClassDeclaration
    ): ProcessedFixtureParameter.EnumParameter = ProcessedFixtureParameter.EnumParameter(
        name = name,
        classType = parameterClassDeclaration.toClassName(),
        entries = parameterClassDeclaration.mapEnumEntries()
    )

    private fun KSDeclaration.mapEnumEntries(): List<String> = (this as KSClassDeclaration)
        .declarations
        .filterIsInstance<KSClassDeclaration>()
        .map { it.simpleName.asString() }
        .toList()

    private fun mapSealedParameter(
        name: String,
        parameterClassDeclaration: KSClassDeclaration
    ): ProcessedFixtureParameter.SealedParameter = ProcessedFixtureParameter.SealedParameter(
        name = name,
        classType = parameterClassDeclaration.toClassName(),
        entries = parameterClassDeclaration.mapSealedEntries()
    )

    private fun KSDeclaration.mapSealedEntries(): List<ProcessedFixtureParameter.SealedParameter.SealedEntry> =
        (this as KSClassDeclaration)
            .declarations
            .filterIsInstance<KSClassDeclaration>()
            .map {
                ProcessedFixtureParameter.SealedParameter.SealedEntry(
                    isObject = it.isObject,
                    isFixture = it.isFixture,
                    name = it.simpleName.asString()
                )
            }
            .toList()

    private fun mapCollectionParameter(
        name: String,
        parameterType: KSType,
        parameterClassDeclaration: KSClassDeclaration
    ): ProcessedFixtureParameter.CollectionParameter {
        val classType = parameterClassDeclaration.toClassName()

        val parameterizedType = classType.parameterizedBy(
            typeArguments = *parameterType.arguments.map { it.toTypeName() }.toTypedArray()
        )

        return ProcessedFixtureParameter.CollectionParameter(
            name = name,
            classType = classType,
            parameterizedType = parameterizedType
        )
    }

    private fun mapFixtureAdapterParameter(
        name: String,
        parameterClassDeclaration: KSClassDeclaration
    ): ProcessedFixtureParameter.FixtureAdapter {
        val className = parameterClassDeclaration.toClassName()

        processedFixtureAdapters[className] ?: throw IllegalArgumentException(
            "${parameterClassDeclaration.simpleName.asString()} is not a known type and no related @FixtureAdapter was found"
        )

        return ProcessedFixtureParameter.FixtureAdapter(
            name = name,
            classType = className
        )
    }
}
