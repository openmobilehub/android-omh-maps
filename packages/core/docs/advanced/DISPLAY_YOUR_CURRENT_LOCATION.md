---
title: Display your current location
layout: default
has_children: false
parent: Advanced features
---

# Display your current location

An `OmhMap` must be acquired using `getMapAsync(OmhOnMapReadyCallback)`. This class automatically initializes the maps system and the view.

1. Implement the `OmhOnMapReadyCallback` interface and override the `onMapReady()` method, to set up the map when the `OmhMap` object is available:

   ```kotlin
    import android.Manifest.permission.ACCESS_COARSE_LOCATION
    import android.Manifest.permission.ACCESS_FINE_LOCATION
    import android.content.pm.PackageManager
    import android.os.Bundle
    import android.util.Log
    import androidx.fragment.app.Fragment
    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import androidx.activity.result.contract.ActivityResultContracts
    import androidx.core.content.ContextCompat
    import com.openmobilehub.android.maps.core.factories.OmhMapProvider
    import com.openmobilehub.android.maps.core.presentation.fragments.OmhMapFragment
    import com.openmobilehub.android.maps.core.presentation.interfaces.location.OmhFailureListener
    import com.openmobilehub.android.maps.core.presentation.interfaces.location.OmhSuccessListener
    import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhMap
    import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhOnMapReadyCallback
    import com.openmobilehub.android.maps.core.presentation.models.OmhMarkerOptions
    import com.openmobilehub.android.maps.starter_sample.databinding.FragmentMapBinding

    class MapFragment : Fragment(), OmhOnMapReadyCallback {

        // ...

        override fun onMapReady(omhMap: OmhMap) {
            if (!hasPermissions()) {
                Log.e("permission error", "Not required permissions to get current location")
                return
            }

            val onSuccessListener = OmhSuccessListener { omhCoordinate ->
                omhMap.moveCamera(omhCoordinate, 15f)
                val omhMarkerOptions = OmhMarkerOptions().apply {
                    title = "My Current Location"
                    position = omhCoordinate
                }
                omhMap.addMarker(omhMarkerOptions)
            }
            val onFailureListener = OmhFailureListener { exception ->
                Log.e("location error", exception.localizedMessage, exception)
            }
            // Safe use of 'noinspection MissingPermission' since it is checking permissions in the if condition
            // noinspection MissingPermission
            OmhMapProvider.getInstance().provideOmhLocation(requireContext()).getCurrentLocation(onSuccessListener, onFailureListener)
        }

        private fun hasPermissions() = arrayOf(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION).all {
            ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
        }
    }
   ```

2. In your fragment's onViewCreated(view: View, savedInstanceState: Bundle?) method, get the `OmhMapFragment` by calling FragmentManager.findFragmentById().
   Then use `getMapAsync()` to register for the map callback:

   ```kotlin
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Request permissions, this can be done in another way, see https://developer.android.com/training/permissions/requesting
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            // Obtain the OmhMapFragment and get notified when the map is ready to be used.
            val omhMapFragment = childFragmentManager.findFragmentById(R.id.fragment_map_container) as? OmhMapFragment
            omhMapFragment?.getMapAsync(this)
        }.launch(arrayOf(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION))
    }
   ```

3. Click `Run` for the app module menu option (or the play button icon) to run your app and see the map with the device's location.

   **Important: For a better experience and accuracy try a phone with a SIM card.**

4. Explore Advanced Features

   Complete the guide and access advanced mapping features: camera events, markers, location management, gestures, network utilities, and custom implementations/plugins. Visit [Advanced Features](https://github.com/openmobilehub/omh-maps/wiki#ohm-map-sdk---advanced-features) for details, examples, and guides. Enhance your mapping experiences. Explore now!
