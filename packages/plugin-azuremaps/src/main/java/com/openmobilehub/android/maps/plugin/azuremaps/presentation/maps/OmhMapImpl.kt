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

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import androidx.annotation.RequiresPermission
import com.azure.android.maps.control.AzureMap
import com.azure.android.maps.control.MapControl
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhInfoWindowViewFactory
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhMap
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhMapLoadedCallback
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhMarker
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhOnCameraIdleListener
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhOnCameraMoveStartedListener
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhOnInfoWindowClickListener
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhOnInfoWindowLongClickListener
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhOnInfoWindowOpenStatusChangeListener
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhOnMarkerClickListener
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhOnMarkerDragListener
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
import com.openmobilehub.android.maps.core.utils.logging.UnsupportedFeatureLogger
import com.openmobilehub.android.maps.plugin.azuremaps.presentation.maps.managers.CameraManager
import com.openmobilehub.android.maps.plugin.azuremaps.presentation.maps.managers.MapMarkerManager
import com.openmobilehub.android.maps.plugin.azuremaps.presentation.maps.managers.MyLocationManager
import com.openmobilehub.android.maps.plugin.azuremaps.utils.Constants
import com.openmobilehub.android.maps.plugin.azuremaps.utils.mapLogger

@SuppressWarnings("TooManyFunctions", "UnusedPrivateMember")
internal class OmhMapImpl(
    private val context: Context,
    private val mapControl: MapControl,
    private val mapView: AzureMap,
    private val cameraManager: CameraManager = CameraManager(mapView),
    private val myLocationManager: MyLocationManager = MyLocationManager(
        context,
        mapControl,
        mapView
    ),
    private val logger: UnsupportedFeatureLogger = mapLogger
) : OmhMap {

    internal val mapMarkerManager = MapMarkerManager(context, mapView)

    override val providerName: String
        get() = Constants.PROVIDER_NAME

    override fun addMarker(options: OmhMarkerOptions): OmhMarker {
        return mapMarkerManager.addMarker(options)
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
        return cameraManager.getCameraPositionCoordinate()
    }

    override fun moveCamera(coordinate: OmhCoordinate, zoomLevel: Float) {
        cameraManager.moveCamera(coordinate, zoomLevel)
    }

    override fun setZoomGesturesEnabled(enableZoomGestures: Boolean) {
        cameraManager.setZoomGesturesEnabled(enableZoomGestures)
    }

    override fun setRotateGesturesEnabled(enableRotateGestures: Boolean) {
        logger.logSetterNotSupported("rotateGestures")
    }

    override fun snapshot(omhSnapshotReadyCallback: OmhSnapshotReadyCallback) {
        logger.logNotSupported("snapshot")
    }

    @RequiresPermission(anyOf = [ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION])
    override fun setMyLocationEnabled(enable: Boolean) {
        myLocationManager.setMyLocationEnabled(enable)
    }

    override fun isMyLocationEnabled(): Boolean {
        return myLocationManager.isMyLocationEnabled()
    }

    override fun setMyLocationButtonClickListener(
        omhOnMyLocationButtonClickListener: OmhOnMyLocationButtonClickListener
    ) {
        myLocationManager.setMyLocationButtonClickListener(omhOnMyLocationButtonClickListener)
    }

    override fun setOnCameraMoveStartedListener(listener: OmhOnCameraMoveStartedListener) {
        cameraManager.setOnCameraMoveStartedListener(listener)
    }

    override fun setOnCameraIdleListener(listener: OmhOnCameraIdleListener) {
        cameraManager.setOnCameraIdleListener(listener)
    }

    override fun setOnMapLoadedCallback(callback: OmhMapLoadedCallback?) {
        cameraManager.setOnMapLoadedCallback(callback)
    }

    override fun setOnMarkerClickListener(listener: OmhOnMarkerClickListener) {
        // To be implemented
    }

    override fun setOnMarkerDragListener(listener: OmhOnMarkerDragListener) {
        // To be implemented
    }

    override fun setOnInfoWindowOpenStatusChangeListener(listener: OmhOnInfoWindowOpenStatusChangeListener) {
        // To be implemented
    }

    override fun setOnInfoWindowClickListener(listener: OmhOnInfoWindowClickListener) {
        // To be implemented
    }

    override fun setOnInfoWindowLongClickListener(listener: OmhOnInfoWindowLongClickListener) {
        // To be implemented
    }

    override fun setOnPolylineClickListener(listener: OmhOnPolylineClickListener) {
        // To be implemented
    }

    override fun setOnPolygonClickListener(listener: OmhOnPolygonClickListener) {
        // To be implemented
    }

    override fun setMapStyle(json: Int?) {
        // To be implemented
    }

    override fun setScaleFactor(scaleFactor: Float) {
        // To be implemented
    }

    override fun setCustomInfoWindowViewFactory(factory: OmhInfoWindowViewFactory?) {
        // To be implemented
    }

    override fun setCustomInfoWindowContentsViewFactory(factory: OmhInfoWindowViewFactory?) {
        // To be implemented
    }
}
