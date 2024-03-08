package com.openmobilehub.android.maps.plugin.mapbox.presentation.maps

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

@SuppressWarnings("TooManyFunctions")
internal class OmhPolygonImpl(
    private val fillLayer: FillLayer,
    private val lineLayer: LineLayer,
    options: OmhPolygonOptions,
    private val scaleFactor: Float,
    private val polygonDelegate: PolygonDelegate,
    private val logger: UnsupportedFeatureLogger = polygonLogger
) : OmhPolygon {
    private var clickable: Boolean = false
    private var outline: List<OmhCoordinate> = options.outline
    private var holes: List<List<OmhCoordinate>>? = null

    init {
        options.clickable?.let { clickable = it }
        options.holes?.let { holes = it }
    }

    private var tag: Any? = null

    override fun getClickable(): Boolean {
        return clickable
    }

    override fun setClickable(clickable: Boolean) {
        this.clickable = clickable
    }

    override fun getStrokeColor(): Int? {
        return lineLayer.lineColorAsColorInt
    }

    override fun setStrokeColor(color: Int) {
        lineLayer.lineColor(color)
    }

    override fun getFillColor(): Int? {
        return fillLayer.fillColorAsColorInt
    }

    override fun setFillColor(color: Int) {
        fillLayer.fillColor(color)
    }

    override fun getStrokeJointType(): Int? {
        return JoinTypeConverter.convertToOmhJointType(lineLayer.lineJoin)
    }

    override fun setStrokeJointType(jointType: Int) {
        lineLayer.lineJoin(JoinTypeConverter.convertToLineJoin(jointType))
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
        polygonDelegate.updatePolygonOutline(fillLayer.sourceId, omhCoordinates, holes)
    }

    override fun getHoles(): List<List<OmhCoordinate>>? {
        return holes
    }

    override fun setHoles(omhCoordinates: List<List<OmhCoordinate>>) {
        holes = omhCoordinates
        polygonDelegate.updatePolygonHoles(fillLayer.sourceId, outline, omhCoordinates)
    }

    override fun getTag(): Any? {
        return tag
    }

    override fun setTag(tag: Any) {
        this.tag = tag
    }

    override fun getStrokeWidth(): Float? {
        return lineLayer.lineWidth?.toFloat()?.times(scaleFactor)
    }

    override fun setStrokeWidth(width: Float) {
        lineLayer.lineWidth((width / scaleFactor).toDouble())
    }

    override fun getZIndex(): Float? {
        logger.logGetterNotSupported("zIndex")
        return null
    }

    override fun setZIndex(zIndex: Float) {
        logger.logSetterNotSupported("zIndex")
    }

    override fun isVisible(): Boolean {
        return lineLayer.visibility === Visibility.VISIBLE && fillLayer.visibility === Visibility.VISIBLE
    }

    override fun setVisible(visible: Boolean) {
        val visibility = if (visible) Visibility.VISIBLE else Visibility.NONE
        lineLayer.visibility(visibility)
        fillLayer.visibility(visibility)
    }
}
