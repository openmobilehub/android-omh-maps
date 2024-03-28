---
title: Advanced features
layout: default
has_children: true
---

# OpenStreetMap Plugin - Advanced Features

This page describes the advanced features of the OpenStreetMap plugin. They are not necessary for typical simple usage, yet they can be very helpful in some advanced scenarios or out-of-the-ordinary cases.

## Custom marker icon

This plugin relies on OSMdroid library, which in turn renders custom Marker icons by measuring their size using `Drawable.getIntrinsicWidth()` and `Drawable.getIntrinsicHeight()`. This means that the custom marker icon should have a proper size. If you are passing a `Drawable` bitmap loaded from resource files, make sure that the resource is scaled properly, otherwise on larger screens the Android OS will scale the loaded bitmap to match a greater size based on screen density. This scaling occurs such that the original bitmap is displaced (i.e., not centered) on the resource plane after scaling and reports a higher resolution than the actual resource, thus being rendered displaced with respect to the actual marker location. To mitigate this issue, either the bitmap should be provided in different scales in [proper](https://developer.android.com/training/multiscreen/screendensities) `drawable-<scale>dpi` directories, or placed inside `drawable-nodpi`, which is not scaled by the OS as per [this documentation](https://developer.android.com/training/multiscreen/screendensities#TaskProvideAltBmp).
