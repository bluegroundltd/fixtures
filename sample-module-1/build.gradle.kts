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

    implementation(project(":fixtures-annotations"))
    ksp(project(":fixtures-processor"))
}
