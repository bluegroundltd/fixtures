plugins {
    kotlin("jvm")
}

repositories {
    mavenCentral()
}

dependencies {
    // Kotlin Dependencies
    implementation(Dependencies.Kotlin.KOTLIN)
    implementation(Dependencies.Kotlin.KSP)

    api(TestDependencies.JUnit.JUNIT)
    api(TestDependencies.Misc.KOTLIN_COMPILE_TESTING)
}
