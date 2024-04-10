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
import com.azure.android.maps.control.ImageManager
import com.azure.android.maps.control.LayerManager
import com.azure.android.maps.control.MapControl
import com.azure.android.maps.control.PopupManager
import com.azure.android.maps.control.SourceManager
import com.azure.android.maps.control.events.OnFeatureClick
import com.mapbox.geojson.Feature
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
import com.openmobilehub.android.maps.plugin.azuremaps.presentation.interfaces.AzureMapInterface
import com.openmobilehub.android.maps.plugin.azuremaps.presentation.maps.managers.CameraManager
import com.openmobilehub.android.maps.plugin.azuremaps.presentation.maps.managers.MapMarkerManager
import com.openmobilehub.android.maps.plugin.azuremaps.presentation.maps.managers.MyLocationManager
import com.openmobilehub.android.maps.plugin.azuremaps.presentation.maps.managers.PolygonManager
import com.openmobilehub.android.maps.plugin.azuremaps.presentation.maps.managers.PolylineManager
import com.openmobilehub.android.maps.plugin.azuremaps.utils.Constants
import com.openmobilehub.android.maps.plugin.azuremaps.utils.mapLogger

@SuppressWarnings("TooManyFunctions", "UnusedPrivateMember", "LongParameterList")
internal class OmhMapImpl(
    private val context: Context,
    private val mapControl: MapControl,
    val mapView: AzureMap,
    private val cameraManager: CameraManager = CameraManager(mapView),
    private val myLocationManager: MyLocationManager = MyLocationManager(
        context,
        mapControl,
        mapView
    ),
    private val logger: UnsupportedFeatureLogger = mapLogger,
    bRunningInTest: Boolean = false
) : OmhMap {

    private val azureMapInterface = object : AzureMapInterface {
        override val sources: SourceManager
            get() = mapView.sources
        override val layers: LayerManager
            get() = mapView.layers
        override val images: ImageManager
            get() = mapView.images
        override val popups: PopupManager
            get() = mapView.popups
    }
    private val polylineManager = PolylineManager(azureMapInterface)
    private val polygonManager = PolygonManager(azureMapInterface)
    private val mapMarkerManager = MapMarkerManager(context, azureMapInterface)

    private var _scaleFactor: Float = 1.0f
        set(value) {
            field = value
            polylineManager.scaleFactor = value
            polygonManager.scaleFactor = value
        }

    override val providerName: String
        get() = Constants.PROVIDER_NAME

    init {
        if (!bRunningInTest) {
            setupTouchInteractionListeners()
        }
    }

    private fun setupTouchInteractionListeners() {
        mapView.events.add(object : OnFeatureClick {
            override fun onFeatureClick(features: MutableList<Feature>?): Boolean {
                for (feature in features ?: listOf()) {
                    if (featureHandleClick(feature)) {
                        return true
                    }
                }
                return false
            }
        })
    }

    @SuppressWarnings("ReturnCount")
    private fun featureHandleClick(feature: Feature): Boolean {
        if (feature.hasProperty(Constants.MARKER_FEATURE_UUID_BINDING)) {
            val markerId = feature.getStringProperty(Constants.MARKER_FEATURE_UUID_BINDING)
            return mapMarkerManager.maybeHandleClick(markerId)
        } else if (feature.hasProperty(Constants.POLYLINE_FEATURE_UUID_BINDING)) {
            val polylineId = feature.getStringProperty(Constants.POLYLINE_FEATURE_UUID_BINDING)
            return polylineManager.maybeHandleClick(polylineId)
        } else if (feature.hasProperty(Constants.POLYGON_FEATURE_UUID_BINDING)) {
            val polygonId = feature.getStringProperty(Constants.POLYGON_FEATURE_UUID_BINDING)
            return polygonManager.maybeHandleClick(polygonId)
        }

        return false
    }

    override fun addMarker(options: OmhMarkerOptions): OmhMarker {
        return mapMarkerManager.addMarker(options)
    }

    override fun addPolyline(options: OmhPolylineOptions): OmhPolyline {
        return polylineManager.addPolyline(options)
    }

    override fun addPolygon(options: OmhPolygonOptions): OmhPolygon {
        return polygonManager.addPolygon(options)
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
        mapMarkerManager.setMarkerClickListener(listener)
    }

    override fun setOnMarkerDragListener(listener: OmhOnMarkerDragListener) {
        logger.logSetterNotSupported("markerDragListener")
    }

    override fun setOnInfoWindowOpenStatusChangeListener(listener: OmhOnInfoWindowOpenStatusChangeListener) {
        mapMarkerManager.setInfoWindowOpenStatusChangeListener(listener)
    }

    override fun setOnInfoWindowClickListener(listener: OmhOnInfoWindowClickListener) {
        mapMarkerManager.setOnInfoWindowClickListener(listener)
    }

    override fun setOnInfoWindowLongClickListener(listener: OmhOnInfoWindowLongClickListener) {
        mapMarkerManager.setOnInfoWindowLongClickListener(listener)
    }

    override fun setOnPolylineClickListener(listener: OmhOnPolylineClickListener) {
        polylineManager.clickListener = listener
    }

    override fun setOnPolygonClickListener(listener: OmhOnPolygonClickListener) {
        polygonManager.clickListener = listener
    }

    override fun setMapStyle(json: Int?) {
        logger.logSetterNotSupported("mapStyle")
    }

    override fun setScaleFactor(scaleFactor: Float) {
        _scaleFactor = scaleFactor
    }

    override fun setCustomInfoWindowViewFactory(factory: OmhInfoWindowViewFactory?) {
        mapMarkerManager.setCustomInfoWindowViewFactory(factory)
    }

    override fun setCustomInfoWindowContentsViewFactory(factory: OmhInfoWindowViewFactory?) {
        mapMarkerManager.setCustomInfoWindowContentsViewFactory(factory)
    }
}
