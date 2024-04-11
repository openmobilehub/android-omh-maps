---
title: Info Windows
layout: default
parent: Advanced features
---

# Info Windows

This plugin renders info windows views, both custom & default, as "live" views. This means that interactivity will function as usual, however, attaching some interaction listeners to the root view of the custom view added via `OmhMarker.setCustomInfoWindowViewFactory` is forbidden.

{: .warning }
Not complying with the below rules may result in broken behaviour of OMH abstraction features.

When working with `OmhMarker.setCustomInfoWindowViewFactory`, it is crucial not to block the on click and on long click listeners in any way, as doing so would block the OMH API from detecting the respective events. This means that using either of these methods of the root view returned from the custom info window view factory is not allowed:

- `setOnClickListener`
- `setOnLongClickListener`

Please note that also consuming a touch event in the custom root view's `setOnTouchListener` by returning `true` will prevent the aforementioned listeners from being invoked.

{: .note }
Please note that the above limitations do not apply to `setCustomInfoWindowContentsViewFactory`, since the view returned from that method is still wrapped in a default info window view that handles the interaction events.
