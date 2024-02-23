# Gradle Setup with OMH Core Plugin

To incorporate OMH Maps into your project, you have two options: utilize the OMH Core Plugin or directly include the OMH Client libraries dependencies. The subsequent instructions will outline the necessary steps for including the OMH Core Plugin as a Gradle dependency.

1. In your app's module-level `build.gradle`under the `plugins` element add the plugin id.

   ```kotlin
   plugins {
      ...
      id("com.openmobilehub.android.omh-core")
   }
   ```

2. Save the file and [sync Project with Gradle Files](https://developer.android.com/studio/build#sync-files).

## Configure the OMH Core plugin

In this sample app, we utilize the `OmhConfig` definition to expand the capabilities of the existing Android Studio variants. For more details, refer to the [OMH Core Plugin Docs](https://github.com/openmobilehub/omh-core/tree/release/1.0).

### Basic configuration

1.  In your app's module-level `build.gradle` file add the following code at the end of the file.

```kotlin
...
dependencies {
   ...
}

OmhConfig {
   bundle("singleBuild") {
      maps {
         gmsService {
            dependency = "com.openmobilehub.android.maps:plugin-googlemaps:1.0"
         }
         nonGmsService {
            dependency = "com.openmobilehub.android.maps:plugin-openstreetmap:1.0"
         }
      }
   }
   bundle("gms") {
      maps {
         gmsService {
            dependency = "com.openmobilehub.android.maps:plugin-googlemaps:1.0"
         }
      }
   }
   bundle("nonGms") {
      maps {
         nonGmsService {
            dependency = "com.openmobilehub.android.maps:plugin-openstreetmap:1.0"
         }
      }
   }
}
```

In this step, you defined the OMH Core Plugin bundles to generate multiple build variants with specific suffixes as their names. For example, if your project has `release` and `debug` variants with `singleBuild`, `gms`, and `nonGms` OMH bundles, the following build variants will be generated:

- `releaseSingleBuild`, `releaseGms`, and `releaseNonGms`
- `debugSingleBuild`, `debugGms`, and `debugNonGms`

##### Variant singleBuild

- Define the `Service`. In this example is maps.
- Define the `ServiceDetails`. In this example are `gmsService` and `nonGmsService`.
- Define the dependency and the path. In this example are `com.openmobilehub.android.maps:plugin-googlemaps:1.0"` and `com.openmobilehub.android.maps:plugin-openstreetmap:1.0`.

  **Note: It's important to observe how a single build encompasses both GMS and Non-GMS configurations.**

##### Variant gms

- Define the `Service`. In this example is maps.
- Define the `ServiceDetails` . In this example is `gmsService`.
- Define the dependency and the path. In this example is `com.openmobilehub.android.maps:plugin-googlemaps:1.0"`.

  **Note:** gms build covers only GMS (Google Mobile Services).

##### Variant nonGms

- Define the `Service`. In this example is maps.
- Define the `ServiceDetails` . In this example is `nonGmsService`.
- Define the dependency and the path. In this example is `com.openmobilehub.android.maps:plugin-openstreetmap:1.0`.

  **Note:** nonGms build covers only Non-GMS configurations.

2. Save and [sync Project with Gradle Files](https://developer.android.com/studio/build#sync-files).
3. Now you can select a build variant. To change the build variant Android Studio uses, do one of the following:

   - Select `Build` > `Select Build Variant...` in the menu.
   - Select `View` > `Tool Windows` > `Build Variants` in the menu.
   - Click the `Build Variants` tab on the tool window bar.

4. You can select any of the 3 variants for the `:apps:maps-sample`:

   - `singleBuild` variant builds for GMS (Google Mobile Services) and Non-GMS devices without changes to the code.(Recommended)
   - `gms` variant builds for devices that has GMS (Google Mobile Services).
   - `nonGms` variant builds for devices that doesn't have GMS (Google Mobile Services).

   **Note:** In the rest of this guide, we will use the `debugSingleBuild` variant to demonstrate the same build running on GMS and Non-GMS devices.

5. Open the app's module-level `MainApplication` class and add the required imports below the package name. The file is in the same level as the `MainActivity`:

   ```kotlin
   import com.openmobilehub.android.maps.core.factories.OmhMapProvider
   ```

   Then initialize the `OmhMapProvider` as follows:

   ```kotlin
    class MainApplication : Application() {

        override fun onCreate() {
            super.onCreate()

        OmhMapProvider.Initiator()
            .addGmsPath(BuildConfig.MAPS_GMS_PATH)
            .addNonGmsPath(BuildConfig.MAPS_NON_GMS_PATH)
            .initialize()
        }
    }
   ```

   **Important:** If you encounter the error "Missing BuildConfig.MAPS_GMS_PATH and BuildConfig.MAPS_NON_GMS_PATH in BuildConfig class". Follow the next steps:

   - [Sync Project with Gradle Files](https://developer.android.com/studio/build#sync-files).
   - Select `Build` from the menu at the top in Android Studio.
   - Click on `Clean Project` and await.
   - Click on `Rebuild Project` and await.
