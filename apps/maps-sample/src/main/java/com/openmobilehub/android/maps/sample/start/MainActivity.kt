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

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.azure.android.maps.control.AzureMaps
import com.mapbox.common.MapboxOptions
import com.openmobilehub.android.maps.core.factories.OmhMapProvider
import com.openmobilehub.android.maps.core.presentation.models.OmhCoordinate
import com.openmobilehub.android.maps.core.utils.MapProvidersUtils
import com.openmobilehub.android.maps.sample.BuildConfig
import com.openmobilehub.android.maps.sample.NavGraphDirections
import com.openmobilehub.android.maps.sample.R
import com.openmobilehub.android.maps.sample.databinding.ActivityMainBinding
import com.openmobilehub.android.maps.sample.utils.Constants.LAT_PARAM
import com.openmobilehub.android.maps.sample.utils.Constants.LNG_PARAM

class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        // Azure maps setup:
        AzureMaps.setSubscriptionKey(BuildConfig.AZURE_MAPS_SUBSCRIPTION_KEY)
        // Mapbox setup:
        MapboxOptions.accessToken = BuildConfig.MAPBOX_PUBLIC_TOKEN

        setContentView(binding.root)
        setupEdgeToEdgeInsets()
        handleIntent(intent)
        setSupportActionBar(binding.toolbar)
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    private fun setupEdgeToEdgeInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            binding.statusbarPlaceholder.updateLayoutParams { height = systemBars.top }
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            insets
        }
    }

    @SuppressLint("MissingSuperCall") // Android error: https://issuetracker.google.com/issues/67035929
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        intent?.data?.let { uri: Uri ->
            val latitude = uri.getQueryParameter(LAT_PARAM)
            val longitude = uri.getQueryParameter(LNG_PARAM)
            if (latitude != null && longitude != null) {
                val coordinate = OmhCoordinate(latitude.toDouble(), longitude.toDouble())
                val action = NavGraphDirections.actionGlobalMapLocationPickerFragment(coordinate)
                try {
                    OmhMapProvider.getInstance()
                } catch (e: IllegalStateException) {
                    OmhMapProvider.Initiator()
                        .addNonGmsPath(MapProvidersUtils().getDefaultMapProvider(this).path)
                        .initialize()
                }
                findNavController(R.id.nav_host_fragment_content_main).navigate(action)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
