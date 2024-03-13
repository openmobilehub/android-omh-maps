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

import com.mapbox.maps.extension.style.layers.generated.SymbolLayer
import com.openmobilehub.android.maps.core.presentation.models.OmhMarkerOptions
import com.openmobilehub.android.maps.plugin.mapbox.presentation.maps.OmhMarkerImpl
import com.openmobilehub.android.maps.plugin.mapbox.utils.AnchorConverter
import com.openmobilehub.android.maps.plugin.mapbox.utils.Constants

internal fun OmhMarkerOptions.applyMarkerOptions(
    markerSymbolLayer: SymbolLayer
) {
    // icon
    // iconImage(markerImageID) will be handled by setIcon
    markerSymbolLayer.iconSize(1.0) // icon scale
    markerSymbolLayer.iconIgnorePlacement(true)
    markerSymbolLayer.iconAllowOverlap(true)

    // backgroundColor
    markerSymbolLayer.iconColor(backgroundColor ?: Constants.DEFAULT_MARKER_COLOR)

    // anchor
    markerSymbolLayer.iconAnchor(AnchorConverter.convertContinuousToDiscreteIconAnchor(anchor))

    // alpha
    markerSymbolLayer.iconOpacity(alpha.toDouble())

    // isVisible
    markerSymbolLayer.visibility(OmhMarkerImpl.getIconsVisibility(isVisible))

    // rotation
    markerSymbolLayer.iconRotate(rotation.toDouble())

    // isFlat
    markerSymbolLayer.iconPitchAlignment(OmhMarkerImpl.getIconsPitchAlignment(isFlat))
    markerSymbolLayer.iconRotationAlignment(OmhMarkerImpl.getIconsRotationAlignment(isFlat))
}
