plugins {
    `android-base-lib`
}

android {
    namespace = "com.openmobilehub.android.maps.plugin.openstreetmap"
}

dependencies {
    api("com.openmobilehub.android.maps:core:2.0.0-beta")

    // KTX
    implementation(Libs.coreKtx)
    implementation(Libs.lifecycleKtx)

    // Android
    implementation(Libs.androidAppCompat)
    implementation(Libs.material)
    implementation(Libs.preference)

    // Open Street Map
    implementation(Libs.osmdroid)
    implementation(project(":packages:core"))
    implementation(project(":packages:core"))

    // Test dependencies
    testImplementation(Libs.junit)
    androidTestImplementation(Libs.androidJunit)
    testImplementation(Libs.mockk)
}