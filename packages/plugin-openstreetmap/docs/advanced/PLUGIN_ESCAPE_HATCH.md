---
title: Plugin Escape Hatch
layout: default
parent: Advanced features
---

# Plugin Escape Hatch

This plugin provides an escape hatch to access the native osmdroid API. This allows developers to use the underlying provider's API directly, should they need to access a feature of the provider that is not supported by the OMH plugin.

You can obtain the native `MapView` map instance by casting the `OmhMap` instance to `OmhMapImpl`:

```kotlin
import com.openmobilehub.android.maps.plugin.openstreetmap.presentation.maps.OmhMapImpl as OSMOmhMapImpl
...
(omhMap as OSMOmhMapImpl).mapView
```

For this provider, the following limitations as to the provider's API apply:

- calling `MapView.setOnTouchListener` will break touch system for OMH
- calling `Marker.remove()` will remove the marker from the map, yet the `OmhMarker` won't be conscious of this change, which will likely lead to an exception
- calling `Marker.setOnMarkerClickListener` or `Marker.setOnMarkerDragListener` will break OMH marker interactions system
- calling `Polyline.setOnClickListener` will break OMH polyline interactions system
- calling `Polyline.remove()` will remove the polyline from the map, yet the `OmhPolyline` won't be conscious of this change, which will likely lead to an exception
- calling `Polygon.setOnClickListener` will break OMH polyline interactions system
- calling `Polygon.remove()` will remove the polyline from the map, yet the `OmhPolygon` won't be conscious of this change, which will likely lead to an exception
- calling `InfoWindow.view.setOnTouchListener`, `InfoWindow.view.setOnClickListener`, `InfoWindow.view.setOnLongClickListener` will break OMH Info Window interactions system
