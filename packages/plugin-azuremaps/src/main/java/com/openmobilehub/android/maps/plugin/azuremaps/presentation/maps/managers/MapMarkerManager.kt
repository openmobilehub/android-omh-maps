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

package com.openmobilehub.android.maps.plugin.azuremaps.presentation.maps.managers

import android.content.Context
import android.graphics.Bitmap
import com.azure.android.maps.control.AzureMap
import com.azure.android.maps.control.layer.SymbolLayer
import com.azure.android.maps.control.source.DataSource
import com.mapbox.geojson.Feature
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
import com.openmobilehub.android.maps.plugin.azuremaps.extensions.toSymbolLayerOptionsList
import com.openmobilehub.android.maps.plugin.azuremaps.presentation.interfaces.IMapLongClickManagerDelegate
import com.openmobilehub.android.maps.plugin.azuremaps.presentation.interfaces.IMapViewDelegate
import com.openmobilehub.android.maps.plugin.azuremaps.presentation.interfaces.ITouchInteractable
import com.openmobilehub.android.maps.plugin.azuremaps.presentation.maps.OmhInfoWindow
import com.openmobilehub.android.maps.plugin.azuremaps.presentation.maps.OmhMarkerImpl
import com.openmobilehub.android.maps.plugin.azuremaps.utils.CoordinateConverter

@SuppressWarnings("TooManyFunctions")
internal class MapMarkerManager(
    private val context: Context,
    private val map: AzureMap,
) : IMapViewDelegate, IMapLongClickManagerDelegate {
    private var markerClickListener: OmhOnMarkerClickListener? = null
    private var markerDragListener: OmhOnMarkerDragListener? = null
    private var infoWindowOpenStatusChangeListener: OmhOnInfoWindowOpenStatusChangeListener? = null
    private var infoWindowClickListener: OmhOnInfoWindowClickListener? = null
    private var infoWindowLongClickListener: OmhOnInfoWindowLongClickListener? = null
    internal val markers = mutableMapOf<String, OmhMarkerImpl>()
    internal val infoWindows = mutableMapOf<String, OmhInfoWindow>()

    fun updateAllInfoWindowsPositions() {
        infoWindows.values.forEach { it.updatePosition() }
    }

    fun addMarker(
        options: OmhMarkerOptions,
        uuidGenerator: UUIDGenerator = DefaultUUIDGenerator()
    ): OmhMarkerImpl {
        val markerUUID = uuidGenerator.generate()
        val pointOnMap = CoordinateConverter.convertToPoint(options.position)

        val markerDataSource = DataSource(OmhMarkerImpl.getSourceID(markerUUID))
        map.sources.add(markerDataSource)
        markerDataSource.add(Feature.fromGeometry(pointOnMap))

        val markerLayer = SymbolLayer(
            markerDataSource,
            OmhMarkerImpl.getSymbolLayerID(markerUUID)
        )
        for (option in options.toSymbolLayerOptionsList()) {
            markerLayer.setOptions(option)
        }

        val omhMarker = OmhMarkerImpl(
            source = markerDataSource,
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
            alpha = options.alpha,
            isVisible = options.isVisible,
            anchor = options.anchor,
            isFlat = options.isFlat,
            rotation = options.rotation,
            mapViewDelegate = this,
        )

        val infoWindowDataSource = DataSource(omhMarker.omhInfoWindow.getSourceID())
        infoWindowDataSource.add(Feature.fromGeometry(pointOnMap))

        omhMarker.omhInfoWindow.setDataSource(infoWindowDataSource)

        map.layers.add(markerLayer)
        map.layers.add(omhMarker.omhInfoWindow.infoWindowSymbolLayer)

        markers[markerLayer.id] = omhMarker
        infoWindows[omhMarker.omhInfoWindow.getSymbolLayerID()] = omhMarker.omhInfoWindow

        return omhMarker
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
                        if (!omhMarker.getIsInfoWindowShown()) {
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

    override fun removeImage(imageId: String) {
        map.images.remove(imageId)
    }

    override fun addImage(imageId: String, image: Bitmap) {
        if (image.density == 0) {
            // required for AzureMaps, since thy use an old version of Mapbox SDK
            // for Android underneath, which requires the density to be != 0
            image.density = context.resources.displayMetrics.densityDpi
        }

        map.images.add(imageId, image)
    }

    override fun getMapWidth(): Int {
        return map.ui.a.width // a is the FrameLayout containing the map
    }

    override fun getMapHeight(): Int {
        return map.ui.a.height // a is the FrameLayout containing the map
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
