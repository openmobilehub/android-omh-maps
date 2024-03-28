@file:Suppress("UnstableApiUsage")

val useLocalProjects = project.rootProject.extra["useLocalProjects"] as Boolean

plugins {
    `android-application`
    id("org.jetbrains.kotlin.android")
    id("androidx.navigation.safeargs.kotlin") version "2.5.3" apply true
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
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
        implementation(project(":packages:plugin-azuremaps")) {
            exclude(group = "org.maplibre.gl", module = "android-sdk-geojson")
        }
    } else {
        implementation("com.openmobilehub.android.maps:plugin-googlemaps:2.0.0-beta")
        implementation("com.openmobilehub.android.maps:plugin-openstreetmap:2.0.0-beta")
        implementation("com.openmobilehub.android.maps:plugin-mapbox:1.0.0-beta")
        implementation("com.openmobilehub.android.maps:plugin-azuremaps:1.0.0-beta") {
            exclude(group = "org.maplibre.gl", module = "android-sdk-geojson")
        }
    }
}

tasks.dokkaHtmlPartial {
    enabled = false
}
