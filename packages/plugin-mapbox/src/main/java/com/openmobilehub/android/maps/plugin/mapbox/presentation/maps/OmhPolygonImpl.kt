package com.openmobilehub.android.maps.plugin.mapbox.presentation.maps

import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.layers.generated.FillLayer
import com.mapbox.maps.extension.style.layers.generated.LineLayer
import com.mapbox.maps.extension.style.layers.properties.generated.Visibility
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhPatternItem
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhPolygon
import com.openmobilehub.android.maps.core.presentation.models.OmhCoordinate
import com.openmobilehub.android.maps.core.presentation.models.OmhPolygonOptions
import com.openmobilehub.android.maps.core.utils.logging.UnsupportedFeatureLogger
import com.openmobilehub.android.maps.plugin.mapbox.utils.JoinTypeConverter
import com.openmobilehub.android.maps.plugin.mapbox.utils.polygonLogger

@SuppressWarnings("TooManyFunctions", "LongParameterList")
class OmhPolygonImpl(
    private var style: Style?,
    private val fillLayer: FillLayer,
    private val lineLayer: LineLayer,
    options: OmhPolygonOptions,
    private val scaleFactor: Float,
    private val polygonDelegate: PolygonDelegate,
    private val logger: UnsupportedFeatureLogger = polygonLogger
) : OmhPolygon {
    // Internal properties
    private var clickable: Boolean = false
    private var outline: List<OmhCoordinate> = options.outline
    private var holes: List<List<OmhCoordinate>>? = null
    private var tag: Any? = null

    // Buffered properties
    private var bufferedStrokeColor: Int? = null
    private var bufferedFillColor: Int? = null
    private var bufferedStrokeJointType: Int? = null
    private var bufferedStrokeWidth: Float? = null
    private var bufferedVisibility: Boolean = true

    init {
        options.clickable?.let { clickable = it }
        options.holes?.let { holes = it }
    }

    private fun isStyleLoaded(): Boolean {
        return style?.isStyleLoaded() ?: false
    }

    override fun getClickable(): Boolean {
        return clickable
    }

    override fun setClickable(clickable: Boolean) {
        this.clickable = clickable
    }

    override fun getStrokeColor(): Int? {
        if (isStyleLoaded()) {
            return lineLayer.lineColorAsColorInt
        }
        return bufferedStrokeColor
    }

    override fun setStrokeColor(color: Int) {
        if (isStyleLoaded()) {
            lineLayer.lineColor(color)
        } else {
            bufferedStrokeColor = color
        }
    }

    override fun getFillColor(): Int? {
        if (isStyleLoaded()) {
            return fillLayer.fillColorAsColorInt
        }
        return bufferedFillColor
    }

    override fun setFillColor(color: Int) {
        if (isStyleLoaded()) {
            fillLayer.fillColor(color)
        } else {
            bufferedFillColor = color
        }
    }

    override fun getStrokeJointType(): Int? {
        if (isStyleLoaded()) {
            return JoinTypeConverter.convertToOmhJointType(lineLayer.lineJoin)
        }
        return bufferedStrokeJointType
    }

    override fun setStrokeJointType(jointType: Int) {
        if (isStyleLoaded()) {
            lineLayer.lineJoin(JoinTypeConverter.convertToLineJoin(jointType))
        } else {
            bufferedStrokeJointType = jointType
        }
    }

    override fun getStrokePattern(): List<OmhPatternItem>? {
        logger.logGetterNotSupported("strokePattern")
        return null
    }

    override fun setStrokePattern(pattern: List<OmhPatternItem>) {
        logger.logSetterNotSupported("strokePattern")
    }

    override fun getOutline(): List<OmhCoordinate> {
        return outline
    }

    override fun setOutline(omhCoordinates: List<OmhCoordinate>) {
        outline = omhCoordinates
        polygonDelegate.updatePolygonSource(fillLayer.sourceId, omhCoordinates, holes)
    }

    override fun getHoles(): List<List<OmhCoordinate>>? {
        return holes
    }

    override fun setHoles(omhCoordinates: List<List<OmhCoordinate>>) {
        holes = omhCoordinates
        polygonDelegate.updatePolygonSource(fillLayer.sourceId, outline, omhCoordinates)
    }

    override fun getTag(): Any? {
        return tag
    }

    override fun setTag(tag: Any) {
        this.tag = tag
    }

    override fun getStrokeWidth(): Float? {
        if (isStyleLoaded()) {
            return lineLayer.lineWidth?.toFloat()
                ?.times(scaleFactor)
        }
        return bufferedStrokeWidth
    }

    override fun setStrokeWidth(width: Float) {
        if (isStyleLoaded()) {
            lineLayer.lineWidth((width / scaleFactor).toDouble())
        } else {
            bufferedStrokeWidth = width
        }
    }

    override fun getZIndex(): Float? {
        logger.logGetterNotSupported("zIndex")
        return null
    }

    override fun setZIndex(zIndex: Float) {
        logger.logSetterNotSupported("zIndex")
    }

    override fun isVisible(): Boolean {
        if (isStyleLoaded()) {
            return lineLayer.visibility === Visibility.VISIBLE && fillLayer.visibility === Visibility.VISIBLE
        }
        return bufferedVisibility
    }

    override fun setVisible(visible: Boolean) {
        if (isStyleLoaded()) {
            val visibility = if (visible) Visibility.VISIBLE else Visibility.NONE
            lineLayer.visibility(visibility)
            fillLayer.visibility(visibility)
        } else {
            bufferedVisibility = visible
        }
    }

    fun applyBufferedProperties(style: Style) {
        this.style = style

        bufferedStrokeColor?.let { setStrokeColor(it) }
        bufferedFillColor?.let { setFillColor(it) }
        bufferedStrokeJointType?.let { setStrokeJointType(it) }
        bufferedStrokeWidth?.let { setStrokeWidth(it) }
        setVisible(bufferedVisibility)

        bufferedStrokeColor = null
        bufferedFillColor = null
        bufferedStrokeJointType = null
        bufferedStrokeWidth = null
    }
}
