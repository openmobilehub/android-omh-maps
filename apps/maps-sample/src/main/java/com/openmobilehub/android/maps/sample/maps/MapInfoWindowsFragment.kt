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
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhMap
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhMarker
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhOnMapReadyCallback
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhOnMarkerClickListener
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhOnMarkerDragListener
import com.openmobilehub.android.maps.core.presentation.models.OmhCoordinate
import com.openmobilehub.android.maps.core.presentation.models.OmhMarkerOptions
import com.openmobilehub.android.maps.core.utils.NetworkConnectivityChecker
import com.openmobilehub.android.maps.sample.R
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
    private var customizableMarker: OmhMarker? = null

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
        // if the marker has been dragged & it is showing the custom view,
        // re-open the info window to have the coordinates description refreshed
        if ((demoShouldReRenderInfoWindowOnDraggingCheckbox?.isChecked != false) && marker.getIsInfoWindowShown() && infoWindowAppearanceTypeNameResourceID[currentAppearancePosition] != R.string.info_window_appearance_type_default
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

        customizableMarker = omhMap.addMarker(OmhMarkerOptions().apply {
            title = "Configurable test marker"
            position = OmhCoordinate().apply {
                latitude = PRIME_MERIDIAN.latitude
                longitude = PRIME_MERIDIAN.longitude
            }
            draggable = true
        })

        omhMap.setOnMarkerClickListener(OmhOnMarkerClickListener { marker ->
            Log.d(
                MapMarkersFragment.LOG_TAG,
                "User clicked marker '${marker.getTitle()}' at ${marker.getPosition().toString()}"
            )

            false
        })

        omhMap.setOnMarkerDragListener(object : OmhOnMarkerDragListener {
            override fun onMarkerDrag(marker: OmhMarker) {
                maybeReRenderMarkerWindowIfShown(marker)
            }

            override fun onMarkerDragEnd(marker: OmhMarker) {
                maybeReRenderMarkerWindowIfShown(marker)
            }

            override fun onMarkerDragStart(marker: OmhMarker) {
                // not used
            }
        })

        demoShouldReRenderInfoWindowOnDraggingCheckbox?.isChecked = true
        isVisibleCheckbox?.isChecked = customizableMarker?.getIsVisible() ?: true
        isClickableCheckbox?.isClickable = customizableMarker?.getClickable() ?: true
        hasSnippetCheckbox?.isChecked = customizableMarker?.getSnippet() != null

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

    private fun applyCustomInfoWindowAppearance() {
        when (infoWindowAppearanceTypeNameResourceID[currentAppearancePosition]) {
            R.string.info_window_appearance_type_default -> {
                omhMap?.setCustomInfoWindowViewFactory(null)
                omhMap?.setCustomInfoWindowContentsViewFactory(null)
            }

            R.string.info_window_appearance_type_custom_window_view -> {
                omhMap?.setCustomInfoWindowViewFactory {
                    inflateInfoWindowView(true, it)
                }
                omhMap?.setCustomInfoWindowContentsViewFactory(null)
            }

            R.string.info_window_appearance_type_custom_contents_view -> {
                omhMap?.setCustomInfoWindowViewFactory(null)
                omhMap?.setCustomInfoWindowContentsViewFactory {
                    inflateInfoWindowView(false, it)
                }
            }
        }
    }

    private fun applyStateToImperativeControls(overrideIsInfoWindowShown: Boolean? = null) {
        val infoWindowControllable = customizableMarker?.getIsVisible() ?: false
        val isInfoWindowShown =
            overrideIsInfoWindowShown ?: customizableMarker?.getIsInfoWindowShown() ?: false;

        buttonOpenInfoWindow?.isEnabled = infoWindowControllable && !isInfoWindowShown
        buttonHideInfoWindow?.isEnabled = infoWindowControllable && isInfoWindowShown
    }

    private fun setupUI(view: View) {
        // demoShouldReRenderInfoWindowOnDraggingCheckbox
        demoShouldReRenderInfoWindowOnDraggingCheckbox =
            view.findViewById(R.id.checkBox_demoShouldReRenderInfoWindowOnDragging)

        // isVisible
        isVisibleCheckbox = view.findViewById(R.id.checkBox_isVisible)
        isVisibleCheckbox?.setOnCheckedChangeListener { _, isChecked ->
            customizableMarker?.setIsVisible(isChecked)
            applyStateToImperativeControls()
        }

        // isClickable
        isClickableCheckbox = view.findViewById(R.id.checkBox_isClickable)
        isClickableCheckbox?.setOnCheckedChangeListener { _, isChecked ->
            customizableMarker?.setClickable(isChecked)
            applyStateToImperativeControls()
        }

        // hasSnippet
        hasSnippetCheckbox = view.findViewById(R.id.checkBox_hasSnippet)
        hasSnippetCheckbox?.setOnCheckedChangeListener { _, isChecked ->
            customizableMarker?.setSnippet(if (isChecked) "A sample snippet with long description" else null)
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
            customizableMarker?.showInfoWindow()
            applyStateToImperativeControls()
        }

        // imperative usage: hideInfoWindow()
        buttonHideInfoWindow = view.findViewById(R.id.button_hideInfoWindow)
        buttonHideInfoWindow?.setOnClickListener {
            customizableMarker?.hideInfoWindow()
            applyStateToImperativeControls()
        }

        applyStateToImperativeControls()
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
        fun newInstance(): MapInfoWindowsFragment {
            return MapInfoWindowsFragment()
        }

        val LOG_TAG: String = "MapInfoWindowsFragment"
    }
}
