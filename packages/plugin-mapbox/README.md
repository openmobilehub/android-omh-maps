# Module plugin-mapbox

## Overview

This plugin provides support for Mapbox by utilizing the [Mapbox Android SDK](https://docs.mapbox.com/android/maps/guides/).

## Getting Started

### Set up the development environment

1. Android Studio is required. If you haven't already done so, [download](https://developer.android.com/studio/index.html) and [install](https://developer.android.com/studio/install.html?pkg=studio) it.
2. Ensure that you are using the [Android Gradle plugin](https://developer.android.com/studio/releases/gradle-plugin) version 7.0 or later in Android Studio.

### Set up the project

1. Add the plugin to the project by following one of the guides:

- [Setup with omh-core plugin](/packages/core/docs/SETUP_WITH_OMH_CORE_PLUGIN.md)
- [Setup without omh-core plugin](/packages/core/docs/SETUP_WITHOUT_OMH_CORE_PLUGIN.md)

2. Configure credentials according the [Official Documentation](https://docs.mapbox.com/android/maps/guides/install#configure-credentials).

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

## Compatibility matrix

### Map

#### OmhMap

| Method                                  | Supported? |
| --------------------------------------- | :--------: |
| addMarker                               |     ?      |
| addPolyline                             |     ✅      |
| addPolygon                              |     ✅     |
| getCameraPositionCoordinate             |     ✅     |
| moveCamera                              |     ✅     |
| setZoomGesturesEnabled                  |     ✅     |
| setRotateGesturesEnabled                |     ✅     |
| setMyLocationEnabled                    |     ✅     |
| isMyLocationEnabled                     |     ✅     |
| setMyLocationButtonClickListener        |     ✅     |
| setOnCameraMoveStartedListener          |     🟨     |
| setOnCameraIdleListener                 |     ✅     |
| setOnMapLoadedCallback                  |     ✅     |
| setOnMarkerClickListener                |     ✅     |
| setOnMarkerDragListener                 |     ✅     |
| setOnInfoWindowOpenStatusChangeListener |     ✅     |
| setOnInfoWindowClickListener            |     ✅     |
| setOnInfoWindowLongClickListener        |     ✅     |
| setOnPolylineClickListener              |     ✅     |
| setOnPolygonClickListener               |     ✅      |
| snapshot                                |     ✅     |
| setMapStyle                             |     ✅     |

Comments for partially supported 🟨 properties:

| Property                       | Comments                                            |
| ------------------------------ | --------------------------------------------------- |
| setOnCameraMoveStartedListener | The reason of the camera changed started is unknown |

### Marker

#### OmhMarkerOptions

| Property         | Support level |
| ---------------- | :-----------: |
| position         |      ✅       |
| title            |      ✅       |
| draggable        |      ✅       |
| anchor           |      🟨       |
| infoWindowAnchor |      ✅       |
| alpha            |      ✅       |
| snippet          |      ✅       |
| isVisible        |      ✅       |
| isFlat           |      ✅       |
| rotation         |      ✅       |
| backgroundColor  |      ✅       |
| clickable        |      ✅       |

Comments for partially supported 🟨 properties:

| Property | Comments                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         |
| -------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| anchor   | Mapbox provider only supports [enumerated (discrete) values](https://docs.mapbox.com/android/maps/api/10.0.0/mapbox-maps-android/com.mapbox.maps.extension.style.layers.properties.generated/-icon-anchor/), opposed to continuous (`Float`) values in OMH; this property is mapped for each axis such that ranges: <br/>&bull; `<0; 0.25>` is mapped to left or top <br/>&bull; `<0.75; 0.1>` is mapped to right or bottom <br/>&bull; `(0.25; 0.75)` is mapped to center <br/> Also taking into account combinations, e.g. `Pair(0.1f, 0.9f)` would be mapped to `BOTTOM_LEFT` |

#### OmhMarker

| Method               | Support level |
| -------------------- | :-----------: |
| getPosition          |      ✅       |
| setPosition          |      ✅       |
| getTitle             |      ✅       |
| setTitle             |      ✅       |
| getClickable         |      ✅       |
| setClickable         |      ✅       |
| getDraggable         |      ✅       |
| setDraggable         |      ✅       |
| setAnchor            |      🟨       |
| setInfoWindowAnchor  |      ✅       |
| getAlpha             |      ✅       |
| setAlpha             |      ✅       |
| getSnippet           |      ✅       |
| setSnippet           |      ✅       |
| setIcon              |      ✅       |
| getIsVisible         |      ✅       |
| setIsVisible         |      ✅       |
| getIsFlat            |      ✅       |
| setIsFlat            |      ✅       |
| getRotation          |      ✅       |
| setRotation          |      ✅       |
| getBackgroundColor   |      ✅       |
| setBackgroundColor   |      ✅       |
| showInfoWindow       |      ✅       |
| hideInfoWindow       |      ✅       |
| getIsInfoWindowShown |      ✅       |

Comments for partially supported 🟨 properties:

| Property | Comments                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         |
| -------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| anchor   | Mapbox provider only supports [enumerated (discrete) values](https://docs.mapbox.com/android/maps/api/10.0.0/mapbox-maps-android/com.mapbox.maps.extension.style.layers.properties.generated/-icon-anchor/), opposed to continuous (`Float`) values in OMH; this property is mapped for each axis such that ranges: <br/>&bull; `<0; 0.25>` is mapped to left or top <br/>&bull; `<0.75; 0.1>` is mapped to right or bottom <br/>&bull; `(0.25; 0.75)` is mapped to center <br/> Also taking into account combinations, e.g. `Pair(0.1f, 0.9f)` would be mapped to `BOTTOM_LEFT` |

### Polyline

#### OmhPolylineOptions

| Property  | Supported? |
| --------- | :--------: |
| points    |     ✅     |
| clickable |     ✅     |
| color     |     ✅     |
| width     |     ✅     |
| isVisible |     ✅     |
| zIndex    |     ❌     |
| jointType |     ✅     |
| pattern   |     ❌     |
| cap       |     ✅     |
| startCap  |     ❌     |
| endCap    |     ❌     |
| spans     |     ❌     |

#### OmhPolyline

| Method       | Supported? |
| ------------ | :--------: |
| getCap       |     ✅     |
| setCap       |     ✅     |
| isClickable  |     ✅     |
| setClickable |     ✅     |
| getColor     |     ✅     |
| setColor     |     ✅     |
| getEndCap    |     ❌     |
| setEndCap    |     ❌     |
| getJoinType  |     ✅     |
| setJoinType  |     ✅     |
| getPattern   |     ❌     |
| setPattern   |     ❌     |
| getPoints    |     ❌     |
| setPoints    |     ✅     |
| getSpans     |     ❌     |
| setSpans     |     ❌     |
| getStartCap  |     ❌     |
| setStartCap  |     ❌     |
| getTag       |     ✅     |
| setTag       |     ✅     |
| getWidth     |     ✅     |
| setWidth     |     ✅     |
| getZIndex    |     ❌     |
| setZIndex    |     ❌     |
| isVisible    |     ✅     |
| setVisible   |     ✅     |

### Polygon

#### OmhPolygonOptions

| Property        | Supported? |
| --------------- | :--------: |
| outline         |     ✅     |
| clickable       |     ✅     |
| fillColor       |     ✅     |
| holes           |     ✅     |
| isVisible       |     ✅     |
| strokeColor     |     ✅     |
| strokeJointType |     ✅     |
| strokePattern   |     ❌     |
| strokeWidth     |     ✅     |
| zIndex          |     ❌     |

#### OmhPolygon

| Method             | Supported? |
| ------------------ | :--------: |
| getClickable       |     ✅     |
| setClickable       |     ✅     |
| getStrokeColor     |     ✅     |
| setStrokeColor     |     ✅     |
| getFillColor       |     ✅     |
| setFillColor       |     ✅     |
| getStrokeJointType |     ✅     |
| setStrokeJointType |     ✅     |
| getStrokePattern   |     ❌     |
| setStrokePattern   |     ❌     |
| getOutline         |     ✅     |
| setOutline         |     ✅     |
| getHoles           |     ✅     |
| setHoles           |     ✅     |
| getTag             |     ✅     |
| setTag             |     ✅     |
| getStrokeWidth     |     ✅     |
| setStrokeWidth     |     ✅     |
| getZIndex          |     ❌     |
| setZIndex          |     ❌     |
| isVisible          |     ✅     |
| setVisible         |     ✅     |

## Documentation

- [Advanced documentation](/packages//plugin-mapbox/docs/advanced/README.md)

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
