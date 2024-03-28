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

package com.openmobilehub.android.maps.core.utils

import kotlin.math.absoluteValue

enum class DiscreteAnchor {
    CENTER,
    LEFT,
    RIGHT,
    TOP,
    BOTTOM,
    TOP_LEFT,
    TOP_RIGHT,
    BOTTOM_LEFT,
    BOTTOM_RIGHT,
}

object AnchorConverter {
    /**
     * Converts a continuous anchor to a [DiscreteAnchor], such that ranges are mapped as follows:
     * - `<0; 0.25>` is mapped to left or top
     * - `<0.75; 0.1>` is mapped to right or bottom
     * - `(0.25; 0.75)` is mapped to center
     *
     * Also taking into account combinations, e.g. `Pair(0.1f, 0.9f)` would be mapped to `BOTTOM_LEFT`
     */
    fun genericConvertContinuousToDiscreteAnchor(anchorContinuous: Pair<Float, Float>): DiscreteAnchor {
        val range = 1f
        val halfRange = range / 2f
        val quarterRange = halfRange / 2f
        // for ease of computation, transform the values from range <0; 1> to <-0.5; 0.5>
        val x = anchorContinuous.first - halfRange
        val y = anchorContinuous.second - halfRange

        val xNearCenter = x.absoluteValue < quarterRange
        val yNearCenter = y.absoluteValue < quarterRange
        val xRight = x >= quarterRange
        val yBottom = y >= quarterRange

        return if (xNearCenter || yNearCenter) {
            if (xNearCenter && yNearCenter) {
                DiscreteAnchor.CENTER
            } else if (xNearCenter) {
                if (yBottom) {
                    DiscreteAnchor.BOTTOM
                } else {
                    // yTop
                    DiscreteAnchor.TOP
                }
            } else {
                // yNearCenter
                if (xRight) {
                    DiscreteAnchor.RIGHT
                } else {
                    // xLeft
                    DiscreteAnchor.LEFT
                }
            }
        } else {
            if (xRight) {
                if (yBottom) {
                    DiscreteAnchor.BOTTOM_RIGHT
                } else {
                    // yTop
                    DiscreteAnchor.TOP_RIGHT
                }
            } else {
                // xLeft
                if (yBottom) {
                    DiscreteAnchor.BOTTOM_LEFT
                } else {
                    // yTop
                    DiscreteAnchor.TOP_LEFT
                }
            }
        }
    }
}
