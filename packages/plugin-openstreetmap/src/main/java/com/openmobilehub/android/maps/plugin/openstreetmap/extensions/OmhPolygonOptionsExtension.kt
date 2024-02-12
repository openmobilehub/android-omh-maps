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

import android.graphics.Color
import android.graphics.Paint
import com.openmobilehub.android.maps.core.presentation.models.OmhPolygonOptions
import com.openmobilehub.android.maps.core.utils.UnsupportedFeatureLogger
import com.openmobilehub.android.maps.plugin.openstreetmap.utils.polylineLogger
import org.osmdroid.views.overlay.Polygon

internal fun OmhPolygonOptions.toPolygonOptions(logger: UnsupportedFeatureLogger = polylineLogger): Polygon {
    val mappedOptions = Polygon()

    mappedOptions.points = (outline.map { it.toGeoPoint() })
    mappedOptions.holes = holes?.map { it.map { point -> point.toGeoPoint() } }
    clickable?.let { mappedOptions.isEnabled = it }
    strokeWidth?.let { mappedOptions.outlinePaint.strokeWidth = it }
    strokeColor?.let { mappedOptions.outlinePaint.color = it }
    isVisible?.let { mappedOptions.isVisible = it }
    fillColor?.let { mappedOptions.fillPaint.color = it }

    zIndex?.let {
        logger.logNotSupported("zIndex")
    }
    strokeJointType.let {
        logger.logNotSupported("jointType")
    }
    strokePattern?.let {
        logger.logNotSupported("pattern")
    }

    mappedOptions.outlinePaint.set(
        Paint().apply {
            color = Color.RED
        }
    )

    return mappedOptions
}
