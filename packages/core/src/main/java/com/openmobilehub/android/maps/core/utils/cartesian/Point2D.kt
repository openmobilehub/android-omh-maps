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

package com.openmobilehub.android.maps.core.utils.cartesian

import kotlin.math.pow
import kotlin.math.sqrt

/**
 * A 2D point.
 *
 * @constructor Creates a new 2D point.
 */
data class Point2D<T : Number>(val x: T, val y: T) {
    @JvmName("plusInt")
    operator fun plus(offset: Offset2D<Int>): Point2D<Int> {
        return Point2D(
            x.toInt() + offset.x,
            y.toInt() + offset.y
        )
    }

    @JvmName("plusDouble")
    operator fun plus(offset: Offset2D<Double>): Point2D<Double> {
        return Point2D(
            x.toDouble() + offset.x,
            y.toDouble() + offset.y
        )
    }

    @JvmName("plusFloat")
    operator fun plus(offset: Offset2D<Float>): Point2D<Float> {
        return Point2D(
            x.toFloat() + offset.x,
            y.toFloat() + offset.y
        )
    }

    @JvmName("distanceToInt")
    fun distanceTo(other: Point2D<Int>): Double {
        return Point2D(
            this.x.toDouble(),
            this.y.toDouble()
        ).distanceTo(
            Point2D(
                other.x.toDouble(),
                other.y.toDouble()
            )
        )
    }

    @JvmName("distanceToFloat")
    fun distanceTo(other: Point2D<Float>): Double {
        return Point2D(
            this.x.toDouble(),
            this.y.toDouble()
        ).distanceTo(
            Point2D(
                other.x.toDouble(),
                other.y.toDouble()
            )
        )
    }

    @JvmName("distanceToDouble")
    fun distanceTo(other: Point2D<Double>): Double {
        return sqrt(
            (other.x - x.toDouble()).pow(2.0) + (other.y - y.toDouble()).pow(2.0)
        )
    }
}
