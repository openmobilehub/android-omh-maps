---
title: Plugin Escape Hatch
layout: default
parent: Advanced features
---

# Plugin Escape Hatch

This plugin provides an escape hatch to access the native Azure Maps Android SDK. This allows developers to use the underlying provider's API directly, should they need to access a feature of the provider that is not supported by the OMH plugin.

You can obtain the native `AzureMap` and `MapControl` instances by casting the `OmhMap` instance to `OmhMapImpl`:

```kotlin
import com.openmobilehub.android.maps.plugin.azuremaps.presentation.maps.OmhMapImpl as AzureMapsOmhMapImpl
...
(omhMap as AzureMapsOmhMapImpl).mapView // for MapView
(omhMap as AzureMapsOmhMapImpl).mapControl // for MapControl
```
