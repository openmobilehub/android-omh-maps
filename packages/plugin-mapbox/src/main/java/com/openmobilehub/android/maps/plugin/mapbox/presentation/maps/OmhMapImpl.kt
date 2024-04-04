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
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.RenderedQueryGeometry
import com.mapbox.maps.RenderedQueryOptions
import com.mapbox.maps.ScreenCoordinate
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.animation.camera
import com.mapbox.maps.plugin.compass.compass
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.plugin.scalebar.scalebar
import com.mapbox.maps.plugin.viewport.data.FollowPuckViewportStateBearing
import com.mapbox.maps.plugin.viewport.data.FollowPuckViewportStateOptions
import com.mapbox.maps.plugin.viewport.state.FollowPuckViewportState
import com.mapbox.maps.plugin.viewport.viewport
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
import com.openmobilehub.android.maps.core.utils.ScreenUnitConverter
import com.openmobilehub.android.maps.core.utils.cartesian.BoundingBox2D
import com.openmobilehub.android.maps.core.utils.logging.Logger
import com.openmobilehub.android.maps.plugin.mapbox.extensions.toPoint2D
import com.openmobilehub.android.maps.plugin.mapbox.presentation.interfaces.IMapDragManagerDelegate
import com.openmobilehub.android.maps.plugin.mapbox.presentation.interfaces.IOmhInfoWindowMapViewDelegate
import com.openmobilehub.android.maps.plugin.mapbox.presentation.interfaces.ITouchInteractable
import com.openmobilehub.android.maps.plugin.mapbox.presentation.maps.managers.MapMarkerManager
import com.openmobilehub.android.maps.plugin.mapbox.presentation.maps.managers.MapTouchInteractionManager
import com.openmobilehub.android.maps.plugin.mapbox.presentation.maps.managers.PolygonManager
import com.openmobilehub.android.maps.plugin.mapbox.presentation.maps.managers.PolylineManager
import com.openmobilehub.android.maps.plugin.mapbox.utils.Constants
import com.openmobilehub.android.maps.plugin.mapbox.utils.CoordinateConverter
import com.openmobilehub.android.maps.plugin.mapbox.utils.JSONUtil
import com.openmobilehub.android.maps.plugin.mapbox.utils.commonLogger

