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

package com.openmobilehub.android.maps.plugin.azuremaps.extensions

import com.azure.android.maps.control.options.Option
import com.azure.android.maps.control.options.SymbolLayerOptions
import com.openmobilehub.android.maps.core.presentation.models.OmhMarkerOptions
import com.openmobilehub.android.maps.plugin.azuremaps.presentation.maps.OmhMarkerImpl
import com.openmobilehub.android.maps.plugin.azuremaps.utils.AnchorConverter

internal fun OmhMarkerOptions.toSymbolLayerOptionsList(): List<Option<*>> {
    return listOf<Option<*>>(
        // icon
        SymbolLayerOptions.iconSize(1f), // icon scale
        SymbolLayerOptions.iconIgnorePlacement(true),
        SymbolLayerOptions.iconAllowOverlap(true),
        // iconImage(markerImageID) will be handled by setIcon
        // backgroundColor - will be handled by setIcon

        // anchor
        SymbolLayerOptions.iconAnchor(
            AnchorConverter.convertContinuousToDiscreteAnchorType(
                anchor
            )
        ),

        // alpha & isVisible
        SymbolLayerOptions.iconOpacity(if (isVisible) alpha else 0f),

        // rotation
        SymbolLayerOptions.iconRotation(rotation),

        // isFlat
        SymbolLayerOptions.iconRotationAlignment(OmhMarkerImpl.getIconsRotationAlignment(isFlat))
    )
}
