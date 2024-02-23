# Module plugin-googlemaps

## Overview

This plugin provides support for Google Maps by utilizing the [Google Maps Android SDK](https://developers.google.com/maps/documentation/android-sdk/overview).

## Getting Started

### Set up the development environment

1. Android Studio is required. If you haven't already done so, [download](https://developer.android.com/studio/index.html) and [install](https://developer.android.com/studio/install.html?pkg=studio) it.
2. Ensure that you are using the [Android Gradle plugin](https://developer.android.com/studio/releases/gradle-plugin) version 7.0 or later in Android Studio.

### Set up your Google Cloud project for applications with Google Services (Google Maps)

Complete the required Cloud Console setup following the next steps, for more information see:

- [Official Documentation](https://developers.google.com/maps/documentation/android-sdk/cloud-setup)
- [Google Cloud Console Guide](/packages//plugin-googlemaps/docs/advanced/CLOUD_CONSOLE_SETUP.md)

### Set up the project

1. Add the plugin to the project by following one of the guides:

- [Setup with omh-core plugin](/packages/core/docs/SETUP_WITH_OMH_CORE_PLUGIN.md)
- [Setup without omh-core plugin](/packages/core/docs/SETUP_WITHOUT_OMH_CORE_PLUGIN.md)

2. Open the `local.properties` in the project level directory, and then add the following code. Replace `YOUR_API_KEY` with your API key.

   ```properties
   MAPS_API_KEY=YOUR_API_KEY
   ```

   You should not check your API key into your version control system, so it is recommended
   storing it in the `local.properties` file, which is located in the root directory of your project.
   For more information about the `local.properties` file, see [Gradle properties files](https://developer.android.com/studio/build#properties-files).

3. In your app's module level `AndroidManifest.xml`file, under the `application` element add the `meta-data` element as follows:

   ```xml
   <manifest ...>
      <application ...>
         ...
         <meta-data
            android:name="com.google.android.geo.API_KEY"
            android:value="${MAPS_API_KEY}" />
      </application>
   </manifest>
   ```

4. In your app's module-level `AndroidManifest.xml` add the required permissions, for more information see [permissions](https://developer.android.com/training/permissions/declaring).

   ```xml
   <manifest ...>
      <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
      <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
      <uses-permission android:name="android.permission.INTERNET" />
      <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
      <application ...>
         ...
      </application>
   </manifest>
   ```

## Compatibility matrix

Legend of support levels:

| Support level       | Symbol |
| ------------------- | :----: |
| Fully supported     |   ✅   |
| Partially supported |   🟨   |
| Not supported       |   ❌   |

### Map

#### OmhMap

| Method                           | Support level |
| -------------------------------- | :-----------: |
| addMarker                        |      ✅       |
| addPolyline                      |      ✅       |
| addPolygon                       |      ✅       |
| getCameraPositionCoordinate      |      ✅       |
| moveCamera                       |      ✅       |
| setZoomGesturesEnabled           |      ✅       |
| setMyLocationEnabled             |      ✅       |
| isMyLocationEnabled              |      ✅       |
| setMyLocationButtonClickListener |      ✅       |
| setOnCameraMoveStartedListener   |      ✅       |
| setOnCameraIdleListener          |      ✅       |
| setOnMapLoadedCallback           |      ✅       |
| setOnPolylineClickListener       |      ✅       |
| setOnPolygonClickListener        |      ✅       |
| snapshot                         |      ✅       |
| setMapStyle                      |      ✅       |

### Marker

#### OmhMarkerOptions

| Property        | Support level |
| --------------- | :-----------: |
| position        |      ✅       |
| title           |      ✅       |
| draggable       |      ✅       |
| anchor          |      ✅       |
| alpha           |      ✅       |
| snippet         |      ✅       |
| isVisible       |      ✅       |
| isFlat          |      ✅       |
| rotation        |      ✅       |
| backgroundColor |      🟨       |
| clickable       |      ✅       |

#### OmhMarker

| Method             | Support level |
| ------------------ | :-----------: |
| getPosition        |      ✅       |
| setPosition        |      ✅       |
| getTitle           |      ✅       |
| setTitle           |      ✅       |
| getClickable       |      ✅       |
| setClickable       |      ✅       |
| getDraggable       |      ✅       |
| setDraggable       |      ✅       |
| setAnchor          |      ✅       |
| getAlpha           |      ✅       |
| setAlpha           |      ✅       |
| getSnippet         |      ✅       |
| setSnippet         |      ✅       |
| setIcon            |      ✅       |
| getIsVisible       |      ✅       |
| setIsVisible       |      ✅       |
| getIsFlat          |      ✅       |
| setIsFlat          |      ✅       |
| getRotation        |      ✅       |
| setRotation        |      ✅       |
| getBackgroundColor |      ❌       |
| setBackgroundColor |      🟨       |

### Polyline

#### OmhPolylineOptions

| Property  | Support level |
| --------- | :-----------: |
| points    |      ✅       |
| clickable |      ✅       |
| color     |      ✅       |
| width     |      ✅       |
| isVisible |      ✅       |
| zIndex    |      ✅       |
| jointType |      ✅       |
| pattern   |      ✅       |
| startCap  |      ✅       |
| endCap    |      ✅       |
| spans     |      ✅       |

#### OmhPolyline

| Method       | Support level |
| ------------ | :-----------: |
| isClickable  |      ✅       |
| setClickable |      ✅       |
| getColor     |      ✅       |
| setColor     |      ✅       |
| getEndCap    |      ✅       |
| setEndCap    |      ✅       |
| getJoinType  |      ✅       |
| setJoinType  |      ✅       |
| getPattern   |      ✅       |
| setPattern   |      ✅       |
| getPoints    |      ✅       |
| setPoints    |      ✅       |
| getSpans     |      ✅       |
| setSpans     |      ✅       |
| getStartCap  |      ✅       |
| setStartCap  |      ✅       |
| getTag       |      ✅       |
| setTag       |      ✅       |
| getWidth     |      ✅       |
| setWidth     |      ✅       |
| getZIndex    |      ✅       |
| setZIndex    |      ✅       |
| isVisible    |      ✅       |
| setVisible   |      ✅       |

### Polygon

#### OmhPolygonOptions

| Property        | Support level |
| --------------- | :-----------: |
| outline         |      ✅       |
| clickable       |      ✅       |
| fillColor       |      ✅       |
| holes           |      ✅       |
| isVisible       |      ✅       |
| strokeColor     |      ✅       |
| strokeJointType |      ✅       |
| strokePattern   |      ✅       |
| strokeWidth     |      ✅       |
| zIndex          |      ✅       |

#### OmhPolygon

| Method             | Support level |
| ------------------ | :-----------: |
| getClickable       |      ✅       |
| setClickable       |      ✅       |
| getStrokeColor     |      ✅       |
| setStrokeColor     |      ✅       |
| getFillColor       |      ✅       |
| setFillColor       |      ✅       |
| getStrokeJointType |      ✅       |
| setStrokeJointType |      ✅       |
| getStrokePattern   |      ✅       |
| setStrokePattern   |      ✅       |
| getOutline         |      ✅       |
| setOutline         |      ✅       |
| getHoles           |      ✅       |
| setHoles           |      ✅       |
| getTag             |      ✅       |
| setTag             |      ✅       |
| getStrokeWidth     |      ✅       |
| setStrokeWidth     |      ✅       |
| getZIndex          |      ✅       |
| setZIndex          |      ✅       |
| isVisible          |      ✅       |
| setVisible         |      ✅       |

## Documentation

- [Advanced documentation](/packages//plugin-googlemaps/docs/advanced/README.md)

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
