import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    `android-application`
    id("org.jetbrains.kotlin.android")
    id("androidx.navigation.safeargs.kotlin") version "2.5.3" apply true
    id("com.openmobilehub.android.omh-core")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

omhConfig {
    bundle("singleBuild") {
        maps {
            gmsService {
                dependency = "com.openmobilehub.android:maps-api-googlemaps:1.0.2-beta"
            }
            nonGmsService {
                dependency = "com.openmobilehub.android:maps-api-openstreetmap:1.0.2-beta"
            }
        }
    }
    bundle("gms") {
        maps {
            gmsService {
                dependency = "com.openmobilehub.android:maps-api-googlemaps:1.0.2-beta"
            }
        }
    }
    bundle("nongms") {
        maps {
            nonGmsService {
                dependency = "com.openmobilehub.android:maps-api-openstreetmap:1.0.2-beta"
            }
        }
    }
}

android {
    namespace = "com.omh.android.maps.sample"
    
    defaultConfig {
        versionCode = 1
        versionName = "1.0"
    }

    signingConfigs {
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
