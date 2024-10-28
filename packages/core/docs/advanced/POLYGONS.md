---
title: Polygons
layout: default
has_children: false
parent: Advanced features
---

# Polygons

The maps API allows you to add a polygon on the map.

## Add a polygon

A polygon is a shape with multiple edges on the map. You can customize the appearance of the polygon by changing its properties. To add a polygon call the function `fun addPolygon(OmhPolygonOptions): OmhPolygon?`.

## Polygon options

Define polygon options for a `Polygon`.
Many properties can be set. To get the full list of properties, see the [OmhPolygonOptions](https://openmobilehub.github.io/android-omh-maps/api-docs/packages/core/com.openmobilehub.android.maps.core.presentation.models/-omh-polygon-options/index.html){:target="\_blank"} class.
Example of usage of `OmhPolygonOptions` and `addPolygon(OmhPolygonOptions)`:

```kotlin
val omhPolygonOptions = OmhPolygonOptions().apply {
    outline = listOf(
        OmhCoordinate(-25.0, -15.0),
        OmhCoordinate(-25.0, 20.0),
        OmhCoordinate(5.0, -15.0),
    )
    strokeColor = Color.RED
    fillColor = Color.BLUE
    strokeWidth = 10f
}

val polygon = omhMap.addPolygon(omhPolygonOptions)
```

## Modifying an existing Polygon

Once a Polygon is added to the map, you can modify its properties by calling the methods of the `OmhPolygon` class.
To get the full list of methods, see the [OmhPolygon](https://openmobilehub.github.io/android-omh-maps/api-docs/packages/core/com.openmobilehub.android.maps.core.presentation.interfaces.maps/-omh-polygon/index.html){:target="\_blank"} class. Example of usage of `OmhPolygon` methods:

```kotlin
polygon.setFillColor(Color.BLUE)
polygon.setWidth(20f)
```

## Polygon events

You can listen to click events on a Polygon by setting a listener on the map and calling the `setOnPolygonClickListener` method.
The `onPolygonClick` method will be called with the `OmhPolygon` as an argument.

```kotlin
val omhOnPolygonClickListener = OmhOnPolygonClickListener { polygon ->
    val alert = AlertDialog.Builder(requireContext())
    alert.setTitle(polygon.getTag().toString())
    alert.setPositiveButton("OK") { dialog, _ ->
        dialog.dismiss()
    }
    alert.show()
}

omhMap.setOnPolygonClickListener(omhOnPolygonClickListener)
```

## Supported features

Not all the features across all the map providers are supported. Make sure to check compatibility matrix for the features you are interested in. The compatibility matrixes are available in the individual map plugin documentation.
