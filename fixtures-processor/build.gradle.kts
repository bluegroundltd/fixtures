plugins {
    kotlin("jvm")
    id("com.google.devtools.ksp")
    id("org.jetbrains.dokka")
    id("com.vanniktech.maven.publish")
}

repositories {
    mavenCentral()
}

apply(plugin = "com.vanniktech.maven.publish")

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
    testImplementation(TestDependencies.Misc.KOTLIN_COMPILE_TESTING_KSP)
}

tasks.dokkaHtml.configure {
    outputDirectory.set(layout.buildDirectory.dir("dokka").get().asFile)
}
