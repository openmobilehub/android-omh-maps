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
import com.azure.android.maps.control.options.LineLayerOptions
import com.azure.android.maps.control.source.DataSource
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhCap
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhPatternItem
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhPolyline
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhStyleSpan
import com.openmobilehub.android.maps.core.presentation.models.OmhCoordinate
import com.openmobilehub.android.maps.core.presentation.models.OmhPolylineOptions
import com.openmobilehub.android.maps.core.utils.logging.UnsupportedFeatureLogger
import com.openmobilehub.android.maps.plugin.azuremaps.presentation.interfaces.IPolylineDelegate
import com.openmobilehub.android.maps.plugin.azuremaps.utils.polylineLogger
import java.util.UUID

@SuppressWarnings("TooManyFunctions")
internal class OmhPolylineImpl(
    private val id: UUID,
    private val source: DataSource,
    private val lineLayer: LineLayer,
    private val delegate: IPolylineDelegate,
    options: OmhPolylineOptions,
    private val logger: UnsupportedFeatureLogger = polylineLogger
) : OmhPolyline {

    private var _clickable: Boolean
    private var _tag: Any? = null
    private var _points: List<OmhCoordinate>
    private var _color: Int = Color.BLUE
        set(value) {
            field = value
            applyColor()
        }

    private var _width: Float = 1.0f
        set(value) {
            field = value
            applyWidth()
        }
    private var _isVisible: Boolean = true
        set(value) {
            field = value
            applyAlpha()
        }

    init {
        _clickable = options.clickable ?: false
        _points = options.points
        options.isVisible?.let { _isVisible = it }
        options.color?.let { _color = it }
        options.width?.let { _width = it }
    }

    override fun getCap(): OmhCap? {
        logger.logGetterNotSupported("cap")
        return null
    }

    override fun setCap(cap: OmhCap) {
        logger.logSetterNotSupported("cap")
    }

    override fun getClickable(): Boolean {
        return _clickable
    }

    override fun setClickable(clickable: Boolean) {
        _clickable = clickable
    }

    override fun getColor(): Int {
        return _color
    }

    override fun setColor(color: Int) {
        _color = color
    }

    override fun getEndCap(): OmhCap? {
        logger.logGetterNotSupported("endCap")
        return null
    }

    override fun setEndCap(endCap: OmhCap) {
        logger.logSetterNotSupported("endCap")
    }

    override fun getJointType(): Int? {
        logger.logGetterNotSupported("jointType")
        return null
    }

    override fun setJointType(jointType: Int) {
        logger.logSetterNotSupported("jointType")
    }

    override fun getPattern(): List<OmhPatternItem>? {
        logger.logGetterNotSupported("pattern")
        return null
    }

    override fun setPattern(pattern: List<OmhPatternItem>) {
        logger.logSetterNotSupported("pattern")
    }

    override fun getPoints(): List<OmhCoordinate> {
        return _points
    }

    override fun setPoints(omhCoordinates: List<OmhCoordinate>) {
        _points = omhCoordinates
        delegate.updatePolylineSourceWithPoints(id, source, _points)
    }

    override fun getSpans(): List<OmhStyleSpan>? {
        logger.logGetterNotSupported("spans")
        return null
    }

    override fun setSpans(spans: List<OmhStyleSpan>) {
        logger.logSetterNotSupported("spans")
    }

    override fun getStartCap(): OmhCap? {
        logger.logGetterNotSupported("startCap")
        return null
    }

    override fun setStartCap(startCap: OmhCap) {
        logger.logSetterNotSupported("startCap")
    }

    override fun getTag(): Any? {
        return _tag
    }

    override fun setTag(tag: Any) {
        _tag = tag
    }

    override fun isVisible(): Boolean {
        return _isVisible
    }

    override fun setVisible(visible: Boolean) {
        _isVisible = visible
    }

    override fun getWidth(): Float {
        return _width
    }

    override fun setWidth(width: Float) {
        _width = width
    }

    override fun getZIndex(): Float? {
        logger.logGetterNotSupported("zIndex")
        return null
    }

    override fun setZIndex(zIndex: Float) {
        logger.logSetterNotSupported("zIndex")
    }

    private fun applyAlpha() {
        lineLayer.setOptions(
            LineLayerOptions.strokeOpacity(if (_isVisible) 1.0f else 0f),
        )
    }

    private fun applyColor() {
        lineLayer.setOptions(
            LineLayerOptions.strokeColor(_color)
        )
    }

    private fun applyWidth() {
        lineLayer.setOptions(
            LineLayerOptions.strokeWidth(_width)
        )
    }

    companion object {
        internal fun getLineLayerID(polylineId: UUID): String {
            return "$polylineId-omh-polyline-layer"
        }

        internal fun getSourceID(polylineId: UUID): String {
            return "$polylineId-omh-polyline-source"
        }
    }
}
