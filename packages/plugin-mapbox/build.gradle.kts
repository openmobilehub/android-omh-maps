plugins {
    `android-base-lib`
}

android {
    namespace = "com.openmobilehub.android.maps.plugin.mapbox"
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

    // Mapbox
    api(Libs.mapbox)

    // Test dependencies
    testImplementation(Libs.junit)
    androidTestImplementation(Libs.androidJunit)
    testImplementation(Libs.mockk)
}
