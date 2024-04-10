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

import android.content.Context
import com.mapbox.geojson.Feature
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.layers.generated.symbolLayer
import com.mapbox.maps.extension.style.sources.generated.geoJsonSource
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhInfoWindowViewFactory
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhMarker
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhOnInfoWindowClickListener
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhOnInfoWindowLongClickListener
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhOnInfoWindowOpenStatusChangeListener
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhOnMarkerClickListener
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhOnMarkerDragListener
import com.openmobilehub.android.maps.core.presentation.models.OmhCoordinate
import com.openmobilehub.android.maps.core.presentation.models.OmhMarkerOptions
import com.openmobilehub.android.maps.core.utils.uuid.DefaultUUIDGenerator
import com.openmobilehub.android.maps.core.utils.uuid.UUIDGenerator
import com.openmobilehub.android.maps.plugin.mapbox.extensions.applyMarkerOptions
import com.openmobilehub.android.maps.plugin.mapbox.presentation.interfaces.IMapInfoWindowManagerDelegate
import com.openmobilehub.android.maps.plugin.mapbox.presentation.interfaces.IMapLongClickManagerDelegate
import com.openmobilehub.android.maps.plugin.mapbox.presentation.interfaces.IOmhInfoWindowMapViewDelegate
import com.openmobilehub.android.maps.plugin.mapbox.presentation.interfaces.ITouchInteractable
import com.openmobilehub.android.maps.plugin.mapbox.presentation.maps.OmhInfoWindow
import com.openmobilehub.android.maps.plugin.mapbox.presentation.maps.OmhMarkerImpl
import com.openmobilehub.android.maps.plugin.mapbox.utils.CoordinateConverter

@SuppressWarnings("TooManyFunctions")
internal class MapMarkerManager(
    private val context: Context,
    private val infoWindowMapViewDelegate: IOmhInfoWindowMapViewDelegate,
) : IMapInfoWindowManagerDelegate, IMapLongClickManagerDelegate {
    internal var markerClickListener: OmhOnMarkerClickListener? = null
    internal var markerDragListener: OmhOnMarkerDragListener? = null
    internal var infoWindowOpenStatusChangeListener: OmhOnInfoWindowOpenStatusChangeListener? = null
    internal var infoWindowClickListener: OmhOnInfoWindowClickListener? = null
    internal var infoWindowLongClickListener: OmhOnInfoWindowLongClickListener? = null
    internal val markers = mutableMapOf<String, OmhMarkerImpl>()
    internal val infoWindows = mutableMapOf<String, OmhInfoWindow>()

    fun updateAllInfoWindowsPositions() {
        infoWindows.values.forEach { it.updatePosition() }
    }

    fun addMarker(
        options: OmhMarkerOptions,
        style: Style?,
        uuidGenerator: UUIDGenerator = DefaultUUIDGenerator()
    ): OmhMarkerImpl {
        val markerUUID = uuidGenerator.generate()
        val markerGeoJsonSourceID = OmhMarkerImpl.getGeoJsonSourceID(markerUUID)

        val pointOnMap = CoordinateConverter.convertToPoint(options.position)

        val markerLayer =
            symbolLayer(OmhMarkerImpl.getSymbolLayerID(markerUUID), markerGeoJsonSourceID) {}

        options.applyMarkerOptions(markerLayer)

        val omhMarker = OmhMarkerImpl(
            markerUUID = markerUUID,
            context = context,
            markerSymbolLayer = markerLayer,
            position = options.position,
            initialTitle = options.title,
            initialSnippet = options.snippet,
            initialInfoWindowAnchor = options.infoWindowAnchor,
            draggable = options.draggable,
            clickable = options.clickable,
            backgroundColor = options.backgroundColor,
            initialIcon = options.icon,
            bufferedAlpha = options.alpha,
            bufferedIsVisible = options.isVisible,
            bufferedAnchor = options.anchor,
            bufferedIsFlat = options.isFlat,
            bufferedRotation = options.rotation,
            infoWindowManagerDelegate = this,
            infoWindowMapViewDelegate = infoWindowMapViewDelegate
        )

        val markerGeoJsonSource = geoJsonSource(markerGeoJsonSourceID) {
            feature(Feature.fromGeometry(pointOnMap))
        }
        omhMarker.setGeoJsonSource(markerGeoJsonSource)

        val infoWindowGeoJsonSourceID = omhMarker.omhInfoWindow.getGeoJsonSourceID()
        val infoWindowGeoJsonSource = geoJsonSource(infoWindowGeoJsonSourceID) {
            feature(Feature.fromGeometry(pointOnMap))
        }
        omhMarker.omhInfoWindow.setGeoJsonSource(infoWindowGeoJsonSource)

        markers[markerLayer.layerId] = omhMarker
        infoWindows[omhMarker.omhInfoWindow.getSymbolLayerID()] = omhMarker.omhInfoWindow

        style?.let { safeStyle ->
            omhMarker.onStyleLoaded(safeStyle)
        }

        return omhMarker
    }

    fun onStyleLoaded(style: Style) {
        markers.values.forEach { omhMarker ->
            // re-apply the icons now, since they can be added to the map for real
            omhMarker.onStyleLoaded(style)
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

    @SuppressWarnings("ReturnCount")
    fun maybeHandleClick(
        layerId: String,
        eventConsumedCallback: (eventConsumed: Boolean) -> Unit
    ): Boolean {
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
                        if (!omhMarker.getIsInfoWindowShown() && !omhMarker.isRemoved) {
                            omhMarker.showInfoWindow()
                        }
                    }

                    eventConsumed
                } ?: false
            )
            return true // prevent further processing
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
            return true // prevent further processing
        }

        // reaching here means no hit, in which case we want the source to fire the callback
        // with all remaining layer IDs (if any), unless there is a hit
        return false
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
