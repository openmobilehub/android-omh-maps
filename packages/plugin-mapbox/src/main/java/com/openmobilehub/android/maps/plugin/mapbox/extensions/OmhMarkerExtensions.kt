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

import android.content.Context
import com.mapbox.geojson.Feature
import com.mapbox.maps.extension.style.expressions.generated.Expression
import com.mapbox.maps.extension.style.layers.generated.SymbolLayer
import com.mapbox.maps.extension.style.layers.generated.symbolLayer
import com.mapbox.maps.extension.style.layers.properties.generated.IconAnchor
import com.mapbox.maps.extension.style.sources.generated.GeoJsonSource
import com.mapbox.maps.extension.style.sources.generated.geoJsonSource
import com.openmobilehub.android.maps.core.presentation.models.OmhMarkerOptions
import com.openmobilehub.android.maps.plugin.mapbox.presentation.maps.OmhMarkerImpl
import com.openmobilehub.android.maps.plugin.mapbox.utils.AnchorConverter
import com.openmobilehub.android.maps.plugin.mapbox.utils.Constants
import com.openmobilehub.android.maps.plugin.mapbox.utils.CoordinateConverter
import java.util.UUID

internal fun OmhMarkerOptions.addOmhMarker(
    context: Context
): Triple<OmhMarkerImpl, GeoJsonSource, SymbolLayer> {
    val pointOnMap = CoordinateConverter.convertToPoint(position)
    val omhMarker = OmhMarkerImpl(
        markerUUID = UUID.randomUUID(),
        context = context,
        position = position,
        title = title,
        snippet = snippet,
        draggable = draggable,
        clickable = clickable,
        backgroundColor = backgroundColor,
        initialIcon = icon,
        bufferedAlpha = alpha,
        bufferedIsVisible = isVisible,
        bufferedAnchor = anchor,
        bufferedIsFlat = isFlat,
        bufferedRotation = rotation
    )

    val geoJsonSourceID = omhMarker.getGeoJsonSourceID()
    val geoJsonSource = geoJsonSource(geoJsonSourceID) {
        feature(Feature.fromGeometry(pointOnMap))
    }
    omhMarker.setGeoJsonSource(geoJsonSource)

    val markerLayer = symbolLayer(omhMarker.getMarkerLayerID(), geoJsonSourceID) {
        // icon
        // iconImage(markerImageID) will be handled by setIcon
        iconIgnorePlacement(true)
        iconAllowOverlap(true)

        // backgroundColor
        iconColor(backgroundColor ?: Constants.DEFAULT_MARKER_COLOR)

        // anchor
        iconAnchor(AnchorConverter.convertContinuousToDiscreteIconAnchor(anchor))

        // alpha
        iconOpacity(alpha.toDouble())

        // isVisible
        visibility(OmhMarkerImpl.getIconsVisibility(isVisible))

        // rotation
        iconRotate(rotation.toDouble())

        // isFlat
        iconPitchAlignment(OmhMarkerImpl.getIconsPitchAlignment(isFlat))
        iconRotationAlignment(OmhMarkerImpl.getIconsRotationAlignment(isFlat))
    }
    omhMarker.setMarkerLayer(markerLayer)

    val infoWindowLayer = symbolLayer(omhMarker.getInfoWindowLayerID(), geoJsonSourceID) {
//        iconImage(omhMarker.getMarkerIconID())
        iconAnchor(IconAnchor.TOP)
        iconAllowOverlap(true)
        // iconOffset(arrayOf<Float>(-2f, -28f))
    }.filter(
        // filter to only show the info window when active
        Expression.literal(true)
    )
    omhMarker.setInfoWindowLayer(infoWindowLayer)

    return Triple(omhMarker, geoJsonSource, markerLayer)
}
