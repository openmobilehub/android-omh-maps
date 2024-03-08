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
import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.widget.ImageView
import androidx.annotation.RequiresPermission
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.RenderedQueryGeometry
import com.mapbox.maps.RenderedQueryOptions
import com.mapbox.maps.ScreenCoordinate
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.compass.compass
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.plugin.scalebar.scalebar
import com.mapbox.maps.plugin.viewport.data.FollowPuckViewportStateBearing
import com.mapbox.maps.plugin.viewport.data.FollowPuckViewportStateOptions
import com.mapbox.maps.plugin.viewport.state.FollowPuckViewportState
import com.mapbox.maps.plugin.viewport.viewport
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhMap
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhMapLoadedCallback
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhMarker
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhOnCameraIdleListener
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhOnCameraMoveStartedListener
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
import com.openmobilehub.android.maps.core.utils.logging.Logger
import com.openmobilehub.android.maps.plugin.mapbox.extensions.distanceTo
import com.openmobilehub.android.maps.plugin.mapbox.extensions.plus
import com.openmobilehub.android.maps.plugin.mapbox.presentation.interfaces.IDraggable
import com.openmobilehub.android.maps.plugin.mapbox.presentation.interfaces.IMapDragManagerDelegate
import com.openmobilehub.android.maps.plugin.mapbox.presentation.interfaces.IMapMarkerManagerDelegate
import com.openmobilehub.android.maps.plugin.mapbox.presentation.maps.managers.MapDragManager
import com.openmobilehub.android.maps.plugin.mapbox.presentation.maps.managers.MapMarkerManager
import com.openmobilehub.android.maps.plugin.mapbox.utils.Constants
import com.openmobilehub.android.maps.plugin.mapbox.utils.CoordinateConverter
import com.openmobilehub.android.maps.plugin.mapbox.utils.DimensionConverter
import com.openmobilehub.android.maps.plugin.mapbox.utils.JSONUtil
import com.openmobilehub.android.maps.plugin.mapbox.utils.commonLogger

