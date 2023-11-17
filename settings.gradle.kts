pluginManagement {
    repositories {
        gradlePluginPortal()
    }

    resolutionStrategy {
        eachPlugin {
            val kotlinVersion: String by settings
            val spotlessVersion: String by settings

            when {
                requested.id.namespace?.startsWith("org.jetbrains.kotlin") == true -> useVersion(kotlinVersion)
                requested.id.id == "com.diffplug.spotless" -> useVersion(spotlessVersion)
            }
        }
    }
}

rootProject.name = "fixtures"
include(":sample-module-1", ":sample-module-2",":fixtures-processor",":fixtures-annotations")
