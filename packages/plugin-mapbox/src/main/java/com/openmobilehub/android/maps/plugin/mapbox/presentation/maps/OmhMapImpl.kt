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
import com.openmobilehub.android.maps.plugin.mapbox.extensions.addOmhMarker
import com.openmobilehub.android.maps.plugin.mapbox.extensions.plus
import com.openmobilehub.android.maps.plugin.mapbox.presentation.interfaces.IDraggable
import com.openmobilehub.android.maps.plugin.mapbox.utils.Constants
import com.openmobilehub.android.maps.plugin.mapbox.utils.CoordinateConverter
import com.openmobilehub.android.maps.plugin.mapbox.utils.DimensionConverter
import com.openmobilehub.android.maps.plugin.mapbox.utils.JSONUtil
import com.openmobilehub.android.maps.plugin.mapbox.utils.TimestampHelper
import com.openmobilehub.android.maps.plugin.mapbox.utils.commonLogger
import kotlin.math.pow
import kotlin.math.sqrt

fun ScreenCoordinate.distanceTo(other: ScreenCoordinate): Double {
    return sqrt(
        (other.x - x).pow(2.0) + (other.y - y).pow(2.0)
    )
}

@SuppressWarnings("TooManyFunctions")
internal class OmhMapImpl(
    @SuppressWarnings("UnusedPrivateMember")
    private val mapView: MapView,
    private val context: Context,
    private val myLocationIcon: ImageView = MyLocationIcon(context),
    private val logger: Logger = commonLogger
) : OmhMap {
    /**
     * This flag is used to prevent the onCameraMoveStarted listener from being called multiple times
     */
    private var isCameraMoving = false

    private var isMyLocationIconAdded = false

    private var onMyLocationButtonClickListener: OmhOnMyLocationButtonClickListener? = null
    private var markerClickListener: OmhOnMarkerClickListener? = null
    private var markerDragListener: OmhOnMarkerDragListener? = null

    private val markers = mutableMapOf<String, OmhMarker>()
    private var currentlyDraggedEntity: Any? = null
    private var mapDragTouchStartTimestamp: Long? = null
    private var mapTouchStartPoint: ScreenCoordinate? = null

    private var style: Style? = null

    init {
        setupMapViewUIControls()
        addPendingMapElements()
        setupTouchInteractionListeners()
    }

    private fun addPendingMapElements() {
        mapView.mapboxMap.loadStyle(Style.STANDARD) { safeStyle ->
            synchronized(this) {
                this.style = safeStyle
                markers.values.forEach { omhMarker ->
                    // re-apply the icons now, since they can be added to the map for real
                    if (omhMarker is OmhMarkerImpl) {
                        omhMarker.applyBufferedProperties(safeStyle)
                    }
                }
            }
        }
    }

    override val providerName: String
        get() = Constants.PROVIDER_NAME

    override fun addMarker(options: OmhMarkerOptions): OmhMarker {
        synchronized(this) {
            val (omhMarker, _, layer) = options.addOmhMarker(mapView)

            style?.let { safeStyle ->
                omhMarker.applyBufferedProperties(safeStyle)
            }

            markers[layer.layerId] = omhMarker

            return omhMarker
        }
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

    private fun handleDragStart(omhCoordinate: OmhCoordinate) {
        when (currentlyDraggedEntity) {
            is OmhMarkerImpl -> {
                val omhMarker = currentlyDraggedEntity as OmhMarkerImpl
                omhMarker.setPosition(omhCoordinate)
                markerDragListener?.onMarkerDragStart(omhMarker)
            }

            else -> {
                // Noop
            }
        }
    }

    private fun handleDragContinuation(omhCoordinate: OmhCoordinate) {
        when (currentlyDraggedEntity) {
            is OmhMarkerImpl -> {
                val omhMarker = currentlyDraggedEntity as OmhMarkerImpl
                omhMarker.setPosition(omhCoordinate)
                markerDragListener?.onMarkerDrag(omhMarker)
            }

            else -> {
                // Noop
            }
        }
    }

    private fun handleDragEnd(omhCoordinate: OmhCoordinate) {
        when (currentlyDraggedEntity) {
            is OmhMarkerImpl -> {
                val omhMarker = currentlyDraggedEntity as OmhMarkerImpl
                omhMarker.setPosition(omhCoordinate)
                markerDragListener?.onMarkerDragEnd(omhMarker)
            }

            else -> {
                // Noop
            }
        }
    }

    /** Returns the [IDraggable] entity nearby the given coordinates on screen.
     *
     * @param pointOnScreen The [com.mapbox.maps.ScreenCoordinate] nearby which to look for an [IDraggable].
     *
     * @return The [IDraggable] entity, if found.
     * */
    private fun findDraggableEntity(pointOnScreen: ScreenCoordinate): IDraggable? {
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
        for (marker in markers.values) {
            val markerPositionOnScreen = mapView.mapboxMap.pixelForCoordinate(
                CoordinateConverter.convertToPoint(marker.getPosition())
            ) + (marker as OmhMarkerImpl).getHandleOffset()
            val distancePx = markerPositionOnScreen.distanceTo(pointOnScreen)

            if (distancePx <= hitRadius && distancePx < leastDistancePx) {
                hit = marker
                leastDistancePx = distancePx
            }
        }

        return hit
    }

    @SuppressLint("ClickableViewAccessibility")
    @SuppressWarnings("LongMethod")
    private fun setupTouchInteractionListeners() {
        mapView.setOnTouchListener { _, event ->
            val screenCoordinates = ScreenCoordinate(event.x.toDouble(), event.y.toDouble())
            val point = mapView.mapboxMap.coordinateForPixel(screenCoordinates)

            val action = event.actionMasked
            val omhCoordinate = CoordinateConverter.convertToOmhCoordinate(point)

            if (action in Constants.ACTIVE_MOTION_EVENTS) {
                if (currentlyDraggedEntity === null) {
                    // drag is not occurring at the moment

                    if (mapDragTouchStartTimestamp === null) {
                        // start counting time to check whether this will actually be a drag
                        // don't consume the event this time, as it may be a click

                        mapDragTouchStartTimestamp = TimestampHelper.getNow()
                        mapTouchStartPoint = screenCoordinates
                    } else {
                        // first, check if we are still at the same point - otherwise, reset the timer
                        // allow a tiny tolerance
                        val distancePx = mapTouchStartPoint!!.distanceTo(screenCoordinates)
                        if (distancePx > Constants.MAP_TOUCH_SAME_COORDINATES_THRESHOLD_PX) {
                            mapDragTouchStartTimestamp = TimestampHelper.getNow()
                            mapTouchStartPoint = screenCoordinates
                        } else {
                            val deltaTime =
                                TimestampHelper.getNow() - mapDragTouchStartTimestamp!!

                            if (deltaTime > Constants.MAP_TOUCH_DRAG_TOUCHDOWN_THRESHOLD_MS) {
                                // the touch interaction time allows for treating this as a drag, if applicable
                                val draggableEntity = findDraggableEntity(screenCoordinates)
                                if (draggableEntity !== null && draggableEntity.getDraggable()) {
                                    // drag start
                                    currentlyDraggedEntity = draggableEntity

                                    handleDragStart(omhCoordinate)
                                } else {
                                    // drag end (draggable entity ceased to be draggable or to exist)
                                    handleDragEnd(omhCoordinate)

                                    currentlyDraggedEntity = null
                                }

                                return@setOnTouchListener true
                            }
                        }
                    }
                } else {
                    // drag continuation
                    handleDragContinuation(omhCoordinate)

                    return@setOnTouchListener true
                }
            } else {
                if (currentlyDraggedEntity !== null) {
                    // drag end (user finished interaction)
                    mapDragTouchStartTimestamp = null

                    handleDragEnd(omhCoordinate)

                    currentlyDraggedEntity = null

                    return@setOnTouchListener true
                } else if (mapTouchStartPoint !== null) {
                    // first, check if we are still at the same point - otherwise, discard the click
                    // allow a tiny tolerance
                    if (
                        mapTouchStartPoint!!.distanceTo(screenCoordinates)
                        <= Constants.MAP_TOUCH_SAME_COORDINATES_THRESHOLD_PX
                    ) {
                        // click happened
                        val entity = findDraggableEntity(screenCoordinates)
                        if (entity is OmhMarkerImpl) {
                            if (entity.getClickable()) {
                                markerClickListener?.onMarkerClick(entity)
                            }
                        }

                        return@setOnTouchListener true
                    }
                }
            }

            false
        }
    }

    override fun setOnMarkerClickListener(listener: OmhOnMarkerClickListener) {
        markerClickListener = listener
    }

    override fun setOnMarkerDragListener(listener: OmhOnMarkerDragListener) {
        markerDragListener = listener
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
