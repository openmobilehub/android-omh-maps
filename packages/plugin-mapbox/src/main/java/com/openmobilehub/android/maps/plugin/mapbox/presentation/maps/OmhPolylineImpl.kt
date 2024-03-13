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
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhCap
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhPatternItem
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhPolyline
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhStyleSpan
import com.openmobilehub.android.maps.core.presentation.models.OmhCoordinate
import com.openmobilehub.android.maps.core.utils.logging.UnsupportedFeatureLogger
import com.openmobilehub.android.maps.plugin.mapbox.utils.CapConverter
import com.openmobilehub.android.maps.plugin.mapbox.utils.JoinTypeConverter
import com.openmobilehub.android.maps.plugin.mapbox.utils.polylineLogger

@SuppressWarnings("TooManyFunctions")
internal class OmhPolylineImpl(
    private val lineLayer: LineLayer,
    private var clickable: Boolean,
    private var scaleFactor: Float,
    private var polylineDelegate: PolylineDelegate,
    private var logger: UnsupportedFeatureLogger = polylineLogger
) : OmhPolyline {
    private var tag: Any? = null

    override fun getCap(): OmhCap? {
        return lineLayer.lineCap?.let { CapConverter.convertToOmhCap(it) }
    }

    override fun setCap(cap: OmhCap) {
        lineLayer.lineCap(CapConverter.convertToLineCap(cap))
    }

    override fun getClickable(): Boolean {
        return clickable
    }

    override fun setClickable(clickable: Boolean) {
        this.clickable = clickable
    }

    override fun getColor(): Int? {
        return lineLayer.lineColorAsColorInt
    }

    override fun setColor(color: Int) {
        lineLayer.lineColor(color)
    }

    override fun getEndCap(): OmhCap? {
        logger.logGetterNotSupported("endCap")
        return null
    }

    override fun setEndCap(endCap: OmhCap) {
        logger.logSetterNotSupported("endCap")
    }

    override fun getJointType(): Int {
        return JoinTypeConverter.convertToOmhJointType(lineLayer.lineJoin)
    }

    override fun setJointType(jointType: Int) {
        lineLayer.lineJoin(JoinTypeConverter.convertToLineJoin(jointType))
    }

    override fun getPattern(): List<OmhPatternItem>? {
        logger.logGetterNotSupported("pattern")
        return null
    }

    override fun setPattern(pattern: List<OmhPatternItem>) {
        logger.logSetterNotSupported("pattern")
    }

    override fun getPoints(): List<OmhCoordinate>? {
        logger.logGetterNotSupported("points")
        return null
    }

    override fun setPoints(omhCoordinates: List<OmhCoordinate>) {
        polylineDelegate.updatePolylinePoints(lineLayer.sourceId, omhCoordinates)
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
        return tag
    }

    override fun setTag(tag: Any) {
        this.tag = tag
    }

    override fun getWidth(): Float? {
        return lineLayer.lineWidth?.toFloat()?.times(scaleFactor)
    }

    override fun setWidth(width: Float) {
        lineLayer.lineWidth((width / scaleFactor).toDouble())
    }

    override fun isVisible(): Boolean {
        return lineLayer.visibility === Visibility.VISIBLE
    }

    override fun setVisible(visible: Boolean) {
        lineLayer.visibility(if (visible) Visibility.VISIBLE else Visibility.NONE)
    }

    override fun getZIndex(): Float? {
        logger.logGetterNotSupported("zIndex")
        return null
    }

    override fun setZIndex(zIndex: Float) {
        logger.logSetterNotSupported("zIndex")
    }
}
