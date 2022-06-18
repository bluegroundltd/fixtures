repositories {
    mavenCentral()
}

plugins {
    `kotlin-dsl`
}

tasks.withType<Test> { enabled = false }

tasks.withType<JavaCompile> { enabled = false }

tasks.withType<GroovyCompile> { enabled = false }