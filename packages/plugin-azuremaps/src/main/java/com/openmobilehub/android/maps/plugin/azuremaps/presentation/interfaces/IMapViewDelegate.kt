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

package com.openmobilehub.android.maps.plugin.azuremaps.presentation.interfaces

import android.graphics.Bitmap
import com.openmobilehub.android.maps.plugin.azuremaps.presentation.maps.OmhMarkerImpl

/**
 * Delegate interface having capabilities to handle info window events.
 */
internal interface IMapViewDelegate {
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

    /**
     * Removes an image from the map.
     *
     * @param imageId The ID of the image.
     */
    fun removeImage(imageId: String)

    /**
     * Adds an image to the map.
     *
     * @param imageId The ID of the image.
     * @param image The image to add.
     *
     * @return The image ID.
     */
    fun addImage(imageId: String, image: Bitmap)

    /**
     * Returns the width of the map in pixels.
     */
    fun getMapWidth(): Int

    /**
     * Returns the height of the map in pixels.
     */
    fun getMapHeight(): Int
}
