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
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.openmobilehub.android.maps.core.presentation.fragments.OmhMapFragment
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhMap
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhOnMapReadyCallback
import com.openmobilehub.android.maps.core.presentation.models.OmhMarkerOptions
import com.openmobilehub.android.maps.core.utils.NetworkConnectivityChecker
import com.openmobilehub.android.maps.sample.R
import com.openmobilehub.android.maps.sample.databinding.FragmentSharedLocationMapBinding


class MapSharedLocationFragment : Fragment(), OmhOnMapReadyCallback {

    private var _binding: FragmentSharedLocationMapBinding? = null
    private val binding get() = _binding!!
    private var networkConnectivityChecker: NetworkConnectivityChecker? = null

    private val args: MapSharedLocationFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSharedLocationMapBinding.inflate(inflater, container, false)
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

    }

    override fun onMapReady(omhMap: OmhMap) {
        if (networkConnectivityChecker?.isNetworkAvailable() != true) {
            Toast.makeText(requireContext(), R.string.no_internet_connection, Toast.LENGTH_LONG)
                .show()
        }

        args.coordinate?.let {
            val markerOptions = OmhMarkerOptions().apply {
                position = it
                title = "Shared Position"
            }
            omhMap.addMarker(markerOptions)
            omhMap.moveCamera(it, 15.0f)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        networkConnectivityChecker?.stopListeningForConnectivity()
    }

    companion object {
        @JvmStatic
        fun newInstance(): MapSharedLocationFragment {
            return MapSharedLocationFragment()
        }
    }
}