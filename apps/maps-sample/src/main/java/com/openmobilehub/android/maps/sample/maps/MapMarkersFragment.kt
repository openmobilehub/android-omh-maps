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
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.openmobilehub.android.maps.core.presentation.fragments.OmhMapFragment
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhMap
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhMarker
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhOnMapReadyCallback
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhOnMarkerClickListener
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhOnMarkerDragListener
import com.openmobilehub.android.maps.core.presentation.models.OmhCoordinate
import com.openmobilehub.android.maps.core.presentation.models.OmhMarkerOptions
import com.openmobilehub.android.maps.core.utils.NetworkConnectivityChecker
import com.openmobilehub.android.maps.sample.R
import com.openmobilehub.android.maps.sample.customviews.PanelColorSeekbar
import com.openmobilehub.android.maps.sample.customviews.PanelSeekbar
import com.openmobilehub.android.maps.sample.customviews.PanelSpinner
import com.openmobilehub.android.maps.sample.databinding.FragmentMapMarkersBinding
import com.openmobilehub.android.maps.sample.utils.Constants.DEFAULT_ZOOM_LEVEL
import com.openmobilehub.android.maps.sample.utils.Constants.PERMISSIONS
import com.openmobilehub.android.maps.sample.utils.Constants.PRIME_MERIDIAN
import com.openmobilehub.android.maps.sample.utils.Constants.SHOW_MESSAGE_TIME

open class MapMarkersFragment : Fragment(), OmhOnMapReadyCallback {
    private var _binding: FragmentMapMarkersBinding? = null
    private val binding get() = _binding!!
    private var networkConnectivityChecker: NetworkConnectivityChecker? = null
    private var handler: Handler? = null
    private var runnable: Runnable? = null

    private var customizableMarker: OmhMarker? = null

    private var isDraggableCheckbox: CheckBox? = null
    private var hasSnippetCheckbox: CheckBox? = null
    private var isFlatCheckbox: CheckBox? = null
    private var isVisibleCheckbox: CheckBox? = null
    private var anchorUSeekbar: PanelSeekbar? = null
    private var anchorVSeekbar: PanelSeekbar? = null
    private var alphaSeekbar: PanelSeekbar? = null
    private var appearanceSpinner: PanelSpinner? = null
    private var colorSeekbar: PanelColorSeekbar? = null
    private var rotationSeekbar: PanelSeekbar? = null
    private var customizableMarkerAnchor: Pair<Float, Float> = Pair(0.5f, 0.5f)
    private var customBackgroundColor: Int = 0
    private var currentAppearancePosition: Int = 0
    private var mapProviderName: String? = null
    private var disabledAppearancePositions: HashSet<Int>? = null

