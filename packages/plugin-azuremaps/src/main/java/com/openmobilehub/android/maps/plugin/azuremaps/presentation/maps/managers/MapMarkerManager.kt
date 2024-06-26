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
import com.azure.android.maps.control.Popup
import com.azure.android.maps.control.layer.SymbolLayer
import com.azure.android.maps.control.source.DataSource
import com.google.gson.JsonObject
import com.mapbox.geojson.Feature
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhInfoWindowViewFactory
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhOnInfoWindowClickListener
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhOnInfoWindowLongClickListener
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhOnInfoWindowOpenStatusChangeListener
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhOnMarkerClickListener
import com.openmobilehub.android.maps.core.presentation.models.OmhMarkerOptions
import com.openmobilehub.android.maps.core.utils.logging.UnsupportedFeatureLogger
import com.openmobilehub.android.maps.core.utils.uuid.DefaultUUIDGenerator
import com.openmobilehub.android.maps.core.utils.uuid.UUIDGenerator
import com.openmobilehub.android.maps.plugin.azuremaps.extensions.toSymbolLayerOptionsList
import com.openmobilehub.android.maps.plugin.azuremaps.presentation.interfaces.AzureMapInterface
import com.openmobilehub.android.maps.plugin.azuremaps.presentation.interfaces.IMapViewDelegate
import com.openmobilehub.android.maps.plugin.azuremaps.presentation.interfaces.IMarkerDelegate
import com.openmobilehub.android.maps.plugin.azuremaps.presentation.maps.OmhInfoWindow
import com.openmobilehub.android.maps.plugin.azuremaps.presentation.maps.OmhMarkerImpl
import com.openmobilehub.android.maps.plugin.azuremaps.utils.Constants
import com.openmobilehub.android.maps.plugin.azuremaps.utils.CoordinateConverter
import com.openmobilehub.android.maps.plugin.azuremaps.utils.markerLogger
import java.util.UUID

@SuppressWarnings("TooManyFunctions")
internal class MapMarkerManager(
    private val context: Context,
    private val map: AzureMapInterface,
) : IMapViewDelegate, IMarkerDelegate {
    internal var markerClickListener: OmhOnMarkerClickListener? = null
    internal var infoWindowOpenStatusChangeListener: OmhOnInfoWindowOpenStatusChangeListener? = null
    internal var infoWindowClickListener: OmhOnInfoWindowClickListener? = null
    internal var infoWindowLongClickListener: OmhOnInfoWindowLongClickListener? = null
    private val markers = mutableMapOf<String, OmhMarkerImpl>()
    private val infoWindows = mutableMapOf<String, OmhInfoWindow>()

    fun addMarker(
        options: OmhMarkerOptions,
        uuidGenerator: UUIDGenerator = DefaultUUIDGenerator(),
        logger: UnsupportedFeatureLogger = markerLogger
    ): OmhMarkerImpl {
        val markerUUID = uuidGenerator.generate()
        val pointOnMap = CoordinateConverter.convertToPoint(options.position)

        val markerDataSource = DataSource(OmhMarkerImpl.getSourceID(markerUUID))
        map.sources.add(markerDataSource)

        val markerFeatureProps = JsonObject().apply {
            addProperty(Constants.MARKER_FEATURE_UUID_BINDING, markerUUID.toString())
        }
        markerDataSource.add(Feature.fromGeometry(pointOnMap, markerFeatureProps))

        val markerLayer = SymbolLayer(
            markerDataSource,
            OmhMarkerImpl.getSymbolLayerID(markerUUID)
        )
        for (option in options.toSymbolLayerOptionsList(logger)) {
            markerLayer.setOptions(option)
        }
        map.layers.add(markerLayer)

        val infoWindowPopup = Popup()
        map.popups.add(infoWindowPopup)

        val omhMarker = OmhMarkerImpl(
            source = markerDataSource,
            markerUUID = markerUUID,
            context = context,
            markerSymbolLayer = markerLayer,
            position = options.position,
            initialTitle = options.title,
            initialSnippet = options.snippet,
            initialInfoWindowAnchor = options.infoWindowAnchor,
            clickable = options.clickable,
            backgroundColor = options.backgroundColor,
            initialIcon = options.icon,
            alpha = options.alpha,
            isVisible = options.isVisible,
            anchor = options.anchor,
            isFlat = options.isFlat,
            rotation = options.rotation,
            infoWindowPopup = infoWindowPopup,
            mapViewDelegate = this,
            markerDelegate = this,
            logger = logger
        )

        val omhMarkerUUIDStr = omhMarker.markerUUID.toString()
        markers[omhMarkerUUIDStr] = omhMarker
        infoWindows[omhMarkerUUIDStr] = omhMarker.omhInfoWindow

        return omhMarker
    }

    override fun removeMarker(markerUUID: UUID) {
        markers.remove(markerUUID.toString())
    }

    fun setMarkerClickListener(listener: OmhOnMarkerClickListener) {
        markerClickListener = listener
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

    fun maybeHandleClick(
        markerId: String
    ): Boolean {
        val marker = markers[markerId]

        if (marker == null || !marker.getClickable()) {
            return false
        }

        return markerClickListener?.onMarkerClick(marker)?.let { eventConsumed ->
            if (!eventConsumed) {
                // to achieve feature parity with GoogleMaps, the info window should be opened on click
                if (!marker.getIsInfoWindowShown()) {
                    marker.showInfoWindow()
                }
            }

            eventConsumed
        } ?: false
    }

    override fun onInfoWindowClick(omhMarkerImpl: OmhMarkerImpl) {
        infoWindowClickListener?.onInfoWindowClick(omhMarkerImpl)
    }

    override fun onInfoWindowLongClick(omhMarkerImpl: OmhMarkerImpl): Boolean {
        return if (infoWindowLongClickListener === null) {
            false
        } else {
            infoWindowLongClickListener!!.onInfoWindowLongClick(omhMarkerImpl)
            true
        }
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

    override fun getContext(): Context {
        return context
    }

    override fun removeSymbolLayer(symbolLayer: SymbolLayer) {
        map.layers.remove(symbolLayer)
    }

    override fun removeDataSource(dataSource: DataSource) {
        map.sources.remove(dataSource)
    }

    override fun removePopup(popup: Popup) {
        map.popups.remove(popup)
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
