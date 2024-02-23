---
title: Map Markers
has_children: false
parent: Core module
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
