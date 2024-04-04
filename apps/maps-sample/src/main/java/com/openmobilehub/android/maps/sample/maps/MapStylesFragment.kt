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
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import com.openmobilehub.android.maps.core.presentation.fragments.OmhMapFragment
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhMap
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhOnMapReadyCallback
import com.openmobilehub.android.maps.core.utils.NetworkConnectivityChecker
import com.openmobilehub.android.maps.sample.R
import com.openmobilehub.android.maps.sample.databinding.FragmentMapStylesBinding
import com.openmobilehub.android.maps.sample.model.InfoDisplay

interface MapStyle {
    val dark: Int
    val retro: Int
    val silver: Int
}

class MapStylesFragment : Fragment(), OmhOnMapReadyCallback {

    private var _binding: FragmentMapStylesBinding? = null
    private val binding get() = _binding!!
    private var networkConnectivityChecker: NetworkConnectivityChecker? = null

    private var omhMap: OmhMap? = null

    private val mapStyles: Map<String, MapStyle> = mapOf(
        "GoogleMaps" to object : MapStyle {
            override val dark = R.raw.google_style_dark
            override val retro = R.raw.google_style_retro
            override val silver = R.raw.google_style_silver
        },
        "Mapbox" to object : MapStyle {
            override val dark = R.raw.mapbox_style_dark
            override val retro = R.raw.mapbox_style_retro
            override val silver = R.raw.mapbox_style_silver
        }
    )

    private var stylesRadioGroup: RadioGroup? = null

    private val infoDisplay by lazy {
        InfoDisplay(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapStylesBinding.inflate(inflater, container, false)
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

        setupUI(view)
    }

    override fun onMapReady(omhMap: OmhMap) {
        this.omhMap = omhMap
        if (networkConnectivityChecker?.isNetworkAvailable() != true) {
            infoDisplay.showMessage(R.string.lost_internet_connection)
        }

        omhMap.setZoomGesturesEnabled(true)
    }

    private fun setupUI(view: View) {
        stylesRadioGroup = view.findViewById(R.id.radioGroup_styles)
        stylesRadioGroup?.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { _, checkedId ->
            val providerMapStyles =
                mapStyles[omhMap?.providerName] ?: return@OnCheckedChangeListener
            when (checkedId) {
                R.id.style_none -> omhMap?.setMapStyle(null)
                R.id.style_retro -> omhMap?.setMapStyle(providerMapStyles.retro)
                R.id.style_dark -> omhMap?.setMapStyle(providerMapStyles.dark)
                R.id.style_silver -> omhMap?.setMapStyle(providerMapStyles.silver)
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        networkConnectivityChecker?.stopListeningForConnectivity()
    }

    companion object {
        @JvmStatic
        fun newInstance(): MapStylesFragment {
            return MapStylesFragment()
        }
    }
}
