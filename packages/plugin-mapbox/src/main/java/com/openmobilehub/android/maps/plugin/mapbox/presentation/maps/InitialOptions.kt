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

package com.openmobilehub.android.maps.plugin.mapbox.presentation.maps

import com.mapbox.maps.extension.style.layers.generated.LineLayer
import com.mapbox.maps.extension.style.layers.properties.generated.Visibility
import com.openmobilehub.android.maps.core.presentation.models.OmhPolylineOptions
import com.openmobilehub.android.maps.core.utils.logging.UnsupportedFeatureLogger
import com.openmobilehub.android.maps.plugin.mapbox.utils.CapConverter
import com.openmobilehub.android.maps.plugin.mapbox.utils.JoinTypeConverter
import com.openmobilehub.android.maps.plugin.mapbox.utils.polylineLogger

object InitialOptions {
    fun applyPolylineOptions(
        layer: LineLayer,
        options: OmhPolylineOptions,
        scaleFactor: Float,
        logger: UnsupportedFeatureLogger = polylineLogger
    ) {
        options.color?.let { layer.lineColor(it) }
        options.width?.let { layer.lineWidth(it.toDouble() / scaleFactor) }
        options.endCap?.let { logger.logNotSupported("endCap") }
        options.jointType?.let { layer.lineJoin(JoinTypeConverter.convertToLineJoin(it)) }
        options.pattern?.let { logger.logNotSupported("pattern") }
        options.spans?.let { logger.logNotSupported("spans") }
        options.startCap?.let { logger.logNotSupported("startCap") }
        options.zIndex?.let { logger.logNotSupported("zIndex") }
        options.isVisible?.let { layer.visibility(if (it) Visibility.VISIBLE else Visibility.NONE) }
        options.cap?.let { layer.lineCap?.let { CapConverter.convertToOmhCap(it) } }
    }
}
