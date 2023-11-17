plugins {
    kotlin("jvm")
    id("com.google.devtools.ksp")
}

repositories {
    mavenCentral()
}

kotlin {
    sourceSets.configureEach {
        val buildDirectory = layout.buildDirectory.get().asFile
        kotlin.srcDir("$buildDirectory/generated/ksp/$name/kotlin/")
    }
}

dependencies {
    // Kotlin Dependencies
    implementation(Dependencies.Kotlin.KOTLIN)
    implementation(Dependencies.Kotlin.KSP)

    testImplementation(TestDependencies.JUnit.JUNIT)

    implementation(project(":fixtures-annotations"))
    ksp(project(":fixtures-processor"))
    implementation(project(":sample-module-1"))
}
