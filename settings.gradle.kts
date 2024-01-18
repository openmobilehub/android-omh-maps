pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_PROJECT)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "omh-maps"
include(":packages:maps-api")
include(":apps:maps-sample")
include(":packages:maps-api-googlemaps")
include(":packages:maps-api-openstreetmap")
