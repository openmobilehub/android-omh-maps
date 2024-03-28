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

import com.openmobilehub.android.maps.plugin.mapbox.presentation.maps.OmhMarkerImpl

/**
 * Delegate interface having capabilities to handle info window events.
 */
internal interface IMapInfoWindowManagerDelegate {
    /**
     * Callback invoked when the info window of the marker is clicked.
     *
     * @param omhMarkerImpl The marker whose info window has been clicked.
     */
    fun onInfoWindowClick(omhMarkerImpl: OmhMarkerImpl)

    /**
     * Callback invoked when the info window of a marker is opened or closed.
     *
     * @param omhMarkerImpl The marker whose info window has been opened/closed.
     * @param isOpen `true` if the info window has just been opened, `false` if closed.
     */
    fun onInfoWindowOpenStatusChange(omhMarkerImpl: OmhMarkerImpl, isOpen: Boolean)
}
