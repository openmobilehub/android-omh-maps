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

package com.openmobilehub.android.maps.plugin.openstreetmap.presentation.maps

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.location.LocationManager.FUSED_PROVIDER
import androidx.annotation.RequiresPermission
import androidx.core.content.ContextCompat
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
import com.openmobilehub.android.maps.plugin.openstreetmap.R
import com.openmobilehub.android.maps.plugin.openstreetmap.extensions.toGeoPoint
import com.openmobilehub.android.maps.plugin.openstreetmap.extensions.toMarkerOptions
import com.openmobilehub.android.maps.plugin.openstreetmap.extensions.toOmhCoordinate
import com.openmobilehub.android.maps.plugin.openstreetmap.extensions.toPolygonOptions
import com.openmobilehub.android.maps.plugin.openstreetmap.extensions.toPolylineOptions
import com.openmobilehub.android.maps.plugin.openstreetmap.utils.Constants
import com.openmobilehub.android.maps.plugin.openstreetmap.utils.Constants.DEFAULT_ZOOM_LEVEL
import com.openmobilehub.android.maps.plugin.openstreetmap.utils.MapListenerController
import com.openmobilehub.android.maps.plugin.openstreetmap.utils.MapTouchListener
import org.osmdroid.api.IGeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polygon
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

