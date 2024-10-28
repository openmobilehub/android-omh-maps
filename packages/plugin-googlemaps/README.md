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
- [Google Cloud Console Guide](/packages/plugin-googlemaps/docs/advanced/CLOUD_CONSOLE_SETUP.md)

### Set up the project

1. Add the plugin to the project by following one of the guides:

- [Setup with omh-core plugin](https://openmobilehub.github.io/android-omh-maps/advanced-docs/core/SETUP_WITH_OMH_CORE_PLUGIN/)
- [Setup without omh-core plugin](https://openmobilehub.github.io/android-omh-maps/advanced-docs/core/SETUP_WITHOUT_OMH_CORE_PLUGIN/)

2. Configure API key according to the [Official Documentation](https://developers.google.com/maps/documentation/android-sdk/start#add-key).

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

Legend of support levels:

| Support level       | Symbol |
| ------------------- | :----: |
| Fully supported     |   ✅   |
| Partially supported |   🟨   |
| Not supported       |   ❌   |

### Map

#### OmhMap

| Method                                  | Support level |
| --------------------------------------- | :-----------: |
| addMarker                               |      ✅       |
| addPolyline                             |      ✅       |
| addPolygon                              |      ✅       |
| getCameraPositionCoordinate             |      ✅       |
| moveCamera                              |      ✅       |
| setZoomGesturesEnabled                  |      ✅       |
| setMyLocationEnabled                    |      ✅       |
| isMyLocationEnabled                     |      ✅       |
| setMyLocationButtonClickListener        |      ✅       |
| setOnCameraMoveStartedListener          |      ✅       |
| setOnCameraIdleListener                 |      ✅       |
| setOnMapLoadedCallback                  |      ✅       |
| setOnMarkerClickListener                |      🟨       |
| setOnMarkerDragListener                 |      ✅       |
| setOnInfoWindowOpenStatusChangeListener |      🟨       |
| setOnInfoWindowClickListener            |      ✅       |
| setOnInfoWindowLongClickListener        |      ✅       |
| setOnPolylineClickListener              |      ✅       |
| setOnPolygonClickListener               |      ✅       |
| snapshot                                |      ✅       |
| setMapStyle                             |      ✅       |
| setCustomInfoWindowContentsViewFactory  |      ✅       |
| setCustomInfoWindowViewFactory          |      ✅       |

Comments for partially supported 🟨 properties:

| Property                                | Comments                                                                                                                                                                                                                                                                                                                  |
| --------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| setOnInfoWindowOpenStatusChangeListener | (1) only the `onInfoWindowClose` event is supported <br/> (2) the listener is invoked **before** `OmhOnMarkerClickListener`, in contrast to other providers, which implies that if the window was already open before a click on the marker, the marker click listener would always be invoked after the window is closed |
| setOnMarkerClickListener                | (1) the default behaviour of the on click listener (or when a custom handler returns `false`) is to center the map on the clicked marker <br/> (2) there is a non-overrideable behaviour of the Google Maps plugin, which hides (closes) an open info window when the map is tapped elsewhere                             |

### Marker

#### OmhMarkerOptions

| Property         | Support level |
| ---------------- | :-----------: |
| position         |      ✅       |
| title            |      ✅       |
| draggable        |      ✅       |
| anchor           |      ✅       |
| infoWindowAnchor |      ✅       |
| alpha            |      ✅       |
| snippet          |      ✅       |
| isVisible        |      ✅       |
| isFlat           |      ✅       |
| rotation         |      ✅       |
| backgroundColor  |      🟨       |
| clickable        |      ✅       |
| zIndex           |      ✅       |

Comments for partially supported 🟨 properties:

| Property        | Comments                                                                                         |
| --------------- | ------------------------------------------------------------------------------------------------ |
| backgroundColor | hue (H) component of HSV color representation is controllable <br/> alpha channel is unsupported |

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
| setAnchor            |      ✅       |
| setInfoWindowAnchor  |      🟨       |
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
| getBackgroundColor   |      ❌       |
| setBackgroundColor   |      🟨       |
| showInfoWindow       |      ✅       |
| hideInfoWindow       |      ✅       |
| getIsInfoWindowShown |      🟨       |
| remove               |      ✅       |
| getZIndex            |      ✅       |
| setZIndex            |      ✅       |

Comments for partially supported 🟨 properties:

| Property             | Comments                                                                                                                                                                                 |
| -------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| setInfoWindowAnchor  | as per [this issue](https://issuetracker.google.com/issues/298082161), sometimes the info window is anchored to the marker's lat/lng instead of the set anchor - the issue is still open |
| setBackgroundColor   | only hue (H) component of HSV color representation is controllable, alpha channel is unsupported                                                                                         |
| getIsInfoWindowShown | calling `getIsInfoWindowShown()` from on click handler will always return `false` as per this [wontfix GoogleMaps issue](https://issuetracker.google.com/issues/35823077)                |

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
| cap       |      ✅       |
| startCap  |      ✅       |
| endCap    |      ✅       |
| spans     |      ✅       |

#### OmhPolyline

| Method       | Support level |
| ------------ | :-----------: |
| getCap       |      ❌       |
| setCap       |      ✅       |
| isClickable  |      ✅       |
| setClickable |      ✅       |
| getColor     |      ✅       |
| setColor     |      ✅       |
| getEndCap    |      ❌       |
| setEndCap    |      ✅       |
| getJoinType  |      ✅       |
| setJoinType  |      ✅       |
| getPattern   |      ✅       |
| setPattern   |      ✅       |
| getPoints    |      ✅       |
| setPoints    |      ✅       |
| getSpans     |      ❌       |
| setSpans     |      ✅       |
| getStartCap  |      ❌       |
| setStartCap  |      ✅       |
| getTag       |      ✅       |
| setTag       |      ✅       |
| getWidth     |      ✅       |
| setWidth     |      ✅       |
| getZIndex    |      ✅       |
| setZIndex    |      ✅       |
| isVisible    |      ✅       |
| setVisible   |      ✅       |
| remove       |      ✅       |

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
| remove             |      ✅       |

## Documentation

- [Advanced documentation](/packages//plugin-googlemaps/docs/advanced/README.md)

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
