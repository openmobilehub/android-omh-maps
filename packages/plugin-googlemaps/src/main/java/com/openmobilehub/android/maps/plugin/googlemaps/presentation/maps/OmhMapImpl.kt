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

package com.openmobilehub.android.maps.plugin.googlemaps.presentation.maps

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.graphics.Bitmap
import androidx.annotation.RequiresPermission
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
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
import com.openmobilehub.android.maps.plugin.googlemaps.extensions.toMarkerOptions
import com.openmobilehub.android.maps.plugin.googlemaps.extensions.toPolygonOptions
import com.openmobilehub.android.maps.plugin.googlemaps.extensions.toPolylineOptions
import com.openmobilehub.android.maps.plugin.googlemaps.utils.Constants
import com.openmobilehub.android.maps.plugin.googlemaps.utils.CoordinateConverter
import com.openmobilehub.android.maps.plugin.googlemaps.utils.commonLogger

@SuppressWarnings("TooManyFunctions")
internal class OmhMapImpl(
    private var googleMap: GoogleMap,
    private val context: Context,
    private val logger: Logger = commonLogger
) : OmhMap {

    override val providerName: String
        get() = Constants.PROVIDER_NAME

    private val markers = mutableMapOf<Marker, OmhMarker>()

    override fun addMarker(options: OmhMarkerOptions): OmhMarker? {
        val googleOptions = options.toMarkerOptions()
        val marker: Marker? = googleMap.addMarker(googleOptions)
        val initiallyClickable = options.clickable

        return marker?.let {
            val omhMarker = OmhMarkerImpl(marker, initiallyClickable)
            markers[marker] = omhMarker

            return@let omhMarker
        }
    }

    override fun addPolyline(options: OmhPolylineOptions): OmhPolyline {
        val googleOptions = options.toPolylineOptions()
        val polyline = googleMap.addPolyline(googleOptions)

        return OmhPolylineImpl(polyline)
    }

    override fun addPolygon(options: OmhPolygonOptions): OmhPolygon {
        val googleOptions = options.toPolygonOptions()
        val polygon = googleMap.addPolygon(googleOptions)

        return OmhPolygonImpl(polygon)
    }

    override fun getCameraPositionCoordinate(): OmhCoordinate {
        val position: LatLng = googleMap.cameraPosition.target
        return CoordinateConverter.convertToOmhCoordinate(position)
    }

    override fun setZoomGesturesEnabled(enableZoomGestures: Boolean) {
        googleMap.uiSettings.isZoomGesturesEnabled = enableZoomGestures
    }

    override fun setRotateGesturesEnabled(enableRotateGestures: Boolean) {
        googleMap.uiSettings.isRotateGesturesEnabled = enableRotateGestures
    }

    @RequiresPermission(anyOf = [ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION])
    override fun setMyLocationEnabled(enable: Boolean) {
        googleMap.isMyLocationEnabled = enable
    }

    override fun isMyLocationEnabled(): Boolean {
        return googleMap.isMyLocationEnabled
    }

    override fun setMyLocationButtonClickListener(
        omhOnMyLocationButtonClickListener: OmhOnMyLocationButtonClickListener
    ) {
        googleMap.setOnMyLocationButtonClickListener {
            omhOnMyLocationButtonClickListener.onMyLocationButtonClick()
        }
    }

    override fun setOnCameraMoveStartedListener(listener: OmhOnCameraMoveStartedListener) {
        googleMap.setOnCameraMoveStartedListener {
            listener.onCameraMoveStarted(it)
        }
    }

    override fun setOnCameraIdleListener(listener: OmhOnCameraIdleListener) {
        googleMap.setOnCameraIdleListener {
            listener.onCameraIdle()
        }
    }

    override fun setOnMapLoadedCallback(callback: OmhMapLoadedCallback?) {
        googleMap.setOnMapLoadedCallback {
            callback?.onMapLoaded()
        }
    }

    override fun setOnMarkerClickListener(listener: OmhOnMarkerClickListener) {
        this.googleMap.setOnMarkerClickListener ClickHandler@{ marker ->
            val omhMarker = markers[marker]

            if (omhMarker != null && omhMarker.getClickable()) {
                return@ClickHandler listener.onMarkerClick(omhMarker)
            }

            true
        }
    }

    override fun setOnMarkerDragListener(listener: OmhOnMarkerDragListener) {
        this.googleMap.setOnMarkerDragListener(object : GoogleMap.OnMarkerDragListener {
            override fun onMarkerDrag(marker: Marker) {
                markers[marker]?.let { omhMarker ->
                    listener.onMarkerDrag(omhMarker)
                }
            }

            override fun onMarkerDragEnd(marker: Marker) {
                markers[marker]?.let { omhMarker ->
                    listener.onMarkerDragEnd(omhMarker)
                }
            }

            override fun onMarkerDragStart(marker: Marker) {
                markers[marker]?.let { omhMarker ->
                    listener.onMarkerDragStart(omhMarker)
                }
            }
        })
    }

    override fun setOnPolylineClickListener(listener: OmhOnPolylineClickListener) {
        googleMap.setOnPolylineClickListener {
            listener.onPolylineClick(OmhPolylineImpl(it))
        }
    }

    override fun setOnPolygonClickListener(listener: OmhOnPolygonClickListener) {
        googleMap.setOnPolygonClickListener {
            listener.onPolygonClick(OmhPolygonImpl(it))
        }
    }

    override fun snapshot(omhSnapshotReadyCallback: OmhSnapshotReadyCallback) {
        googleMap.snapshot { bitmap: Bitmap? ->
            omhSnapshotReadyCallback.onSnapshotReady(bitmap)
        }
    }

    override fun setMapStyle(json: Int?) {
        if (json == null) {
            googleMap.setMapStyle(null)
            return
        }
        val isStyleApplied =
            googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(context, json))
        if (!isStyleApplied) {
            logger.logWarning("Failed to apply custom map style. Check logs from Google Maps SDK.")
        }
    }

    override fun moveCamera(coordinate: OmhCoordinate, zoomLevel: Float) {
        val latLng: LatLng = CoordinateConverter.convertToLatLng(coordinate)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel))
    }

    override fun updateScaleFactor(callback: (zoomLevel: Float) -> Float) {
        // Not required for Google Maps as Google Maps SDK is a reference implementation
    }
}
