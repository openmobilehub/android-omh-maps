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

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.openmobilehub.android.maps.core.presentation.fragments.OmhMapFragment
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhInfoWindowViewFactory
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhMap
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhMarker
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhOnInfoWindowClickListener
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhOnInfoWindowLongClickListener
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhOnInfoWindowOpenStatusChangeListener
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhOnMapReadyCallback
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhOnMarkerClickListener
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhOnMarkerDragListener
import com.openmobilehub.android.maps.core.presentation.models.Constants
import com.openmobilehub.android.maps.core.presentation.models.OmhCoordinate
import com.openmobilehub.android.maps.core.presentation.models.OmhMarkerOptions
import com.openmobilehub.android.maps.core.utils.NetworkConnectivityChecker
import com.openmobilehub.android.maps.sample.R
import com.openmobilehub.android.maps.sample.customviews.PanelSeekbar
import com.openmobilehub.android.maps.sample.customviews.PanelSpinner
import com.openmobilehub.android.maps.sample.databinding.FragmentMapInfoWindowsBinding
import com.openmobilehub.android.maps.sample.utils.Constants.DEFAULT_ZOOM_LEVEL
import com.openmobilehub.android.maps.sample.utils.Constants.PERMISSIONS
import com.openmobilehub.android.maps.sample.utils.Constants.PRIME_MERIDIAN
import com.openmobilehub.android.maps.sample.utils.Constants.SHOW_MESSAGE_TIME
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

open class MapInfoWindowsFragment : Fragment(), OmhOnMapReadyCallback {
    private var _binding: FragmentMapInfoWindowsBinding? = null
    private val binding get() = _binding!!
    private var networkConnectivityChecker: NetworkConnectivityChecker? = null
    private var handler: Handler? = null
    private var runnable: Runnable? = null

    private var omhMap: OmhMap? = null
    private var demoMarker: OmhMarker? = null

    private var isClickableCheckbox: CheckBox? = null
    private var hasSnippetCheckbox: CheckBox? = null
    private var isVisibleCheckbox: CheckBox? = null
    private var demoShouldReRenderInfoWindowOnDraggingCheckbox: CheckBox? = null
    private var appearanceSpinner: PanelSpinner? = null
    private var currentAppearancePosition: Int = 0
    private var mapProviderName: String? = null
    private var disabledAppearancePositions: HashSet<Int>? = null
    private var buttonOpenInfoWindow: Button? = null
    private var buttonHideInfoWindow: Button? = null
    private var wasLastInfoWindowStateOpen: Boolean = false

    private var anchorUSeekbar: PanelSeekbar? = null
    private var anchorVSeekbar: PanelSeekbar? = null
    private var anchorIWUSeekbar: PanelSeekbar? = null
    private var anchorIWVSeekbar: PanelSeekbar? = null

    private var rotationSeekbar: PanelSeekbar? = null
    private var markerAnchor: Pair<Float, Float> =
        Pair(Constants.ANCHOR_CENTER, Constants.ANCHOR_CENTER).copy()
    private var infoWindowAnchor: Pair<Float, Float> =
        Pair(Constants.ANCHOR_CENTER, Constants.ANCHOR_TOP).copy()

    private val infoWindowAppearanceTypeNameResourceID = intArrayOf(
        R.string.info_window_appearance_type_default,
        R.string.info_window_appearance_type_custom_window_view,
        R.string.info_window_appearance_type_custom_contents_view,
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapInfoWindowsBinding.inflate(inflater, container, false)
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
                childFragmentManager.findFragmentById(R.id.fragment_info_windows_map_container) as? OmhMapFragment
            omhMapFragment?.getMapAsync(this)

        }.launch(PERMISSIONS)

