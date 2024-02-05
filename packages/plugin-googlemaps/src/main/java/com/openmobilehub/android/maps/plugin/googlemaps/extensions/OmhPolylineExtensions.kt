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

import com.google.android.gms.maps.model.PolylineOptions
import com.openmobilehub.android.maps.core.presentation.models.OmhPolylineOptions
import com.openmobilehub.android.maps.plugin.googlemaps.utils.CapConverter
import com.openmobilehub.android.maps.plugin.googlemaps.utils.CoordinateConverter
import com.openmobilehub.android.maps.plugin.googlemaps.utils.PatternConverter
import com.openmobilehub.android.maps.plugin.googlemaps.utils.SpanConverter

internal fun OmhPolylineOptions.toPolylineOptions(): PolylineOptions {
    val options = PolylineOptions().addAll(points.map { CoordinateConverter.convertToLatLng(it) })

    color?.let { options.color(it) }
    width?.let { options.width(it) }
    isVisible?.let { options.visible(it) }
    zIndex?.let { options.zIndex(it) }
    jointType?.let { options.jointType(it) }
    pattern?.let {
        options.pattern(
            it.map { patternItem ->
                PatternConverter.convertToPatternItem(
                    patternItem
                )
            }
        )
    }
    startCap?.let {
        val cap = CapConverter.convertToCap(it)
        cap?.let { options.startCap(cap) }
    }
    endCap?.let {
        val cap = CapConverter.convertToCap(it)
        cap?.let { options.endCap(cap) }
    }
    spans?.let { options.addAllSpans(it.map { span -> SpanConverter.convertToStyleSpan(span) }) }

    return options
}
