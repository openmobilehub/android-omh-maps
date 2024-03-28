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

package com.openmobilehub.android.maps.plugin.openstreetmap.presentation.maps

import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhCap
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhPatternItem
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhPolyline
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhStyleSpan
import com.openmobilehub.android.maps.core.presentation.models.OmhCoordinate
import com.openmobilehub.android.maps.core.utils.logging.UnsupportedFeatureLogger
import com.openmobilehub.android.maps.plugin.openstreetmap.utils.CapConverter
import com.openmobilehub.android.maps.plugin.openstreetmap.utils.ConverterUtils
import com.openmobilehub.android.maps.plugin.openstreetmap.utils.polylineLogger
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Polyline

@SuppressWarnings("TooManyFunctions")
internal class OmhPolylineImpl(
    private val polyline: Polyline,
    private val mapView: MapView,
    private var clickable: Boolean,
    private val logger: UnsupportedFeatureLogger = polylineLogger
) : OmhPolyline {

    override fun getClickable(): Boolean {
        return clickable
    }

    override fun setClickable(clickable: Boolean) {
        this.clickable = clickable
    }

    override fun getColor(): Int {
        return polyline.outlinePaint.color
    }

    override fun setColor(color: Int) {
        polyline.outlinePaint.color = color
        mapView.postInvalidate()
    }

    override fun getEndCap(): OmhCap? {
        logger.logGetterNotSupported("endCap")
        return null
    }

    override fun setEndCap(endCap: OmhCap) {
        logger.logSetterNotSupported("endCap")
        mapView.postInvalidate()
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
        return polyline.actualPoints.map { ConverterUtils.convertToOmhCoordinate(it) }
    }

    override fun setPoints(omhCoordinates: List<OmhCoordinate>) {
        polyline.setPoints(omhCoordinates.map { ConverterUtils.convertToGeoPoint(it) })
        mapView.postInvalidate()
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
        return polyline.relatedObject
    }

    override fun setTag(tag: Any) {
        polyline.relatedObject = tag
        mapView.postInvalidate()
    }

    override fun getWidth(): Float {
        return polyline.outlinePaint.strokeWidth
    }

    override fun setWidth(width: Float) {
        polyline.outlinePaint.strokeWidth = width
        mapView.postInvalidate()
    }

    override fun getZIndex(): Float? {
        logger.logGetterNotSupported("zIndex")
        return null
    }

    override fun setZIndex(zIndex: Float) {
        logger.logSetterNotSupported("zIndex")
    }

    override fun setCap(cap: OmhCap) {
        polyline.outlinePaint.strokeCap = CapConverter.convertToPaintCap(cap)
        mapView.postInvalidate()
    }

    override fun getCap(): OmhCap? {
        polyline.outlinePaint.strokeCap?.let { return CapConverter.convertToOmhCap(it) }
            ?: return null
    }

    override fun isVisible(): Boolean {
        return polyline.isVisible
    }

    override fun setVisible(visible: Boolean) {
        polyline.isVisible = visible
        mapView.postInvalidate()
    }
}
