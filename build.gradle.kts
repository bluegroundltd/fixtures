import com.diffplug.gradle.spotless.SpotlessExtension
import com.diffplug.gradle.spotless.SpotlessPlugin
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {

    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }

    dependencies {
        classpath(kotlin("gradle-plugin", version = "2.1.0"))
        classpath("com.google.devtools.ksp:com.google.devtools.ksp.gradle.plugin:2.1.0-1.0.29")
        classpath("org.jetbrains.dokka:dokka-gradle-plugin:1.5.31")
        classpath("com.vanniktech:gradle-maven-publish-plugin:0.18.0")
    }
}

plugins {
    kotlin("jvm") version "2.1.0" apply false
    id("com.diffplug.spotless")
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

val projectJvmTarget = JavaVersion.VERSION_17.toString()

subprojects {
    pluginManager.configureSpotlessIntegration(subProject = project)

    tasks.withType<KotlinCompile>().configureEach {
        dependsOn("spotlessKotlinApply")

        compilerOptions {
            jvmTarget.set(JvmTarget.fromTarget(projectJvmTarget))
        }
    }
}

fun PluginManager.configureSpotlessIntegration(subProject: Project) = apply {
    val spotlessConfiguration: (AppliedPlugin) -> Unit = {
        subProject.pluginManager.apply(SpotlessPlugin::class.java)
        subProject.configure<SpotlessExtension> {
            kotlin {
                target("src/**/*.kt")
                ktlint()
                trimTrailingWhitespace()
                endWithNewline()
            }

            kotlinGradle {
                ktlint()
                trimTrailingWhitespace()
                endWithNewline()
            }
        }
    }

    withPlugin("org.jetbrains.kotlin.jvm", spotlessConfiguration)
}
