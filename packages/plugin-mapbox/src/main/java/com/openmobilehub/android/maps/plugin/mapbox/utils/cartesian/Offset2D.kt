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

data class Offset2D<T : Number>(val x: T, val y: T) {
    operator fun plus(other: Offset2D<T>): Offset2D<T> {
        return when (x) {
            is Int -> Offset2D(
                x.toInt() + other.x.toInt(),
                y.toInt() + other.y.toInt()
            )

            is Double -> Offset2D(
                x.toDouble() + other.x.toDouble(),
                y.toDouble() + other.y.toDouble()
            )

            is Float -> Offset2D(
                x.toFloat() + other.x.toFloat(),
                y.toFloat() + other.y.toFloat()
            )

            else -> throw IllegalArgumentException("Unsupported type passed to Offset2D")
        } as Offset2D<T>
    }
}
