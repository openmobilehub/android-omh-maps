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
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhInfoWindowViewFactory
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhMarker
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhOnInfoWindowClickListener
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhOnInfoWindowLongClickListener
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhOnInfoWindowOpenStatusChangeListener
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhOnMarkerClickListener
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhOnMarkerDragListener
import com.openmobilehub.android.maps.core.presentation.models.OmhCoordinate
import com.openmobilehub.android.maps.core.presentation.models.OmhMarkerOptions
import com.openmobilehub.android.maps.plugin.mapbox.extensions.addOmhMarker
import com.openmobilehub.android.maps.plugin.mapbox.presentation.interfaces.IMapInfoWindowManagerDelegate
import com.openmobilehub.android.maps.plugin.mapbox.presentation.interfaces.IMapLongClickManagerDelegate
import com.openmobilehub.android.maps.plugin.mapbox.presentation.interfaces.IMapMarkerManagerDelegate
import com.openmobilehub.android.maps.plugin.mapbox.presentation.interfaces.IOmhInfoWindowMapViewDelegate
import com.openmobilehub.android.maps.plugin.mapbox.presentation.interfaces.ITouchInteractable
import com.openmobilehub.android.maps.plugin.mapbox.presentation.maps.OmhInfoWindow
import com.openmobilehub.android.maps.plugin.mapbox.presentation.maps.OmhMarkerImpl

@SuppressWarnings("TooManyFunctions")
internal class MapMarkerManager(
    private val mapMarkerManagerDelegate: IMapMarkerManagerDelegate,
    private val infoWindowMapViewDelegate: IOmhInfoWindowMapViewDelegate,
) : IMapInfoWindowManagerDelegate, IMapLongClickManagerDelegate {
    private var markerClickListener: OmhOnMarkerClickListener? = null
    private var markerDragListener: OmhOnMarkerDragListener? = null
    private var infoWindowOpenStatusChangeListener: OmhOnInfoWindowOpenStatusChangeListener? = null
    private var infoWindowClickListener: OmhOnInfoWindowClickListener? = null
    private var infoWindowLongClickListener: OmhOnInfoWindowLongClickListener? = null
    internal val markers = mutableMapOf<String, OmhMarkerImpl>()
    internal val infoWindows = mutableMapOf<String, OmhInfoWindow>()

    fun addMarker(options: OmhMarkerOptions, style: Style?): OmhMarkerImpl {
        synchronized(this) {
            val (omhMarker, _, layers) = options.addOmhMarker(
                mapMarkerManagerDelegate.mapView.context,
                infoWindowManagerDelegate = this,
                infoWindowMapViewDelegate = infoWindowMapViewDelegate
            )
            val (markerIconLayer, infoWindowLayer) = layers

            style?.let { safeStyle ->
                omhMarker.applyBufferedProperties(safeStyle)
            }

            markers[markerIconLayer.layerId] = omhMarker
            infoWindows[infoWindowLayer.layerId] = omhMarker.omhInfoWindow

            return omhMarker
        }
    }

    fun addQueuedElementsToStyle(style: Style) {
        markers.values.forEach { omhMarker ->
            // re-apply the icons now, since they can be added to the map for real
            omhMarker.applyBufferedProperties(style)
        }
    }

    fun setMarkerClickListener(listener: OmhOnMarkerClickListener) {
        markerClickListener = listener
    }

    fun setMarkerDragListener(listener: OmhOnMarkerDragListener) {
        markerDragListener = listener
    }

    fun setInfoWindowOpenStatusChangeListener(listener: OmhOnInfoWindowOpenStatusChangeListener) {
        infoWindowOpenStatusChangeListener = listener
    }

    fun setOnInfoWindowClickListener(listener: OmhOnInfoWindowClickListener) {
        infoWindowClickListener = listener
    }

    fun setOnInfoWindowLongClickListener(listener: OmhOnInfoWindowLongClickListener) {
        infoWindowLongClickListener = listener
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

    fun handleMapClick(
        point: Point,
        eventConsumedCallback: (eventConsumed: Boolean) -> Unit
    ): Boolean {
        val screenCoordinate = mapMarkerManagerDelegate.mapView.mapboxMap.pixelForCoordinate(point)

        mapMarkerManagerDelegate.queryRenderedLayerIdAt(screenCoordinate) { layerId ->
            // try if a marker was hit
            val omhMarker = markers[layerId]

            if (omhMarker !== null && omhMarker.getClickable()) {
                // note: here, markerClick returns a boolean informing whether the event
                // was consumed by the handler, yet we swallow it instead of returning
                // as this is a callback and there is no way to return in main scope here
                // either way, this makes no difference, since Mapbox doesn't have a default
                // behaviour in situation when false is returned; for safety, the outer function
                // always returns true to inform that the event has been handled
                // moreover, the real consumed result is passed back in a callback for side effects
                eventConsumedCallback(
                    markerClickListener?.onMarkerClick(omhMarker)?.let { eventConsumed ->
                        if (!eventConsumed) {
                            // to achieve feature parity with GoogleMaps, the info window should be opened on click
                            if (!omhMarker.getIsInfoWindowShown()) {
                                omhMarker.showInfoWindow()
                            }
                        }

                        eventConsumed
                    } ?: false
                )
                return@queryRenderedLayerIdAt true // prevent further processing
            }

            // try if an info window was hit
            val omhInfoWindow = infoWindows[layerId]

            if (omhInfoWindow !== null && omhInfoWindow.getClickable()) {
                eventConsumedCallback(
                    infoWindowClickListener?.let {
                        it.onInfoWindowClick(omhInfoWindow.omhMarker)
                        true
                    } ?: false
                )
                return@queryRenderedLayerIdAt true // prevent further processing
            }

            // reaching here means no hit, in which case we want the source to fire the callback
            // with all remaining layer IDs (if any), unless there is a hit
            return@queryRenderedLayerIdAt false
        }

        return true
    }

    override fun onInfoWindowClick(omhMarkerImpl: OmhMarkerImpl) {
        infoWindowClickListener?.onInfoWindowClick(omhMarkerImpl)
    }

    override fun onInfoWindowOpenStatusChange(omhMarkerImpl: OmhMarkerImpl, isOpen: Boolean) {
        if (isOpen) {
            infoWindowOpenStatusChangeListener?.onInfoWindowOpen(omhMarkerImpl)
        } else {
            infoWindowOpenStatusChangeListener?.onInfoWindowClose(omhMarkerImpl)
        }
    }

    override fun handleLongClick(longClickedEntity: ITouchInteractable): Boolean {
        return when (longClickedEntity) {
            is OmhInfoWindow -> infoWindowLongClickListener?.let {
                it.onInfoWindowLongClick(longClickedEntity.omhMarker)
                true
            } ?: false

            else -> false // Noop
        }
    }

    fun setCustomInfoWindowViewFactory(factory: OmhInfoWindowViewFactory?) {
        infoWindows.values.forEach {
            it.setCustomInfoWindowViewFactory(factory)
        }
    }

    fun setCustomInfoWindowContentsViewFactory(factory: OmhInfoWindowViewFactory?) {
        infoWindows.values.forEach {
            it.setCustomInfoWindowContentsViewFactory(factory)
        }
    }
}
