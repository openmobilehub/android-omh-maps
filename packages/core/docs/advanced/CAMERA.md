---
title: Camera Features
has_children: false
parent: Core module
---

# Camera Features

The maps API allows you to change which part of the world is visible on the map. This is achieved by changing the position of the camera.

## Updating the camera view

You can move the camera instantly. To move the camera instantly you can call `OmhMap.moveCamera(OmhCoordinate, Float)`.

```kotlin
val sydneyLocation = OmhCoordinate(-34.0, 151.0)
val zoomLevel = 15f

omhMap.moveCamera(sydneyLocation, zoomLevel)
```

## Listening when the camera motion starts and movement has ended

There is a method `setOnCameraMoveStartedListener(OmhOnCameraMoveStartedListener)` that sets a callback that's invoked when the camera starts moving.
There is another method `setOnCameraIdleListener(OmhOnCameraIdleListener)` that sets a callback that's invoked when the camera movement has ended.

## Camera move started listener

The `OmhOnCameraMoveStartedListener` is a callback interface for when the camera motion starts and the method called is `onCameraMoveStarted(Int)`. **Do not** update or animate the camera from within this method.
This is called on the Android UI thread.

```kotlin
val omhOnCameraMoveStartedListener = OmhOnCameraMoveStartedListener {
    // The camera motion starts
    // For example display a Toast
    Toast.makeText(requireContext(), "Camera started moving", Toast.LENGTH_SHORT).show()
}
```

Now the `OmhOnCameraMoveListenerStartedListener` can be set to the `OmhMap`.

```kotlin
omhMap.setOnCameraMoveStartedListener(omhOnCameraMoveStartedListener)
```

## Camera idle listener

The `OmhOnCameraIdleListener` is a callback interface for when camera movement has ended and the method called is `onCameraIdle()`.
This is called on the Android UI thread.

```kotlin
val omhOnCameraIdleListener = OmhOnCameraIdleListener {
    // The camera movement has ended
    // For example display a Toast
    Toast.makeText(requireContext(), "Camera ended moving", Toast.LENGTH_SHORT).show()
}
```

Now the `OmhOnCameraIdleListener` can be set in the `OmhMap`.

```kotlin
omhMap.setOnCameraIdleListener(omhOnCameraIdleListener)
```

## Get camera position

To get the camera's position `fun getCameraPositionCoordinate(): OmhCoordinate`.

```kotlin
val cameraPosition: OmhCoordinate = omhMap.getCameraPositionCoordinate()

```
