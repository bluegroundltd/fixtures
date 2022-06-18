plugins {
    kotlin("jvm")
}

dependencies {
    // Kotlin Dependencies
    implementation(Dependencies.Kotlin.KOTLIN)
    implementation(Dependencies.Kotlin.KSP)
    implementation(Dependencies.Square.Poet.KOTLIN)
    implementation(Dependencies.Square.Poet.KSP)

    implementation(project(":fixtures-annotations"))

    testImplementation(TestDependencies.JUnit.JUNIT)
    testImplementation(TestDependencies.Google.TRUTH)
    testImplementation(TestDependencies.Misc.KOTLIN_COMPILE_TESTING)
}