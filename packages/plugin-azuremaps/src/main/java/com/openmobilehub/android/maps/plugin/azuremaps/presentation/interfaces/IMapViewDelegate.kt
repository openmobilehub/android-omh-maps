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

import android.content.Context
import android.graphics.Bitmap
import com.azure.android.maps.control.Popup
import com.azure.android.maps.control.layer.SymbolLayer
import com.azure.android.maps.control.source.DataSource
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
     * Callback invoked when the info window of the marker is long clicked.
     *
     * @param omhMarkerImpl The marker whose info window has been long clicked.
     *
     * @return `true` if the listener was registered & has been called (note: it does not return
     * any boolean value), `false` otherwise.
     */
    fun onInfoWindowLongClick(omhMarkerImpl: OmhMarkerImpl): Boolean

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
     * Returns the [Context] of the map.
     *
     * @return The context of the map.
     */
    fun getContext(): Context

    /**
     * Removes a symbol layer from the map.
     *
     * @param symbolLayer The [SymbolLayer].
     */
    fun removeSymbolLayer(symbolLayer: SymbolLayer)

    /**
     * Removes a data source from the map.
     *
     * @param dataSource The [DataSource].
     */
    fun removeDataSource(dataSource: DataSource)

    /**
     * Removes a popup from the map.
     *
     * @param popup The [Popup].
     */
    fun removePopup(popup: Popup)
}
