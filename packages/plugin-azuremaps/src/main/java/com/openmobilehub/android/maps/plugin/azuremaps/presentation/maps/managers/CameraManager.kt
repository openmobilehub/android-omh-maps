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

package com.openmobilehub.android.maps.plugin.azuremaps.presentation.maps.managers

import com.azure.android.maps.control.AzureMap
import com.azure.android.maps.control.events.OnCameraIdle
import com.azure.android.maps.control.events.OnCameraMoveStarted
import com.azure.android.maps.control.events.OnLoaded
import com.azure.android.maps.control.options.CameraOptions
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhMapLoadedCallback
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhOnCameraIdleListener
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhOnCameraMoveStartedListener
import com.openmobilehub.android.maps.core.presentation.models.OmhCoordinate
import com.openmobilehub.android.maps.plugin.azuremaps.utils.Constants

class CameraManager(private val mapView: AzureMap) {
    private var zoomGestureEnabled = true

    fun setZoomGesturesEnabled(enableZoomGestures: Boolean) {
        zoomGestureEnabled = enableZoomGestures
        if (enableZoomGestures) {
            return unlockZoomLevel()
        }
        lockZoomLevel()
    }

    fun moveCamera(coordinate: OmhCoordinate, zoomLevel: Float) {
        unlockZoomLevel()

        mapView.setCamera(
            CameraOptions.center(coordinate.latitude, coordinate.longitude),
            CameraOptions.zoom(zoomLevel.toDouble() - Constants.ZOOM_LEVEL_SUBTRAHEND)
        )
        if (!zoomGestureEnabled) {
            lockZoomLevel()
        }
    }

    fun setOnCameraMoveStartedListener(listener: OmhOnCameraMoveStartedListener) {
        mapView.events.add(
            OnCameraMoveStarted { reason ->
                listener.onCameraMoveStarted(reason)
            }
        )
    }

    fun setOnCameraIdleListener(listener: OmhOnCameraIdleListener) {
        mapView.events.add(
            OnCameraIdle {
                listener.onCameraIdle()
            }
        )
    }

    fun setOnMapLoadedCallback(callback: OmhMapLoadedCallback?) {
        mapView.events.add(
            OnLoaded {
                callback?.onMapLoaded()
            }
        )
    }

    fun getCameraPositionCoordinate(): OmhCoordinate {
        return OmhCoordinate(mapView.camera.center.latitude, mapView.camera.center.longitude)
    }

    private fun lockZoomLevel() {
        val currentZoomLevel = mapView.camera.zoom
        mapView.setCamera(
            CameraOptions.minZoom(currentZoomLevel),
            CameraOptions.maxZoom(currentZoomLevel)
        )
    }

    private fun unlockZoomLevel() {
        mapView.setCamera(
            CameraOptions.minZoom(Constants.DEFAULT_MIN_ZOOM),
            CameraOptions.maxZoom(Constants.DEFAULT_MAX_ZOOM)
        )
    }
}
