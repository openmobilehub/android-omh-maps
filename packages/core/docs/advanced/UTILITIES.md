---
title: Utilities
layout: default
has_children: false
parent: Advanced features
---

# Utilities

There is a utility or helper class to check the network connectivity.

## Network connectivity checker

Helper class `NetworkConnectivityChecker` to check the network connectivity.

```kotlin
val networkConnectivityChecker = NetworkConnectivityChecker(context)
```

## On lost Connection

Inner class `OmhOnLostConnection` to handle when the network disconnects or otherwise no longer satisfies this request or callback.
The method to execute is `fun onLost(Network)`.

```kotlin
val omhOnLostConnection = NetworkConnectivityChecker.OmhOnLostConnection {
    Toast.makeText(requireContext(), "Lost internet connection", Toast.LENGTH_SHORT).show()
}
```

## Start listening for connectivity changes

Register callbacks to receive notifications when different network states change.
In this method a `OmhOnLostConnection` will be registered.

```kotlin
val omhOnLostConnection = NetworkConnectivityChecker.OmhOnLostConnection {
    Toast.makeText(requireContext(), "Lost internet connection", Toast.LENGTH_SHORT).show()
}

networkConnectivityChecker.startListeningForConnectivityChanges(omhOnLostConnection)
```

## Stop listening for connectivity

Unregisters the all registered callbacks if possible.

```kotlin
networkConnectivityChecker.stopListeningForConnectivity()
```

## Is network available

Checks if there is internet connection.

```kotlin
val thereIsInternetConnection: Boolean = networkConnectivityChecker.isNetworkAvailable()
```
