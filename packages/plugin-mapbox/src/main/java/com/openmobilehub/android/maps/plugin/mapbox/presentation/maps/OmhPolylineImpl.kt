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

import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.layers.addLayer
import com.mapbox.maps.extension.style.layers.generated.LineLayer
import com.mapbox.maps.extension.style.layers.properties.generated.Visibility
import com.mapbox.maps.extension.style.sources.Source
import com.mapbox.maps.extension.style.sources.addSource
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhCap
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhPatternItem
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhPolyline
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhStyleSpan
import com.openmobilehub.android.maps.core.presentation.models.OmhCoordinate
import com.openmobilehub.android.maps.core.presentation.models.OmhPolylineOptions
import com.openmobilehub.android.maps.core.utils.logging.UnsupportedFeatureLogger
import com.openmobilehub.android.maps.plugin.mapbox.presentation.interfaces.IPolylineDelegate
import com.openmobilehub.android.maps.plugin.mapbox.utils.CapConverter
import com.openmobilehub.android.maps.plugin.mapbox.utils.JoinTypeConverter
import com.openmobilehub.android.maps.plugin.mapbox.utils.polylineLogger

@SuppressWarnings("TooManyFunctions")
internal class OmhPolylineImpl(
    private var source: Source,
    private val lineLayer: LineLayer,
    options: OmhPolylineOptions,
    private var scaleFactor: Float,
    private var polylineDelegate: IPolylineDelegate,
    private var logger: UnsupportedFeatureLogger = polylineLogger
) : OmhPolyline {
    private lateinit var style: Style

    // Internal properties
    private var clickable: Boolean = false
    private var points: List<OmhCoordinate> = options.points
    private var tag: Any? = null

    // Buffered properties
    private var bufferedCap: OmhCap? = null
    private var bufferedColor: Int? = null
    private var bufferedJointType: Int? = null
    private var bufferedWidth: Float? = null
    private var bufferedVisibility: Boolean = true

    init {
        options.clickable?.let { clickable = it }
    }

    private fun isStyleReady(): Boolean {
        return this::style.isInitialized && this.style.isStyleLoaded()
    }

    override fun getCap(): OmhCap? {
        if (isStyleReady()) {
            return lineLayer.lineCap?.let { CapConverter.convertToOmhCap(it) }
        }
        return bufferedCap
    }

    override fun setCap(cap: OmhCap) {
        if (isStyleReady()) {
            lineLayer.lineCap(CapConverter.convertToLineCap(cap))
        } else {
            bufferedCap = cap
        }
    }

    override fun getClickable(): Boolean {
        return clickable
    }

    override fun setClickable(clickable: Boolean) {
        this.clickable = clickable
    }

    override fun getColor(): Int? {
        if (isStyleReady()) {
            return lineLayer.lineColorAsColorInt
        }
        return bufferedColor
    }

    override fun setColor(color: Int) {
        if (isStyleReady()) {
            lineLayer.lineColor(color)
        } else {
            bufferedColor = color
        }
    }

    override fun getEndCap(): OmhCap? {
        logger.logGetterNotSupported("endCap")
        return null
    }

    override fun setEndCap(endCap: OmhCap) {
        logger.logSetterNotSupported("endCap")
    }

    override fun getJointType(): Int? {
        if (isStyleReady()) {
            return JoinTypeConverter.convertToOmhJointType(lineLayer.lineJoin)
        }
        return bufferedJointType
    }

    override fun setJointType(jointType: Int) {
        if (isStyleReady()) {
            lineLayer.lineJoin(JoinTypeConverter.convertToLineJoin(jointType))
        } else {
            bufferedJointType = jointType
        }
    }

    override fun getPattern(): List<OmhPatternItem>? {
        logger.logGetterNotSupported("pattern")
        return null
    }

    override fun setPattern(pattern: List<OmhPatternItem>) {
        logger.logSetterNotSupported("pattern")
    }

    override fun getPoints(): List<OmhCoordinate> {
        return points
    }

    override fun setPoints(omhCoordinates: List<OmhCoordinate>) {
        polylineDelegate.updatePolylinePoints(lineLayer.sourceId, omhCoordinates)
        points = omhCoordinates
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
        if (isStyleReady()) {
            return lineLayer.lineWidth?.toFloat()?.times(scaleFactor)
        }
        return bufferedWidth
    }

    override fun setWidth(width: Float) {
        if (isStyleReady()) {
            lineLayer.lineWidth((width / scaleFactor).toDouble())
        } else {
            bufferedWidth = width
        }
    }

    override fun isVisible(): Boolean {
        if (isStyleReady()) {
            return lineLayer.visibility === Visibility.VISIBLE
        }
        return bufferedVisibility
    }

    override fun setVisible(visible: Boolean) {
        if (isStyleReady()) {
            lineLayer.visibility(if (visible) Visibility.VISIBLE else Visibility.NONE)
        } else {
            bufferedVisibility = visible
        }
    }

    override fun getZIndex(): Float? {
        logger.logGetterNotSupported("zIndex")
        return null
    }

    override fun setZIndex(zIndex: Float) {
        logger.logSetterNotSupported("zIndex")
    }

    private fun applyBufferedProperties() {
        bufferedCap?.let { setCap(it) }
        bufferedColor?.let { setColor(it) }
        bufferedJointType?.let { setJointType(it) }
        bufferedWidth?.let { setWidth(it) }
        setVisible(bufferedVisibility)

        bufferedCap = null
        bufferedColor = null
        bufferedJointType = null
        bufferedWidth = null
    }

    private fun addSourceAndLayersToMap() {
        style.addSource(source)
        style.addLayer(lineLayer)
    }

    fun onStyleLoaded(style: Style) {
        check(!isStyleReady()) { "Buffered properties have already been applied" }

        synchronized(this) {
            this.style = style
            addSourceAndLayersToMap()
            applyBufferedProperties()
        }
    }
}