@SuppressWarnings("TooManyFunctions")
internal class OmhMapImpl(
    private val mapView: MapView,
    private val mapListenerController: MapListenerController
) : OmhMap {

    private var myLocationNewOverlay: MyLocationNewOverlay? = null
    private var myLocationIconOverlay: MyLocationIconOverlay? = null
    private val gestureOverlay = GestureOverlay()
    private var polylineClickListener: OmhOnPolylineClickListener? = null
    private var polygonClickListener: OmhOnPolygonClickListener? = null
    private var markerClickListener: OmhOnMarkerClickListener? = null
    private var markerDragListener: OmhOnMarkerDragListener? = null
    private var enableZoomGestures: Boolean = false
    private var enableRotateGestures: Boolean = false

    private val polylines = mutableMapOf<Polyline, OmhPolyline>()
    private val polygons = mutableMapOf<Polygon, OmhPolygon>()
    private val markers = mutableMapOf<Marker, OmhMarker>()

    init {
        mapView.addMapListener(mapListenerController)
        mapView.setOnTouchListener(MapTouchListener(mapListenerController))
        mapView.overlayManager.add(gestureOverlay)

        setZoomGesturesEnabled(true)
        setRotateGesturesEnabled(true)
    }

    override val providerName: String
        get() = Constants.PROVIDER_NAME

    private fun applyOnMarkerClickListener(marker: Marker, omhMarker: OmhMarker) {
        marker.setOnMarkerClickListener { _, _ ->
            if (omhMarker.getIsVisible() && omhMarker.getClickable()) {
                marker.showInfoWindow()
                return@setOnMarkerClickListener markerClickListener?.onMarkerClick(omhMarker)
                    ?: false
            }

            false
        }
    }

    private fun applyOnMarkerDragListener(marker: Marker, omhMarker: OmhMarker) {
        marker.setOnMarkerDragListener(object : Marker.OnMarkerDragListener {
            override fun onMarkerDrag(marker: Marker) {
                if (omhMarker.getIsVisible()) {
                    markerDragListener?.onMarkerDrag(omhMarker)
                }
            }

            override fun onMarkerDragEnd(marker: Marker) {
                if (omhMarker.getIsVisible()) {
                    markerDragListener?.onMarkerDragEnd(omhMarker)
                }
            }

            override fun onMarkerDragStart(marker: Marker) {
                if (omhMarker.getIsVisible()) {
                    markerDragListener?.onMarkerDragStart(omhMarker)
                }
            }
        })
    }

    override fun addMarker(options: OmhMarkerOptions): OmhMarker? {
        val marker = options.toMarkerOptions(mapView)
        val initiallyClickable = options.clickable

        val omhMarker = OmhMarkerImpl(marker, mapView, initiallyClickable)
        markers[marker] = omhMarker

        applyOnMarkerClickListener(marker, omhMarker)
        applyOnMarkerDragListener(marker, omhMarker)

        mapView.run {
            overlayManager.add(marker)
            postInvalidate()
        }

        return omhMarker
    }

    override fun addPolyline(options: OmhPolylineOptions): OmhPolyline? {
        val initiallyClickable = options.clickable ?: false

        val polyline = options.toPolylineOptions()
        val omhPolyline = OmhPolylineImpl(polyline, mapView, initiallyClickable)

        polylines[polyline] = omhPolyline
        polyline.setOnClickListener { _, _, _ ->
            if (omhPolyline.getClickable()) {
                polylineClickListener?.onPolylineClick(omhPolyline)
            }
            true
        }

        mapView.run {
            overlayManager.add(polyline)
            postInvalidate()
        }

        return omhPolyline
    }

    override fun addPolygon(options: OmhPolygonOptions): OmhPolygon? {
        val initiallyClickable = options.clickable ?: false

        val polygon = options.toPolygonOptions()
        val omhPolygon = OmhPolygonImpl(polygon, mapView, initiallyClickable)

        polygons[polygon] = omhPolygon
        polygon.setOnClickListener { _, _, _ ->
            if (omhPolygon.getClickable()) {
                polygonClickListener?.onPolygonClick(omhPolygon)
            }
            true
        }

        mapView.run {
            overlayManager.add(polygon)
            postInvalidate()
        }

        return omhPolygon
    }

    override fun getCameraPositionCoordinate(): OmhCoordinate {
        val centerPosition: IGeoPoint = mapView.mapCenter
        return centerPosition.toOmhCoordinate()
    }

    override fun moveCamera(coordinate: OmhCoordinate, zoomLevel: Float) {
        with(mapView.controller) {
            val geoPoint: IGeoPoint = coordinate.toGeoPoint()
            setZoom(zoomLevel.toDouble())
            setCenter(geoPoint)
        }
        mapView.postInvalidate()
    }

    private fun updateMultiTouchControlsSetting() {
        mapView.setMultiTouchControls(enableZoomGestures || enableRotateGestures)
    }

    override fun setZoomGesturesEnabled(enableZoomGestures: Boolean) {
        this.enableZoomGestures = enableZoomGestures

        gestureOverlay.setEnableZoomGestures(enableZoomGestures)
        if (!enableZoomGestures) {
            mapView.zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
        }

        updateMultiTouchControlsSetting()
    }

    override fun setRotateGesturesEnabled(enableRotateGestures: Boolean) {
        this.enableRotateGestures = enableRotateGestures

        val rotationGestureOverlay = RotationGestureOverlay(mapView)
        rotationGestureOverlay.isEnabled = true
        mapView.overlayManager.add(rotationGestureOverlay)

        updateMultiTouchControlsSetting()
    }

    override fun snapshot(omhSnapshotReadyCallback: OmhSnapshotReadyCallback) {
        val drawable: Drawable? =
            ContextCompat.getDrawable(mapView.context, R.drawable.img_map_placeholder)
        if (drawable == null) {
            omhSnapshotReadyCallback.onSnapshotReady(null)
            return
        }
        val bitmap: Bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        omhSnapshotReadyCallback.onSnapshotReady(bitmap)
    }

    @RequiresPermission(anyOf = [ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION])
    override fun setMyLocationEnabled(enable: Boolean) {
        if (enable) {
            enableMyLocation()
        } else {
            myLocationNewOverlay?.disableMyLocation()
            mapView.overlayManager.remove(myLocationIconOverlay)
            mapView.overlayManager.remove(myLocationNewOverlay)
        }
    }

    @RequiresPermission(anyOf = [ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION])
    private fun enableMyLocation() {
        if (myLocationNewOverlay?.isMyLocationEnabled != true) {
            myLocationIconOverlay = MyLocationIconOverlay(mapView.context).apply {
                setCenterLocation { setMyLocationEnabled(true) }
            }
            val gpsMyLocationProvider = GpsMyLocationProvider(mapView.context).apply {
                addLocationSource(FUSED_PROVIDER)
            }
            myLocationNewOverlay = MyLocationNewOverlay(gpsMyLocationProvider, mapView).apply {
                enableMyLocation()
            }
            mapView.overlayManager.add(myLocationNewOverlay)
            mapView.overlayManager.add(myLocationIconOverlay)
        }
        myLocationNewOverlay?.myLocation?.let { geoPoint ->
            with(mapView.controller) {
                setZoom(DEFAULT_ZOOM_LEVEL)
                animateTo(geoPoint)
            }
            mapView.postInvalidate()
        }
    }

    override fun isMyLocationEnabled(): Boolean {
        return myLocationNewOverlay?.isMyLocationEnabled == true
    }

    override fun setMyLocationButtonClickListener(
        omhOnMyLocationButtonClickListener: OmhOnMyLocationButtonClickListener
    ) {
        myLocationIconOverlay?.setOnClickListener {
            omhOnMyLocationButtonClickListener.onMyLocationButtonClick()
        }
    }

    override fun setOnCameraMoveStartedListener(listener: OmhOnCameraMoveStartedListener) {
        mapListenerController.setOnStartListener(listener)
    }

    override fun setOnMapLoadedCallback(callback: OmhMapLoadedCallback?) {
        callback?.onMapLoaded()
    }

    override fun setOnMarkerClickListener(listener: OmhOnMarkerClickListener) {
        markerClickListener = listener

        markers.forEach() { (marker, omhMarker) ->
            applyOnMarkerClickListener(marker, omhMarker)
        }
    }

    override fun setOnMarkerDragListener(listener: OmhOnMarkerDragListener) {
        markerDragListener = listener

        markers.forEach() { (marker, omhMarker) ->
            applyOnMarkerDragListener(marker, omhMarker)
        }
    }

    override fun setOnPolylineClickListener(listener: OmhOnPolylineClickListener) {
        polylineClickListener = listener

        polylines.forEach() { (polyline, omhPolyline) ->
            polyline.setOnClickListener { _, _, _ ->
                if (omhPolyline.getClickable()) {
                    listener.onPolylineClick(omhPolyline)
                }
                true
            }
        }
    }

    override fun setOnPolygonClickListener(listener: OmhOnPolygonClickListener) {
        polygonClickListener = listener

        polygons.forEach() { (polygon, omhPolygon) ->
            polygon.setOnClickListener { _, _, _ ->
                if (omhPolygon.getClickable()) {
                    listener.onPolygonClick(omhPolygon)
                }
                true
            }
        }
    }

    override fun setOnCameraIdleListener(listener: OmhOnCameraIdleListener) {
        mapListenerController.setOnIdleListener(listener)
    }

    override fun setMapStyle(json: Int?) {
        // To be implemented
    }

    override fun setScaleFactor(scaleFactor: Float) {
        // Not required for OpenStreetMap
    }
}
