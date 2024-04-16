plugins {
    `android-base-lib`
}

android {
    namespace = "com.openmobilehub.android.maps.plugin.azuremaps"
}

val useLocalProjects = project.rootProject.extra["useLocalProjects"] as Boolean

tasks.named("jacocoCoverageVerification").configure { dependsOn("mergeDebugJniLibFolders") }
tasks.named("jacocoCoverageVerification").configure { dependsOn("copyDebugJniLibsProjectAndLocalJars") }
tasks.named("jacocoCoverageVerification").configure { dependsOn("copyDebugJniLibsProjectOnly") }
tasks.named("jacocoCoverageVerification").configure { dependsOn("syncDebugLibJars") }

dependencies {
    if (useLocalProjects) {
        api(project(":packages:core"))
    } else {
        api("com.openmobilehub.android.maps:core:${Versions.omhMapsCore}")
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
}
