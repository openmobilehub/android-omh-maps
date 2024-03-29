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

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import androidx.fragment.app.Fragment
import com.openmobilehub.android.maps.core.presentation.fragments.OmhMapFragment
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhMap
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhOnMapReadyCallback
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhOnPolygonClickListener
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhPatternItem
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhPolygon
import com.openmobilehub.android.maps.core.presentation.models.OmhCoordinate
import com.openmobilehub.android.maps.core.presentation.models.OmhDash
import com.openmobilehub.android.maps.core.presentation.models.OmhDot
import com.openmobilehub.android.maps.core.presentation.models.OmhGap
import com.openmobilehub.android.maps.core.utils.NetworkConnectivityChecker
import com.openmobilehub.android.maps.sample.R
import com.openmobilehub.android.maps.sample.customviews.PanelColorSeekbar
import com.openmobilehub.android.maps.sample.customviews.PanelSeekbar
import com.openmobilehub.android.maps.sample.customviews.PanelSpinner
import com.openmobilehub.android.maps.sample.databinding.FragmentMapPolygonsBinding
import com.openmobilehub.android.maps.sample.model.InfoDisplay
import com.openmobilehub.android.maps.sample.utils.Constants

class MapPolygonsFragment : Fragment(), OmhOnMapReadyCallback {

    private var _binding: FragmentMapPolygonsBinding? = null
    private val binding get() = _binding!!
    private var networkConnectivityChecker: NetworkConnectivityChecker? = null

    private var omhMap: OmhMap? = null
    private var customizablePolygon: OmhPolygon? = null

    private val jointTypeNameResourceID = intArrayOf(
        R.string.joint_type_miter,
        R.string.joint_type_bevel,
        R.string.joint_type_round
    )
    private val patternTypeNameResourceID = intArrayOf(
        R.string.pattern_type_none,
        R.string.pattern_type_dotted,
        R.string.pattern_type_dashed,
        R.string.pattern_type_custom
    )

    private var randomizeOutlineButton: Button? = null
    private var isVisibleCheckbox: CheckBox? = null
    private var isClickableCheckbox: CheckBox? = null
    private var withHolesCheckbox: CheckBox? = null
    private var strokeWidthSeekbar: PanelSeekbar? = null
    private var strokeColorSeekbar: PanelColorSeekbar? = null
    private var fillColorSeekbar: PanelColorSeekbar? = null
    private var strokeJointTypeSpinner: PanelSpinner? = null
    private var strokePatternSpinner: PanelSpinner? = null
    private var zIndexSeekbar: PanelSeekbar? = null

    private val infoDisplay by lazy {
        InfoDisplay(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapPolygonsBinding.inflate(inflater, container, false)
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
            infoDisplay.showMessage(R.string.no_internet_connection)
        }

        omhMap.setZoomGesturesEnabled(true)

        val omhOnPolygonClickListener = OmhOnPolygonClickListener {
            val alert = AlertDialog.Builder(requireContext())
            alert.setTitle(it.getTag().toString())
            alert.setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            alert.show()
            return@OmhOnPolygonClickListener true
        }
        omhMap.setOnPolygonClickListener(omhOnPolygonClickListener)

        customizablePolygon = DebugPolygonHelper.addDebugPolygon(omhMap)
        DebugPolygonHelper.addReferencePolygon(omhMap)

        view?.let { setupUI(it) }
    }

    private fun getSupportedStatus(supportedProviders: List<String>): Boolean {
        return supportedProviders.contains(omhMap?.providerName)
    }

    private fun mapSpinnerPositionToOmhPattern(position: Int): List<OmhPatternItem>? {
        return when (position) {
            1 -> listOf(OmhDot(), OmhGap(10f))
            2 -> listOf(OmhDash(10f), OmhGap(10f))
            3 -> listOf(
                OmhDash(10f),
                OmhGap(2f),
                OmhDash(10f),
                OmhGap(2f),
                OmhDash(10f),
                OmhGap(10f),
                OmhDash(10f),
                OmhGap(2f),
                OmhDash(10f),
                OmhGap(10f),
                OmhDot(),
                OmhGap(5f),
                OmhDot(),
                OmhGap(5f),
                OmhDot(),
                OmhGap(5f),
                OmhDot(),
                OmhGap(20f)
            )

            else -> null
        }
    }

