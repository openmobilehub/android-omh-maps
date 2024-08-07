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

rootProject.name = "OMH Maps"
include(":packages:core")
include(":apps:maps-sample")
include(":packages:plugin-googlemaps")
include(":packages:plugin-openstreetmap")
include(":packages:plugin-mapbox")
include(":packages:plugin-azuremaps")
