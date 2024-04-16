plugins {
    `android-base-lib`
}

android {
    namespace = "com.openmobilehub.android.maps.plugin.openstreetmap"
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
    implementation(Libs.preference)

    // Open Street Map
    api(Libs.osmdroid)

    // Test dependencies
    testImplementation(Libs.junit)
    androidTestImplementation(Libs.androidJunit)
    testImplementation(Libs.mockk)
}