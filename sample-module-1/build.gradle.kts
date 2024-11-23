import com.google.devtools.ksp.gradle.KspTask

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
        kotlin.srcDir("$buildDirectory/generated/ksp/$name/groovy/")
    }
}

dependencies {
    // Kotlin Dependencies
    implementation(Dependencies.Kotlin.KOTLIN)
    implementation(Dependencies.Kotlin.KSP)

    implementation(project(":fixtures-annotations"))
    ksp(project(":fixtures-processor-kotlin"))
    ksp(project(":fixtures-processor-groovy"))
}

// Needed until this https://github.com/google/ksp/issues/1677 is resolved
val copyGeneratedGroovyFiles by tasks.registering(Copy::class) {
    // Set the source and destination directories
    val buildDirectory = layout.buildDirectory.get().asFile
    val generatedGroovyDir = "$buildDirectory/generated/ksp/main/groovy"
    val sourceDir = "$buildDirectory/generated/ksp/main/resources"

    // Specify the source and destination for copying
    from(sourceDir)
    into(generatedGroovyDir)
}

tasks.withType<KspTask> {
    finalizedBy(copyGeneratedGroovyFiles)
}
