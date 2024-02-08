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

import com.google.android.gms.maps.model.Polygon
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhPatternItem
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhPolygon
import com.openmobilehub.android.maps.core.presentation.models.OmhCoordinate
import com.openmobilehub.android.maps.plugin.googlemaps.utils.CoordinateConverter
import com.openmobilehub.android.maps.plugin.googlemaps.utils.PatternConverter

@SuppressWarnings("TooManyFunctions")
internal class OmhPolygonImpl(
    private val polygon: Polygon
) : OmhPolygon {

    override fun getClickable(): Boolean {
        return polygon.isClickable
    }

    override fun setClickable(clickable: Boolean) {
        polygon.isClickable = clickable
    }

    override fun getStrokeColor(): Int {
        return polygon.strokeColor
    }

    override fun setStrokeColor(color: Int) {
        polygon.strokeColor = color
    }

    override fun getFillColor(): Int {
        return polygon.fillColor
    }

    override fun setFillColor(color: Int) {
        polygon.fillColor = color
    }

    override fun getStrokeJointType(): Int {
        return polygon.strokeJointType
    }

    override fun setStrokeJointType(jointType: Int) {
        polygon.strokeJointType = jointType
    }

    override fun getStrokePattern(): List<OmhPatternItem>? {
        return polygon.strokePattern?.mapNotNull { patternItem ->
            PatternConverter.convertToOmhPatternItem(patternItem)
        }
    }

    override fun setStrokePattern(pattern: List<OmhPatternItem>?) {
        pattern?.let {
            polygon.strokePattern = it.map { patternItem ->
                PatternConverter.convertToPatternItem(patternItem)
            }
        }
    }

    override fun getOutline(): List<OmhCoordinate> {
        return polygon.points.map { CoordinateConverter.convertToOmhCoordinate(it) }
    }

    override fun setOutline(omhCoordinates: List<OmhCoordinate>) {
        polygon.points = omhCoordinates.map { CoordinateConverter.convertToLatLng(it) }
    }

    override fun getHoles(): List<List<OmhCoordinate>> {
        return polygon.holes.map { hole ->
            hole.map { CoordinateConverter.convertToOmhCoordinate(it) }
        }
    }

    override fun setHoles(omhCoordinates: List<List<OmhCoordinate>>) {
        polygon.holes = omhCoordinates.map { hole ->
            hole.map { CoordinateConverter.convertToLatLng(it) }
        }
    }

    override fun getTag(): Any? {
        return polygon.tag
    }

    override fun setTag(tag: Any?) {
        polygon.tag = tag
    }

    override fun getStrokeWidth(): Float {
        return polygon.strokeWidth
    }

    override fun setStrokeWidth(width: Float) {
        polygon.strokeWidth = width
    }

    override fun isVisible(): Boolean {
        return polygon.isVisible
    }

    override fun setVisible(visible: Boolean) {
        polygon.isVisible = visible
    }

    override fun getZIndex(): Float {
        return polygon.zIndex
    }

    override fun setZIndex(zIndex: Float) {
        polygon.zIndex = zIndex
    }
}
