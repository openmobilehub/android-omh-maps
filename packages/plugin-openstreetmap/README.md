# OpenStreetMap Plugin

This plugin provides support for OpenStreetMap maps by utilizing the [osmdroid](https://github.com/osmdroid/osmdroid) library.

## Getting started

1. Add the plugin to the project by following one of the guides:
- [Adding plugin with omh-core plugin](TODO: Add missing link)
- [Adding plugin without omh-core plugin](TODO: Add missing link)

2. In your app's module-level `AndroidManifest.xml` add the required permissions, for more information see [permissions](https://developer.android.com/training/permissions/declaring).

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

| Method                            | Supported? |
|-----------------------------------|:----------:|
| addMarker                         |      ✅     |
| addPolyline                       |      ✅     |
| addPolygon                        |      ✅     |
| getCameraPositionCoordinate       |      ✅     |
| moveCamera                        |      ✅     |
| setZoomGesturesEnabled            |      ✅     |
| setMyLocationEnabled              |      ✅     |
| isMyLocationEnabled               |      ✅     |
| setMyLocationButtonClickListener  |      ✅     |
| setOnCameraMoveStartedListener    |      ✅     |
| setOnCameraIdleListener           |      ✅     |
| setOnMapLoadedCallback            |      ✅     |
| setOnPolylineClickListener        |      ✅     |
| setOnPolygonClickListener         |      ✅     |
| snapshot                          |      ✅     |
| setMapStyle                       |      ❌     |

### Marker

#### OmhMarkerOptions

| Property    | Supported? |
|-------------|:----------:|
| position    |      ✅     |
| title       |      ✅     |

#### OmhMarker

| Method       | Supported? |
|--------------|:----------:|
| getPosition  |      ✅     |
| setPosition  |      ✅     |
| getTitle     |      ✅     |
| setTitle     |      ✅     |

### Polyline

#### OmhPolylineOptions

| Property  | Supported? |
|-----------|:----------:|
| points    |      ✅     |
| clickable |      ✅     |
| color     |      ✅     |
| width     |      ✅     |
| isVisible |      ✅     |
| zIndex    |      ❌     |
| jointType |      ❌     |
| pattern   |      ❌     |
| startCap  |      ❌     |
| endCap    |      ❌     |
| spans     |      ❌     |

#### OmhPolyline

| Method       | Supported? |
|--------------|:----------:|
| isClickable  |      ✅     |
| setClickable |      ✅     |
| getColor     |      ✅     |
| setColor     |      ✅     |
| getEndCap    |      ❌     |
| setEndCap    |      ❌     |
| getJoinType  |      ❌     |
| setJoinType  |      ❌     |
| getPattern   |      ❌     |
| setPattern   |      ❌     |
| getPoints    |      ✅     |
| setPoints    |      ✅     |
| getSpans     |      ❌     |
| setSpans     |      ❌     |
| getStartCap  |      ❌     |
| setStartCap  |      ❌     |
| getTag       |      ✅     |
| setTag       |      ✅     |
| getWidth     |      ✅     |
| setWidth     |      ✅     |
| getZIndex    |      ❌     |
| setZIndex    |      ❌     |
| isVisible    |      ✅     |
| setVisible   |      ✅     |

### Polygon

#### OmhPolygonOptions

| Property        | Supported? |
|-----------------|:----------:|
| outline         |      ✅     |
| clickable       |      ✅     |
| fillColor       |      ✅     |
| holes           |      ✅     |
| isVisible       |      ✅     |
| strokeColor     |      ✅     |
| strokeJointType |      ❌     |
| strokePattern   |      ❌     |
| strokeWidth     |      ✅     |
| zIndex          |      ❌     |

#### OmhPolygon

| Method             | Supported? |
|--------------------|:----------:|
| getClickable       |      ✅     |
| setClickable       |      ✅     |
| getStrokeColor     |      ✅     |
| setStrokeColor     |      ✅     |
| getFillColor       |      ✅     |
| setFillColor       |      ✅     |
| getStrokeJointType |      ❌     |
| setStrokeJointType |      ❌     |
| getStrokePattern   |      ❌     |
| setStrokePattern   |      ❌     |
| getOutline         |      ✅     |
| setOutline         |      ✅     |
| getHoles           |      ✅     |
| setHoles           |      ✅     |
| getTag             |      ✅     |
| setTag             |      ✅     |
| getStrokeWidth     |      ✅     |
| setStrokeWidth     |      ✅     |
| getZIndex          |      ❌     |
| setZIndex          |      ❌     |
| isVisible          |      ✅     |
| setVisible         |      ✅     |

## Documentation

- [Advanced documentation](TODO: Add missing link)

## Contributing

Please contribute! We will gladly review any pull requests. Make sure to read the [Contributing](TODO: Add missing link) page first though.

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
