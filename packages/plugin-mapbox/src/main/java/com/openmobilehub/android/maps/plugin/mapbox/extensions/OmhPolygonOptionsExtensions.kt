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

import com.mapbox.maps.extension.style.layers.generated.FillLayer
import com.mapbox.maps.extension.style.layers.generated.LineLayer
import com.mapbox.maps.extension.style.layers.properties.generated.Visibility
import com.openmobilehub.android.maps.core.presentation.models.OmhPolygonOptions
import com.openmobilehub.android.maps.core.utils.ScreenUnitConverter
import com.openmobilehub.android.maps.core.utils.logging.UnsupportedFeatureLogger
import com.openmobilehub.android.maps.plugin.mapbox.utils.Constants
import com.openmobilehub.android.maps.plugin.mapbox.utils.JoinTypeConverter
import com.openmobilehub.android.maps.plugin.mapbox.utils.polygonLogger

internal fun OmhPolygonOptions.applyPolygonOptions(
    lineLayer: LineLayer,
    fillLayer: FillLayer,
    logger: UnsupportedFeatureLogger = polygonLogger
) {
    strokeColor?.let { lineLayer.lineColor(it) }
        ?: lineLayer.lineColor(Constants.DEFAULT_POLYGON_STROKE_COLOR)
    fillColor?.let { fillLayer.fillColor(it) }
        ?: fillLayer.fillColor(Constants.DEFAULT_POLYGON_FILL_COLOR)
    strokeJointType?.let { lineLayer.lineJoin(JoinTypeConverter.convertToLineJoin(it)) }
    strokePattern?.let { logger.logNotSupported("strokePattern") }
    strokeWidth?.let { lineLayer.lineWidth(ScreenUnitConverter.pxToDp(it).toDouble()) }
    zIndex?.let { logger.logNotSupported("zIndex") }
    isVisible?.let {
        val visibility = if (it) Visibility.VISIBLE else Visibility.NONE
        fillLayer.visibility(visibility)
        lineLayer.visibility(visibility)
    }
}
