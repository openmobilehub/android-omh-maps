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
 * [OmhPolygon] is an interface that provides an abstraction for a polygon on a map.
 * A polygon is a shape with multiple edges on a map.
 * You can customize the appearance of the polygon by changing its properties.
 */
@SuppressWarnings("TooManyFunctions")
interface OmhPolygon {

    /**
     * Checks if the polygon is clickable.
     * @return A boolean indicating whether the polygon is clickable.
     */
    fun getClickable(): Boolean

    /**
     * Sets the clickable state of the polygon.
     * @param clickable A boolean indicating whether the polygon should be clickable.
     */
    fun setClickable(clickable: Boolean)

    /**
     * Gets the color of the polygon.
     * @return An integer representing the color of the polygon.
     */
    fun getFillColor(): Int?

    /**
     * Sets the fill color of the polygon.
     * @param color An integer representing the fill color of the polygon.
     */
    fun setFillColor(color: Int)

    /**
     * Gets the holes in the polygon.
     * @return A list of lists of [OmhCoordinate] objects specifying the holes in the polygon.
     */
    fun getHoles(): List<List<OmhCoordinate>>?

    /**
     * Sets the holes in the polygon.
     * @param omhCoordinates A list of lists of [OmhCoordinate] objects specifying the holes in the polygon.
     */
    fun setHoles(omhCoordinates: List<List<OmhCoordinate>>)

    /**
     * Gets the points that make up the polygon.
     * @return A list of [OmhCoordinate] objects specifying the polygon's points.
     */
    fun getOutline(): List<OmhCoordinate>?

    /**
     * Sets the points that make up the polygon.
     * @param omhCoordinates A list of [OmhCoordinate] objects specifying the polygon's points.
     */
    fun setOutline(omhCoordinates: List<OmhCoordinate>)

    /**
     * Get the tag associated with the polygon.
     * @return The tag of the polygon.
     */
    fun getTag(): Any?

    /**
     * Sets the tag associated with the polygon.
     * @param tag The tag to be set for the polygon.
     */
    fun setTag(tag: Any)

    /**
     * Gets the color of the polygon's stroke.
     * @return An integer representing the color of the polygon's stroke.
     */
    fun getStrokeColor(): Int?

    /**
     * Sets the color of the polygon's stroke.
     * @param color An integer representing the color of the polygon's stroke.
     */
    fun setStrokeColor(color: Int)

    /**
     * Gets the joint type of the polygon's stroke.
     * @return An integer representing the joint type of the polygon's stroke.
     */
    fun getStrokeJointType(): Int?

    /**
     * Sets the joint type of the polygon's stroke.
     * @param jointType An integer representing the joint type of the polygon's stroke.
     */
    fun setStrokeJointType(jointType: Int)

    /**
     * Gets the pattern of the polygon's stroke.
     * @return A list of [OmhPatternItem] objects representing the pattern of the polygon's stroke.
     */
    fun getStrokePattern(): List<OmhPatternItem>?

    /**
     * Sets the pattern of the polygon's stroke.
     * @param pattern A list of [OmhPatternItem] objects representing the pattern of the polygon's stroke.
     */
    fun setStrokePattern(pattern: List<OmhPatternItem>)

    /**
     * Gets the width of the polygon's stroke.
     * @return A float representing the width of the polygon's stroke.
     */
    fun getStrokeWidth(): Float?

    /**
     * Sets the width of the polygon's stroke.
     * @param width A float representing the width of the polygon's stroke.
     */
    fun setStrokeWidth(width: Float)

    /**
     * Gets the visibility of the polygon.
     * @return A boolean indicating whether the polygon is visible.
     */
    fun isVisible(): Boolean

    /**
     * Sets the visibility of the polygon.
     * @param visible A boolean indicating whether the polygon should be visible.
     */
    fun setVisible(visible: Boolean)

    /**
     * Gets the z-index of the polygon.
     * @return A float representing the z-index of the polygon.
     */
    fun getZIndex(): Float?

    /**
     * Sets the z-index of the polygon.
     * @param zIndex A float representing the z-index of the polygon.
     */
    fun setZIndex(zIndex: Float)
}
