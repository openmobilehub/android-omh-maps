---
title: My Location Layer
layout: default
has_children: false
parent: Advanced features
---

# My Location Layer

Functions related to the current location of the device:
Enable or disable the location layer, check if the layer is enabled or disabled, and set additional functionality when the icon is clicked to center location.

## Set my location enabled

Enables or disables the my location layer.
By default the layer is disabled.
**Important:** To use this function is required Access Coarse Location or Access Fine Location permission.

## Enable my location layer

To enable My Location Layer call the function and pass true `fun setMyLocationEnabled(Boolean)`. It is necessary to have granted the Location Permission. [Request Permissions](https://developer.android.com/training/permissions/requesting)

```kotlin
// Before checking the permissions, you have to ask the permissions.
if (checkSelfPermission(requireContext(), ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
    checkSelfPermission(requireContext(), ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
        omhMap.setMyLocationEnabled(true)
}
```

## Disable my location layer

To disable My Location Layer call the function and pass false `fun setMyLocationEnabled(Boolean)`.

```kotlin
omhMap.setMyLocationEnabled(false)
```

## Is my location layer enabled

To check if the My Location Layer is enabled call `fun isMyLocationEnabled(): Boolean`.

```kotlin
if (omhMap.isMyLocationEnabled()) {
    // Is enabled
} else {
    // Is not enabled
}
```

## Set MyLocation button click listener

To set a click listener that's invoked when the my location button is clicked. The listener is set in the method `fun setMyLocationButtonClickListener(OmhOnMyLocationButtonClickListener)`.

## My location button click listener

Callback when the My Location Dot(which signifies the user's location) is clicked. This callback has the method `fun onMyLocationButtonClick(): Boolean`.

```kotlin
val omhOnMyLocationButtonClickListener = OmhOnMyLocationButtonClickListener {
    // Example display a Toast that is centering the camera
    Toast.makeText(requireContext(), "Center location", Toast.LENGTH_SHORT).show()
    true
}
```

Now the `OmhOnMyLocationButtonListener` can be set to `fun setMyLocationButtonClickListener(OmhOnMyLocationButtonClickListener)`.

```kotlin
omhMap.setMyLocationButtonClickListener(omhOnMyLocationButtonClickListener)
```