@SuppressWarnings("TooManyFunctions")
internal class OmhMapImpl(
    @SuppressWarnings("UnusedPrivateMember")
    override val mapView: MapView,
    private val context: Context,
    private val myLocationIcon: ImageView = MyLocationIcon(context),
    private val logger: Logger = commonLogger
) : OmhMap, IMapDragManagerDelegate, IMapMarkerManagerDelegate {
    /**
     * This flag is used to prevent the onCameraMoveStarted listener from being called multiple times
     */
    private var isCameraMoving = false

    private var isMyLocationIconAdded = false

    private var onMyLocationButtonClickListener: OmhOnMyLocationButtonClickListener? = null

    private var mapDragManager = MapDragManager(this)

    private var markerManager = MapMarkerManager(this)

    private var style: Style? = null

    init {
        setupMapViewUIControls()
        addQueuedMapElements()
        setupTouchInteractionListeners()
    }

    private fun addQueuedMapElements() {
        mapView.mapboxMap.loadStyle(Style.STANDARD) { safeStyle ->
            synchronized(this) {
                this.style = safeStyle
                markerManager.addQueuedElementsToStyle(safeStyle)
            }
        }
    }

    override val providerName: String
        get() = Constants.PROVIDER_NAME

    override fun addMarker(options: OmhMarkerOptions): OmhMarker {
        return markerManager.addMarker(options, style)
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
        mapView.mapboxMap.cameraState.center.let {
            return CoordinateConverter.convertToOmhCoordinate(it)
        }
    }

    override fun moveCamera(coordinate: OmhCoordinate, zoomLevel: Float) {
        val cameraPosition = CameraOptions.Builder()
            .zoom(zoomLevel.toDouble())
            .center(CoordinateConverter.convertToPoint(coordinate))
            .build()

        mapView.mapboxMap.setCamera(cameraPosition)
    }

    override fun setZoomGesturesEnabled(enableZoomGestures: Boolean) {
        mapView.gestures.apply {
            pinchToZoomEnabled = enableZoomGestures
            doubleTapToZoomInEnabled = enableZoomGestures
        }
    }

    override fun setRotateGesturesEnabled(enableRotateGestures: Boolean) {
        mapView.gestures.apply {
            rotateEnabled = enableRotateGestures
        }
    }

    override fun snapshot(omhSnapshotReadyCallback: OmhSnapshotReadyCallback) {
        mapView.snapshot {
            omhSnapshotReadyCallback.onSnapshotReady(it)
        }
    }

    @RequiresPermission(anyOf = [ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION])
    override fun setMyLocationEnabled(enable: Boolean) {
        mapView.location.enabled = enable

        if (enable) {
            if (!isMyLocationIconAdded) {
                isMyLocationIconAdded = true
                updateMyLocationIconClickListener()
                mapView.addView(myLocationIcon)
            }
        } else {
            mapView.removeView(myLocationIcon)
            isMyLocationIconAdded = false
        }
    }

    override fun isMyLocationEnabled(): Boolean {
        return mapView.location.enabled
    }

    override fun setMyLocationButtonClickListener(
        omhOnMyLocationButtonClickListener: OmhOnMyLocationButtonClickListener
    ) {
        onMyLocationButtonClickListener = omhOnMyLocationButtonClickListener
        updateMyLocationIconClickListener()
    }

    override fun setOnCameraMoveStartedListener(listener: OmhOnCameraMoveStartedListener) {
        mapView.mapboxMap.subscribeCameraChanged {
            if (!isCameraMoving) {
                listener.onCameraMoveStarted(null)
            }

            isCameraMoving = true
        }
    }

    override fun setOnCameraIdleListener(listener: OmhOnCameraIdleListener) {
        mapView.mapboxMap.subscribeMapIdle {
            isCameraMoving = false
            listener.onCameraIdle()
        }
    }

    override fun setOnMapLoadedCallback(callback: OmhMapLoadedCallback?) {
        mapView.mapboxMap.subscribeMapLoaded {
            callback?.onMapLoaded()
        }
    }

    override fun handleDragStart(omhCoordinate: OmhCoordinate, draggedEntity: Any) {
        when (draggedEntity) {
            is OmhMarkerImpl -> markerManager.markerDragStart(omhCoordinate, draggedEntity)
            else -> {} // Noop
        }
    }

    override fun handleDragContinuation(omhCoordinate: OmhCoordinate, draggedEntity: Any) {
        when (draggedEntity) {
            is OmhMarkerImpl -> markerManager.markerDrag(omhCoordinate, draggedEntity)
            else -> {} // Noop
        }
    }

    override fun handleDragEnd(omhCoordinate: OmhCoordinate, draggedEntity: Any) {
        when (draggedEntity) {
            is OmhMarkerImpl -> markerManager.markerDragEnd(omhCoordinate, draggedEntity)
            else -> {} // Noop
        }
    }

    override fun findDraggableEntity(screenCoordinate: ScreenCoordinate): IDraggable? {
        val minScreenDimension = mapView.context.resources.displayMetrics.widthPixels.coerceAtMost(
            mapView.context.resources.displayMetrics.heightPixels
        )
        val hitRadius =
            (minScreenDimension.toDouble() * Constants.MAP_TOUCH_HIT_RADIUS_PERCENT_OF_SCREEN_DIM)
                .coerceIn(
                    Constants.MAP_TOUCH_HIT_RADIUS_MIN_PX..Constants.MAP_TOUCH_HIT_RADIUS_MAX_PX
                )
        var hit: IDraggable? = null
        var leastDistancePx = Double.MAX_VALUE
        for (marker in markerManager.markers.values) {
            val markerPositionOnScreen = mapView.mapboxMap.pixelForCoordinate(
                CoordinateConverter.convertToPoint(marker.getPosition())
            ) + (marker as OmhMarkerImpl).getHandleOffset()
            val distancePx = markerPositionOnScreen.distanceTo(screenCoordinate)

            if (distancePx <= hitRadius && distancePx < leastDistancePx) {
                hit = marker
                leastDistancePx = distancePx
            }
        }

        return hit
    }

    override fun queryRenderedLayerIdAt(
        screenCoordinate: ScreenCoordinate,
        callback: (layerId: String?) -> Unit
    ) {
        mapView.mapboxMap.queryRenderedFeatures(
            RenderedQueryGeometry(screenCoordinate),
            RenderedQueryOptions(null, null)
        ) {
            val layerId = it.value?.getOrNull(0)?.layers?.getOrNull(0)

            callback(layerId)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupTouchInteractionListeners() {
        mapView.gestures.addOnMapClickListener { point ->
            markerManager.handleMapClick(point)
        }

        mapView.setOnTouchListener { _, event ->
            val screenCoordinates = ScreenCoordinate(event.x.toDouble(), event.y.toDouble())
            val point = mapView.mapboxMap.coordinateForPixel(screenCoordinates)

            val dragManagerConsumedEvent = mapDragManager.handleOnTouch(
                event.actionMasked,
                screenCoordinates,
                point
            )

            dragManagerConsumedEvent || mapView.gestures.onTouchEvent(event)
        }
    }

    override fun setOnMarkerClickListener(listener: OmhOnMarkerClickListener) {
        markerManager.setMarkerClickListener(listener)
    }

    override fun setOnMarkerDragListener(listener: OmhOnMarkerDragListener) {
        markerManager.setMarkerDragListener(listener)
    }

    override fun setOnPolylineClickListener(listener: OmhOnPolylineClickListener) {
        // To be implemented
    }

    override fun setOnPolygonClickListener(listener: OmhOnPolygonClickListener) {
        // To be implemented
    }

    override fun setMapStyle(json: Int?) {
        if (json == null) {
            mapView.mapboxMap.loadStyle(Style.STANDARD)
            return
        }

        val styleJSONString = JSONUtil.convertJSONToString(context, json, logger)

        val onStyleLoadedCallback = { style: Style ->
            if (!style.isValid()) {
                logger.logWarning("Failed to apply custom map style. Check logs from Mapbox SDK.")
            }
        }

        styleJSONString?.let { jsonString ->
            mapView.mapboxMap.loadStyle(jsonString, onStyleLoadedCallback)
        } ?: logger.logError("Failed to load style from resource with id: $json")
    }

    private fun setupMapViewUIControls() {
        // To have parity with Google Maps
        val iconMargin =
            DimensionConverter.pxFromDp(context, Constants.MAPBOX_ICON_MARGIN).toFloat()
        mapView.compass.marginLeft = iconMargin
        mapView.compass.marginTop = iconMargin
        mapView.compass.position = Gravity.TOP or Gravity.START

        mapView.scalebar.enabled = false
    }

    private fun updateMyLocationIconClickListener() {
        myLocationIcon.setOnClickListener {
            onMyLocationButtonClickListener?.onMyLocationButtonClick()
            centerMapOnUserLocation()
        }
    }

    private fun centerMapOnUserLocation() {
        val viewportPlugin = mapView.viewport

        val cameraState = mapView.mapboxMap.cameraState

        val followPuckViewportState: FollowPuckViewportState =
            viewportPlugin.makeFollowPuckViewportState(
                FollowPuckViewportStateOptions.Builder()
                    .pitch(cameraState.pitch)
                    .zoom(cameraState.zoom)
                    .bearing(FollowPuckViewportStateBearing.Constant(cameraState.bearing))
                    .padding(cameraState.padding)
                    .build()
            )

        viewportPlugin.transitionTo(followPuckViewportState)
    }
}
