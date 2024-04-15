[![Discord](https://img.shields.io/discord/1115727214827278446)](https://discord.gg/X8QB9DJXX6)
![Apache-2.0](https://img.shields.io/badge/license-Apache-blue)

<!--
// TODO - enable when the repo gets released and is public
![GitHub contributors](https://img.shields.io/github/contributors/openmobilehub/omh-maps)
-->

[![Publish Maps API](https://github.com/openmobilehub/omh-maps/actions/workflows/publish_maps_api.yml/badge.svg)](https://github.com/openmobilehub/omh-maps/actions/workflows/publish_maps_api.yml)
[![Publish Maps API Google Maps Implementation](https://github.com/openmobilehub/omh-maps/actions/workflows/publish_maps_api_gms.yml/badge.svg)](https://github.com/openmobilehub/omh-maps/actions/workflows/publish_maps_api_gms.yml)
[![Publish Maps API OpenStreetMap Implementation](https://github.com/openmobilehub/omh-maps/actions/workflows/publish_maps_api_ngms.yml/badge.svg)](https://github.com/openmobilehub/omh-maps/actions/workflows/publish_maps_api_ngms.yml)

# OMH Maps Client Library

## Overview

OMH Maps Client Library is an Android SDK that simplifies the integration of maps on both Google Mobile Services (GMS) and non-GMS devices. It provides a unified interface and components for a consistent map experience, eliminating the need for separate codebases for different Android builds. This repository contains a detailed Getting Started guide to help developers learn and effectively implement the OMH Maps Client Library into their Android projects. For a general overview and understanding of the philosophy behind OMH, please visit the official website at https://www.openmobilehub.com.

### A single codebase, running seamlessly on any device

For instance, the following screenshots showcase multiple devices with Android, both with GMS and Non-GMS. The same app works without changing a single line of code, supporting multiple map provider implementations (Google Maps and OpenStreetMap).

<div align="center">

| Google Maps                    | Open Street Maps             | MapBox                          | Azure Maps                     |
|--------------------------------|------------------------------|---------------------------------|--------------------------------|
| Camera Map                     |
| <img src="images/gmaps_1.gif"> | <img src="images/osm_1.gif"> | <img src="images/mapbox_1.gif"> | <img src="images/azure_1.gif"> |
| Location Sharing Map           |
| <img src="images/gmaps_2.gif"> | <img src="images/osm_2.gif"> | <img src="images/mapbox_2.gif"> | <img src="images/azure_2.gif"> |
| Marker Map                     |
| <img src="images/gmaps_3.gif"> | <img src="images/osm_3.gif"> | <img src="images/mapbox_3.gif"> | <img src="images/azure_3.gif"> |
| Info Windows Map               |
| <img src="images/gmaps_4.gif"> | <img src="images/osm_4.gif"> | <img src="images/mapbox_4.gif"> | <img src="images/azure_4.gif"> |
| Polyline Map                   |
| <img src="images/gmaps_5.gif"> | <img src="images/osm_5.gif"> | <img src="images/mapbox_5.gif"> | <img src="images/azure_5.gif"> |
| Polygon Map                    |
| <img src="images/gmaps_6.gif"> | <img src="images/osm_6.gif"> | <img src="images/mapbox_6.gif"> | <img src="images/azure_6.gif"> |
| Custom Styles Map              |
| <img src="images/gmaps_7.gif"> | <img src="images/osm_7.png"> | <img src="images/mapbox_7.gif"> | <img src="images/azure_7.png"> |
</div>

## Getting Started

This section describes how to setup an Android Studio project to use the OMH Maps SDK for Android. For greater ease, a base code will be used within the repository.

**Note: To quickly run a full-featured app with all OMH Maps functionality, refer to the [`Sample App`](#sample-app) section and follow the provided steps.**

### Set up the development environment

1. Android Studio is required. If you haven't already done so, [download](https://developer.android.com/studio/index.html) and [install](https://developer.android.com/studio/install.html?pkg=studio) it.
2. Ensure that you are using the [Android Gradle plugin](https://developer.android.com/studio/releases/gradle-plugin) version 7.0 or later in Android Studio.

### Clone the repository

To clone the repository and checkout the `starter-code` branch, use the following command in your Terminal:

```
git clone --branch starter-code https://github.com/openmobilehub/omh-maps.git
```

### Provider specific setup

There are different setup requirements based on the provider you will be including into your app.
Please find the specific setup instruction for the providers below:

- [Google Maps](/packages/plugin-googlemaps/README.md)
- [OpenStreetMap](/packages/plugin-openstreetmap/README.md)
- [Mapbox](/packages/plugin-mapbox/README.md)
- [Azure Maps](/packages/plugin-azuremaps/README.md)

### Add the map into your app

The main interfaces that you will be interacting with are called `OmhMap`, `OmhMapView` and `OmhLocation`.
It contains all your basic maps and location functions like displaying a marker, map gestures, getting current location and more.
Additionally a fragment `OmhMapFragment` is provided, this fragment manages the life cycle of the map.

#### Add a Map fragment

`OmhMapFragment` is the simplest way to place a map in an application.
Fragment has to declare `android:name` that sets the class name of the fragment to `OmhMapFragment`, which is the fragment type used in the maps fragment file.

1. Insert the XML fragment snippet into the `fragment_map.xml`.

   ```xml
   ...
       <fragment
           android:id="@+id/fragment_map_container"
           android:name="com.openmobilehub.android.maps.core.presentation.fragments.OmhMapFragment"
           android:layout_width="0dp"
           android:layout_height="0dp"
           app:layout_constraintBottom_toBottomOf="parent"
           app:layout_constraintEnd_toEndOf="parent"
           app:layout_constraintStart_toStartOf="parent"
           app:layout_constraintTop_toTopOf="parent" />
   ...
   ```

   And the complete fragment's layout should look similar to this example:

   ```xml
   <?xml version="1.0" encoding="utf-8"?>
   <androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
       xmlns:app="http://schemas.android.com/apk/res-auto"
       xmlns:tools="http://schemas.android.com/tools"
       android:layout_width="match_parent"
       android:layout_height="match_parent"
       tools:context=".MapFragment">

   <fragment
       android:id="@+id/fragment_map_container"
       android:name="com.openmobilehub.android.maps.core.presentation.fragments.OmhMapFragment"
       android:layout_width="0dp"
       android:layout_height="0dp"
       app:layout_constraintBottom_toBottomOf="parent"
       app:layout_constraintEnd_toEndOf="parent"
       app:layout_constraintStart_toStartOf="parent"
       app:layout_constraintTop_toTopOf="parent" />

   </androidx.constraintlayout.widget.ConstraintLayout>
   ```
      
2. Click `Run` for the app module.

## Sample App

This repository includes a [maps-sample](/apps/maps-sample) that demonstrates the functionality of the OMH Maps Client Library. By cloning the repo and executing the app, you can explore the various features offered by the library.

However, if you prefer a step-by-step approach to learn the SDK from scratch, we recommend following the detailed Getting Started guide provided in this repository. The guide will walk you through the implementation process and help you integrate the OMH Maps Client Library into your projects effectively.

## Provider Implementations / Plugins

OMH Maps SDK is open-source, promoting community collaboration and plugin support from other map providers to enhance capabilities and expand supported map services. More details can be found [here](./packages/core/docs/plugins/PLUGINS.md).

## Documentation

- Check out the [API Reference Docs](https://openmobilehub.github.io/omh-maps)
- Check out the [Core Package documentation](/packages/core/README.md)
- Check out the [Google Maps Plugin documentation](/packages/plugin-googlemaps/README.md)
- Check out the [OpenStreetMap Plugin documentation](/packages/plugin-openstreetmap/README.md)
- Check out the [Azure Maps Plugin documentation](/packages/plugin-azuremaps/README.md)

## Contributing

Please contribute! We will gladly review any pull requests. Make sure to read the [Contributing](/CONTRIBUTING.md) page first though.

## License

```
Copyright 2023 Open Mobile Hub

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
