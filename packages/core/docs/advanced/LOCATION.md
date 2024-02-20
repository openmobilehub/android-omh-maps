
## OMH Location
The main entry point for location services integration. In order to use most location APIs, clients are required to hold either the [Manifest.permission.ACCESS_COARSE_LOCATION](https://developer.android.com/reference/android/Manifest.permission.html#ACCESS_COARSE_LOCATION) permission or the [Manifest.permission.ACCESS_FINE_LOCATION](https://developer.android.com/reference/android/Manifest.permission.html#ACCESS_FINE_LOCATION).

There are several types of use cases for location. One of the most common is simply obtaining a single location in order to determine where the device is now, and continue from there.

### Get current location
The `getCurrentLocation(OmhSuccessListener, OmhFailureListener)` API is designed to get the device current location.
It requires two listeners: `OmhSuccessListener` and `OmhFailureListener`.

### Success listener
Callback for when a 'OmhCoordinate' was obtained.
The method `onSuccess(OmhCoordinate)` is executed after successfully obtained.

```kotlin
val onSuccessListener = OmhSuccessListener { omhCoordinate: OmhCoordinate ->
    currentLocation = omhCoordinate
    // Handle current location
}
```

### Failure listener
Callback interface for when a `OmhCoordinate` failed to obtain.
The method `onFailure(Exception)` is executed after failling to obtain.
```kotlin
val onFailureListener = OmhFailureListener { exception: Exception ->
    Log.e(TAG, "Error", exception)
    // Handle the failure case
}
```

### Example of usage to get the current location with the success listener and failure listener

```kotlin
omhLocation.getCurrentLocation(
    omhOnSuccessListener = { omhCoordinate ->
        currentLocation = omhCoordinate
        // Handle current location
    },
    omhOnFailureListener = { exception ->
        Log.e(TAG, "Error", exception)
        // Handle the failure case
    }
)
```

### Get last location
The `getLastLocation(OmhSuccessListener, OmhFailureListener)` API is designed to get the most recent historical location currently available.
It will return null if no historical location is available. The historical location may be of an arbitrary age.

```kotlin
omhLocation.getLastLocation(
    omhOnSuccessListener = { omhCoordinate: OmhCoordinate ->
        lastLocation = omhCoordinate
        // Handle last location
    },
    omhOnFailureListener = { exception: Exception ->
        Log.e(TAG, "Error", exception)
        // Handle the failure case
    }
)
```
