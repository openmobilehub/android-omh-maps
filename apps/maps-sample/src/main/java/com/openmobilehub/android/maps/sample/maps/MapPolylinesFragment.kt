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

import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.openmobilehub.android.maps.core.presentation.fragments.OmhMapFragment
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhCap
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhMap
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhOnMapReadyCallback
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhOnPolylineClickListener
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhPatternItem
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhPolyline
import com.openmobilehub.android.maps.core.presentation.models.OmhButtCap
import com.openmobilehub.android.maps.core.presentation.models.OmhCustomCap
import com.openmobilehub.android.maps.core.presentation.models.OmhDash
import com.openmobilehub.android.maps.core.presentation.models.OmhDot
import com.openmobilehub.android.maps.core.presentation.models.OmhGap
import com.openmobilehub.android.maps.core.presentation.models.OmhRoundCap
import com.openmobilehub.android.maps.core.presentation.models.OmhSquareCap
import com.openmobilehub.android.maps.core.presentation.models.OmhStyleSpanGradient
import com.openmobilehub.android.maps.core.presentation.models.OmhStyleSpanMonochromatic
import com.openmobilehub.android.maps.core.utils.NetworkConnectivityChecker
import com.openmobilehub.android.maps.sample.R
import com.openmobilehub.android.maps.sample.customviews.PanelColorSeekbar
import com.openmobilehub.android.maps.sample.customviews.PanelSeekbar
import com.openmobilehub.android.maps.sample.customviews.PanelSpinner
import com.openmobilehub.android.maps.sample.databinding.FragmentMapPolylinesBinding


class MapPolylinesFragment : Fragment(), OmhOnMapReadyCallback {

    private var _binding: FragmentMapPolylinesBinding? = null
    private val binding get() = _binding!!
    private var networkConnectivityChecker: NetworkConnectivityChecker? = null

    private var omhMap: OmhMap? = null
    private var customizablePolyline: OmhPolyline? = null

    private var polylineColor: Int = Color.BLUE
    private var withSpan = false
    private var withSpanGradient = false
    private var withSpanPattern = false
    private var spanGradientFromColor: Int = Color.RED
    private var spanGradientToColor: Int = Color.BLUE
    private var spanSegments: Double = 1.0
    private var spanColor: Int = Color.RED

    private val jointTypeNameResourceID = intArrayOf(
        R.string.joint_type_mitter,
        R.string.joint_type_bevel,
        R.string.joint_type_round
    )
    private val capTypeNameResourceID = intArrayOf(
        R.string.cap_type_butt,
        R.string.cap_type_square,
        R.string.cap_type_round,
        R.string.cap_type_custom
    )
    private val patternTypeNameResourceID = intArrayOf(
        R.string.pattern_type_none,
        R.string.pattern_type_dotted,
        R.string.pattern_type_dashed,
        R.string.pattern_type_custom
    )

    private var spanProperties: LinearLayout? = null
    private var spanGradientProperties: LinearLayout? = null

    private var isVisibleCheckbox: CheckBox? = null
    private var strokeWidthSeekbar: PanelSeekbar? = null
    private var colorSeekbar: PanelColorSeekbar? = null
    private var startCapSpinner: PanelSpinner? = null
    private var endCapSpinner: PanelSpinner? = null
    private var jointTypeSpinner: PanelSpinner? = null
    private var patternSpinner: PanelSpinner? = null
    private var zIndexSeekbar: PanelSeekbar? = null
    private var withSpanCheckbox: CheckBox? = null
    private var spanSegmentsSeekbar: PanelSeekbar? = null
    private var spanColorSeekbar: PanelColorSeekbar? = null
    private var withGradientCheckbox: CheckBox? = null
    private var spanGradientFromColorSeekbar: PanelColorSeekbar? = null
    private var spanGradientToColorSeekbar: PanelColorSeekbar? = null
    private var withSpanPatternCheckbox: CheckBox? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapPolylinesBinding.inflate(inflater, container, false)
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

        omhMap.setZoomGesturesEnabled(true)

        val omhOnPolylineClickListener = OmhOnPolylineClickListener {
            Toast.makeText(requireContext(), it.getTag().toString(), Toast.LENGTH_SHORT).show()
        }
        omhMap.setOnPolylineClickListener(omhOnPolylineClickListener)

