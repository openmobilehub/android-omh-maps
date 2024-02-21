# Sample App

## Overview

The Sample App showcases the capabilities of the Omh Maps plugins, featuring all available providers for the specific operating system.

## Getting Started

### Set up the development environment

1. Android Studio is required. If you haven't already done so, [download](https://developer.android.com/studio/index.html) and [install](https://developer.android.com/studio/install.html?pkg=studio) it.
2. Ensure that you are using the [Android Gradle plugin](https://developer.android.com/studio/releases/gradle-plugin) version 7.0 or later in Android Studio.

### Credentials Setup

The app integrates both Google Maps and Mapbox, requiring setup for each.

#### Google Maps

1. **API Key**: In the project's `local.properties` file, add your Google Maps API key as follows:
   ```
   MAPS_API_KEY=YOUR_API_KEY
   ```
   Replace `YOUR_API_KEY` with your actual Google Maps API key.

   Further Reading:
   - [Google Cloud Console Setup Guide](/packages/plugin-googlemaps/docs/advanced/CLOUD_CONSOLE_SETUP.md)
   - [Google Maps Documentation](https://developers.google.com/maps/documentation/android-sdk/cloud-setup)

#### Mapbox

1. **Secret Token**: Configure your Mapbox secret token in `<USER_HOME>/.gradle/gradle.properties` (create the file if it doesn't exist):
   ```
   MAPBOX_DOWNLOADS_TOKEN=YOUR_SECRET_MAPBOX_ACCESS_TOKEN
   ```
   Replace `YOUR_SECRET_MAPBOX_ACCESS_TOKEN` with your Mapbox secret token.

2. **Public Token**: Add your Mapbox public API key to the project's `local.properties`:
   ```
   MAPBOX_PUBLIC_TOKEN=YOUR_PUBLIC_MAPBOX_ACCESS_TOKEN
   ```
   Replace `YOUR_PUBLIC_MAPBOX_ACCESS_TOKEN` with your Mapbox public token.

   Further Reading:
   - [Mapbox Installation Guide](https://docs.mapbox.com/android/maps/guides/install#configure-credentials)

### Dependencies Setup

To use the not-yet-published plugins, configure the dependencies in `local.properties` at the project level:

```
useLocalProjects=true
```

Alternatively, enable `useMavenLocal` and then publish plugins to your local Maven repository.

More information can be found on the [Contributing](/CONTRIBUTING.md) page.

### Running the App

After setting up the development environment, credentials, and dependencies, you can run the app on either a physical device or an emulator. Choose between `debug` or `release` build variants.

## Contributing

Please contribute! We will gladly review any pull requests. Make sure to read the [Contributing](/CONTRIBUTING.md) page first though.

## License

```
Copyright 2023 Open Mobile Hub

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
