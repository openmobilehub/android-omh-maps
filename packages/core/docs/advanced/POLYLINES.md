---
title: Polylines
layout: default
has_children: false
parent: Advanced features
---

# Polylines

The maps API allows you to add a polyline on the map.

## Add a polyline

A polyline is a series of connected line segments that can form any shape you want on a map. You can customize the appearance of the polyline by changing its properties.
To add a polyline call the function `fun addPolyline(OmhPolylineOptions): OmhPolyline?`.

## Polyline options

Define polyline options for a `Polyline`.
Many properties can be set. To get the full list of properties, see the [OmhPolylineOptions](TODO: Add missing link) class.
Example of usage of `OmhPolylineOptions` and `addPolyline(OmhPolylineOptions)`:

```kotlin
val omhPolylineOptions = OmhPolylineOptions().apply {
    points = listOf(
        OmhCoordinate(0.0, 0.0),
        OmhCoordinate(30.0, 10.0),
        OmhCoordinate(20.0, 20.0),
    )
    color = Color.RED
    width = 10f
}

val polyline = omhMap.addPolyline(omhPolylineOptions)
```

## Modifying an existing polyline

Once a polyline is added to the map, you can modify its properties by calling the methods of the `OmhPolyline` class.
To get the full list of methods, see the [OmhPolyline](TODO: Add missing link) class. Example of usage of `OmhPolyline` methods:

```kotlin
polyline.setColor(Color.BLUE)
polyline.setWidth(20f)
```

## Polyline events

You can listen to click events on a polyline by setting a listener on the map and calling the `setOnPolylineClickListener` method.
The `onPolylineClick` method will be called with the `OmhPolyline` as an argument.

```kotlin
val omhOnPolylineClickListener = OmhOnPolylineClickListener { polyline ->
    val alert = AlertDialog.Builder(requireContext())
    alert.setTitle(polyline.getTag().toString())
    alert.setPositiveButton("OK") { dialog, _ ->
        dialog.dismiss()
    }
    alert.show()
}

omhMap.setOnPolylineClickListener(omhOnPolylineClickListener)
```

## Supported features

Not all the features across all the map providers are supported. Make sure to check compatibility matrix for the features you are interested in. The compatibility matrixes are available in the individual map plugin documentation.
