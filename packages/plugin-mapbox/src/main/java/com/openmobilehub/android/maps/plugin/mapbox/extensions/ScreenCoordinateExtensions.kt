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

package com.openmobilehub.android.maps.plugin.mapbox.extensions

import com.mapbox.maps.ScreenCoordinate
import com.openmobilehub.android.maps.plugin.mapbox.utils.cartesian.Offset2D
import kotlin.math.pow
import kotlin.math.sqrt

operator fun ScreenCoordinate.plus(offset: Offset2D<Double>): ScreenCoordinate {
    return ScreenCoordinate(x + offset.x, y + offset.y)
}

fun ScreenCoordinate.distanceTo(other: ScreenCoordinate): Double {
    return sqrt(
        (other.x - x).pow(2.0) + (other.y - y).pow(2.0)
    )
}