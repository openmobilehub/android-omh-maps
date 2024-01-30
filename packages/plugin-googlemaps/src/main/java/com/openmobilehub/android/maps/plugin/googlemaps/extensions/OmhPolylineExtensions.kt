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

import android.graphics.Color
import androidx.lifecycle.Transformations.map
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.gms.maps.model.StyleSpan
import com.openmobilehub.android.maps.core.presentation.models.OmhPolylineOptions
import com.openmobilehub.android.maps.plugin.googlemaps.utils.ConverterUtils


internal fun OmhPolylineOptions.toPolylineOptions(): PolylineOptions {
    return PolylineOptions()
            .add(
                LatLng(0.789258, -122.387819),
                LatLng(0.786572, -122.390556),
                LatLng(0.786140, 30.390899)
            )
            .addSpan(StyleSpan(Color.MAGENTA))
            .addSpan(StyleSpan(Color.GREEN))

//    color?.let { mappedOptions.color(it) }
//    width?.let { mappedOptions.width(it) }
//    isVisible?.let { mappedOptions.visible(it) }
//    zIndex?.let { mappedOptions.zIndex(it) }
//    jointType?.let { mappedOptions.jointType(it) }
//    pattern?.let { mappedOptions.pattern(it.map { ConverterUtils.convertToPatternItem(it) }) }
//    startCap?.let { mappedOptions.startCap(ConverterUtils.convertToCap(it)) }
//    endCap?.let { mappedOptions.endCap(ConverterUtils.convertToCap(it)) }

//    mappedOptions.color(Color.RED)
//    mappedOptions.addSpan(StyleSpan(Color.GREEN, 2.0))
//    return mappedOptions.addAllSpans(listOf(StyleSpan(Color.RED), StyleSpan(Color.BLUE)))


//       Make it work
//        .addAllSpans(
//            listOf(
//                StyleSpan(Color.RED),
//                StyleSpan(Color.BLUE)
//            )
//        )
//        .addSpan(StyleSpan(Color.RED, 2.0))
//        .addSpan(StyleSpan(Color.GREEN))

//    return mappedOptions
}