        setupUI(view)
    }

    private fun maybeReRenderMarkerWindowIfShown(marker: OmhMarker) {
        // if the marker has been dragged & re-rendering is enabled,
        // re-open the info window to have the coordinates description refreshed
        if (
            (demoShouldReRenderInfoWindowOnDraggingCheckbox?.isChecked != false)
            && marker.getIsInfoWindowShown()
        ) {
            marker.showInfoWindow()
        }
    }

    override fun onMapReady(omhMap: OmhMap) {
        mapProviderName = omhMap.providerName
        this.omhMap = omhMap

        if (networkConnectivityChecker?.isNetworkAvailable() != true) {
            Toast.makeText(requireContext(), R.string.no_internet_connection, Toast.LENGTH_LONG)
                .show()
        }
        omhMap.setZoomGesturesEnabled(true)

        omhMap.moveCamera(PRIME_MERIDIAN, DEFAULT_ZOOM_LEVEL)

        demoMarker = omhMap.addMarker(OmhMarkerOptions().apply {
            title = "Configurable test marker"
            position = OmhCoordinate().apply {
                latitude = PRIME_MERIDIAN.latitude
                longitude = PRIME_MERIDIAN.longitude
            }
            draggable = true
        })

        omhMap.setOnMarkerClickListener(OmhOnMarkerClickListener { marker ->
            val executeToggle = {
                if (marker.getIsInfoWindowShown()) {
                    marker.hideInfoWindow()
                    wasLastInfoWindowStateOpen = false
                } else {
                    marker.showInfoWindow()
                    wasLastInfoWindowStateOpen = true
                }
            }

            if (mapProviderName == "GoogleMaps") {
                // as per https://issuetracker.google.com/issues/35823077, the marker is
                // automatically closed regardless of click handler return value, thus we
                // don't want to toggle the info window state if it was already closed
                if (wasLastInfoWindowStateOpen) {
                    wasLastInfoWindowStateOpen =
                        false // the window is now closed as per GMaps behaviour
                } else {
                    executeToggle() // open the window manually
                }

                // GoogleMaps don't support onInfoWindowOpen
                applyStateToImperativeControls(marker.getIsInfoWindowShown())
            } else {
                executeToggle()

                applyStateToImperativeControls()
            }

            true // prevent centering the map
        })

        omhMap.setOnMarkerDragListener(object : OmhOnMarkerDragListener {
            override fun onMarkerDrag(marker: OmhMarker) {
                Log.d(
                    LOG_TAG,
                    "User is dragging info window for marker '${marker.getTitle()}' at ${marker.getPosition()}"
                )
            }

            override fun onMarkerDragEnd(marker: OmhMarker) {
                Log.d(
                    LOG_TAG,
                    "User ended dragging info window for marker '${marker.getTitle()}' at ${marker.getPosition()}"
                )

                maybeReRenderMarkerWindowIfShown(marker)
            }

            override fun onMarkerDragStart(marker: OmhMarker) {
                Log.d(
                    LOG_TAG,
                    "User started dragging info window for marker '${marker.getTitle()}' at ${marker.getPosition()}"
                )

                maybeReRenderMarkerWindowIfShown(marker)
            }
        })

        val eventsToast = Toast.makeText(
            requireContext(),
            "",
            Toast.LENGTH_SHORT
        ) // single instance to quickly show new toasts while an old one may still be shown

        omhMap.setOnInfoWindowOpenStatusChangeListener(object :
            OmhOnInfoWindowOpenStatusChangeListener {
            override fun onInfoWindowOpen(marker: OmhMarker) {
                Log.d(
                    LOG_TAG,
                    "User opened info window for marker '${marker.getTitle()}' at ${marker.getPosition()}"
                )

                eventsToast.setText(R.string.info_window_opened)
                eventsToast.show()

                applyStateToImperativeControls(true) // getIsInfoWindowShown() will not yet be true
            }

            override fun onInfoWindowClose(marker: OmhMarker) {
                Log.d(
                    LOG_TAG,
                    "User closed info window for marker '${marker.getTitle()}' at ${marker.getPosition()}"
                )

                eventsToast.setText(R.string.info_window_closed)
                eventsToast.show()

                applyStateToImperativeControls(false) // getIsInfoWindowShown() will not yet be false
            }
        })

        omhMap.setOnInfoWindowClickListener(object : OmhOnInfoWindowClickListener {
            override fun onInfoWindowClick(marker: OmhMarker) {
                Log.d(
                    LOG_TAG,
                    "User clicked info window for marker '${marker.getTitle()}' at ${marker.getPosition()}"
                )

                eventsToast.setText(R.string.info_window_clicked)
                eventsToast.show()
            }
        })

        omhMap.setOnInfoWindowLongClickListener(object : OmhOnInfoWindowLongClickListener {
            override fun onInfoWindowLongClick(marker: OmhMarker) {
                Log.d(
                    LOG_TAG,
                    "User long-clicked info window for marker '${marker.getTitle()}' at ${marker.getPosition()}"
                )

                eventsToast.setText(R.string.info_window_long_clicked)
                eventsToast.show()
            }
        })

        demoShouldReRenderInfoWindowOnDraggingCheckbox?.isChecked = true
        isVisibleCheckbox?.isChecked = demoMarker?.getIsVisible() ?: true
        isClickableCheckbox?.isClickable = demoMarker?.getClickable() ?: true
        hasSnippetCheckbox?.isChecked = demoMarker?.getSnippet() != null
        anchorUSeekbar?.setProgress(50)
        anchorVSeekbar?.setProgress(50)
        anchorIWUSeekbar?.setProgress(50)
        anchorIWVSeekbar?.setProgress(0)

        if (mapProviderName == "OpenStreetMap") {
            disabledAppearancePositions =
                hashSetOf(infoWindowAppearanceTypeNameResourceID.indexOf(R.string.info_window_appearance_type_custom_contents_view))
        }

        appearanceSpinner?.setDisabledPositions(disabledAppearancePositions)

        applyStateToImperativeControls()
    }

    @SuppressLint("InflateParams", "SetTextI18n")
    private fun inflateInfoWindowView(isWholeWindow: Boolean, omhMarker: OmhMarker): View {

        val view = this.layoutInflater.inflate(
            if (isWholeWindow) R.layout.info_window else R.layout.info_window_contents,
            null
        )

        val position = omhMarker.getPosition()
        val snippet = omhMarker.getSnippet()

        val titleTv = view.findViewById<TextView>(R.id.titleTextView)
        val descriptionTv = view.findViewById<TextView>(R.id.descriptionTextView)
        val coordinatesTv = view.findViewById<TextView>(R.id.coordinatesTextView)

        titleTv.text = omhMarker.getTitle()
        descriptionTv.text =
            """${snippet ?: "(snippet currently not set)"}
                |Rendered at: ${
                SimpleDateFormat(
                    "HH:mm:ss",
                    Locale.ENGLISH
                ).format(
                    Date()
                )
            }
            """.trimMargin()
        if (snippet == null) {
            descriptionTv.setTypeface(null, Typeface.ITALIC)
        }

        coordinatesTv.text =
            "(${"%.4f".format(position.latitude)}, ${"%.4f".format(position.longitude)})"

        return view
    }

    private fun applyMarkerAnchor() {
        demoMarker?.setAnchor(markerAnchor.first, markerAnchor.second)
    }

    private fun applyInfoWindowAnchor() {
        demoMarker?.setInfoWindowAnchor(infoWindowAnchor.first, infoWindowAnchor.second)
    }

    private fun applyCustomInfoWindowAppearance() {
        when (infoWindowAppearanceTypeNameResourceID[currentAppearancePosition]) {
            R.string.info_window_appearance_type_default -> {
                omhMap?.setCustomInfoWindowViewFactory(null)
                omhMap?.setCustomInfoWindowContentsViewFactory(null)
            }

            R.string.info_window_appearance_type_custom_window_view -> {
                omhMap?.setCustomInfoWindowViewFactory(object : OmhInfoWindowViewFactory {
                    override fun createInfoWindowView(marker: OmhMarker): View {
                        return inflateInfoWindowView(true, marker)
                    }
                })
                omhMap?.setCustomInfoWindowContentsViewFactory(null)
            }

            R.string.info_window_appearance_type_custom_contents_view -> {
                omhMap?.setCustomInfoWindowViewFactory(null)
                omhMap?.setCustomInfoWindowContentsViewFactory(object : OmhInfoWindowViewFactory {
                    override fun createInfoWindowView(marker: OmhMarker): View {
                        return inflateInfoWindowView(false, marker)
                    }
                })
            }
        }

        demoMarker?.let { maybeReRenderMarkerWindowIfShown(it) }
    }

    private fun applyStateToImperativeControls(overrideIsInfoWindowOpen: Boolean? = null) {
        val infoWindowControllable = demoMarker?.getIsVisible() ?: false
        val isInfoWindowOpen =
            overrideIsInfoWindowOpen ?: demoMarker?.getIsInfoWindowShown() ?: false

        buttonOpenInfoWindow?.isEnabled = infoWindowControllable && !isInfoWindowOpen
        buttonHideInfoWindow?.isEnabled = infoWindowControllable && isInfoWindowOpen
    }

    private fun setupUI(view: View) {
        // demoShouldReRenderInfoWindowOnDraggingCheckbox
        demoShouldReRenderInfoWindowOnDraggingCheckbox =
            view.findViewById(R.id.checkBox_demoShouldReRenderInfoWindowOnDragging)

        // isVisible
        isVisibleCheckbox = view.findViewById(R.id.checkBox_isVisible)
        isVisibleCheckbox?.setOnCheckedChangeListener { _, isChecked ->
            demoMarker?.setIsVisible(isChecked)
            applyStateToImperativeControls()
        }

        // isClickable
        isClickableCheckbox = view.findViewById(R.id.checkBox_isClickable)
        isClickableCheckbox?.setOnCheckedChangeListener { _, isChecked ->
            demoMarker?.setClickable(isChecked)
        }

        // hasSnippet
        hasSnippetCheckbox = view.findViewById(R.id.checkBox_hasSnippet)
        hasSnippetCheckbox?.setOnCheckedChangeListener { _, isChecked ->
            demoMarker?.setSnippet(if (isChecked) "A sample snippet with long description" else null)
        }

        // anchorU
        anchorUSeekbar = view.findViewById(R.id.panelSeekbar_anchorU)
        anchorUSeekbar?.setOnProgressChangedCallback { progress: Int ->
            markerAnchor = Pair(progress.toFloat() / 100f, markerAnchor.second)
            applyMarkerAnchor()
        }

        // anchorV
        anchorVSeekbar = view.findViewById(R.id.panelSeekbar_anchorV)
        anchorVSeekbar?.setOnProgressChangedCallback { progress: Int ->
            markerAnchor = Pair(markerAnchor.first, progress.toFloat() / 100f)
            applyMarkerAnchor()
        }

        // anchorIWU
        anchorIWUSeekbar = view.findViewById(R.id.panelSeekbar_anchorIWU)
        anchorIWUSeekbar?.setOnProgressChangedCallback { progress: Int ->
            infoWindowAnchor = Pair(progress.toFloat() / 100f, infoWindowAnchor.second)
            applyInfoWindowAnchor()
        }

        // anchorIWV
        anchorIWVSeekbar = view.findViewById(R.id.panelSeekbar_anchorIWV)
        anchorIWVSeekbar?.setOnProgressChangedCallback { progress: Int ->
            infoWindowAnchor = Pair(infoWindowAnchor.first, progress.toFloat() / 100f)
            applyInfoWindowAnchor()
        }

        // rotation
        rotationSeekbar = view.findViewById(R.id.panelSeekbar_rotation)
        rotationSeekbar?.setOnProgressChangedCallback { rotation: Int ->
            demoMarker?.setRotation(rotation.toFloat())
        }

        // appearance
        appearanceSpinner = view.findViewById(R.id.panelSpinner_infoWindowAppearance)
        appearanceSpinner?.setValues(
            requireContext(), infoWindowAppearanceTypeNameResourceID
        )
        appearanceSpinner?.setOnItemSelectedCallback { position: Int ->
            currentAppearancePosition = position
            applyCustomInfoWindowAppearance()
        }

        // imperative usage: showInfoWindow()
        buttonOpenInfoWindow = view.findViewById(R.id.button_openInfoWindow)
        buttonOpenInfoWindow?.setOnClickListener {
            demoMarker?.showInfoWindow()
            wasLastInfoWindowStateOpen = true
            applyStateToImperativeControls(true)
        }

        // imperative usage: hideInfoWindow()
        buttonHideInfoWindow = view.findViewById(R.id.button_hideInfoWindow)
        buttonHideInfoWindow?.setOnClickListener {
            demoMarker?.hideInfoWindow()
            wasLastInfoWindowStateOpen = false
            // applyStateToImperativeControls will be called from the listener
        }

        applyStateToImperativeControls()
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
        fun newInstance(): MapInfoWindowsFragment {
            return MapInfoWindowsFragment()
        }

        val LOG_TAG: String = "MapInfoWindowsFragment"
    }
}
