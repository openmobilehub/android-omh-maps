---
title: Plugin Escape Hatch
layout: default
parent: Advanced features
---

# Plugin Escape Hatch

This plugin provides an escape hatch to access the native Google Maps SDK for Android API. This allows developers to use the underlying provider's API directly, should they need to access a feature of the provider that is not supported by the OMH plugin.

You can obtain the native `GoogleMap` map instance by casting the `OmhMap` instance to `OmhMapImpl`:

```kotlin
import com.openmobilehub.android.maps.plugin.googlemaps.presentation.maps.OmhMapImpl as GoogleMapsOmhMapImpl
...
(omhMap as GoogleMapsOmhMapImpl).googleMap
```

For this provider, the following limitations as to the provider's API apply:

- calling `GoogleMap.setInfoWindowAdapter` will break OMH custom info windows system
- calling `GoogleMap.setOnMyLocationButtonClickListener` will break my location button in the OMH map view
- calling `GoogleMap.setOnCameraMoveStartedListener`, `GoogleMap.setOnCameraIdleListener` will break OMH interaction listeners
- calling `GoogleMap.setOnMapLoadedCallback` will entirely break the OMH implementation
- calling `GoogleMap.setOnMarkerClickListener`, `GoogleMap.setOnMarkerDragListener` will break OMH Marker interaction listeners system
- calling `GoogleMap.setOnInfoWindowCloseListener`, `GoogleMap.setOnInfoWindowClickListener`, `GoogleMap.setOnInfoWindowLongClickListener` will break OMH Info Window interaction listeners system
- calling `GoogleMap.setOnPolylineClickListener` will break OMH Polyline interaction listeners system
- calling `GoogleMap.setOnPolygonClickListener` will break OMH Polygon interaction listeners system
- calling `Marker.remove()` will remove the marker from the map, yet the `OmhMarker` won't be conscious of this change, which will likely lead to an exception
- calling `Polyline.remove()` will remove the marker from the map, yet the `OmhPolyline` won't be conscious of this change, which will likely lead to an exception
- calling `Polygon.remove()` will remove the marker from the map, yet the `OmhPolygon` won't be conscious of this change, which will likely lead to an exception
