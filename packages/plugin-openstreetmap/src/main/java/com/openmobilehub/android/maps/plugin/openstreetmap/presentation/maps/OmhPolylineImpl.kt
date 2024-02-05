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
import com.openmobilehub.android.maps.core.utils.UnsupportedFeatureLogger
import com.openmobilehub.android.maps.plugin.openstreetmap.utils.ConverterUtils
import org.osmdroid.views.overlay.Polyline

val Logger = UnsupportedFeatureLogger("OmhPolyline", "OpenStreetMap")

@SuppressWarnings("TooManyFunctions")
internal class OmhPolylineImpl(private val polyline: Polyline) : OmhPolyline {

    override fun getColor(): Int {
        return polyline.outlinePaint.color
    }

    override fun setColor(color: Int) {
        polyline.outlinePaint.color = color
    }

    override fun getEndCap(): OmhCap? {
        Logger.logGetterNotSupported("endCap")
        return null
    }

    override fun setEndCap(endCap: OmhCap?) {
        Logger.logSetterNotSupported("endCap")
    }

    override fun getJointType(): Int {
        Logger.logGetterNotSupported("jointType")
        return 0
    }

    override fun setJointType(jointType: Int) {
        Logger.logSetterNotSupported("jointType")
    }

    override fun getPattern(): List<OmhPatternItem>? {
        Logger.logGetterNotSupported("pattern")
        return null
    }

    override fun setPattern(pattern: List<OmhPatternItem>?) {
        Logger.logSetterNotSupported("pattern")
    }

    override fun getPoints(): List<OmhCoordinate> {
        return polyline.actualPoints.map { ConverterUtils.convertToOmhCoordinate(it) }
    }

    override fun setPoints(omhCoordinates: List<OmhCoordinate>) {
        polyline.setPoints(omhCoordinates.map { ConverterUtils.convertToGeoPoint(it) })
    }

    override fun getSpans(): List<OmhStyleSpan>? {
        Logger.logGetterNotSupported("spans")
        return null
    }

    override fun setSpans(spans: List<OmhStyleSpan>?) {
        Logger.logSetterNotSupported("spans")
    }

    override fun getStartCap(): OmhCap? {
        Logger.logGetterNotSupported("startCap")
        return null
    }

    override fun setStartCap(startCap: OmhCap?) {
        Logger.logSetterNotSupported("startCap")
    }

    override fun getWidth(): Float {
        return polyline.outlinePaint.strokeWidth
    }

    override fun setWidth(width: Float) {
        polyline.outlinePaint.strokeWidth = width
    }

    override fun getZIndex(): Float {
        Logger.logGetterNotSupported("zIndex")
        return 0f
    }

    override fun setZIndex(zIndex: Float) {
        Logger.logSetterNotSupported("zIndex")
    }

    override fun isVisible(): Boolean {
        return polyline.isVisible
    }

    override fun setVisible(visible: Boolean) {
        polyline.isVisible = visible
    }
}
