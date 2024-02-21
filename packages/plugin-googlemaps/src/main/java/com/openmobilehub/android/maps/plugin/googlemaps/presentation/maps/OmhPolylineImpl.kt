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

package com.openmobilehub.android.maps.plugin.googlemaps.presentation.maps

import com.google.android.gms.maps.model.Polyline
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhCap
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhPatternItem
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhPolyline
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhStyleSpan
import com.openmobilehub.android.maps.core.presentation.models.OmhCoordinate
import com.openmobilehub.android.maps.core.utils.logging.UnsupportedFeatureLogger
import com.openmobilehub.android.maps.plugin.googlemaps.utils.CapConverter
import com.openmobilehub.android.maps.plugin.googlemaps.utils.CoordinateConverter
import com.openmobilehub.android.maps.plugin.googlemaps.utils.PatternConverter
import com.openmobilehub.android.maps.plugin.googlemaps.utils.SpanConverter
import com.openmobilehub.android.maps.plugin.googlemaps.utils.polylineLogger

@SuppressWarnings("TooManyFunctions")
internal class OmhPolylineImpl(
    private val polyline: Polyline,
    private val logger: UnsupportedFeatureLogger = polylineLogger
) : OmhPolyline {

    override fun getClickable(): Boolean {
        return polyline.isClickable
    }

    override fun setClickable(clickable: Boolean) {
        polyline.isClickable = clickable
    }

    override fun getColor(): Int {
        return polyline.color
    }

    override fun setColor(color: Int) {
        polyline.color = color
    }

    override fun getEndCap(): OmhCap? {
        logger.logGetterNotSupported("endCap")
        return null
    }

    override fun setEndCap(endCap: OmhCap?) {
        if (endCap == null) {
            return
        }

        CapConverter.convertToCap(endCap)?.let {
            polyline.endCap = it
        }
    }

    override fun getJointType(): Int {
        return polyline.jointType
    }

    override fun setJointType(jointType: Int) {
        polyline.jointType = jointType
    }

    override fun getPattern(): List<OmhPatternItem>? {
        return polyline.pattern?.mapNotNull { patternItem ->
            PatternConverter.convertToOmhPatternItem(patternItem)
        }
    }

    override fun setPattern(pattern: List<OmhPatternItem>?) {
        polyline.pattern = pattern?.map { patternItem ->
            PatternConverter.convertToPatternItem(patternItem)
        }
    }

    override fun getPoints(): List<OmhCoordinate> {
        return polyline.points.map { CoordinateConverter.convertToOmhCoordinate(it) }
    }

    override fun setPoints(omhCoordinates: List<OmhCoordinate>) {
        polyline.points = omhCoordinates.map { CoordinateConverter.convertToLatLng(it) }
    }

    override fun getSpans(): List<OmhStyleSpan>? {
        logger.logGetterNotSupported("spans")
        return null
    }

    override fun setSpans(spans: List<OmhStyleSpan>?) {
        spans?.let {
            polyline.spans = spans.map { span -> SpanConverter.convertToStyleSpan(span) }
        }
    }

    override fun getStartCap(): OmhCap? {
        logger.logGetterNotSupported("startCap")
        return null
    }

    override fun setStartCap(startCap: OmhCap?) {
        if (startCap == null) {
            return
        }

        CapConverter.convertToCap(startCap)?.let {
            polyline.startCap = it
        }
    }

    override fun getTag(): Any? {
        return polyline.tag
    }

    override fun setTag(tag: Any?) {
        polyline.tag = tag
    }

    override fun getWidth(): Float {
        return polyline.width
    }

    override fun setWidth(width: Float) {
        polyline.width = width
    }

    override fun isVisible(): Boolean {
        return polyline.isVisible
    }

    override fun setVisible(visible: Boolean) {
        polyline.isVisible = visible
    }

    override fun getZIndex(): Float {
        return polyline.zIndex
    }

    override fun setZIndex(zIndex: Float) {
        polyline.zIndex = zIndex
    }
}
