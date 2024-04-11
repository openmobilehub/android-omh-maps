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

package com.openmobilehub.android.maps.core.presentation.interfaces.maps

import com.openmobilehub.android.maps.core.presentation.models.OmhCoordinate

/**
 * [OmhPolyline] is an interface that provides an abstraction for a polyline on a map.
 * A polyline is a series of connected line segments that can form any shape you want on a map.
 * You can customize the appearance of the polyline by changing its properties.
 */
@SuppressWarnings("TooManyFunctions")
interface OmhPolyline {

    /**
     * Gets the cap of the polyline.
     * @return An [OmhCap] object representing the cap of the polyline.
     */
    fun getCap(): OmhCap?

    /**
     * Sets the cap of the polyline for both ends.
     * @param cap An [OmhCap] object representing the cap of the polyline.
     */
    fun setCap(cap: OmhCap)

    /**
     * Checks if the polyline is clickable.
     * @return A boolean indicating whether the polyline is clickable.
     */
    fun getClickable(): Boolean

    /**
     * Sets the clickable state of the polyline.
     * @param clickable A boolean indicating whether the polyline should be clickable.
     */
    fun setClickable(clickable: Boolean)

    /**
     * Gets the color of the polyline.
     * @return An integer representing the color of the polyline.
     */
    fun getColor(): Int?

    /**
     * Sets the color of the polyline.
     * @param color An integer representing the color of the polyline.
     */
    fun setColor(color: Int)

    /**
     * Gets the end cap of the polyline.
     * @return An [OmhCap] object representing the end cap of the polyline.
     */
    fun getEndCap(): OmhCap?

    /**
     * Sets the end cap of the polyline.
     * @param endCap An [OmhCap] object representing the end cap of the polyline.
     */
    fun setEndCap(endCap: OmhCap)

    /**
     * Gets the joint type of the polyline.
     * @return An integer representing the joint type of the polyline.
     */
    fun getJointType(): Int?

    /**
     * Sets the joint type of the polyline.
     * @param jointType An integer representing the joint type of the polyline.
     */
    fun setJointType(jointType: Int)

    /**
     * Gets the pattern of the polyline.
     * @return A list of [OmhPatternItem] objects representing the pattern of the polyline.
     */
    fun getPattern(): List<OmhPatternItem>?

    /**
     * Sets the pattern of the polyline.
     * @param pattern A list of [OmhPatternItem] objects representing the pattern of the polyline.
     */
    fun setPattern(pattern: List<OmhPatternItem>)

    /**
     * Gets the points that make up the polyline.
     * @return An array of [OmhCoordinate] objects specifying the polyline's points.
     */
    fun getPoints(): List<OmhCoordinate>?

    /**
     * Sets the points that make up the polyline.
     * @param omhCoordinates An array of [OmhCoordinate] objects specifying the polyline's points.
     */
    fun setPoints(omhCoordinates: List<OmhCoordinate>)

    /**
     * Gets the spans of the polyline.
     * @return A list of [OmhStyleSpan] objects representing the spans of the polyline.
     */
    fun getSpans(): List<OmhStyleSpan>?

    /**
     * Sets the spans of the polyline.
     * @param spans A list of [OmhStyleSpan] objects representing the spans of the polyline.
     */
    fun setSpans(spans: List<OmhStyleSpan>)

    /**
     * Gets the start cap of the polyline.
     * @return An [OmhCap] object representing the start cap of the polyline.
     */
    fun getStartCap(): OmhCap?

    /**
     * Sets the start cap of the polyline.
     * @param startCap An [OmhCap] object representing the start cap of the polyline.
     */
    fun setStartCap(startCap: OmhCap)

    /**
     * Retrieves the tag associated with the polyline.
     * @return The tag of the polyline.
     */
    fun getTag(): Any?

    /**
     * Sets the tag associated with the polyline.
     * @param tag The tag to be set for the polyline.
     */
    fun setTag(tag: Any)

    /**
     * Gets the visibility of the polyline.
     * @return A boolean indicating whether the polyline is visible.
     */
    fun isVisible(): Boolean

    /**
     * Sets the visibility of the polyline.
     * @param visible A boolean indicating whether the polyline should be visible.
     */
    fun setVisible(visible: Boolean)

    /**
     * Gets the width of the polyline.
     * @return A float representing the width of the polyline.
     */
    fun getWidth(): Float?

    /**
     * Sets the width of the polyline.
     * @param width A float representing the width of the polyline.
     */
    fun setWidth(width: Float)

    /**
     * Gets the z-index of the polyline.
     * @return A float representing the z-index of the polyline.
     */
    fun getZIndex(): Float?

    /**
     * Sets the z-index of the polyline.
     * @param zIndex A float representing the z-index of the polyline.
     */
    fun setZIndex(zIndex: Float)

    /**
     * Removes the polyline
     */
    fun remove()
}
