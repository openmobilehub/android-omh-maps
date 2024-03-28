# Module plugin-azuremaps

## Overview

This plugin provides support for Azure Maps by utilizing the [Azure Maps Android SDK](https://learn.microsoft.com/en-us/azure/azure-maps/).

## Getting Started

### Set up the development environment

1. Android Studio is required. If you haven't already done so, [download](https://developer.android.com/studio/index.html) and [install](https://developer.android.com/studio/install.html?pkg=studio) it.
2. Ensure that you are using the [Android Gradle plugin](https://developer.android.com/studio/releases/gradle-plugin) version 7.0 or later in Android Studio.

### Set up the project

1. Add the plugin to the project by following one of the guides:

- [Setup with omh-core plugin](/packages/core/docs/SETUP_WITH_OMH_CORE_PLUGIN.md)
- [Setup without omh-core plugin](/packages/core/docs/SETUP_WITHOUT_OMH_CORE_PLUGIN.md)

2. Configure credentials according to the [Official Documentation](https://learn.microsoft.com/en-us/azure/azure-maps/quick-android-map?pivots=programming-language-kotlin#create-an-azure-maps-account)

3. In your app's module-level `AndroidManifest.xml` add the required permissions, for more information see [permissions](https://developer.android.com/training/permissions/declaring).

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
4. (Optional - only when using the `plugin-mapbox` in the single application). Exclude the `android-sdk-geojson` module from the `plugin-azuremaps` dependency to avoid conflicts with the `plugin-mapbox` dependency.

   ```kotlin
   dependencies {
   ...
      implementation("com.openmobilehub.android.maps:plugin-azuremaps:1.0.0-beta") {
         exclude(group = "org.maplibre.gl", module = "android-sdk-geojson")
      }
   }
   ```

## Compatibility matrix

### Map

#### OmhMap

| Method                                  | Supported? |
| --------------------------------------- | :--------: |
| addMarker                               |     ?      |
| addPolyline                             |     ?      |
| addPolygon                              |     ?      |
| getCameraPositionCoordinate             |     ✅     |
| moveCamera                              |     ✅     |
| setZoomGesturesEnabled                  |     ✅     |
| setRotateGesturesEnabled                |     ❌     |
| setMyLocationEnabled                    |     ?      |
| isMyLocationEnabled                     |     ?      |
| setMyLocationButtonClickListener        |     ?      |
| setOnCameraMoveStartedListener          |     ✅     |
| setOnCameraIdleListener                 |     ✅     |
| setOnMapLoadedCallback                  |     ✅     |
| setOnMarkerClickListener                |     ?      |
| setOnMarkerDragListener                 |     ?      |
| setOnInfoWindowOpenStatusChangeListener |     ?      |
| setOnInfoWindowClickListener            |     ?      |
| setOnInfoWindowLongClickListener        |     ?      |
| setOnPolylineClickListener              |     ?      |
| setOnPolygonClickListener               |     ?      |
| snapshot                                |     ❌     |
| setMapStyle                             |     ?      |

### Marker

#### OmhMarkerOptions

| Property         | Support level |
| ---------------- | :-----------: |
| position         |       ?       |
| title            |       ?       |
| draggable        |       ?       |
| anchor           |       ?       |
| infoWindowAnchor |       ?       |
| alpha            |       ?       |
| snippet          |       ?       |
| isVisible        |       ?       |
| isFlat           |       ?       |
| rotation         |       ?       |
| backgroundColor  |       ?       |
| clickable        |       ?       |

#### OmhMarker

| Method               | Support level |
| -------------------- | :-----------: |
| getPosition          |       ?       |
| setPosition          |       ?       |
| getTitle             |       ?       |
| setTitle             |       ?       |
| getClickable         |       ?       |
| setClickable         |       ?       |
| getDraggable         |       ?       |
| setDraggable         |       ?       |
| setAnchor            |       ?       |
| setInfoWindowAnchor  |       ?       |
| getAlpha             |       ?       |
| setAlpha             |       ?       |
| getSnippet           |       ?       |
| setSnippet           |       ?       |
| setIcon              |       ?       |
| getIsVisible         |       ?       |
| setIsVisible         |       ?       |
| getIsFlat            |       ?       |
| setIsFlat            |       ?       |
| getRotation          |       ?       |
| setRotation          |       ?       |
| getBackgroundColor   |       ?       |
| setBackgroundColor   |       ?       |
| showInfoWindow       |       ?       |
| hideInfoWindow       |       ?       |
| getIsInfoWindowShown |       ?       |

### Polyline

#### OmhPolylineOptions

| Property  | Supported? |
| --------- | :--------: |
| points    |     ?      |
| clickable |     ?      |
| color     |     ?      |
| width     |     ?      |
| isVisible |     ?      |
| zIndex    |     ?      |
| jointType |     ?      |
| pattern   |     ?      |
| cap       |     ?      |
| startCap  |     ?      |
| endCap    |     ?      |
| spans     |     ?      |

#### OmhPolyline

| Method       | Supported? |
| ------------ | :--------: |
| getCap       |     ?      |
| setCap       |     ?      |
| isClickable  |     ?      |
| setClickable |     ?      |
| getColor     |     ?      |
| setColor     |     ?      |
| getEndCap    |     ?      |
| setEndCap    |     ?      |
| getJoinType  |     ?      |
| setJoinType  |     ?      |
| getPattern   |     ?      |
| setPattern   |     ?      |
| getPoints    |     ?      |
| setPoints    |     ?      |
| getSpans     |     ?      |
| setSpans     |     ?      |
| getStartCap  |     ?      |
| setStartCap  |     ?      |
| getTag       |     ?      |
| setTag       |     ?      |
| getWidth     |     ?      |
| setWidth     |     ?      |
| getZIndex    |     ?      |
| setZIndex    |     ?      |
| isVisible    |     ?      |
| setVisible   |     ?      |

### Polygon

#### OmhPolygonOptions

| Property        | Supported? |
| --------------- | :--------: |
| outline         |     ?      |
| clickable       |     ?      |
| fillColor       |     ?      |
| holes           |     ?      |
| isVisible       |     ?      |
| strokeColor     |     ?      |
| strokeJointType |     ?      |
| strokePattern   |     ?      |
| strokeWidth     |     ?      |
| zIndex          |     ?      |

#### OmhPolygon

| Method             | Supported? |
| ------------------ | :--------: |
| getClickable       |     ?      |
| setClickable       |     ?      |
| getStrokeColor     |     ?      |
| setStrokeColor     |     ?      |
| getFillColor       |     ?      |
| setFillColor       |     ?      |
| getStrokeJointType |     ?      |
| setStrokeJointType |     ?      |
| getStrokePattern   |     ?      |
| setStrokePattern   |     ?      |
| getOutline         |     ?      |
| setOutline         |     ?      |
| getHoles           |     ?      |
| setHoles           |     ?      |
| getTag             |     ?      |
| setTag             |     ?      |
| getStrokeWidth     |     ?      |
| setStrokeWidth     |     ?      |
| getZIndex          |     ?      |
| setZIndex          |     ?      |
| isVisible          |     ?      |
| setVisible         |     ?      |

## Documentation

- [Advanced documentation](/packages//plugin-azuremaps/docs/advanced/README.md)

## Contributing

Please contribute! We will gladly review any pull requests. Make sure to read the [Contributing](https://github.com/openmobilehub/android-omh-maps/blob/main/CONTRIBUTING.md) page first though.

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
