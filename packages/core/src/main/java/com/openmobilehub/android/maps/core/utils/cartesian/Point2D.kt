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
data class Point2D<T : Number>(val x: T, val y: T)

@JvmName("Point2DIntPlusIntToInt")
operator fun Point2D<Int>.plus(offset: Offset2D<Int>): Point2D<Int> {
    return Point2D(x + offset.x, y + offset.y)
}

@JvmName("Point2DDoublePlusDoubleToDouble")
operator fun Point2D<Double>.plus(offset: Offset2D<Double>): Point2D<Double> {
    return Point2D(x + offset.x, y + offset.y)
}

@JvmName("Point2DIntDistanceToInt")
fun Point2D<Int>.distanceTo(other: Point2D<Int>): Double {
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

@JvmName("Point2DDoubleDistanceToDouble")
fun Point2D<Double>.distanceTo(other: Point2D<Double>): Double {
    return sqrt(
        (other.x - x).pow(2.0) + (other.y - y).pow(2.0)
    )
}
