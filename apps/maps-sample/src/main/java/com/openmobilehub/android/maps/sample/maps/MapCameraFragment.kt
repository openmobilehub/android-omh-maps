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

import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.openmobilehub.android.maps.core.presentation.fragments.OmhMapFragment
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhMap
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhOnCameraMoveStartedListener
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhOnMapReadyCallback
import com.openmobilehub.android.maps.core.presentation.models.OmhCoordinate
import com.openmobilehub.android.maps.core.utils.NetworkConnectivityChecker
import com.openmobilehub.android.maps.sample.R
import com.openmobilehub.android.maps.sample.databinding.FragmentMapCameraBinding
import com.openmobilehub.android.maps.sample.model.InfoDisplay
import com.openmobilehub.android.maps.sample.utils.Constants

class MapCameraFragment : Fragment(), OmhOnMapReadyCallback {

    private var _binding: FragmentMapCameraBinding? = null
    private val binding get() = _binding!!
    private var networkConnectivityChecker: NetworkConnectivityChecker? = null

    private var omhMap: OmhMap? = null

    private val infoDisplay by lazy {
        InfoDisplay(requireView())
    }

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
                infoDisplay.showMessage(R.string.lost_internet_connection)
            }
        }

        val omhMapFragment =
            childFragmentManager.findFragmentById(R.id.fragment_map_container) as? OmhMapFragment
        omhMapFragment?.getMapAsync(this)
    }

    override fun onMapReady(omhMap: OmhMap) {
        this.omhMap = omhMap
        if (networkConnectivityChecker?.isNetworkAvailable() != true) {
            infoDisplay.showMessage(R.string.lost_internet_connection)
        }

        omhMap.setOnMapLoadedCallback {
            infoDisplay.showMessage("Map Loaded")
        }

        omhMap.setZoomGesturesEnabled(true)
        omhMap.setOnCameraMoveStartedListener {
            val reason = when (it) {
                OmhOnCameraMoveStartedListener.REASON_API_ANIMATION -> "API Animation"
                OmhOnCameraMoveStartedListener.REASON_DEVELOPER_ANIMATION -> "Developer Animation"
                OmhOnCameraMoveStartedListener.REASON_GESTURE -> "Gesture"
                else -> "Unknown Action"
            }

            infoDisplay.showMessage("Camera move started by $reason")
        }

        omhMap.setOnCameraIdleListener {
            infoDisplay.showMessage("Camera is idle")
        }

        view?.let { setupUI(it) }
    }

    private fun getSupportedStatus(supportedProviders: List<String>): Boolean {
        return supportedProviders.contains(omhMap?.providerName)
    }

    private fun setupUI(view: View) {
        // Zoom Gestures
        val zoomGesturesCheckbox = view.findViewById<CheckBox>(R.id.checkBox_zoomGesturesEnabled)
        zoomGesturesCheckbox.isEnabled = getSupportedStatus(Constants.ALL_PROVIDERS)
        zoomGesturesCheckbox?.setOnCheckedChangeListener { _, isChecked ->
            omhMap?.setZoomGesturesEnabled(isChecked)
        }

        // Rotate Gestures
        val rotateGesturesCheckbox =
            view.findViewById<CheckBox>(R.id.checkBox_rotateGesturesEnabled)
        rotateGesturesCheckbox.isEnabled = getSupportedStatus(
            listOf(Constants.GOOGLE_PROVIDER, Constants.OSM_PROVIDER, Constants.MAPBOX_PROVIDER)
        )
        rotateGesturesCheckbox?.setOnCheckedChangeListener { _, isChecked ->
            omhMap?.setRotateGesturesEnabled(isChecked)
        }

        // Show camera position coordinate
        val showCameraPositionCoordinateButton =
            view.findViewById<Button>(R.id.button_showCameraPositionCoordinate)
        showCameraPositionCoordinateButton.isEnabled = getSupportedStatus(Constants.ALL_PROVIDERS)
        showCameraPositionCoordinateButton?.setOnClickListener {
            val cameraPositionCoordinate = omhMap?.getCameraPositionCoordinate()
            infoDisplay.showMessage("Camera position coordinate: $cameraPositionCoordinate")
        }

        // Move camera
        val moveMapToEverestButton = view.findViewById<Button>(R.id.button_moveMapToEverest)
        moveMapToEverestButton?.isEnabled = getSupportedStatus(Constants.ALL_PROVIDERS)
        moveMapToEverestButton?.setOnClickListener {
            val everestCoordinate = OmhCoordinate(27.9881, 86.9250)
            omhMap?.moveCamera(
                everestCoordinate, 15f
            )
        }
        val moveMapToSaharaButton = view.findViewById<Button>(R.id.button_moveMapSahara)
        moveMapToSaharaButton?.isEnabled = getSupportedStatus(Constants.ALL_PROVIDERS)
        moveMapToSaharaButton?.setOnClickListener {
            val saharaCoordinate = OmhCoordinate(23.4162, 25.6628)
            omhMap?.moveCamera(
                saharaCoordinate, 5f
            )
        }

        // Snapshot
        val makeSnapshotButton = view.findViewById<Button>(R.id.button_makeSnapshot)
        makeSnapshotButton?.isEnabled = getSupportedStatus(
            listOf(Constants.GOOGLE_PROVIDER, Constants.MAPBOX_PROVIDER)
        )
        makeSnapshotButton?.setOnClickListener {
            omhMap?.snapshot {
                if (it !== null) {
                    showDialogWithImage(requireContext(), it)
                } else {
                    infoDisplay.showMessage("Failed to take snapshot")
                }
            }
        }
    }

    private fun showDialogWithImage(context: Context, bitmap: Bitmap) {
        val snapshotDialog = Dialog(context)
        snapshotDialog.setContentView(R.layout.snapshot_dialog_image)

        val imageView = snapshotDialog.findViewById<ImageView>(R.id.image_snapshot)
        imageView?.setImageBitmap(bitmap)

        val closeButton = snapshotDialog.findViewById<Button>(R.id.button_close)
        closeButton?.setOnClickListener {
            snapshotDialog.dismiss()
        }
        snapshotDialog.show()
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
