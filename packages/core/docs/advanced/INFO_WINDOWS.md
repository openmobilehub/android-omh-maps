---
title: Info Windows
layout: default
has_children: false
parent: Core module
---

# Info Windows

The maps API allows you to add info windows that are assigned to markers and can be opened upon interaction and programmatically.

The default behaviour is for the markers to show the info windows upon click.
The Google Maps plugin by default will also re-render an info window whenever its marker is clicked while it is open.

By default, an info window can have a title and snippet. However, in more advanced cases it is possible to use any arbitrary view and even update it when desired (see [Custom views](#custom-views)).

## Add an info window

To add an info window, it is required that you first add a marker to the map using `fun addMarker(OmhMarkerOptions): OmhMarker?`. To enable an info window in a marker, the minimal setup that is required is setting the `title` property - after it is set, the info window will be shown by default upon tapping the marker (unless the on marker click handler returns `true`) and can be used programmatically (see [Programmatic usage](#programmatic-usage)).

Minimal usage would be for instance:

```kotlin
val omhMarkerOptions = OmhMarkerOptions().apply {
    position = OmhCoordinate(-34.0, 151.0)
    title = "Sydney",
    snippet = "Some snippet to show inside this info window"
}
omhMap.addMarker(omhMarkerOptions)
```

## Listeners

You can listen to marker events by setting a listener on the `OmhMap` instance. The following event listeners are available:

- `setOnMarkerClickListener` - listens for click events on markers; please note a few important things:
  - the listener returns a `Boolean` value: `true` for suppressing the default behaviour of the provider's implementation, or `false` for allowing it
  - that, for instance, in the Google Maps plugin, there is a discrepancy [described in the official documentation](https://developers.google.com/android/reference/com/google/android/gms/maps/GoogleMap.OnMarkerClickListener#public-abstract-boolean-onmarkerclick-marker-marker) in the behaviour of `getIsInfoWindowShown` inside `setOnInfoWindowClickListener` with respect to other providers and it has been documented in the [parity matrix](/packages/core/README.md) of the plugin
- `setOnMarkerDragListener` - listens for drag events on markers
- `setOnInfoWindowClickListener` - listens for click events on info windows assigned to markers

## Programmatic usage

Info windows can be opened and closed programmatically using their `OmhMarker` instances:

### Showing / re-opening windows

This method will show an info window if it is currently closed or re-open (i.e., close-then-open) it if it is currently open.

```kotlin
omhMarker?.showInfoWindow()
```

### Hiding windows

This method will hide the info window if it is currently open or do nothing otherwise.

```kotlin
omhMarker?.showInfoWindow()
```

## Custom views

Info windows can be customized to show any arbitrary view. This is done by setting a custom view factory on the instance of `OmhMap`. There are two approaches supported by OMH Maps (only if the provider supports each of the ways - please refer to the parity matrix of individual provider modules):

### Using a custom window view factory

This is the primary approach that entirely overrides the default info window view and renders a custom one instead. If this factory is set with `OmhMap` method `fun setCustomInfoWindowViewFactory(listener: OmhInfoWindowViewFactory): Unit`, then **it overrides the contents view approach**.

```kotlin
omhMap?.setCustomInfoWindowViewFactory(object : OmhInfoWindowViewFactory {
    override fun createInfoWindowView(marker: OmhMarker): View {
        val view = this.layoutInflater.inflate(
            R.layout.info_window,
            null
        )

        val position = omhMarker.getPosition()
        val snippet = omhMarker.getSnippet()

        val titleTv = view.findViewById<TextView>(R.id.titleTextView)
        val descriptionTv = view.findViewById<TextView>(R.id.descriptionTextView)
        val coordinatesTv = view.findViewById<TextView>(R.id.coordinatesTextView)

        titleTv.text = omhMarker.getTitle()
        descriptionTv.text =
            """${snippet ?: "(snippet currently not set)"}
                |Rendered at: ${
                SimpleDateFormat(
                    "HH:mm:ss",
                    Locale.ENGLISH
                ).format(
                    Date()
                )
            }
            """.trimMargin()
        if (snippet == null) {
            descriptionTv.setTypeface(null, Typeface.ITALIC)
        }

        coordinatesTv.text =
            "(${"%.4f".format(position.latitude)}, ${"%.4f".format(position.longitude)})"

        return view
    }
})
```

### Using a custom contents view factory

If a custom window view factory has not been set, then OMH Maps checks if a custom contents view factory has been set with `OmhMap` method `fun setCustomInfoWindowContentsViewFactory(listener: OmhInfoWindowViewFactory): Unit`. If it has been set, then it is used to render the contents of the info window inside the default template, which means that while the outer design of the info window will be of the provider, the inside of it would be an arbitrary view.

```kotlin
omhMap?.setCustomInfoWindowContentsViewFactory(object : OmhInfoWindowViewFactory {
    override fun createInfoWindowView(marker: OmhMarker): View {
        val view = this.layoutInflater.inflate(
            R.layout.info_window_contents,
            null
        )

        val position = omhMarker.getPosition()
        val snippet = omhMarker.getSnippet()

        val titleTv = view.findViewById<TextView>(R.id.titleTextView)
        val descriptionTv = view.findViewById<TextView>(R.id.descriptionTextView)
        val coordinatesTv = view.findViewById<TextView>(R.id.coordinatesTextView)

        titleTv.text = omhMarker.getTitle()
        descriptionTv.text =
            """${snippet ?: "(snippet currently not set)"}
                |Rendered at: ${
                SimpleDateFormat(
                    "HH:mm:ss",
                    Locale.ENGLISH
                ).format(
                    Date()
                )
            }
            """.trimMargin()
        if (snippet == null) {
            descriptionTv.setTypeface(null, Typeface.ITALIC)
        }

        coordinatesTv.text =
            "(${"%.4f".format(position.latitude)}, ${"%.4f".format(position.longitude)})"

        return view
    }
})
```
