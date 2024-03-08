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

import com.mapbox.maps.MapView
import com.mapbox.maps.ScreenCoordinate
import com.openmobilehub.android.maps.plugin.mapbox.presentation.maps.managers.MapMarkerManager

/**
 * Delegate interface having capabilities to provide interfaces for the [MapMarkerManager]
 *  to interact with the [MapView].
 */
internal interface IMapMarkerManagerDelegate {
    val mapView: MapView

    /**
     * Queries ID of the layer managed by the [MapMarkerManager] at the given screen
     * coordinate [screenCoordinate] and returns the result through [callback].
     *
     * @param screenCoordinate The screen coordinate to query the layer ID at.
     * @param callback The callback to return the layer ID through. Returns `true` if there was
     * a hit (that does not necessarily have to end with the consummation of any event)
     * or `false` otherwise. A `false` means that the hit was not used and
     * the callback shall be called sequentially with other layer IDs found at the coordinates,
     * unless a `true` is returned or there are no more layers.
     */
    fun queryRenderedLayerIdAt(
        screenCoordinate: ScreenCoordinate,
        callback: (layerId: String?) -> Boolean
    )
}
