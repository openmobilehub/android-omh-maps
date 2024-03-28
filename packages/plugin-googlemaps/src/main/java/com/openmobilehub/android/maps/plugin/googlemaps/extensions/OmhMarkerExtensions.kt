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

package com.openmobilehub.android.maps.plugin.googlemaps.extensions

import com.google.android.gms.maps.model.MarkerOptions
import com.openmobilehub.android.maps.core.presentation.models.OmhMarkerOptions
import com.openmobilehub.android.maps.core.utils.logging.UnsupportedFeatureLogger
import com.openmobilehub.android.maps.plugin.googlemaps.utils.CoordinateConverter
import com.openmobilehub.android.maps.plugin.googlemaps.utils.MarkerIconConverter
import com.openmobilehub.android.maps.plugin.googlemaps.utils.markerLogger

internal fun OmhMarkerOptions.toMarkerOptions(logger: UnsupportedFeatureLogger = markerLogger): MarkerOptions {
    val mappedOptions = MarkerOptions()
        .position(CoordinateConverter.convertToLatLng(position))
        .title(title)
        .draggable(draggable)

    mappedOptions.anchor(anchor.first, anchor.second)
    mappedOptions.infoWindowAnchor(infoWindowAnchor.first, infoWindowAnchor.second)
    mappedOptions.alpha(alpha)
    snippet?.let { mappedOptions.snippet(snippet) }
    mappedOptions.visible(isVisible)
    mappedOptions.flat(isFlat)
    mappedOptions.rotation(rotation)

    if (icon != null) {
        mappedOptions.icon(
            MarkerIconConverter.convertDrawableToBitmapDescriptor(
                icon!!
            )
        )
    } else if (backgroundColor != null) {
        logger.logFeatureSetterPartiallySupported(
            "backgroundColor",
            "only hue (H) component of HSV color representation is controllable, alpha channel is unsupported"
        )

        mappedOptions.icon(
            MarkerIconConverter.convertColorToBitmapDescriptor(
                backgroundColor
            )
        )
    } else {
        mappedOptions.icon(null)
    }

    return mappedOptions
}
