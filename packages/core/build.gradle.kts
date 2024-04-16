plugins {
    `android-base-lib`
    `kotlin-parcelize`
}

android {
    namespace = "com.openmobilehub.android.maps.core"

    viewBinding {
        enable = true
    }
}

tasks.named("jacocoCoverageVerification").configure { dependsOn("mergeDebugJniLibFolders") }
tasks.named("jacocoCoverageVerification").configure { dependsOn("copyDebugJniLibsProjectAndLocalJars") }
tasks.named("jacocoCoverageVerification").configure { dependsOn("copyDebugJniLibsProjectOnly") }
tasks.named("jacocoCoverageVerification").configure { dependsOn("syncDebugLibJars") }

dependencies {
    // KTX
    implementation(Libs.coreKtx)
    implementation(Libs.lifecycleKtx)

    // Android
    implementation(Libs.androidAppCompat)
    implementation(Libs.material)
    implementation(Libs.reflection)

    implementation(Libs.googlePlayBase)

    // Test dependencies
    testImplementation(Libs.junit)
    androidTestImplementation(Libs.androidJunit)
    testImplementation(Libs.mockk)
}