/*
 * Copyright 2023 Open Mobile Hub
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.openmobilehub.android.maps.sample.maps

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.openmobilehub.android.maps.core.factories.OmhMapProvider
import com.openmobilehub.android.maps.core.presentation.fragments.OmhMapFragment
import com.openmobilehub.android.maps.core.presentation.interfaces.location.OmhFailureListener
import com.openmobilehub.android.maps.core.presentation.interfaces.location.OmhSuccessListener
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhMap
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhMarker
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhOnMapReadyCallback
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhOnMarkerClickListener
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhOnMarkerDragListener
import com.openmobilehub.android.maps.core.presentation.models.OmhCoordinate
import com.openmobilehub.android.maps.core.presentation.models.OmhMarkerOptions
import com.openmobilehub.android.maps.core.utils.NetworkConnectivityChecker
import com.openmobilehub.android.maps.sample.R
import com.openmobilehub.android.maps.sample.databinding.FragmentMapMarkersBinding
import com.openmobilehub.android.maps.sample.utils.Constants
import com.openmobilehub.android.maps.sample.utils.Constants.DEFAULT_ZOOM_LEVEL
import com.openmobilehub.android.maps.sample.utils.Constants.LOCATION_KEY
import com.openmobilehub.android.maps.sample.utils.Constants.PERMISSIONS
import com.openmobilehub.android.maps.sample.utils.Constants.PRIME_MERIDIAN
import com.openmobilehub.android.maps.sample.utils.Constants.SHOW_MESSAGE_TIME
import com.openmobilehub.android.maps.sample.utils.Constants.ZOOM_LEVEL_5
import com.openmobilehub.android.maps.sample.utils.PermissionsUtils

open class MapMarkersFragment : Fragment(), OmhOnMapReadyCallback {
    private var currentLocation: OmhCoordinate = PRIME_MERIDIAN
    private var _binding: FragmentMapMarkersBinding? = null
    private val binding get() = _binding!!
    private var networkConnectivityChecker: NetworkConnectivityChecker? = null
    private var handler: Handler? = null
    private var runnable: Runnable? = null
    private var handledCurrentLocation = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapMarkersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        networkConnectivityChecker = NetworkConnectivityChecker(requireContext()).apply {
            startListeningForConnectivityChanges {
                Toast.makeText(
                    requireContext(),
                    R.string.lost_internet_connection,
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            val omhMapFragment =
                childFragmentManager.findFragmentById(R.id.fragment_markers_demo_map_container) as? OmhMapFragment
            omhMapFragment?.getMapAsync(this)
        }.launch(PERMISSIONS)
    }

    override fun onMapReady(omhMap: OmhMap) {
        if (networkConnectivityChecker?.isNetworkAvailable() != true) {
            Toast.makeText(requireContext(), R.string.no_internet_connection, Toast.LENGTH_LONG)
                .show()
        }
        omhMap.setZoomGesturesEnabled(true)

        getCurrentLocation(omhMap)

        omhMap.addMarker(OmhMarkerOptions().apply {
            title = "Static icon marker (non-draggable)"
            position = OmhCoordinate().apply {
                latitude = Constants.PRIME_MERIDIAN.latitude + 0.0016
                longitude = Constants.PRIME_MERIDIAN.longitude + 0.0008
            }
            icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_map_marker, null);
        })

        omhMap.addMarker(OmhMarkerOptions().apply {
            title = "Configurable test marker"
            position = OmhCoordinate().apply {
                latitude = Constants.PRIME_MERIDIAN.latitude
                longitude = Constants.PRIME_MERIDIAN.longitude + 0.0008
            }
            isDraggable = true
        })

        omhMap.addMarker(OmhMarkerOptions().apply {
            title = "Static colored marker"
            position = OmhCoordinate().apply {
                latitude = Constants.PRIME_MERIDIAN.latitude
                longitude = Constants.PRIME_MERIDIAN.longitude - 0.0008
            }
            backgroundColor = 0x00FF12 // green-ish
        })

        omhMap.setOnMarkerClickListener(OmhOnMarkerClickListener { marker ->
            Log.d(
                LOG_TAG,
                "User clicked marker '${marker.getTitle()}' at ${marker.getPosition().toString()}"
            )

            Toast.makeText(
                context,
                "Marker '${marker.getTitle()}' has been clicked",
                Toast.LENGTH_SHORT
            ).show()

            false
        })

        omhMap.setOnMarkerDragListener(object : OmhOnMarkerDragListener {
            override fun onMarkerDrag(marker: OmhMarker) {
                Log.d(
                    LOG_TAG,
                    "User is dragging marker '${marker.getTitle()}', now at ${
                        marker.getPosition()
                    }"
                )
            }

            override fun onMarkerDragEnd(marker: OmhMarker) {
                Log.d(
                    LOG_TAG,
                    "User ended dragging marker '${marker.getTitle()}' at ${
                        marker.getPosition()
                    }"
                )

                Toast.makeText(
                    context,
                    "Marker '${marker.getTitle()}' has just ended being dragged",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onMarkerDragStart(marker: OmhMarker) {
                Log.d(
                    LOG_TAG,
                    "User started dragging marker '${marker.getTitle()}' at ${
                        marker.getPosition()
                    }"
                )

                Toast.makeText(
                    context,
                    "Marker '${marker.getTitle()}' is being dragged",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun enableMyLocation(omhMap: OmhMap) {
        if (PermissionsUtils.grantedRequiredPermissions(requireContext())) {
            // Safe use of 'noinspection MissingPermission' since it is checking permissions in the if condition
            // noinspection MissingPermission
            omhMap.setMyLocationEnabled(true)
            omhMap.setMyLocationButtonClickListener {
                Toast.makeText(requireContext(), R.string.center_message, Toast.LENGTH_SHORT).show()
                false
            }
        }
    }

    private fun getCurrentLocation(omhMap: OmhMap) {
        if (PermissionsUtils.grantedRequiredPermissions(requireContext())) {
            initializeRunnable()
            runnable?.let { handler?.postDelayed(it, SHOW_MESSAGE_TIME) }
            val onSuccessListener = OmhSuccessListener { omhCoordinate ->
                currentLocation = omhCoordinate
                handleAfterGetLocation(omhMap)
            }
            val onFailureListener = OmhFailureListener {
                currentLocation = PRIME_MERIDIAN
                handleAfterGetLocation(omhMap)
            }
            binding.progressIndicatorIcon.visibility = View.VISIBLE
            // Safe use of 'noinspection MissingPermission' since it is checking permissions in the if condition
            // noinspection MissingPermission
            OmhMapProvider.getInstance().provideOmhLocation(requireContext())
                .getCurrentLocation(onSuccessListener, onFailureListener)
        } else {
            moveToCurrentLocation(omhMap, ZOOM_LEVEL_5)
            enableMyLocation(omhMap)
        }
    }

    private fun initializeRunnable() {
        handler = Handler(Looper.getMainLooper())
        runnable = Runnable {
            Toast.makeText(requireContext(), R.string.move_message, Toast.LENGTH_LONG).show()
            runnable?.let { validRunnable ->
                handler?.postDelayed(validRunnable, SHOW_MESSAGE_TIME)
            }
        }
    }

    private fun handleAfterGetLocation(omhMap: OmhMap) {
        handledCurrentLocation = true
        handler?.removeCallbacksAndMessages(null)
        binding.progressIndicatorIcon.visibility = View.GONE
        moveToCurrentLocation(omhMap, DEFAULT_ZOOM_LEVEL)
        enableMyLocation(omhMap)
    }

    private fun moveToCurrentLocation(omhMap: OmhMap, zoomLevel: Float) {
        omhMap.moveCamera(currentLocation, zoomLevel)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(LOCATION_KEY, currentLocation)
    }

    override fun onResume() {
        super.onResume()
        if (handledCurrentLocation) {
            return
        }
        runnable?.let { validRunnable ->
            handler?.postDelayed(validRunnable, SHOW_MESSAGE_TIME)
        }
    }

    override fun onPause() {
        super.onPause()
        handler?.removeCallbacksAndMessages(null)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        networkConnectivityChecker?.stopListeningForConnectivity()
    }

    companion object {
        @JvmStatic
        fun newInstance(): MapMarkersFragment {
            return MapMarkersFragment()
        }

        val LOG_TAG: String = "MarkersDemoMapFragment"
    }
}
