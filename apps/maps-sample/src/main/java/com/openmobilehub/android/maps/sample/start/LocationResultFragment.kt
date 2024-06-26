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

package com.openmobilehub.android.maps.sample.start

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.openmobilehub.android.maps.core.presentation.models.OmhCoordinate
import com.openmobilehub.android.maps.sample.R
import com.openmobilehub.android.maps.sample.databinding.FragmentLocationResultBinding
import com.openmobilehub.android.maps.sample.utils.Constants.AUTHORITY_URL
import com.openmobilehub.android.maps.sample.utils.Constants.LAT_PARAM
import com.openmobilehub.android.maps.sample.utils.Constants.LNG_PARAM
import com.openmobilehub.android.maps.sample.utils.Constants.PATH
import com.openmobilehub.android.maps.sample.utils.Constants.PERMISSIONS
import com.openmobilehub.android.maps.sample.utils.Constants.SCHEME_PROTOCOL
import com.openmobilehub.android.maps.sample.utils.Constants.TYPE_TEXT_PLAIN

class LocationResultFragment : Fragment() {
    private var _binding: FragmentLocationResultBinding? = null
    private val binding get() = _binding!!
    private val args: LocationResultFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLocationResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val coordinate: OmhCoordinate? = args.coordinate
        coordinate?.let {
            binding.textViewCoordinate.text = coordinate.toString()
            binding.buttonShareLocation.visibility = View.VISIBLE
            binding.buttonShareLocation.setOnClickListener {
                intentSend(coordinate)
            }
        }
    }

    private fun intentSend(coordinate: OmhCoordinate) {
        val intent = Intent(Intent.ACTION_SEND)
        val uriBuilder = Uri.Builder()
            .scheme(SCHEME_PROTOCOL)
            .authority(AUTHORITY_URL)
            .appendPath(PATH)
            .appendQueryParameter(LAT_PARAM, coordinate.latitude.toString())
            .appendQueryParameter(LNG_PARAM, coordinate.longitude.toString())
        intent.type = TYPE_TEXT_PLAIN
        intent.putExtra(Intent.EXTRA_TEXT, uriBuilder.build().toString())
        startActivity(Intent.createChooser(intent, R.string.share_link.toString()))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun instantiate(): LocationResultFragment {
            return LocationResultFragment()
        }
    }
}
