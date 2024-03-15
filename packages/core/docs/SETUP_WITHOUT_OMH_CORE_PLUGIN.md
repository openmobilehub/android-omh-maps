---
title: Setup without omh-core plugin
layout: default
has_children: false
---

# Gradle Setup without OMH Core Gradle Plugin

Without the OMH Core Gradle Plugin, the project shall be set up as a standard Android project. Build variants will not be created automatically, dependencies for providers will not be added and reflection paths for providers will not be injected into `BuildConfig`.

This approach is primarily designed for projects which want to specifically choose the maps provider at runtime using a more complex, custom logic.

The subsequent instructions will outline the necessary steps for including and configuring OMH Map provider plugins in an application without the use of the OMH Core Gradle Plugin.

1. Add necessary dependencies to your application's module-level `build.gradle.kts`:

   ```kotlin
       dependencies {
           // ...
           implementation("com.openmobilehub.android.maps:plugin-googlemaps:...")
           implementation("com.openmobilehub.android.maps:plugin-mapbox:...")
       }
   ```

2. Open the app's module-level `MainApplication` class and add the required imports below the package name. The file is in the same level as the `MainActivity`:

   ```kotlin
   import com.openmobilehub.android.maps.core.factories.OmhMapProvider
   ```

   Then initialize the `OmhMapProvider`, for instance in one of these ways, depending on your use case:

   - To just switch between providers depending on GMS services availability:

     ```kotlin
         class MainApplication : Application() {

             override fun onCreate() {
                 super.onCreate()

             OmhMapProvider.Initiator()
                 .addGmsPath("com.openmobilehub.android.maps.plugin.googlemaps.presentation.OmhMapFactoryImpl")
                 .addNonGmsPath("com.openmobilehub.android.maps.plugin.mapbox.presentation.OmhMapFactoryImpl")
                 .initialize()
             }
         }
     ```

   - To leverage some more complex logic to choose the provider, for instance depending on user preference in your application, you can configure just `addNonGmsPath` with the provider of choice that will always be used:

   ```kotlin
   OmhMapProvider.Initiator()
                       .addNonGmsPath(MapProvidersUtils.getDefaultMapProvider(this).path)
                       .initialize()
   ```

   Please note that in such a case, handling of GMS availability and choice of a provider that will be compatible with this state would be your responsibility. For a working example, please consult the sample application's `MainActivity` and module-level `build.gradle.kts` files.
