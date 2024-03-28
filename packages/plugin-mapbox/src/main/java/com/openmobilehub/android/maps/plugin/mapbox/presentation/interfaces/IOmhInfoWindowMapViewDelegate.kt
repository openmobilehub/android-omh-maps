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

package com.openmobilehub.android.maps.plugin.mapbox.presentation.interfaces

import com.mapbox.geojson.Point
import com.mapbox.maps.ScreenCoordinate
import com.openmobilehub.android.maps.plugin.mapbox.presentation.maps.OmhInfoWindow

/**
 * Delegate interface having capabilities to provide interfaces for the [OmhInfoWindow] to access the map.
 */
internal interface IOmhInfoWindowMapViewDelegate {
    /**
     * Returns the width of the map in pixels.
     */
    fun getMapWidth(): Int

    /**
     * Returns the height of the map in pixels.
     */
    fun getMapHeight(): Int

    /**
     * Converts the screen coordinate to the map position.
     *
     * @param screenCoordinate The screen coordinate to convert (in pixels).
     *
     * @return The map position corresponding to the screen coordinate.
     */
    fun coordinateForPixel(screenCoordinate: ScreenCoordinate): Point

    /**
     * Converts the map position to the screen coordinate.
     *
     * @param point The map position to convert.
     *
     * @return The screen coordinate corresponding to the map position (in pixels).
     */
    fun pixelForCoordinate(point: Point): ScreenCoordinate
}