        customizablePolyline = DebugPolylineHelper.addSinglePolyline(omhMap)
        DebugPolylineHelper.addReferencePolyline(omhMap)
    }

    private fun mapSpinnerPositionToOmhCap(position: Int): OmhCap? {
        return when (position) {
            0 -> OmhButtCap()
            1 -> OmhSquareCap()
            2 -> OmhRoundCap()
            3 -> OmhCustomCap(BitmapFactory.decodeResource(resources, R.drawable.soccer_ball), 100f)
            else -> null
        }
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
        spanProperties = view.findViewById(R.id.spanProperties)
        spanGradientProperties = view.findViewById(R.id.spanGradientProperties)
        updatePanelUI()

        // isClickable
        isVisibleCheckbox = view.findViewById(R.id.checkBox_isVisible)
        isVisibleCheckbox?.setOnCheckedChangeListener { _, isChecked ->
            customizablePolyline?.setVisible(isChecked)
        }
        // strokeWidth
        strokeWidthSeekbar = view.findViewById(R.id.panelSeekbar_width)
        strokeWidthSeekbar?.setOnProgressChangedCallback { progress: Int ->
            customizablePolyline?.setWidth(progress.toFloat())
        }
        // color
        colorSeekbar = view.findViewById(R.id.panelColorSeekbar_color)
        colorSeekbar?.setOnColorChangedCallback { color: Int ->
            polylineColor = color
            customizablePolyline?.setColor(color)
            updateSpan()
        }
        // startCap
        startCapSpinner = view.findViewById(R.id.panelSpinner_startCap)
        startCapSpinner?.setValues(requireContext(), capTypeNameResourceID)
        startCapSpinner?.setOnItemSelectedCallback { position: Int ->
            customizablePolyline?.setStartCap(mapSpinnerPositionToOmhCap(position))
        }
        // endCap
        endCapSpinner = view.findViewById(R.id.panelSpinner_endCap)
        endCapSpinner?.setValues(requireContext(), capTypeNameResourceID)
        endCapSpinner?.setOnItemSelectedCallback { position: Int ->
            customizablePolyline?.setEndCap(mapSpinnerPositionToOmhCap(position))
        }
        // jointType
        jointTypeSpinner = view.findViewById(R.id.panelSpinner_joinType)
        jointTypeSpinner?.setValues(requireContext(), jointTypeNameResourceID)
        jointTypeSpinner?.setOnItemSelectedCallback { position: Int ->
            customizablePolyline?.setJointType(position)
        }
        // pattern
        patternSpinner = view.findViewById(R.id.panelSpinner_pattern)
        patternSpinner?.setValues(requireContext(), patternTypeNameResourceID)
        patternSpinner?.setOnItemSelectedCallback { position: Int ->
            customizablePolyline?.setPattern(mapSpinnerPositionToOmhPattern(position))
        }
        // zIndex
        zIndexSeekbar = view.findViewById(R.id.panelSeekbar_zIndex)
        zIndexSeekbar?.setOnProgressChangedCallback { progress: Int ->
            customizablePolyline?.setZIndex(progress.toFloat())
        }
        // withSpan
        withSpanCheckbox = view.findViewById(R.id.checkBox_withSpan)
        withSpanCheckbox?.setOnCheckedChangeListener { _, isChecked ->
            withSpan = isChecked
            updatePanelUI()
            updateSpan()
        }
        // spanSegments
        spanSegmentsSeekbar = view.findViewById(R.id.panelSeekbar_spanSegments)
        spanSegmentsSeekbar?.setOnProgressChangedCallback { progress: Int ->
            spanSegments = progress.toDouble()
            updateSpan()
        }
        // spanColor
        spanColorSeekbar = view.findViewById(R.id.panelColorSeekbar_spanColor)
        spanColorSeekbar?.setOnColorChangedCallback { color: Int ->
            spanColor = color
            updateSpan()
        }
        // withGradient
        withGradientCheckbox = view.findViewById(R.id.checkBox_withGradient)
        withGradientCheckbox?.setOnCheckedChangeListener { _, isChecked ->
            withSpanGradient = isChecked
            updatePanelUI()
            updateSpan()
        }
        // fromColor
        spanGradientFromColorSeekbar = view.findViewById(R.id.panelColorSeekbar_spanFromColor)
        spanGradientFromColorSeekbar?.setOnColorChangedCallback { color: Int ->
            spanGradientFromColor = color
            updateSpan()
        }
        // toColor
        spanGradientToColorSeekbar = view.findViewById(R.id.panelColorSeekbar_spanToColor)
        spanGradientToColorSeekbar?.setOnColorChangedCallback { color: Int ->
            spanGradientToColor = color
            updateSpan()
        }
        // spanPattern
        withSpanPatternCheckbox = view.findViewById(R.id.checkBox_withPattern)
        withSpanPatternCheckbox?.setOnCheckedChangeListener { _, isChecked ->
            withSpanPattern = isChecked
            updateSpan()
        }
    }

    private fun updatePanelUI() {
        spanProperties?.isVisible = withSpan
        spanGradientProperties?.isVisible = withSpanGradient
    }

    private fun updateSpan() {
        val patternBitmap = if (withSpanPattern) BitmapFactory.decodeResource(
            resources,
            R.drawable.soccer_ball
        ) else null

        val safeSegments = if (spanSegments < 1) 1.0 else spanSegments

        val span = when {
            withSpan && withSpanGradient -> OmhStyleSpanGradient(
                spanGradientFromColor,
                spanGradientToColor,
                safeSegments,
                patternBitmap
            )

            withSpan -> OmhStyleSpanMonochromatic(
                spanColor,
                safeSegments,
                patternBitmap
            )

            else -> null
        }

        val defaultSpan = OmhStyleSpanMonochromatic(polylineColor)
        customizablePolyline?.setSpans(listOfNotNull(span, defaultSpan))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        networkConnectivityChecker?.stopListeningForConnectivity()
    }

    companion object {
        @JvmStatic
        fun newInstance(): MapPolylinesFragment {
            return MapPolylinesFragment()
        }
    }
}
