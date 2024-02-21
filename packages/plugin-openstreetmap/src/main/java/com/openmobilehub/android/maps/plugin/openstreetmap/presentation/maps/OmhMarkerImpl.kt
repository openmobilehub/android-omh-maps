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

package com.openmobilehub.android.maps.plugin.openstreetmap.presentation.maps

import android.graphics.drawable.Drawable
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhMarker
import com.openmobilehub.android.maps.core.presentation.models.OmhCoordinate
import com.openmobilehub.android.maps.core.utils.logging.UnsupportedFeatureLogger
import com.openmobilehub.android.maps.plugin.openstreetmap.extensions.toGeoPoint
import com.openmobilehub.android.maps.plugin.openstreetmap.extensions.toOmhCoordinate
import com.openmobilehub.android.maps.plugin.openstreetmap.utils.markerLogger
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@SuppressWarnings("TooManyFunctions")
internal class OmhMarkerImpl(
    private val marker: Marker,
    private val mapView: MapView,
    private val logger: UnsupportedFeatureLogger = markerLogger
) : OmhMarker {
    override fun getPosition(): OmhCoordinate {
        return marker.position.toOmhCoordinate()
    }

    override fun setPosition(omhCoordinate: OmhCoordinate) {
        marker.position = omhCoordinate.toGeoPoint()
        mapView.postInvalidate()
    }

    override fun getTitle(): String? {
        return marker.title
    }

    override fun setTitle(title: String?) {
        val shouldInvalidateInfoWindow = marker.title != title

        marker.title = title

        mapView.postInvalidate()
        if (shouldInvalidateInfoWindow) {
            invalidateInfoWindow()
        }
    }

    override fun getIsDraggable(): Boolean {
        return marker.isDraggable
    }

    override fun setIsDraggable(isDraggable: Boolean) {
        marker.isDraggable = isDraggable
        mapView.postInvalidate()
    }

    override fun setAnchor(anchorU: Float, anchorV: Float) {
        marker.setAnchor(anchorU, anchorV)
        mapView.postInvalidate()
    }

    override fun getAlpha(): Float {
        return marker.alpha
    }

    override fun setAlpha(alpha: Float) {
        marker.alpha = alpha
        mapView.postInvalidate()
    }

    override fun getSnippet(): String? {
        return marker.snippet
    }

    override fun setSnippet(snippet: String?) {
        val shouldInvalidateInfoWindow = marker.snippet != snippet

        marker.snippet = snippet

        if (shouldInvalidateInfoWindow) {
            invalidateInfoWindow()
        }

        mapView.postInvalidate()
    }

    override fun setIcon(icon: Drawable?) {
        marker.icon = icon
        mapView.postInvalidate()
    }

    override fun getIsVisible(): Boolean {
        return marker.alpha != 0f
    }

    override fun setIsVisible(visible: Boolean) {
        marker.setVisible(visible)

        if (!visible && marker.isInfoWindowShown) {
            marker.closeInfoWindow()
        }

        mapView.postInvalidate()
    }

    override fun getIsFlat(): Boolean {
        return marker.isFlat
    }

    override fun setIsFlat(flat: Boolean) {
        marker.isFlat = flat
        mapView.postInvalidate()
    }

    override fun getRotation(): Float {
        return marker.rotation
    }

    override fun setRotation(rotation: Float) {
        marker.rotation = rotation
        mapView.postInvalidate()
    }

    override fun getBackgroundColor(): Int? {
        logger.logGetterNotSupported("backgroundColor")

        return null
    }

    override fun setBackgroundColor(color: Int?) {
        logger.logSetterNotSupported("setBackgroundColor")
    }

    private fun invalidateInfoWindow() {
        if (marker.isInfoWindowShown) {
            marker.showInfoWindow() // open or close & reopen to apply the new contents
        }
    }
}
