object Dependencies {

    object Kotlin {

        const val KOTLIN = "org.jetbrains.kotlin:kotlin-stdlib"

        const val KSP = "com.google.devtools.ksp:symbol-processing-api:2.3.0"
    }

    object Square {

        object Poet {

            private const val VERSION = "1.18.1"

            const val KOTLIN = "com.squareup:kotlinpoet:$VERSION"

            const val KSP = "com.squareup:kotlinpoet-ksp:$VERSION"
        }
    }
}