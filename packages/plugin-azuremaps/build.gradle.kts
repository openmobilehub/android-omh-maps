plugins {
    `android-base-lib`
}

android {
    namespace = "com.openmobilehub.android.maps.plugin.azuremaps"
}

val useLocalProjects = project.rootProject.extra["useLocalProjects"] as Boolean

dependencies {
    if (useLocalProjects) {
        api(project(":packages:core"))
    } else {
        api("com.openmobilehub.android.maps:core:2.0.0-beta")
    }

    // KTX
    implementation(Libs.coreKtx)
    implementation(Libs.lifecycleKtx)

    // Android
    implementation(Libs.androidAppCompat)
    implementation(Libs.material)

    // Azure Maps
    api(Libs.azureMaps)

    // Test dependencies
    testImplementation(Libs.junit)
    androidTestImplementation(Libs.androidJunit)
    testImplementation(Libs.mockk)
    testImplementation("com.squareup.okhttp3:okhttp:4.12.0")
}
