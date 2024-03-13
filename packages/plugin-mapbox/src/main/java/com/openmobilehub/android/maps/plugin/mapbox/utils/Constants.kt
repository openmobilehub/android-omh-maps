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

package com.openmobilehub.android.maps.plugin.mapbox.utils

import android.graphics.Color
import android.view.MotionEvent
import androidx.annotation.ColorInt

internal object Constants {
    @ColorInt
    const val DEFAULT_MARKER_COLOR: Int = 0xFFEA393F.toInt()

    // Common
    const val PROVIDER_NAME = "Mapbox"

    // To have parity with Google Maps
    const val INITIAL_REGION_LATITUDE = 0.0
    const val INITIAL_REGION_LONGITUDE = 0.0
    const val INITIAL_REGION_ZOOM = 0.0

    // UI Controls
    const val MAPBOX_ICON_SIZE = 48
    const val MAPBOX_ICON_MARGIN = 8

    /** The list of motion events that are considered "active" (i.e., issued when the user is dragging). */
    val ACTIVE_MOTION_EVENTS =
        listOf(MotionEvent.ACTION_POINTER_DOWN, MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE)

    /** 5% of smaller screen dimension for hit radius (clipped from the bottom by MAP_TOUCH_HIT_RADIUS_MIN_PX) */
    const val MAP_TOUCH_HIT_RADIUS_PERCENT_OF_SCREEN_DIM = 0.065

    /** Absolute minimum value for hit radius */
    const val MAP_TOUCH_HIT_RADIUS_MIN_PX = 35.0

    /** Absolute maximum value for hit radius */
    const val MAP_TOUCH_HIT_RADIUS_MAX_PX = 85.0

    /** Time threshold for continuous pointer event in [ACTIVE_MOTION_EVENTS] down to be considered a drag */
    const val MAP_TOUCH_DRAG_TOUCHDOWN_THRESHOLD_MS = 550L

    /**
     * The threshold in pixels denoting, how far from the original touch down point can the event happen
     * to be considered still in the same place.
     */
    const val MAP_TOUCH_SAME_COORDINATES_THRESHOLD_PX = 20.0
    const val DEFAULT_POLYLINE_COLOR = Color.BLACK

    // Map Style
    const val DEFAULT_POLYGON_FILL_COLOR = Color.TRANSPARENT
    const val DEFAULT_POLYGON_STROKE_COLOR = Color.BLACK

    // Map Elements
    const val LineString = "LineString"
    const val MARKER_OR_INFO_WINDOW_LAYER_TYPE = "Point"
}
