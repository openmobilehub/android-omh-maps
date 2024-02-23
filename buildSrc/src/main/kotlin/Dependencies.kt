object BuildPlugins {
    val android by lazy { "com.android.tools.build:gradle:${Versions.androidGradlePlugin}" }
    val kotlin by lazy { "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}" }
    val detekt by lazy { "io.gitlab.arturbosch.detekt:detekt-gradle-plugin:${Versions.detekt}" }
    val jacoco by lazy { "org.jacoco:org.jacoco.core:${Versions.jacoco}" }
    val dokka by lazy { "org.jetbrains.dokka:org.jetbrains.dokka.gradle.plugin:${Versions.dokka}" }
    val omhCore by lazy { "com.openmobilehub.android:omh-core:${Versions.omhCore}" }
}

object Libs {
    // Kotlin
    val reflection by lazy { "org.jetbrains.kotlin:kotlin-reflect:${Versions.kotlin}" }

    // Dokka
    val dokka by lazy { "org.jetbrains.dokka:org.jetbrains.dokka.gradle.plugin:${Versions.dokka}" }

    // KTX
    val coreKtx by lazy { "androidx.core:core-ktx:${Versions.coreKtx}" }
    val lifecycleKtx by lazy { "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.lifecycleKtx}" }

    // Android
    val androidAppCompat by lazy { "androidx.appcompat:appcompat:${Versions.androidAppCompat}" }
    val material by lazy { "com.google.android.material:material:${Versions.material}" }
    val preference by lazy { "androidx.preference:preference:${Versions.preference}" }

    // Play services
    val playServicesMaps by lazy { "com.google.android.gms:play-services-maps:${Versions.playServicesMaps}" }
    val playServicesLocation by lazy {
        "com.google.android.gms:play-services-location:".plus(
            Versions.playServicesLocation
        )
    }
    val googlePlayBase by lazy { "com.google.android.gms:play-services-base:${Versions.googlePlayBase}" }

    // Open Street Map
    val osmdroid by lazy { "org.osmdroid:osmdroid-android:${Versions.osmdroid}" }

    // Mapbox
    val mapbox by lazy { "com.mapbox.maps:android:${Versions.mapbox}" }

    // Testing
    val junit by lazy { "junit:junit:${Versions.junit}" }
    val androidJunit by lazy { "androidx.test.ext:junit:${Versions.androidJunit}" }
    val mockk by lazy { "io.mockk:mockk:${Versions.mockk}" }
}