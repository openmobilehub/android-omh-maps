package com.openmobilehub.android.maps.plugin.mapbox.extensions

import com.mapbox.maps.extension.style.layers.generated.FillLayer
import com.mapbox.maps.extension.style.layers.generated.LineLayer
import com.mapbox.maps.extension.style.layers.properties.generated.Visibility
import com.openmobilehub.android.maps.core.presentation.models.OmhPolygonOptions
import com.openmobilehub.android.maps.core.utils.logging.UnsupportedFeatureLogger
import com.openmobilehub.android.maps.plugin.mapbox.utils.Constants
import com.openmobilehub.android.maps.plugin.mapbox.utils.JoinTypeConverter
import com.openmobilehub.android.maps.plugin.mapbox.utils.polygonLogger

internal fun OmhPolygonOptions.applyPolygonOptions(
    lineLayer: LineLayer,
    fillLayer: FillLayer,
    scaleFactor: Float,
    logger: UnsupportedFeatureLogger = polygonLogger
) {
    strokeColor?.let { lineLayer.lineColor(it) }
        ?: lineLayer.lineColor(Constants.DEFAULT_POLYGON_STROKE_COLOR)
    fillColor?.let { fillLayer.fillColor(it) }
        ?: fillLayer.fillColor(Constants.DEFAULT_POLYGON_FILL_COLOR)
    strokeJointType?.let { lineLayer.lineJoin(JoinTypeConverter.convertToLineJoin(it)) }
    strokePattern?.let { logger.logNotSupported("strokePattern") }
    strokeWidth?.let { lineLayer.lineWidth(it.toDouble() / scaleFactor) }
    zIndex?.let { logger.logNotSupported("zIndex") }
    isVisible?.let {
        val visibility = if (it) Visibility.VISIBLE else Visibility.NONE
        fillLayer.visibility(visibility)
        lineLayer.visibility(visibility)
    }
}
