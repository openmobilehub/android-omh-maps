@file:Suppress("UnstableApiUsage")
import org.jetbrains.kotlin.konan.properties.hasProperty
import java.util.Properties

var properties = Properties()
var localPropertiesFile = project.file("local.properties")
if(localPropertiesFile.exists()) {
    properties.load(localPropertiesFile.inputStream())
}
var useLocalProjects = (rootProject.ext.has("useLocalProjects") && rootProject.ext.get("useLocalProjects") == "true") || (properties.hasProperty("useLocalProjects") && properties.getProperty("useLocalProjects") == "true")

plugins {
    `android-application`
    id("org.jetbrains.kotlin.android")
    id("androidx.navigation.safeargs.kotlin") version "2.5.3" apply true
    id("com.openmobilehub.android.omh-core")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

var googlemapsDependency = "com.openmobilehub.android.maps:plugin-googlemaps:2.0.0-beta"
var openstreetmapDependency = "com.openmobilehub.android.maps:plugin-openstreetmap:2.0.0-beta"

var googlemapsPath = "com.openmobilehub.android.maps.plugin.googlemaps.presentation.OmhMapFactoryImpl"
var openstreetmapPath = "com.openmobilehub.android.maps.plugin.openstreetmap.presentation.OmhMapFactoryImpl"

omhConfig {
    enableLocalProjects = useLocalProjects

    bundle("singleBuild") {
        maps {
            gmsService {
                dependency = googlemapsDependency
                path = googlemapsPath
            }
            nonGmsService {
                dependency = openstreetmapDependency
                path = openstreetmapPath
            }
        }
    }
    bundle("gms") {
        maps {
            gmsService {
                dependency = googlemapsDependency
                path = googlemapsPath
            }
        }
    }
    bundle("nongms") {
        maps {
            nonGmsService {
                dependency = openstreetmapDependency
                path = openstreetmapPath
            }
        }
    }
}

android {
    namespace = "com.openmobilehub.android.maps.sample"

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {
    implementation(Libs.coreKtx)
    implementation(Libs.lifecycleKtx)
    implementation(Libs.androidAppCompat)
    implementation(Libs.material)
    implementation(Libs.reflection)
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment-ktx:2.5.3")
    implementation("androidx.navigation:navigation-ui-ktx:2.5.3")

    // Test
    testImplementation(Libs.junit)
    androidTestImplementation(Libs.androidJunit)

    // Use local implementation instead of dependencies
    if(useLocalProjects) {
        implementation(project(":packages:core"))
        implementation(project(":packages:plugin-googlemaps"))
        implementation(project(":packages:plugin-openstreetmap"))
    }
}