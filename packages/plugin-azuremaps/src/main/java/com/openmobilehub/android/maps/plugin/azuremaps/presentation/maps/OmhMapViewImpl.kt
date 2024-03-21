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

package com.openmobilehub.android.maps.plugin.azuremaps.presentation.maps

import android.content.Context
import android.os.Bundle
import android.view.View
import com.azure.android.maps.control.MapControl
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhMapView
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhOnMapReadyCallback

@Suppress("TooManyFunctions") // Suppress issue since interface has more than 12 functions.
internal class OmhMapViewImpl(private val context: Context) : OmhMapView {

    private var mapControl: MapControl = MapControl(context)

    override fun getView(): View {
        return mapControl
    }

    override fun getMapAsync(omhOnMapReadyCallback: OmhOnMapReadyCallback) {
        mapControl.getMapAsync {
            val omhMapView = OmhMapImpl(it)

            omhOnMapReadyCallback.onMapReady(omhMapView)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        mapControl.onCreate(savedInstanceState)
    }

    override fun onDestroy() {
        mapControl.onDestroy()
    }

    override fun onLowMemory() {
        mapControl.onLowMemory()
    }

    override fun onPause() {
        mapControl.onPause()
    }

    override fun onResume() {
        mapControl.onResume()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        mapControl.onSaveInstanceState(outState)
    }

    override fun onStart() {
        mapControl.onStart()
    }

    override fun onStop() {
        mapControl.onStart()
    }

    internal class Builder : OmhMapView.Builder {
        override fun build(context: Context): OmhMapView {
            return OmhMapViewImpl(context)
        }
    }
}