    private val markerAppearanceTypeNameResourceID = intArrayOf(
        R.string.marker_appearance_type_default,
        R.string.marker_appearance_type_custom_icon,
        R.string.marker_appearance_type_custom_color
    )

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
                childFragmentManager.findFragmentById(R.id.fragment_markers_map_container) as? OmhMapFragment
            omhMapFragment?.getMapAsync(this)

        }.launch(PERMISSIONS)

        setupUI(view)
    }

    override fun onMapReady(omhMap: OmhMap) {
        mapProviderName = omhMap.providerName

        if (networkConnectivityChecker?.isNetworkAvailable() != true) {
            Toast.makeText(requireContext(), R.string.no_internet_connection, Toast.LENGTH_LONG)
                .show()
        }
        omhMap.setZoomGesturesEnabled(true)

        omhMap.moveCamera(PRIME_MERIDIAN, DEFAULT_ZOOM_LEVEL)

        omhMap.addMarker(OmhMarkerOptions().apply {
            title = "Static icon marker (non-draggable)"
            position = OmhCoordinate().apply {
                latitude = PRIME_MERIDIAN.latitude + 0.0016
                longitude = PRIME_MERIDIAN.longitude + 0.0008
            }
            icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_map_marker, null)
        })

        omhMap.addMarker(OmhMarkerOptions().apply {
            title = "Static colored marker (draggable)"
            position = OmhCoordinate().apply {
                latitude = PRIME_MERIDIAN.latitude
                longitude = PRIME_MERIDIAN.longitude - 0.0008
            }
            backgroundColor = 0x00FF12 // green-ish
            isDraggable = true
        })

        customizableMarker = omhMap.addMarker(OmhMarkerOptions().apply {
            title = "Configurable test marker"
            position = OmhCoordinate().apply {
                latitude = PRIME_MERIDIAN.latitude
                longitude = PRIME_MERIDIAN.longitude + 0.0008
            }
            isDraggable = true
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

        isVisibleCheckbox?.isChecked = customizableMarker?.getIsVisible() ?: true
        isFlatCheckbox?.isChecked = customizableMarker?.getIsFlat() ?: false
        isDraggableCheckbox?.isChecked = customizableMarker?.getIsDraggable() ?: true
        hasSnippetCheckbox?.isChecked = customizableMarker?.getSnippet() != null
        anchorUSeekbar?.setProgress(50)
        anchorVSeekbar?.setProgress(50)
        alphaSeekbar?.setProgress(100)

        if (mapProviderName == "OpenStreetMap") {
            disabledAppearancePositions =
                hashSetOf(markerAppearanceTypeNameResourceID.indexOf(R.string.marker_appearance_type_custom_color))
        }
    }

    private fun applyCustomizableMarkerAnchor() {
        customizableMarker?.setAnchor(
            customizableMarkerAnchor.first,
            customizableMarkerAnchor.second
        )
    }

    private fun applyCustomizableMarkerAppearance() {
        when (markerAppearanceTypeNameResourceID[currentAppearancePosition]) {
            R.string.marker_appearance_type_default -> customizableMarker?.setIcon(null)

            R.string.marker_appearance_type_custom_color -> customizableMarker?.setBackgroundColor(
                customBackgroundColor
            )

            R.string.marker_appearance_type_custom_icon -> customizableMarker?.setIcon(
                ResourcesCompat.getDrawable(resources, R.drawable.soccer_ball, null)
            )
        }
    }

    private fun setupUI(view: View) {
        // isVisible
        isVisibleCheckbox = view.findViewById(R.id.checkBox_isVisible)
        isVisibleCheckbox?.setOnCheckedChangeListener { _, isChecked ->
            customizableMarker?.setIsVisible(isChecked)
        }

        // isFlat
        isFlatCheckbox = view.findViewById(R.id.checkBox_isFlat)
        isFlatCheckbox?.setOnCheckedChangeListener { _, isChecked ->
            customizableMarker?.setIsFlat(isChecked)
        }

        // isDraggable
        isDraggableCheckbox = view.findViewById(R.id.checkBox_isDraggable)
        isDraggableCheckbox?.setOnCheckedChangeListener { _, isChecked ->
            customizableMarker?.setIsDraggable(isChecked)
        }

        // hasSnippet
        hasSnippetCheckbox = view.findViewById(R.id.checkBox_hasSnippet)
        hasSnippetCheckbox?.setOnCheckedChangeListener { _, isChecked ->
            customizableMarker?.setSnippet(if (isChecked) "A sample snippet with long description" else null)
        }

        // anchorU
        anchorUSeekbar = view.findViewById(R.id.panelSeekbar_anchorU)
        anchorUSeekbar?.setOnProgressChangedCallback { progress: Int ->
            customizableMarkerAnchor =
                Pair(progress.toFloat() / 100f, customizableMarkerAnchor.second)
            applyCustomizableMarkerAnchor()
        }

        // anchorV
        anchorVSeekbar = view.findViewById(R.id.panelSeekbar_anchorV)
        anchorVSeekbar?.setOnProgressChangedCallback { progress: Int ->
            customizableMarkerAnchor =
                Pair(customizableMarkerAnchor.first, progress.toFloat() / 100f)
            applyCustomizableMarkerAnchor()
        }

        // alpha
        alphaSeekbar = view.findViewById(R.id.panelSeekbar_alpha)
        alphaSeekbar?.setOnProgressChangedCallback { alpha: Int ->
            customizableMarker?.setAlpha(alpha.toFloat() / 100f)
        }

        // backgroundColor
        colorSeekbar = view.findViewById(R.id.panelSeekbar_color)
        colorSeekbar?.setOnColorChangedCallback { color: Int ->
            customBackgroundColor = color
            applyCustomizableMarkerAppearance()
        }

        // rotation
        rotationSeekbar = view.findViewById(R.id.panelSeekbar_rotation)
        rotationSeekbar?.setOnProgressChangedCallback { rotation: Int ->
            customizableMarker?.setRotation(rotation.toFloat())
        }

        // appearance
        appearanceSpinner = view.findViewById(R.id.panelSpinner_markerAppearance)
        appearanceSpinner?.setValues(
            requireContext(), markerAppearanceTypeNameResourceID
        )
        appearanceSpinner?.setOnItemSelectedCallback { position: Int ->
            if (disabledAppearancePositions?.contains(position) == true) {
                Toast.makeText(
                    context,
                    context?.getString(R.string.option_unavailable_for_provider),
                    Toast.LENGTH_SHORT
                ).show()

                appearanceSpinner?.spinner?.setSelection(currentAppearancePosition)
            } else {
                currentAppearancePosition = position
                applyCustomizableMarkerAppearance()
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }

    override fun onResume() {
        super.onResume()
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

        val LOG_TAG: String = "MapMarkersFragment"
    }
}