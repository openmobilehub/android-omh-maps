---
title: Scale Factor
layout: default
parent: Advanced features
---

# Scale Factor

Plugin supports the scale factor feature. The scale factor is used to adjust the size of the map elements, e.g. polylines width. It might be useful when the single app uses multiple map providers and the map elements should have the same or similar size.

To set the scale factor use the `setMapScaleFactor` method of the `OmhMap` class:

```kt
omhMap.setScaleFactor(if (omhMap.providerName === "AzureMaps") 0.5f else 1.0f)
```
