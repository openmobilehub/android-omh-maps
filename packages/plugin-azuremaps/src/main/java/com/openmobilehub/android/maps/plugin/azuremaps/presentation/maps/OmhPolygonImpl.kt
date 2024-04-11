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

package com.openmobilehub.android.maps.plugin.azuremaps.presentation.maps

import android.graphics.Color
import com.azure.android.maps.control.layer.LineLayer
import com.azure.android.maps.control.layer.PolygonLayer
import com.azure.android.maps.control.options.LayerOptions
import com.azure.android.maps.control.options.LineLayerOptions
import com.azure.android.maps.control.options.PolygonLayerOptions
import com.azure.android.maps.control.source.DataSource
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhPatternItem
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhPolygon
import com.openmobilehub.android.maps.core.presentation.models.OmhCoordinate
import com.openmobilehub.android.maps.core.presentation.models.OmhJointType
import com.openmobilehub.android.maps.core.presentation.models.OmhPolygonOptions
import com.openmobilehub.android.maps.core.utils.logging.UnsupportedFeatureLogger
import com.openmobilehub.android.maps.plugin.azuremaps.presentation.interfaces.IPolygonDelegate
import com.openmobilehub.android.maps.plugin.azuremaps.utils.JointTypeConverter
import com.openmobilehub.android.maps.plugin.azuremaps.utils.PatternConverter
import com.openmobilehub.android.maps.plugin.azuremaps.utils.polygonLogger
import java.util.UUID

@SuppressWarnings("TooManyFunctions", "LongParameterList")
internal class OmhPolygonImpl(
    val id: UUID,
    private val source: DataSource,
    private val polygonLayer: PolygonLayer,
    private val lineLayer: LineLayer,
    private val delegate: IPolygonDelegate,
    options: OmhPolygonOptions,
    private val logger: UnsupportedFeatureLogger = polygonLogger
) : OmhPolygon {

    private var _clickable: Boolean = false
    private var _tag: Any? = null
    private var _outline: List<OmhCoordinate>
    private var _holes: List<List<OmhCoordinate>>? = null

    private var _strokeColor: Int = Color.BLUE
        set(value) {
            field = value
            applyStrokeColor()
        }

    private var _strokeWidth: Float = 1.0f
        set(value) {
            field = value
            applyStrokeWidth()
        }

    private var _isVisible: Boolean = true
        set(value) {
            field = value
            applyIsVisible()
        }

    private var _strokeJointType: Int = OmhJointType.ROUND
        set(value) {
            field = value
            applyStrokeJointType()
        }

    private var _fillColor: Int = 0
        set(value) {
            field = value
            applyFillColor()
            applyIsVisible()
        }

    private var _strokePattern: List<OmhPatternItem>? = null
        set(value) {
            field = value
            applyStrokePattern()
        }

    init {
        _outline = options.outline
        options.clickable?.let { _clickable = it }
        options.fillColor?.let { _fillColor = it }
        options.holes?.let { _holes = it }
        options.isVisible?.let { _isVisible = it }
        options.strokeColor?.let { _strokeColor = it }
        options.strokeJointType?.let { _strokeJointType = it }
        options.strokePattern?.let { _strokePattern = it }
        options.strokeWidth?.let { _strokeWidth = it }

        applyFillColor()
    }

    override fun getClickable() = _clickable

    override fun setClickable(clickable: Boolean) {
        _clickable = clickable
    }

    override fun getFillColor() = _fillColor

    override fun setFillColor(color: Int) {
        _fillColor = color
    }

    override fun getHoles() = _holes
    override fun setHoles(omhCoordinates: List<List<OmhCoordinate>>) {
        _holes = omhCoordinates
        delegate.updatePolygonSourceWithOutline(id, source, _outline, _holes)
    }

    override fun getOutline() = _outline

    override fun setOutline(omhCoordinates: List<OmhCoordinate>) {
        _outline = omhCoordinates
        delegate.updatePolygonSourceWithOutline(id, source, _outline, _holes)
    }

    override fun getTag() = _tag

    override fun setTag(tag: Any) {
        _tag = tag
    }

    override fun getStrokeColor() = _strokeColor

    override fun setStrokeColor(color: Int) {
        _strokeColor = color
    }

    override fun getStrokeJointType() = _strokeJointType

    override fun setStrokeJointType(jointType: Int) {
        _strokeJointType = jointType
    }

    override fun getStrokePattern() = _strokePattern

    override fun setStrokePattern(pattern: List<OmhPatternItem>) {
        _strokePattern = pattern
    }

    override fun getStrokeWidth() = _strokeWidth

    override fun setStrokeWidth(width: Float) {
        _strokeWidth = width
    }

    override fun isVisible(): Boolean = _isVisible

    override fun setVisible(visible: Boolean) {
        _isVisible = visible
    }

    override fun getZIndex(): Float? {
        logger.logGetterNotSupported("zIndex")
        return null
    }

    override fun setZIndex(zIndex: Float) {
        logger.logSetterNotSupported("zIndex")
    }

    private fun applyStrokeColor() {
        lineLayer.setOptions(
            LineLayerOptions.strokeColor(_strokeColor)
        )
    }

    private fun applyStrokeWidth() {
        lineLayer.setOptions(
            LineLayerOptions.strokeWidth(_strokeWidth)
        )
    }

    private fun applyIsVisible() {
        polygonLayer.setOptions(
            LayerOptions.visible(_isVisible)
        )
        lineLayer.setOptions(
            LayerOptions.visible(_isVisible)
        )
    }

    private fun applyStrokeJointType() {
        lineLayer.setOptions(
            LineLayerOptions.lineJoin(JointTypeConverter.convertToAzureMapsLineJoin(_strokeJointType))
        )
    }

    private fun applyFillColor() {
        polygonLayer.setOptions(
            PolygonLayerOptions.fillColor(_fillColor),
        )
    }

    private fun applyStrokePattern() {
        _strokePattern?.let { pattern ->
            lineLayer.setOptions(
                LineLayerOptions.strokeDashArray(
                    PatternConverter.convertToAzureMapsPattern(pattern, logger)
                        .map { it / _strokeWidth }
                        .toTypedArray()
                )
            )
        }
    }

    override fun remove() {
        delegate.removePolygon(id)
    }

    companion object {
        internal fun getPolygonLayerID(polygonId: UUID): String {
            return "$polygonId-omh-polygon-layer"
        }

        internal fun getPolygonLineLayerID(polygonId: UUID): String {
            return "$polygonId-omh-polygon-line-layer"
        }

        internal fun getSourceID(polygonId: UUID): String {
            return "$polygonId-omh-polygon-source"
        }
    }
}
