# Module plugin-azuremaps

## Overview

This plugin provides support for Azure Maps by utilizing the [Azure Maps Android SDK](https://learn.microsoft.com/en-us/azure/azure-maps/).

## Getting Started

### Set up the development environment

1. Android Studio is required. If you haven't already done so, [download](https://developer.android.com/studio/index.html) and [install](https://developer.android.com/studio/install.html?pkg=studio) it.
2. Ensure that you are using the [Android Gradle plugin](https://developer.android.com/studio/releases/gradle-plugin) version 7.0 or later in Android Studio.

### Set up the project

1. Add the plugin to the project by following one of the guides:

- [Setup with omh-core plugin](https://www.openmobilehub.com/android-omh-maps/advanced-docs/core/SETUP_WITH_OMH_CORE_PLUGIN/)
- [Setup without omh-core plugin](https://www.openmobilehub.com/android-omh-maps/advanced-docs/core/SETUP_WITHOUT_OMH_CORE_PLUGIN/)

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
      implementation("com.openmobilehub.android.maps:plugin-azuremaps:2.0.0") {
         exclude(group = "org.maplibre.gl", module = "android-sdk-geojson")
      }
   }
   ```

## Compatibility matrix

### Map

#### OmhMap

| Method                                  | Supported? |
| --------------------------------------- | :--------: |
| addMarker                               |     ✅     |
| addPolyline                             |     ✅     |
| addPolygon                              |     ✅     |
| getCameraPositionCoordinate             |     ✅     |
| moveCamera                              |     ✅     |
| setZoomGesturesEnabled                  |     ✅     |
| setRotateGesturesEnabled                |     ❌     |
| setMyLocationEnabled                    |     ✅     |
| isMyLocationEnabled                     |     ✅     |
| setMyLocationButtonClickListener        |     ✅     |
| setOnCameraMoveStartedListener          |     ✅     |
| setOnCameraIdleListener                 |     ✅     |
| setOnMapLoadedCallback                  |     ✅     |
| setOnMarkerClickListener                |     ✅     |
| setOnMarkerDragListener                 |     ❌     |
| setOnInfoWindowOpenStatusChangeListener |     ✅     |
| setOnInfoWindowClickListener            |     ✅     |
| setOnInfoWindowLongClickListener        |     ✅     |
| setOnPolylineClickListener              |     ✅     |
| setOnPolygonClickListener               |     ✅     |
| snapshot                                |     ❌     |
| setMapStyle                             |     ❌     |
| setCustomInfoWindowContentsViewFactory  |     ✅     |
| setCustomInfoWindowViewFactory          |     🟨     |

Comments for partially supported 🟨 properties:

| Property                       | Comments                                                                                                                                                                                                                                                                                                                                                                                                                              |
| ------------------------------ | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| setCustomInfoWindowViewFactory | The provider supports this method in full, however since the view is rendered "live" (i.e., the view is mounted instead of being rendered to a bitmap), attaching interaction listeners to the root view returned by the factory is forbidden. Please consult the [advanced documentation](https://www.openmobilehub.com/android-omh-maps/advanced-docs/plugin-azuremaps/advanced/INFO_WINDOWS/) of this plugin for more information. |

### Marker

#### OmhMarkerOptions

| Property         | Support level |
| ---------------- | :-----------: |
| position         |      ✅       |
| title            |      ✅       |
| draggable        |      ❌       |
| anchor           |      🟨       |
| infoWindowAnchor |      ✅       |
| alpha            |      ✅       |
| snippet          |      ✅       |
| isVisible        |      ✅       |
| isFlat           |      ✅       |
| rotation         |      ✅       |
| backgroundColor  |      ✅       |
| clickable        |      ✅       |
| zIndex           |      ❌       |

Comments for partially supported 🟨 properties:

| Property | Comments                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          |
| -------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| anchor   | Azure Maps SDK utilizes Mapbox underneath, which only supports [enumerated (discrete) values](https://docs.mapbox.com/android/maps/api/10.0.0/mapbox-maps-android/com.mapbox.maps.extension.style.layers.properties.generated/-icon-anchor/), opposed to continuous (`Float`) values in OMH; this property is mapped for each axis such that ranges: <br/>&bull; `<0; 0.25>` is mapped to left or top <br/>&bull; `<0.75; 0.1>` is mapped to right or bottom <br/>&bull; `(0.25; 0.75)` is mapped to center <br/> Also taking into account combinations, e.g. `Pair(0.1f, 0.9f)` would be mapped to `BOTTOM_LEFT` |

#### OmhMarker

| Method               | Support level |
| -------------------- | :-----------: |
| getPosition          |      ✅       |
| setPosition          |      ✅       |
| getTitle             |      ✅       |
| setTitle             |      ✅       |
| getClickable         |      ✅       |
| setClickable         |      ✅       |
| getDraggable         |      ❌       |
| setDraggable         |      ❌       |
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
| remove               |      ✅       |
| getZIndex            |      ❌       |
| setZIndex            |      ❌       |

Comments for partially supported 🟨 properties:

| Property  | Comments                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          |
| --------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| setAnchor | Azure Maps SDK utilizes Mapbox underneath, which only supports [enumerated (discrete) values](https://docs.mapbox.com/android/maps/api/10.0.0/mapbox-maps-android/com.mapbox.maps.extension.style.layers.properties.generated/-icon-anchor/), opposed to continuous (`Float`) values in OMH; this property is mapped for each axis such that ranges: <br/>&bull; `<0; 0.25>` is mapped to left or top <br/>&bull; `<0.75; 0.1>` is mapped to right or bottom <br/>&bull; `(0.25; 0.75)` is mapped to center <br/> Also taking into account combinations, e.g. `Pair(0.1f, 0.9f)` would be mapped to `BOTTOM_LEFT` |

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
| pattern   |     🟨     |
| cap       |     🟨     |
| startCap  |     ❌     |
| endCap    |     ❌     |
| spans     |     ❌     |

Comments for partially supported 🟨 properties:

| Property | Comments                                                                                             |
| -------- | ---------------------------------------------------------------------------------------------------- |
| pattern  | Azure Maps SDK only supports alternating dashes and gaps that form the dash pattern.                 |
| cap      | Azure Maps SDK supports BUTT, SQUARE and ROUND caps. Defaults to ROUND when other value is provided. |

#### OmhPolyline

| Method       | Supported? |
| ------------ | :--------: |
| getCap       |     ✅     |
| setCap       |     🟨     |
| isClickable  |     ✅     |
| setClickable |     ✅     |
| getColor     |     ✅     |
| setColor     |     ✅     |
| getEndCap    |     ❌     |
| setEndCap    |     ❌     |
| getJoinType  |     ✅     |
| setJoinType  |     ✅     |
| getPattern   |     ✅     |
| setPattern   |     🟨     |
| getPoints    |     ✅     |
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
| remove       |     ✅     |

Comments for partially supported 🟨 properties:

| Property   | Comments                                                                                             |
| ---------- | ---------------------------------------------------------------------------------------------------- |
| setPattern | Azure Maps SDK only supports alternating dashes and gaps that form the dash pattern.                 |
| setCap     | Azure Maps SDK supports BUTT, SQUARE and ROUND caps. Defaults to ROUND when other value is provided. |

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
| strokePattern   |     🟨     |
| strokeWidth     |     ✅     |
| zIndex          |     ❌     |

Comments for partially supported 🟨 properties:

| Property      | Comments                                                                             |
| ------------- | ------------------------------------------------------------------------------------ |
| strokePattern | Azure Maps SDK only supports alternating dashes and gaps that form the dash pattern. |

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
| getStrokePattern   |     ✅     |
| setStrokePattern   |     🟨     |
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
| remove             |     ✅     |

Comments for partially supported 🟨 properties:

| Property         | Comments                                                                             |
| ---------------- | ------------------------------------------------------------------------------------ |
| setStrokePattern | Azure Maps SDK only supports alternating dashes and gaps that form the dash pattern. |

## Known issues

### Flat markers clicks are not detected correctly

In certain scenarios (e.g., when the map is rotated by more than 90 degrees), the Azure Maps SDK does not correctly handle click listeners when the flat marker is in use. The root cause of the issue is that the map processes clicks based on geographical coordinates, not the screen position of the markers. Therefore, if the marker's anchor is changed, its position on the screen changes, but its geographical position remains the same. This leads to situations where the marker is not clickable, even though it is visible on the screen. To mitigate this issue, the marker's anchor should be set to its default value, or the marker should be set to non-flat mode.

### Miter joint types are not rendered consistently

When Miter joint type is set, Azure Maps SDK can still render certain joints with acute angle as Bevel.

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
