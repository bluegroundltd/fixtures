import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {

    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }

    dependencies {
        classpath(kotlin("gradle-plugin", version = "1.6.10"))
    }
}

plugins {
    kotlin("jvm") version "1.6.10" apply false
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}