    private fun setupUI(view: View) {
        // randomizeOutline
        randomizeOutlineButton = view.findViewById(R.id.button_randomizeOutline)
        randomizeOutlineButton?.isEnabled = getSupportedStatus(Constants.ALL_PROVIDERS)
        randomizeOutlineButton?.setOnClickListener {
            customizablePolygon?.setOutline(DebugPolygonHelper.getRandomizedOutlinePoints())
        }
        // isVisible
        isVisibleCheckbox = view.findViewById(R.id.checkBox_isVisible)
        isVisibleCheckbox?.isEnabled = getSupportedStatus(Constants.ALL_PROVIDERS)
        isVisibleCheckbox?.setOnCheckedChangeListener { _, isChecked ->
            customizablePolygon?.setVisible(isChecked)
        }
        // isClickable
        isClickableCheckbox = view.findViewById(R.id.checkBox_isClickable)
        isClickableCheckbox?.isEnabled = getSupportedStatus(Constants.ALL_PROVIDERS)
        isClickableCheckbox?.setOnCheckedChangeListener { _, isChecked ->
            customizablePolygon?.setClickable(isChecked)
        }
        // holes
        withHolesCheckbox = view.findViewById(R.id.checkBox_withHoles)
        withHolesCheckbox?.isEnabled = getSupportedStatus(Constants.ALL_PROVIDERS)
        withHolesCheckbox?.setOnCheckedChangeListener { _, isChecked ->
            val holes = if (isChecked)
                listOf(
                    listOf(
                        OmhCoordinate(-20.0, -10.0),
                        OmhCoordinate(-20.0, 0.0),
                        OmhCoordinate(-10.0, 0.0),
                        OmhCoordinate(-10.0, -10.0)
                    ), listOf(
                        OmhCoordinate(0.0, 5.0),
                        OmhCoordinate(0.0, 10.0),
                        OmhCoordinate(10.0, 7.5),
                    )
                )
            else emptyList()

            customizablePolygon?.setHoles(holes)
        }
        // strokeWidth
        strokeWidthSeekbar = view.findViewById(R.id.panelSeekbar_width)
        strokeWidthSeekbar?.isEnabled = getSupportedStatus(Constants.ALL_PROVIDERS)
        strokeWidthSeekbar?.setOnProgressChangedCallback { progress: Int ->
            customizablePolygon?.setStrokeWidth(progress.toFloat())
        }
        // strokeColor
        strokeColorSeekbar = view.findViewById(R.id.panelColorSeekbar_color)
        strokeColorSeekbar?.isEnabled = getSupportedStatus(Constants.ALL_PROVIDERS)
        strokeColorSeekbar?.setOnColorChangedCallback { color: Int ->
            customizablePolygon?.setStrokeColor(color)
        }
        // fillColor
        fillColorSeekbar = view.findViewById(R.id.panelColorSeekbar_fillColor)
        fillColorSeekbar?.isEnabled = getSupportedStatus(Constants.ALL_PROVIDERS)
        fillColorSeekbar?.setOnColorChangedCallback { color: Int ->
            customizablePolygon?.setFillColor(color)
        }
        // jointType
        strokeJointTypeSpinner = view.findViewById(R.id.panelSpinner_joinType)
        strokeJointTypeSpinner?.isEnabled =
            getSupportedStatus(listOf(Constants.GOOGLE_PROVIDER, Constants.MAPBOX_PROVIDER))
        strokeJointTypeSpinner?.setValues(requireContext(), jointTypeNameResourceID)
        strokeJointTypeSpinner?.setOnItemSelectedCallback { position: Int ->
            customizablePolygon?.setStrokeJointType(position)
        }
        // pattern
        strokePatternSpinner = view.findViewById(R.id.panelSpinner_pattern)
        strokePatternSpinner?.isEnabled = getSupportedStatus(listOf(Constants.GOOGLE_PROVIDER))
        strokePatternSpinner?.setValues(requireContext(), patternTypeNameResourceID)
        strokePatternSpinner?.setOnItemSelectedCallback { position: Int ->
            val pattern = mapSpinnerPositionToOmhPattern(position)
            customizablePolygon?.setStrokePattern(pattern ?: emptyList())
        }
        // zIndex
        zIndexSeekbar = view.findViewById(R.id.panelSeekbar_zIndex)
        zIndexSeekbar?.isEnabled = getSupportedStatus(listOf(Constants.GOOGLE_PROVIDER))
        zIndexSeekbar?.setOnProgressChangedCallback { progress: Int ->
            customizablePolygon?.setZIndex(progress.toFloat())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        networkConnectivityChecker?.stopListeningForConnectivity()
    }

    companion object {
        @JvmStatic
        fun newInstance(): MapPolygonsFragment {
            return MapPolygonsFragment()
        }
    }
}
