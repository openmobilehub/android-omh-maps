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

package com.openmobilehub.android.maps.plugin.mapbox.presentation.maps

import android.content.Context
import android.os.Bundle
import android.view.View
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapInitOptions
import com.mapbox.maps.MapView
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhMapView
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhOnMapReadyCallback
import com.openmobilehub.android.maps.plugin.mapbox.utils.Constants

@Suppress("TooManyFunctions") // Suppress issue since interface has more than 12 functions.
internal class OmhMapViewImpl(context: Context) : OmhMapView {

    private var mapView: MapView

    init {
        val defaultCameraOptions = CameraOptions.Builder()
            .center(
                Point.fromLngLat(
                    Constants.INITIAL_REGION_LATITUDE,
                    Constants.INITIAL_REGION_LONGITUDE
                )
            )
            .zoom(Constants.INITIAL_REGION_ZOOM)
            .build()

        val mapInitOptions = MapInitOptions(
            context = context,
            cameraOptions = defaultCameraOptions,
        )

        mapView = MapView(context, mapInitOptions)
    }

    override fun getView(): View {
        return mapView
    }

    override fun getMapAsync(omhOnMapReadyCallback: OmhOnMapReadyCallback) {
        mapView.let { secureMapView ->
            val omhMapView = OmhMapImpl(secureMapView)
            omhOnMapReadyCallback.onMapReady(omhMapView)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // It's not required to call this method for mapbox
        // https://docs.mapbox.com/android/maps/guides/migrate-to-v10/#simplified-lifecycle-management
    }

    override fun onDestroy() {
        // It's not required to call this method for mapbox
        // https://docs.mapbox.com/android/maps/guides/migrate-to-v10/#simplified-lifecycle-management
    }

    override fun onLowMemory() {
        // It's not required to call this method for mapbox
        // https://docs.mapbox.com/android/maps/guides/migrate-to-v10/#simplified-lifecycle-management
    }

    override fun onPause() {
        // It's not required to call this method for mapbox
        // https://docs.mapbox.com/android/maps/guides/migrate-to-v10/#simplified-lifecycle-management
    }

    override fun onResume() {
        // It's not required to call this method for mapbox
        // https://docs.mapbox.com/android/maps/guides/migrate-to-v10/#simplified-lifecycle-management
    }

    override fun onSaveInstanceState(outState: Bundle) {
        // It's not required to call this method for mapbox
        // https://docs.mapbox.com/android/maps/guides/migrate-to-v10/#simplified-lifecycle-management
    }

    override fun onStart() {
        // It's not required to call this method for mapbox
        // https://docs.mapbox.com/android/maps/guides/migrate-to-v10/#simplified-lifecycle-management
    }

    override fun onStop() {
        // It's not required to call this method for mapbox
        // https://docs.mapbox.com/android/maps/guides/migrate-to-v10/#simplified-lifecycle-management
    }

    internal class Builder : OmhMapView.Builder {

        override fun build(context: Context): OmhMapView {
            return OmhMapViewImpl(context)
        }
    }
}
