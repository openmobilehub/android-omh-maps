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

package com.openmobilehub.android.maps.plugin.mapbox.presentation.maps.managers

import com.mapbox.geojson.Point
import com.mapbox.maps.Style
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhMarker
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhOnMarkerClickListener
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhOnMarkerDragListener
import com.openmobilehub.android.maps.core.presentation.models.OmhCoordinate
import com.openmobilehub.android.maps.core.presentation.models.OmhMarkerOptions
import com.openmobilehub.android.maps.plugin.mapbox.extensions.addOmhMarker
import com.openmobilehub.android.maps.plugin.mapbox.presentation.interfaces.IMapMarkerManagerDelegate
import com.openmobilehub.android.maps.plugin.mapbox.presentation.maps.OmhMarkerImpl

internal class MapMarkerManager(private val delegate: IMapMarkerManagerDelegate) {
    private var markerClickListener: OmhOnMarkerClickListener? = null
    private var markerDragListener: OmhOnMarkerDragListener? = null
    internal val markers = mutableMapOf<String, OmhMarker>()

    fun addMarker(options: OmhMarkerOptions, style: Style?): OmhMarkerImpl {
        synchronized(this) {
            val (omhMarker, _, layer) = options.addOmhMarker(delegate.mapView.context)

            style?.let { safeStyle ->
                omhMarker.applyBufferedProperties(safeStyle)
            }

            markers[layer.layerId] = omhMarker

            return omhMarker
        }
    }

    fun addQueuedElementsToStyle(style: Style) {
        markers.values.forEach { omhMarker ->
            // re-apply the icons now, since they can be added to the map for real
            if (omhMarker is OmhMarkerImpl) {
                omhMarker.applyBufferedProperties(style)
            }
        }
    }

    fun setMarkerClickListener(listener: OmhOnMarkerClickListener) {
        markerClickListener = listener
    }

    fun setMarkerDragListener(listener: OmhOnMarkerDragListener) {
        markerDragListener = listener
    }

    fun markerDragStart(omhCoordinate: OmhCoordinate, omhMarker: OmhMarker) {
        omhMarker.setPosition(omhCoordinate)
        markerDragListener?.onMarkerDragStart(omhMarker)
    }

    fun markerDrag(omhCoordinate: OmhCoordinate, omhMarker: OmhMarker) {
        omhMarker.setPosition(omhCoordinate)
        markerDragListener?.onMarkerDrag(omhMarker)
    }

    fun markerDragEnd(omhCoordinate: OmhCoordinate, omhMarker: OmhMarker) {
        omhMarker.setPosition(omhCoordinate)
        markerDragListener?.onMarkerDragEnd(omhMarker)
    }

    fun handleMapClick(point: Point): Boolean {
        val screenCoordinate = delegate.mapView.mapboxMap.pixelForCoordinate(point)

        delegate.queryRenderedLayerIdAt(screenCoordinate) { layerId ->
            val omhMarker = markers[layerId]

            if (omhMarker !== null && omhMarker.getClickable()) {
                // note: here, markerClick returns a boolean informing whether the event
                // was consumed by the handler, yet we swallow it instead of returning
                // as this is a callback and there is no way to return in main scope here
                // either way, this makes no difference, since Mapbox doesn't have a default
                // behaviour in situation when false is returned; for safety, the outer function
                // always returns true to inform that the event has been handled
                markerClickListener?.onMarkerClick(omhMarker)
            }
        }

        return true
    }
}
