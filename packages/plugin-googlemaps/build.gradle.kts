plugins {
    `android-base-lib`
}

android {
    namespace = "com.openmobilehub.android.maps.plugin.googlemaps"

    viewBinding {
        enable = true
    }
}

dependencies {
    api("com.openmobilehub.android.maps:core:2.0.0-beta")

    // KTX
    implementation(Libs.coreKtx)
    implementation(Libs.lifecycleKtx)

    // Android
    implementation(Libs.androidAppCompat)
    implementation(Libs.material)

    // Play services
    implementation(Libs.playServicesMaps)
    implementation(Libs.playServicesLocation)

    // Test dependencies
    testImplementation(Libs.junit)
    androidTestImplementation(Libs.androidJunit)
    testImplementation(Libs.mockk)
}