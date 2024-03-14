@file:Suppress("UnstableApiUsage")

import org.gradle.internal.Cast.uncheckedCast

val useLocalProjects = project.rootProject.extra["useLocalProjects"] as Boolean

plugins {
    `android-application`
    id("org.jetbrains.kotlin.android")
    id("androidx.navigation.safeargs.kotlin") version "2.5.3" apply true
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

apply("../../plugin/propertiesHelpers.gradle.kts")
val getValueFromEnvOrProperties =
    uncheckedCast<(name: String, propertiesFile: File?) -> String?>(extra["getValueFromEnvOrProperties"])!!
val getRequiredValueFromEnvOrProperties =
    uncheckedCast<(name: String, propertiesFile: File?) -> String>(extra["getRequiredValueFromEnvOrProperties"])!!

android {
    namespace = "com.openmobilehub.android.maps.sample"

    defaultConfig {
        val mapboxPublicToken = getRequiredValueFromEnvOrProperties(
            "MAPBOX_PUBLIC_TOKEN",
            rootProject.file("local.properties")
        )

        versionCode = 1
        versionName = "1.0"
        resValue(
            "string",
            "mapbox_access_token_value",
            mapboxPublicToken
        )
    }

    signingConfigs {
        // It creates a signing config for release builds if the required properties are set.
        // The if statement is necessary to avoid errors when the packages are built on CI.
        // The alternative would be to pass all the environment variables for signing apk to the packages workflows.
        create("release") {
            val storeFileName =
                getValueFromEnvOrProperties("SAMPLE_APP_KEYSTORE_FILE_NAME", null)
            val storePassword =
                getValueFromEnvOrProperties("SAMPLE_APP_KEYSTORE_STORE_PASSWORD", null)
            val keyAlias =
                getValueFromEnvOrProperties("SAMPLE_APP_KEYSTORE_KEY_ALIAS", null)
            val keyPassword =
                getValueFromEnvOrProperties("SAMPLE_APP_KEYSTORE_KEY_PASSWORD", null)

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
    implementation(Libs.googlePlayBase)
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment-ktx:2.5.3")
    implementation("androidx.navigation:navigation-ui-ktx:2.5.3")

    // Test
    testImplementation(Libs.junit)
    androidTestImplementation(Libs.androidJunit)

    // Use local implementation instead of dependencies
    if (useLocalProjects) {
        implementation(project(":packages:core"))
        implementation(project(":packages:plugin-googlemaps"))
        implementation(project(":packages:plugin-openstreetmap"))
        implementation(project(":packages:plugin-mapbox"))
    } else {
        implementation("com.openmobilehub.android.maps:plugin-googlemaps:2.0.0-beta")
        implementation("com.openmobilehub.android.maps:plugin-openstreetmap:2.0.0-beta")
        implementation("com.openmobilehub.android.maps:plugin-mapbox:1.0.0-beta")
    }
}

tasks.dokkaHtmlPartial {
    enabled = false
}