@SuppressWarnings("TooManyFunctions", "LongParameterList")
class OmhMapImpl(
    val mapView: MapView,
    private val context: Context,
    private val myLocationIcon: ImageView = MyLocationIcon(context),
    private var scaleFactor: Float = 1.0f,
    private val polylineManager: PolylineManager = PolylineManager(mapView, scaleFactor),
    private val polygonManager: PolygonManager = PolygonManager(mapView, scaleFactor),
    private val logger: Logger = commonLogger,
) : OmhMap, IMapDragManagerDelegate, IOmhInfoWindowMapViewDelegate {
    /**
     * This flag is used to prevent the onCameraMoveStarted listener from being called multiple times
     */
    private var isCameraMoving = false

    private var isMyLocationIconAdded = false

    private var style: Style? = null

    private var onMyLocationButtonClickListener: OmhOnMyLocationButtonClickListener? = null

    internal val mapMarkerManager = MapMarkerManager(mapView.context, this)
    private val mapTouchInteractionManager = MapTouchInteractionManager(this, mapMarkerManager)

    init {
        setupMapViewUIControls()
        loadStyle()
        setupTouchInteractionListeners()
        setupZoomListenerForInfoWindows()
    }

    private fun setupZoomListenerForInfoWindows() {
        mapView.camera.addCameraZoomChangeListener {
            mapMarkerManager.updateAllInfoWindowsPositions()
        }
    }

    private fun loadStyle() {
        mapView.mapboxMap.loadStyle(Style.STANDARD) { safeStyle ->
            synchronized(this) {
                this.style = safeStyle
                mapMarkerManager.onStyleLoaded(safeStyle)
                polygonManager.onStyleLoaded(safeStyle)
                polylineManager.onStyleLoaded(safeStyle)
            }
        }
    }

    override val providerName: String
        get() = Constants.PROVIDER_NAME

    override fun addMarker(options: OmhMarkerOptions): OmhMarker {
        return mapMarkerManager.addMarker(options, style)
    }

    override fun addPolyline(options: OmhPolylineOptions): OmhPolyline {
        return polylineManager.addPolyline(options, style)
    }

    override fun addPolygon(options: OmhPolygonOptions): OmhPolygon {
        return polygonManager.addPolygon(options, style)
    }

    override fun getCameraPositionCoordinate(): OmhCoordinate {
        mapView.mapboxMap.cameraState.center.let {
            return CoordinateConverter.convertToOmhCoordinate(it)
        }
    }

    override fun moveCamera(coordinate: OmhCoordinate, zoomLevel: Float) {
        val cameraPosition = CameraOptions.Builder().zoom(zoomLevel.toDouble())
            .center(CoordinateConverter.convertToPoint(coordinate)).build()

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

    override fun handleDragStart(omhCoordinate: OmhCoordinate, draggedEntity: ITouchInteractable) {
        when (draggedEntity) {
            is OmhMarkerImpl -> mapMarkerManager.markerDragStart(omhCoordinate, draggedEntity)
            else -> {} // Noop
        }
    }

    override fun handleDragMove(omhCoordinate: OmhCoordinate, draggedEntity: ITouchInteractable) {
        when (draggedEntity) {
            is OmhMarkerImpl -> mapMarkerManager.markerDrag(omhCoordinate, draggedEntity)
            else -> {} // Noop
        }
    }

    override fun handleDragEnd(omhCoordinate: OmhCoordinate, draggedEntity: ITouchInteractable) {
        when (draggedEntity) {
            is OmhMarkerImpl -> mapMarkerManager.markerDragEnd(omhCoordinate, draggedEntity)
            else -> {} // Noop
        }
    }

    @SuppressWarnings("LongMethod")
    override fun findInteractableEntities(
        screenCoordinate: ScreenCoordinate,
        validator: ((predicate: ITouchInteractable) -> Boolean)?
    ): List<ITouchInteractable> {
        val screenCoordinatePoint2D = screenCoordinate.toPoint2D()
        val minScreenDimension = mapView.context.resources.displayMetrics.widthPixels.coerceAtMost(
            mapView.context.resources.displayMetrics.heightPixels
        )
        val hitRadius =
            (minScreenDimension.toDouble() * Constants.MAP_TOUCH_HIT_RADIUS_PERCENT_OF_SCREEN_DIM).coerceIn(
                Constants.MAP_TOUCH_HIT_RADIUS_MIN_PX..Constants.MAP_TOUCH_HIT_RADIUS_MAX_PX
            )
        val hits = mutableListOf<ITouchInteractable>()

        // try if an info window was hit
        for (infoWindow in mapMarkerManager.infoWindows.values.reversed()) {
            if (validator?.invoke(infoWindow) == false) continue

            val markerPositionOnScreen = mapView.mapboxMap.pixelForCoordinate(
                CoordinateConverter.convertToPoint(infoWindow.omhMarker.getPosition())
            ).toPoint2D() + infoWindow.omhMarker.getHandleTopOffset()
            val centerPositionOnScreen = markerPositionOnScreen + infoWindow.getHandleCenterOffset()

            val iwBoundingBox = BoundingBox2D(
                centerPoint = centerPositionOnScreen,
                width = infoWindow.iwBitmapWidth.toDouble(),
                height = infoWindow.iwBitmapHeight.toDouble(),
                hitBorder = hitRadius
            )

            if (iwBoundingBox.contains(screenCoordinatePoint2D)) {
                hits.add(infoWindow)
            }
        }

        // try if a marker was hit
        for (marker in mapMarkerManager.markers.values.reversed()) {
            if (validator?.invoke(marker) == false) continue

            val markerCenterPositionOnScreen = mapView.mapboxMap.pixelForCoordinate(
                CoordinateConverter.convertToPoint(marker.getPosition())
            ).toPoint2D() + marker.getHandleCenterOffset()

            val markerBoundingBox = BoundingBox2D(
                centerPoint = markerCenterPositionOnScreen,
                width = marker.iconWidth.toDouble(),
                height = marker.iconHeight.toDouble(),
                hitBorder = hitRadius
            )

            if (markerBoundingBox.contains(screenCoordinatePoint2D)) {
                hits.add(marker)
            }
        }

        return hits
    }

    /**
     * Queries ID of the layer managed by the [MapMarkerManager] at the given screen
     * coordinate [screenCoordinate] and returns the result through [callback].
     *
     * @param screenCoordinate The screen coordinate to query the layer ID at.
     * @param callback The callback to return the layer ID through. Returns `true` if there was
     * a hit (that does not necessarily have to end with the consummation of any event)
     * or `false` otherwise. A `false` means that the hit was not used and
     * the callback shall be called sequentially with other layer IDs found at the coordinates,
     * unless a `true` is returned or there are no more layers. The callback's parameter is `null`
     * if no layer has been found or a pair of: layer ID and layer type otherwise.
     */
    private fun queryRenderedLayerIdsAt(
        screenCoordinate: ScreenCoordinate,
        callback: (layerInfo: Pair<String, String>?) -> Boolean
    ) {
        mapView.mapboxMap.queryRenderedFeatures(
            RenderedQueryGeometry(screenCoordinate),
            RenderedQueryOptions(
                null,
                null
            )
        ) {
            val hit = it.value?.getOrNull(0)
            val layerIds = hit?.layers
            val layerType = hit?.queriedFeature?.feature?.geometry()?.type()

            if (layerIds === null || layerType === null) {
                callback(null)
            } else {
                for (layerId in layerIds) {
                    if (callback(layerId to layerType)) break
                }
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupTouchInteractionListeners() {
        mapView.gestures.addOnMapClickListener { point ->
            val screenCoordinate = mapView.mapboxMap.pixelForCoordinate(point)

            queryRenderedLayerIdsAt(screenCoordinate) lambda@{ layerInfo ->
                if (layerInfo === null) return@lambda false

                val (layerId, layerType) = layerInfo

                when (layerType) {
                    Constants.MARKER_OR_INFO_WINDOW_LAYER_TYPE -> mapMarkerManager.maybeHandleClick(
                        layerId
                    ) { eventConsumed ->
                        if (eventConsumed) mapTouchInteractionManager.resetDragState()
                    }

                    Constants.POLYLINE_LAYER_TYPE,
                    Constants.POLYLINE_LAYER_TYPE_ALTERNATIVE -> polylineManager.maybeHandleClick(
                        layerId
                    )

                    Constants.POLYGON_LAYER_TYPE,
                    Constants.POLYGON_LAYER_TYPE_ALTERNATIVE -> polygonManager.maybeHandleClick(
                        layerId,
                    )

                    else -> false // Noop
                }
            }

            true
        }

        mapView.setOnTouchListener touchListener@{ _, event ->
            val screenCoordinates = ScreenCoordinate(event.x.toDouble(), event.y.toDouble())
            val point = mapView.mapboxMap.coordinateForPixel(screenCoordinates)

            mapTouchInteractionManager.handleOnTouch(
                event.actionMasked,
                screenCoordinates,
                point
            ) || mapView.gestures.onTouchEvent(event)
        }
    }

    override fun setOnMarkerClickListener(listener: OmhOnMarkerClickListener) {
        mapMarkerManager.setMarkerClickListener(listener)
    }

    override fun setOnMarkerDragListener(listener: OmhOnMarkerDragListener) {
        mapMarkerManager.setMarkerDragListener(listener)
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

    override fun setScaleFactor(scaleFactor: Float) {
        this.scaleFactor = scaleFactor
    }

    private fun setupMapViewUIControls() {
        // To have parity with Google Maps
        val iconMargin = ScreenUnitConverter.dpToPx(Constants.MAPBOX_ICON_MARGIN.toFloat(), context)
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
                FollowPuckViewportStateOptions.Builder().pitch(cameraState.pitch)
                    .zoom(cameraState.zoom)
                    .bearing(FollowPuckViewportStateBearing.Constant(cameraState.bearing))
                    .padding(cameraState.padding).build()
            )

        viewportPlugin.transitionTo(followPuckViewportState)
    }

    override fun setCustomInfoWindowViewFactory(factory: OmhInfoWindowViewFactory?) {
        mapMarkerManager.setCustomInfoWindowViewFactory(factory)
    }

    override fun setCustomInfoWindowContentsViewFactory(factory: OmhInfoWindowViewFactory?) {
        mapMarkerManager.setCustomInfoWindowContentsViewFactory(factory)
    }

    override fun getMapWidth(): Int {
        return mapView.width
    }

    override fun getMapHeight(): Int {
        return mapView.height
    }

    override fun coordinateForPixel(screenCoordinate: ScreenCoordinate): Point {
        return mapView.mapboxMap.coordinateForPixel(screenCoordinate)
    }

    override fun pixelForCoordinate(point: Point): ScreenCoordinate {
        return mapView.mapboxMap.pixelForCoordinate(point)
    }
}
