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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.openmobilehub.android.maps.core.presentation.fragments.OmhMapFragment
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhMap
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhOnCameraMoveStartedListener
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhOnMapReadyCallback
import com.openmobilehub.android.maps.core.presentation.models.OmhCoordinate
import com.openmobilehub.android.maps.core.utils.NetworkConnectivityChecker
import com.openmobilehub.android.maps.sample.R
import com.openmobilehub.android.maps.sample.databinding.FragmentMapCameraBinding

class MapCameraFragment : Fragment(), OmhOnMapReadyCallback {

    private var _binding: FragmentMapCameraBinding? = null
    private val binding get() = _binding!!
    private var networkConnectivityChecker: NetworkConnectivityChecker? = null

    private var omhMap: OmhMap? = null

    private var zoomGesturesCheckbox: CheckBox? = null
    private var showCameraPositionCoordinateButton: Button? = null
    private var moveMapToEverestButton: Button? = null
    private var moveMapToSaharaButton: Button? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapCameraBinding.inflate(inflater, container, false)
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

        val omhMapFragment =
            childFragmentManager.findFragmentById(R.id.fragment_map_container) as? OmhMapFragment
        omhMapFragment?.getMapAsync(this)

        setupUI(view)
    }

    override fun onMapReady(omhMap: OmhMap) {
        this.omhMap = omhMap
        if (networkConnectivityChecker?.isNetworkAvailable() != true) {
            Toast.makeText(requireContext(), R.string.no_internet_connection, Toast.LENGTH_LONG)
                .show()
        }

        omhMap.setOnMapLoadedCallback {
            Toast.makeText(requireContext(), "Map Loaded", Toast.LENGTH_LONG).show()
        }

        omhMap.setZoomGesturesEnabled(true)
        omhMap.setOnCameraMoveStartedListener{
            val reason = when(it) {
                OmhOnCameraMoveStartedListener.REASON_API_ANIMATION -> "API Animation"
                OmhOnCameraMoveStartedListener.REASON_DEVELOPER_ANIMATION -> "Developer Animation"
                OmhOnCameraMoveStartedListener.REASON_GESTURE -> "Gesture"
                else -> "Unknown"
            }
            Toast.makeText(requireContext(), "Camera move started by $reason", Toast.LENGTH_LONG).show()
        }
    }

    private fun setupUI(view: View) {
        // zoomGestures
        zoomGesturesCheckbox = view.findViewById(R.id.checkBox_zoomGesturesEnabled)
        zoomGesturesCheckbox?.setOnCheckedChangeListener { _, isChecked ->
            omhMap?.setZoomGesturesEnabled(isChecked)
        }
        // Show camera position coordinate
        showCameraPositionCoordinateButton = view.findViewById(R.id.button_showCameraPositionCoordinate)
        showCameraPositionCoordinateButton?.setOnClickListener {
            val cameraPositionCoordinate = omhMap?.getCameraPositionCoordinate()
            Toast.makeText(
                requireContext(),
                "Camera position coordinate: $cameraPositionCoordinate",
                Toast.LENGTH_LONG
            ).show()
        }
        // Move camera
        moveMapToEverestButton = view.findViewById(R.id.button_moveMapToEverest)
        moveMapToEverestButton?.setOnClickListener {
            omhMap?.moveCamera(
                OmhCoordinate(
                    27.9881,
                    86.9250
                ), 15f
            )
        }
        moveMapToSaharaButton = view.findViewById(R.id.button_moveMapSahara)
        moveMapToSaharaButton?.setOnClickListener {
            omhMap?.moveCamera(
                OmhCoordinate(
                    23.4162,
                    25.6628
                ), 5f
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        networkConnectivityChecker?.stopListeningForConnectivity()
    }

    companion object {
        @JvmStatic
        fun newInstance(): MapCameraFragment {
            return MapCameraFragment()
        }
    }
}
