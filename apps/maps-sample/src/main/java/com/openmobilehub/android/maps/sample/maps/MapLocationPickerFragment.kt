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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.openmobilehub.android.maps.core.factories.OmhMapProvider
import com.openmobilehub.android.maps.core.presentation.fragments.OmhMapFragment
import com.openmobilehub.android.maps.core.presentation.interfaces.location.OmhFailureListener
import com.openmobilehub.android.maps.core.presentation.interfaces.location.OmhSuccessListener
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhMap
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhOnCameraIdleListener
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhOnCameraMoveStartedListener
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhOnMapReadyCallback
import com.openmobilehub.android.maps.core.presentation.models.OmhCoordinate
import com.openmobilehub.android.maps.core.utils.NetworkConnectivityChecker
import com.openmobilehub.android.maps.sample.R
import com.openmobilehub.android.maps.sample.databinding.FragmentLocationPickerMapBinding
import com.openmobilehub.android.maps.sample.model.InfoDisplay
import com.openmobilehub.android.maps.sample.utils.Constants.ANIMATION_DURATION
import com.openmobilehub.android.maps.sample.utils.Constants.DEFAULT_ZOOM_LEVEL
import com.openmobilehub.android.maps.sample.utils.Constants.FINAL_TRANSLATION
import com.openmobilehub.android.maps.sample.utils.Constants.INITIAL_TRANSLATION
import com.openmobilehub.android.maps.sample.utils.Constants.LOCATION_KEY
import com.openmobilehub.android.maps.sample.utils.Constants.OVERSHOOT_INTERPOLATOR
import com.openmobilehub.android.maps.sample.utils.Constants.PERMISSIONS
import com.openmobilehub.android.maps.sample.utils.Constants.PRIME_MERIDIAN
import com.openmobilehub.android.maps.sample.utils.Constants.SHOW_MESSAGE_TIME
import com.openmobilehub.android.maps.sample.utils.Constants.ZOOM_LEVEL_5
import com.openmobilehub.android.maps.sample.utils.PermissionsUtils
import com.openmobilehub.android.maps.sample.utils.getOmhCoordinate

class MapLocationPickerFragment : Fragment(), OmhOnMapReadyCallback {
    private var currentLocation: OmhCoordinate = PRIME_MERIDIAN
    private var _binding: FragmentLocationPickerMapBinding? = null
    private val binding get() = _binding!!
    private var networkConnectivityChecker: NetworkConnectivityChecker? = null
    private var handler: Handler? = null
    private var runnable: Runnable? = null
    private var handledCurrentLocation = false
    private var myLocationEnabled = true

    private val infoDisplay by lazy {
        InfoDisplay(requireView())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val coordinate = savedInstanceState?.getOmhCoordinate(LOCATION_KEY)
        coordinate?.let {
            currentLocation = coordinate
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLocationPickerMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        networkConnectivityChecker = NetworkConnectivityChecker(requireContext()).apply {
            startListeningForConnectivityChanges {
                infoDisplay.showMessage(R.string.lost_internet_connection)
            }
        }
        binding.fabShareLocation.setOnClickListener {
            val action =
                MapLocationPickerFragmentDirections.actionMapLocationPickerFragmentToLocationResultFragment(
                    currentLocation
                )
            findNavController().navigate(action)
        }

        val displayText: (v: View) -> Unit = {
            val isVisible = binding.textViewLocation.isVisible
            binding.textViewLocation.visibility = if (isVisible) {
                View.GONE
            } else {
                View.VISIBLE
            }
        }

        binding.textViewLocation.setOnClickListener(displayText)
        binding.markerImageView.setOnClickListener(displayText)

        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            val omhMapFragment =
                childFragmentManager.findFragmentById(R.id.fragment_map_container) as? OmhMapFragment
            omhMapFragment?.getMapAsync(this)
        }.launch(PERMISSIONS)
    }

    override fun onMapReady(omhMap: OmhMap) {
        if (networkConnectivityChecker?.isNetworkAvailable() != true) {
            infoDisplay.showMessage(R.string.no_internet_connection)
        }
        omhMap.setZoomGesturesEnabled(true)

        val omhOnCameraMoveStartedListener = OmhOnCameraMoveStartedListener {
            binding.markerImageView.animate()
                .translationY(INITIAL_TRANSLATION)
                .setInterpolator(OVERSHOOT_INTERPOLATOR)
                .setDuration(ANIMATION_DURATION)
                .start()
        }

        omhMap.setOnCameraMoveStartedListener(omhOnCameraMoveStartedListener)

        val omhOnCameraIdleListener = OmhOnCameraIdleListener {
            binding.markerImageView.animate()
                .translationY(FINAL_TRANSLATION)
                .setInterpolator(OVERSHOOT_INTERPOLATOR)
                .setDuration(ANIMATION_DURATION)
                .start()

            currentLocation = omhMap.getCameraPositionCoordinate()
            binding.textViewLocation.text = getString(
                R.string.latitude_longitude_text,
                currentLocation.latitude.toString(),
                currentLocation.longitude.toString()
            )
        }

        omhMap.setOnCameraIdleListener(omhOnCameraIdleListener)
        getCurrentLocation(omhMap)

        binding.checkBoxIsMyLocationEnabled.setOnCheckedChangeListener { _, isChecked ->
            myLocationEnabled = isChecked
            if (isChecked) {
                enableMyLocation(omhMap)
            } else {
                disableMyLocation(omhMap)
            }
        }
    }

    private fun disableMyLocation(omhMap: OmhMap) {
        if (PermissionsUtils.grantedRequiredPermissions(requireContext())) {
            // Safe use of 'noinspection MissingPermission' since it is checking permissions in the if condition
            // noinspection MissingPermission
            omhMap.setMyLocationEnabled(false)
        }
    }

    private fun enableMyLocation(omhMap: OmhMap) {
        if (PermissionsUtils.grantedRequiredPermissions(requireContext())) {
            // Safe use of 'noinspection MissingPermission' since it is checking permissions in the if condition
            // noinspection MissingPermission
            omhMap.setMyLocationEnabled(true)
            omhMap.setMyLocationButtonClickListener {
                infoDisplay.showMessage(R.string.center_message)
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
            infoDisplay.showMessage(R.string.move_message)
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
        fun newInstance(): MapLocationPickerFragment {
            return MapLocationPickerFragment()
        }
    }
}
