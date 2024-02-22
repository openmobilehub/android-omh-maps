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

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import androidx.annotation.RequiresPermission
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.EdgeInsets
import com.mapbox.maps.MapView
import com.mapbox.maps.plugin.animation.camera
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhMap
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhMapLoadedCallback
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhMarker
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhOnCameraIdleListener
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhOnCameraMoveStartedListener
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhOnMyLocationButtonClickListener
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhOnPolygonClickListener
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhOnPolylineClickListener
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhPolygon
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhPolyline
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhSnapshotReadyCallback
import com.openmobilehub.android.maps.core.presentation.models.OmhCoordinate
import com.openmobilehub.android.maps.core.presentation.models.OmhMarkerOptions
import com.openmobilehub.android.maps.core.presentation.models.OmhPolygonOptions
import com.openmobilehub.android.maps.core.presentation.models.OmhPolylineOptions
import com.openmobilehub.android.maps.plugin.mapbox.utils.Constants

@SuppressWarnings("TooManyFunctions")
internal class OmhMapImpl(
    @SuppressWarnings("UnusedPrivateMember")
    private val mapView: MapView,
) : OmhMap {

    override val providerName: String
        get() = Constants.PROVIDER_NAME

    override fun addMarker(options: OmhMarkerOptions): OmhMarker? {
        return null
    }

    override fun addPolyline(options: OmhPolylineOptions): OmhPolyline? {
        // To be implemented
        return null
    }

    override fun addPolygon(options: OmhPolygonOptions): OmhPolygon? {
        // To be implemented
        return null
    }

    override fun getCameraPositionCoordinate(): OmhCoordinate {
        // To be implemented
        mapView.mapboxMap.cameraState
        return OmhCoordinate(0.0, 0.0)
    }

    override fun moveCamera(coordinate: OmhCoordinate, zoomLevel: Float) {
        // To be implemented
    }

    override fun setZoomGesturesEnabled(enableZoomGestures: Boolean) {
        // To be implemented
    }

    override fun snapshot(omhSnapshotReadyCallback: OmhSnapshotReadyCallback) {
        // To be implemented
    }

    @RequiresPermission(anyOf = [ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION])
    override fun setMyLocationEnabled(enable: Boolean) {
        // To be implemented
    }

    override fun isMyLocationEnabled(): Boolean {
        // To be implemented
        return false
    }

    override fun setMyLocationButtonClickListener(
        omhOnMyLocationButtonClickListener: OmhOnMyLocationButtonClickListener
    ) {
        // To be implemented
    }

    override fun setOnCameraMoveStartedListener(listener: OmhOnCameraMoveStartedListener) {
        // To be implemented
    }

    override fun setOnMapLoadedCallback(callback: OmhMapLoadedCallback?) {
        // To be implemented
        mapView.mapboxMap.subscribeMapLoaded {
            callback?.onMapLoaded()
        }
    }

    override fun setOnPolylineClickListener(listener: OmhOnPolylineClickListener) {
        // To be implemented
    }

    override fun setOnPolygonClickListener(listener: OmhOnPolygonClickListener) {
        // To be implemented
    }

    override fun setOnCameraIdleListener(listener: OmhOnCameraIdleListener) {
        // To be implemented
    }

    override fun setMapStyle(json: Int?) {
        // To be implemented
    }
}
