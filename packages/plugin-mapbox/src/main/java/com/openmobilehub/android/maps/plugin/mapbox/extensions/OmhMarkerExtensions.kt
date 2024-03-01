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

package com.openmobilehub.android.maps.plugin.mapbox.extensions

import com.mapbox.geojson.Feature
import com.mapbox.maps.MapView
import com.mapbox.maps.extension.style.layers.addLayer
import com.mapbox.maps.extension.style.layers.generated.symbolLayer
import com.mapbox.maps.extension.style.sources.addSource
import com.mapbox.maps.extension.style.sources.generated.geoJsonSource
import com.openmobilehub.android.maps.core.presentation.models.OmhMarkerOptions
import com.openmobilehub.android.maps.plugin.mapbox.presentation.maps.OmhMarkerImpl
import com.openmobilehub.android.maps.plugin.mapbox.utils.AnchorConverter
import com.openmobilehub.android.maps.plugin.mapbox.utils.Constants
import com.openmobilehub.android.maps.plugin.mapbox.utils.CoordinateConverter
import java.util.UUID

internal fun OmhMarkerOptions.addOmhMarker(
    mapView: MapView,
): OmhMarkerImpl {
    val pointOnMap = CoordinateConverter.convertToPoint(position)
    val omhMarker = OmhMarkerImpl(
        markerUUID = UUID.randomUUID(),
        mapView = mapView,
        position = position,
        title = title,
        snippet = snippet,
        draggable = draggable,
        clickable = clickable,
        backgroundColor = backgroundColor,
        initialIcon = icon
    )

    val geoJsonSourceID = omhMarker.getGeoJsonSourceID()
    val geoJsonSource = geoJsonSource(geoJsonSourceID) {
        feature(Feature.fromGeometry(pointOnMap))
    }
    mapView.mapboxMap.addSource(geoJsonSource)
    omhMarker.setGeoJsonSource(geoJsonSource)

    // apply icon
    val markerImageID = omhMarker.addOrUpdateMarkerIconImage(icon)

    val layer = symbolLayer(omhMarker.getSymbolLayerID(), geoJsonSourceID) {
        // icon
        iconImage(markerImageID)
        iconIgnorePlacement(true)
        iconAllowOverlap(true)

        // backgroundColor
        iconColor(backgroundColor ?: Constants.DEFAULT_MARKER_COLOR)

        // anchor
        iconAnchor(AnchorConverter.convertContinuousToDiscreteIconAnchor(anchor))

        // alpha
        iconOpacity(alpha.toDouble())

        // isVisible
        visibility(OmhMarkerImpl.getIconVisibility(isVisible))

        // rotation
        iconRotate(rotation.toDouble())

        // isFlat
        iconPitchAlignment(OmhMarkerImpl.getIconPitchAlignment(isFlat))
        iconRotationAlignment(OmhMarkerImpl.getIconRotationAlignment(isFlat))
    }
    mapView.mapboxMap.addLayer(layer)
    omhMarker.setSymbolLayer(layer)

    // TODO: handle title
    // TODO: handle snippet
    // TODO: handle draggable
    // TODO: handle clickable

    return omhMarker
}
