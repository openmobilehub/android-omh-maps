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

package com.openmobilehub.android.maps.plugin.openstreetmap.extensions

import com.openmobilehub.android.maps.core.presentation.models.OmhMarkerOptions
import com.openmobilehub.android.maps.core.utils.logging.UnsupportedFeatureLogger
import com.openmobilehub.android.maps.plugin.openstreetmap.presentation.maps.CustomMarker
import com.openmobilehub.android.maps.plugin.openstreetmap.presentation.maps.OmhMapImpl
import com.openmobilehub.android.maps.plugin.openstreetmap.utils.markerLogger
import org.osmdroid.views.MapView

internal fun OmhMarkerOptions.toMarkerOptions(
    omhMapImpl: OmhMapImpl,
    mapView: MapView,
    logger: UnsupportedFeatureLogger = markerLogger
): CustomMarker {
    val marker = CustomMarker(omhMapImpl, mapView)

    marker.position = position.toGeoPoint()
    marker.title = title
    marker.isDraggable = draggable

    marker.setAnchor(anchor.first, anchor.second)
    marker.setInfoWindowAnchor(infoWindowAnchor.first, infoWindowAnchor.second)
    marker.alpha = alpha

    // since setVisible controls the alpha in OSM implementation, it needs separate handling after alpha
    if (!isVisible) {
        marker.setVisible(isVisible)
    }

    marker.snippet = snippet
    marker.isFlat = isFlat
    marker.rotation =
        -rotation // counter-clockwise -> clockwise to be consistent with GoogleMaps implementation

    if (icon != null) {
        marker.icon = icon
    } else if (backgroundColor != null) {
        logger.logSetterNotSupported("backgroundColor")
    } else {
        marker.setDefaultIcon()
    }

    return marker
}
