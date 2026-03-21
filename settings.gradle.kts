pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "avito test chat"
include(":app")
include(":core")
include(":chat")
include(":auth")
include(":chatlist")
include(":chatlist:api")
include(":chatlist:impl")
include(":chat:api")
include(":chat:impl")
include(":auth:api")
include(":auth:impl")
include(":navigation")
include(":navigation:api")
include(":navigation:impl")
include(":core:database")
include(":core:common")
include(":core:ui")
include(":profile")
include(":profile:api")
include(":profile:impl")
