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

import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhPatternItem
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhPolygon
import com.openmobilehub.android.maps.core.presentation.models.OmhCoordinate
import com.openmobilehub.android.maps.core.utils.UnsupportedFeatureLogger
import com.openmobilehub.android.maps.plugin.mapbox.utils.ConverterUtils
import com.openmobilehub.android.maps.plugin.mapbox.utils.polylineLogger
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Polygon

@SuppressWarnings("TooManyFunctions")
internal class OmhPolygonImpl(
    private val polygon: Polygon,
    private val mapView: MapView,
    initiallyClickable: Boolean,
    private val logger: UnsupportedFeatureLogger = polylineLogger
) : OmhPolygon {
    private var isClickable = initiallyClickable

    override fun getClickable(): Boolean {
        return isClickable
    }

    override fun setClickable(clickable: Boolean) {
        isClickable = clickable
    }

    override fun getStrokeColor(): Int {
        return polygon.outlinePaint.color
    }

    override fun setStrokeColor(color: Int) {
        polygon.outlinePaint.color = color
        mapView.postInvalidate()
    }

    override fun getFillColor(): Int {
        return polygon.fillPaint.color
    }

    override fun setFillColor(color: Int) {
        polygon.fillPaint.color = color
        mapView.postInvalidate()
    }

    override fun getStrokeJointType(): Int {
        logger.logGetterNotSupported("strokeJointType")
        return DEFAULT_JOINT_TYPE
    }

    override fun setStrokeJointType(jointType: Int) {
        logger.logSetterNotSupported("strokeJointType")
    }

    override fun getStrokePattern(): List<OmhPatternItem>? {
        logger.logGetterNotSupported("strokePattern")
        return null
    }

    override fun setStrokePattern(pattern: List<OmhPatternItem>?) {
        logger.logSetterNotSupported("strokePattern")
    }

    override fun getOutline(): List<OmhCoordinate> {
        return polygon.actualPoints.map { ConverterUtils.convertToOmhCoordinate(it) }
    }

    override fun setOutline(omhCoordinates: List<OmhCoordinate>) {
        polygon.points = (omhCoordinates.map { ConverterUtils.convertToGeoPoint(it) })
        mapView.postInvalidate()
    }

    override fun getHoles(): List<List<OmhCoordinate>> {
        return polygon.holes.map { it.map { ConverterUtils.convertToOmhCoordinate(it) } }
    }

    override fun setHoles(omhCoordinates: List<List<OmhCoordinate>>) {
        polygon.holes = omhCoordinates.map { it.map { ConverterUtils.convertToGeoPoint(it) } }
        mapView.postInvalidate()
    }

    override fun getTag(): Any? {
        return polygon.relatedObject
    }

    override fun setTag(tag: Any?) {
        polygon.relatedObject = tag
        mapView.postInvalidate()
    }

    override fun getStrokeWidth(): Float {
        return polygon.outlinePaint.strokeWidth
    }

    override fun setStrokeWidth(width: Float) {
        polygon.outlinePaint.strokeWidth = width
        mapView.postInvalidate()
    }

    override fun getZIndex(): Float {
        logger.logGetterNotSupported("zIndex")
        return DEFAULT_Z_INDEX
    }

    override fun setZIndex(zIndex: Float) {
        logger.logSetterNotSupported("zIndex")
    }

    override fun isVisible(): Boolean {
        return polygon.isVisible
    }

    override fun setVisible(visible: Boolean) {
        polygon.isVisible = visible
        mapView.postInvalidate()
    }

    companion object {
        private const val DEFAULT_JOINT_TYPE = 0
        private const val DEFAULT_Z_INDEX = 0f
    }
}
