---
title: Plugins OMH Map
layout: default
has_children: false
parent: Core module
---

# Customize OMH Map

This is the main class of the OMH Maps SDK for Android and is the entry point for all methods related to the map. You cannot instantiate a `OmhMap` object directly, rather, you must obtain one from the `getMapAsync()` method on a `OmhMapFragment` or `OmhMapView` that you have added to your application.

**Note:** Similar to a `View` object, an `OmhMap` can only be read and modified from the Android UI thread. Calling `OmhMap` methods from another thread will result in an exception.

Any object obtained from the `OmhMap` is associated with the view. It's important to not hold on to objects (e.g. `OmhMarker`) beyond the view's life. Otherwise it will cause a memory leak as the view cannot be released.

## Map View

Alternatively to the `OmhMapFragment` you can use the `OmhMapView` which is a `View` that shows a map.
Users of this class must forward all the life cycle methods from the `Activity` or `Fragment` containing this view to the corresponding ones in this class. In particular, you must forward on the following methods:

- onCreate(Bundle)
- onStart()
- onResume()
- onPause()
- onStop()
- onDestroy()
- onSaveInstanceState(Bundle)
- onLowMemory()

The snippet below shows how to use the `OmhMapView`. `OmhMapView` displays a map getting a `View` using `getView(): View?`

```kotlin
val view = OmhMapView.getView()
```

An `OmhMap` must be acquired using `getMapAsync(OnOmhMapCallback)`. The `OmhMapView` automatically initializes the maps system and the view.

```kotlin
OmhMapView.getMapAsync { OmhMap ->
     // OmhMap object ready to use.
}
```

**Note:** Advised not to add children to this view.

You can use the map's camera to set parameters as location and zoom level. For more information, see [Camera](#camera)

```kotlin
override fun onMapReady(OmhMap: OmhMap) {
    // OmhMap object is ready to use.
    // Example of usage.
    OmhMap.setZoomGesturesEnabled(true)
    OmhMap.setMyLocationEnabled(true)
}
```

## Map

`OmhMap` is the main class of the OMH Maps SDK for Android and is the entry point for all methods related to the map. You cannot instantiate a `OmhMap` object directly, rather, you must obtain one from the `getMapAsync() `method on a `OmhMapFragment` or `OmhMapView` that you have added to your application.

**Note:** Similar to a `View` object, an `OmhMap` can only be read and modified from the Android UI thread. Calling `OmhMap` methods from another thread will result in an exception.

`OmhMap` offers multiple funcionalities. Example, you can use the map's camera to set parameters as location and zoom level. For more information, see [Documentation](#documentation)

```kotlin
override fun onMapReady(OmhMap: OmhMap) {
    // OmhMap object is ready to use.
    // Example of usage.
    OmhMap.setZoomGesturesEnabled(true)
    OmhMap.setMyLocationEnabled(true)
}
```
