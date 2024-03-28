---
title: Map Markers
layout: default
has_children: false
parent: Advanced features
---

# Map Markers

The maps API allows you to add a marker on the map.

## Add a marker

You can add a marker to an specific position on the map. The marker's icon is rendered on the map at the position.
To add a marker call the function `fun addMarker(OmhMarkerOptions): OmhMarker?`.

## Marker options

Define marker options for a `Marker`.
A position and title can be set.
Example of usage of `OmhMarkerOptions` and `addOmhMarker(OmhMarkerOptions)`:

```kotlin
val omhMarkerOptions = OmhMarkerOptions().apply {
    position = OmhCoordinate(-34.0, 151.0)
    title = "Sydney"
}
omhMap.addMarker(omhMarkerOptions)
```

## Events

You can listen to marker events by setting a listener on the `OmhMap` instance. The following event listeners are available:

### `setOnMarkerClickListener`

This listener is invoked upon click events on markers. Please note that, e.g., for the Google Maps plugin, there is a discrepancy in the behaviour of `getIsInfoWindowShown` inside `setOnInfoWindowClickListener` with respect to other providers and it has been documented in the [parity matrix](/packages/core/README.md) of the plugin.

```kotlin
omhMap.setOnMarkerClickListener({ marker ->
    // do something with the marker
})
```

### `setOnMarkerDragListener`

This listener is invoked upon drag events on markers.

```kotlin
omhMap.setOnMarkerDragListener(object : OmhOnMarkerDragListener {
    override fun onMarkerDrag(marker: OmhMarker) {
        // ...
    }

    override fun onMarkerDragEnd(marker: OmhMarker) {
        // ...
    }

    override fun onMarkerDragStart(marker: OmhMarker) {
        // ...
    }
})
```

### `setOnInfoWindowClickListener`

This listener is invoked upon click events on open info windows.

```kotlin
omhMap.setOnInfoWindowClickListener { marker ->
    // ...
}
```
