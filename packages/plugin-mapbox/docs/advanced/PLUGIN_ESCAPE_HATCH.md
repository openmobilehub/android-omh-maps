---
title: Plugin Escape Hatch
layout: default
parent: Advanced features
---

# Plugin Escape Hatch

This plugin provides an escape hatch to access the native Mapbox SDK for Android API. This allows developers to use the underlying provider's API directly, should they need to access a feature of the provider that is not supported by the OMH plugin.

You can obtain the native `MapView` and `MapboxMap` instances by casting the `OmhMap` instance to `OmhMapImpl`:

```kotlin
import com.openmobilehub.android.maps.plugin.mapbox.presentation.maps.OmhMapImpl as MapboxOmhMapImpl
...
(omhMap as MapboxOmhMapImpl).mapView // for MapView
(omhMap as MapboxOmhMapImpl).mapView.mapboxMap // for MapboxMap
```

For this provider, the following limitations as to the provider's API apply:

- removing a `SymbolLayer` or `GeoJsonSource` from the map will break the plugin
- modifying the `iconImage` property of the `SymbolLayer` can make the `OmhMarker` become out of sync with the underlying layer
- altering the feature of the underlying `GeoJsonSource` of an `OmhMarker` can make it become out of sync with the marker's actual position on the map
