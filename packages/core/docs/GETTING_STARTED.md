# Getting started

This section describes how to setup an Android Studio project to use the OMH Maps SDK for Android. For greater ease, a base code will be used within the repository.

**Note: To quickly run a full-featured app with all OMH Maps functionality, refer to the [`Sample App`](#sample-app) section and follow the provided steps.**

## Set up the development environment

1. Android Studio is required. If you haven't already done so, [download](https://developer.android.com/studio/index.html) and [install](https://developer.android.com/studio/install.html?pkg=studio) it.
2. Ensure that you are using the [Android Gradle plugin](https://developer.android.com/studio/releases/gradle-plugin) version 7.0 or later in Android Studio.

## Clone the repository

To clone the repository and checkout the `starter-code` branch, use the following command in your Terminal:

```
git clone --branch starter-code https://github.com/openmobilehub/omh-maps.git
```

## Set up your Google Cloud project for applications with Google Services(Google Maps)

Complete the required Cloud Console setup following the next steps, for more information see [Documentation](https://developers.google.com/maps/documentation/android-sdk/cloud-setup)

### Steps

## Add the API key to your app

You should not check your API key into your version control system, so it is recommended
storing it in the `local.properties` file, which is located in the root directory of your project.
For more information about the `local.properties` file, see [Gradle properties files](https://developer.android.com/studio/build#properties-files).

1. Open the `local.properties` in the project level directory, and then add the following code. Replace `YOUR_API_KEY` with your API key.

   ```
   MAPS_API_KEY=YOUR_API_KEY
   ```

2. Save the file.
3. In your app's module level `AndroidManifest.xml`file, under the `application` element add the `meta-data` element as follows:

   ```xml
   <manifest ...>
      <application ...>
         ...
         <meta-data
            android:name="com.google.android.geo.API_KEY"
            android:value="${MAPS_API_KEY}" />
      </application>
   </manifest>
   ```

4. In your app's module-level `AndroidManifest.xml` add the required permissions, for more information see [permissions](https://developer.android.com/training/permissions/declaring).

   ```xml
   <manifest ...>
      <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
      <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
      <uses-permission android:name="android.permission.INTERNET" />
      <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
      <application ...>
         ...
      </application>
   </manifest>
   ```

## Gradle configuration

To integrate the OMH Maps into your project, it is necessary to add a few Gradle dependencies.

TODO: With core plugin

TODO: Without core plugin

## Add the map into your app

The main interfaces that you will be interacting with are called `OmhMap`, `OmhMapView` and `OmhLocation`.
It contains all your basic maps and location functions like displaying a marker, map gestures, getting current location and more.
Additionally a fragment `OmhMapFragment` is provided, this fragment manages the life cycle of the map.

### Add a Map fragment

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

2. Change the variant to `debugSingleBuild`.
3. Click `Run` for the app module.
