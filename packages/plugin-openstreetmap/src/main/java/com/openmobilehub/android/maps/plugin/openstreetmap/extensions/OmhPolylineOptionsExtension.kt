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

import com.openmobilehub.android.maps.core.presentation.models.OmhPolylineOptions
import com.openmobilehub.android.maps.core.utils.UnsupportedFeatureLogger
import org.osmdroid.views.overlay.Polyline

val Logger = UnsupportedFeatureLogger("OmhPolyline", "OpenStreetMap")

internal fun OmhPolylineOptions.toPolylineOptions(): Polyline {
    val mappedOptions = Polyline()

    mappedOptions.setPoints(points.map { it.toGeoPoint() })
    width?.let { mappedOptions.outlinePaint.strokeWidth = it }
    color?.let { mappedOptions.outlinePaint.color = it }
    isVisible?.let { mappedOptions.isVisible = it }

    zIndex?.let {
        Logger.logNotSupported("zIndex")
    }
    jointType.let {
        Logger.logNotSupported("jointType")
    }
    pattern?.let {
        Logger.logNotSupported("pattern")
    }
    startCap?.let {
        Logger.logNotSupported("startCap")
    }
    endCap?.let {
        Logger.logNotSupported("endCap")
    }
    spans?.let {
        Logger.logNotSupported("spans")
    }

    return mappedOptions
}
