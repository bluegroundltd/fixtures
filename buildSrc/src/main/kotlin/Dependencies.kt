object Dependencies {

    object Kotlin {

        const val KOTLIN = "org.jetbrains.kotlin:kotlin-stdlib"

        const val KSP = "com.google.devtools.ksp:symbol-processing-api:1.7.21-1.0.8"
    }

    object Square {

        object Poet {

            private const val VERSION = "1.12.0"

            const val KOTLIN = "com.squareup:kotlinpoet:$VERSION"

            const val KSP = "com.squareup:kotlinpoet-ksp:$VERSION"
        }
    }
}