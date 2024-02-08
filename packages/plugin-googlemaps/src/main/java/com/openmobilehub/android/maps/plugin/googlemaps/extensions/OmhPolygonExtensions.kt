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

import com.google.android.gms.maps.model.PolygonOptions
import com.openmobilehub.android.maps.core.presentation.models.OmhPolygonOptions
import com.openmobilehub.android.maps.plugin.googlemaps.utils.CoordinateConverter
import com.openmobilehub.android.maps.plugin.googlemaps.utils.PatternConverter

internal fun OmhPolygonOptions.toPolygonOptions(): PolygonOptions {
    val options = PolygonOptions()
    options.addAll(outline.map { point -> CoordinateConverter.convertToLatLng(point) })

    holes?.forEach { hole ->
        options.addHole(hole.map { point -> CoordinateConverter.convertToLatLng(point) })
    }

    clickable?.let { options.clickable(it) }
    strokeColor?.let { options.strokeColor(it) }
    fillColor?.let { options.fillColor(it) }
    strokeWidth?.let { options.strokeWidth(it) }
    isVisible?.let { options.visible(it) }
    zIndex?.let { options.zIndex(it) }
    strokeJointType?.let { options.strokeJointType(it) }
    strokePattern?.let {
        options.strokePattern(
            it.map { patternItem ->
                PatternConverter.convertToPatternItem(
                    patternItem
                )
            }
        )
    }

    return options
}
