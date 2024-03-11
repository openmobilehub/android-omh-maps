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

import com.mapbox.maps.extension.style.layers.generated.LineLayer
import com.mapbox.maps.extension.style.layers.properties.generated.Visibility
import com.openmobilehub.android.maps.core.presentation.models.OmhPolylineOptions
import com.openmobilehub.android.maps.core.utils.logging.UnsupportedFeatureLogger
import com.openmobilehub.android.maps.plugin.mapbox.utils.CapConverter
import com.openmobilehub.android.maps.plugin.mapbox.utils.JoinTypeConverter
import com.openmobilehub.android.maps.plugin.mapbox.utils.polygonLogger

internal fun OmhPolylineOptions.applyPolylineOptions(
    lineLayer: LineLayer,
    scaleFactor: Float,
    logger: UnsupportedFeatureLogger = polygonLogger
) {
    color?.let { lineLayer.lineColor(it) }
    width?.let { lineLayer.lineWidth(it.toDouble() / scaleFactor) }
    endCap?.let { logger.logNotSupported("endCap") }
    jointType?.let { lineLayer.lineJoin(JoinTypeConverter.convertToLineJoin(it)) }
    pattern?.let { logger.logNotSupported("pattern") }
    spans?.let { logger.logNotSupported("spans") }
    startCap?.let { logger.logNotSupported("startCap") }
    zIndex?.let { logger.logNotSupported("zIndex") }
    isVisible?.let { lineLayer.visibility(if (it) Visibility.VISIBLE else Visibility.NONE) }
    cap?.let { lineLayer.lineCap(CapConverter.convertToLineCap(it)) }
}
