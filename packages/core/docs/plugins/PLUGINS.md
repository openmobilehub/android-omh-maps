---
title: Plugins
layout: default
has_children: false
---

# Create a custom Map Implementation/Plugin

The Omh Maps SDK offers developers the flexibility to create custom map implementations or plugins for any map provider. With built-in support for popular providers like Google Maps and OpenStreetMap, developers can easily integrate maps into their applications. Additionally, the Omh Maps community is actively working to expand support for more providers such as MapBox, TomTom, Azure Maps, and others. By leveraging the comprehensive interfaces and utilities provided by Omh Maps, developers can seamlessly integrate their preferred mapping services and customize their mapping experiences to suit their specific needs.

1. in your app in the `OmhConfig` add the path of your library:

```
OmhConfig {
        bundle("singleBuild") {
            maps {
                gmsService {
                    dependency = "com.openmobilehub.android:plugin-googlemaps:1.0"
                }
                nonGmsService {
                    dependency = "com.openmobilehub.android:plugin-openstreetmap:1.0"
                    path = "your libraries path"
                }
            }
        }
}
```

2. In your library you need to add the references like: `implementation("com.openmobilehub.android.maps:core:1.0")`

3. Basically is implement all the interfaces from the `OmhMapApi`.
   This means to implement the `OmhMapFactory`, `OmhMap`, `OmhMapView`, `OmhMarker`, `OmhPolyline`, `OmhPolygon` and `OmhLocation` interfaces.

## How to implement?

### Omh Map Factory

The interface `OmhMapFactory` a factory to provide any of the interfaces of the Omh Maps Api module.
This isn't designed to be used directly from the client side, instead use the `OmhMapProvider`.

- The method `getOmhMapView` provides the `OmhMapView` that is the main entry point with the OMH Maps module.
- The method `getOmhMapView` provides the `OmhLocation` that is the entry point for Locations.

As example the Open Street Map module implements the OmhMapFactory as follows:

```kotlin
internal object OmhMapFactoryImpl : OmhMapFactory {

    override fun getOmhMapView(context: Context): OmhMapView = OmhMapViewImpl.Builder().build(context)

    override fun getOmhLocation(context: Context): OmhLocation = OmhLocationImpl.Builder().build(context)
}
```

This means that the classes `OmhMapViewImpl` and `OmhLocationImpl` have implemented a `Builder`.

```kotlin
internal class OmhMapViewImpl(context: Context) : OmhMapView {
    // ...
    internal class Builder : OmhMapView.Builder {

        override fun build(context: Context): OmhMapView {
            return OmhMapViewImpl(context)
        }
    }
}
```

```kotlin
internal class OmhLocationImpl(context: Context) : OmhLocation {
    // ...
    internal class Builder : OmhLocation.Builder {
        override fun build(context: Context): OmhLocation {
            return OmhLocationImpl(context)
        }
    }
}
```

For more information about the OMH Map functions,see [Docs](https://openmobilehub.github.io/omh-maps).
