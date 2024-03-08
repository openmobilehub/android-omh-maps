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

package com.openmobilehub.android.maps.plugin.mapbox.utils.cartesian

import com.mapbox.maps.ScreenCoordinate

/**
 * A 2D bounding box.
 *
 * @constructor Creates a new bounding box with the given center point, width, height and hit border.
 * The `hitBorder` will be divided by 2 and each half will be applied on each of the sides of this BB.
 */
class BoundingBox2D(
    centerPoint: ScreenCoordinate,
    width: Double,
    height: Double,
    hitBorder: Double
) {
    internal val left: Double
    internal val right: Double
    internal val top: Double
    internal val bottom: Double

    init {
        val halfWidthAndHitRadius = (width + hitBorder) / 2.0
        val halfHeightAndHitRadius = (height + hitBorder) / 2.0

        left = centerPoint.x - halfWidthAndHitRadius
        right = centerPoint.x + halfWidthAndHitRadius
        top = centerPoint.y + halfHeightAndHitRadius
        bottom = centerPoint.y - halfHeightAndHitRadius
    }

    /**
     * Tests if the give [screenCoordinate] lays inside the bounding box
     * (in all directions: within half width or height + half of hit border from the center point,
     * inclusive).
     *
     * @param screenCoordinate The screen coordinate to test.
     *
     * @return `true` if the [screenCoordinate] lays inside the bounding box, `false` otherwise.
     */
    fun contains(screenCoordinate: ScreenCoordinate): Boolean {
        return (left..right).contains(screenCoordinate.x) && (bottom..top).contains(screenCoordinate.y)
    }
}
