@file:Suppress("UnstableApiUsage")

import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

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
    
    defaultConfig {
        versionCode = 1
        versionName = "1.0"
    }

    signingConfigs {
        // It creates a signing config for release builds if the required properties are set.
        // The if statement is necessary to avoid errors when the packages are built on CI.
        // The alternative would be to pass all the environment variables for signing apk to the packages workflows.
        create("release") {
            val storeFileName = getValueFromEnvOrProperties("SAMPLE_APP_KEYSTORE_FILE_NAME") as? String
            val storePassword = getValueFromEnvOrProperties("SAMPLE_APP_KEYSTORE_STORE_PASSWORD") as? String
            val keyAlias = getValueFromEnvOrProperties("SAMPLE_APP_KEYSTORE_KEY_ALIAS") as? String
            val keyPassword = getValueFromEnvOrProperties("SAMPLE_APP_KEYSTORE_KEY_PASSWORD") as? String

            if (storeFileName != null && storePassword != null && keyAlias != null && keyPassword != null) {
                this.storeFile = file(storeFileName)
                this.storePassword = storePassword
                this.keyAlias = keyAlias
                this.keyPassword = keyPassword
            }
        }
    }
    
    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // If the signing config is set, it will be used for release builds.
            if (signingConfigs["release"].storeFile != null) {
                signingConfig = signingConfigs.getByName("release")
            }
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
}

fun getValueFromEnvOrProperties(name: String): Any? {
    val localProperties = gradleLocalProperties(file("."))
    return System.getenv(name) ?: localProperties[name]
}
