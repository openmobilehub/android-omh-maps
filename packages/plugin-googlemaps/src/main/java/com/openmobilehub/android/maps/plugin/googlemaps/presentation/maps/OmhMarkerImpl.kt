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

package com.openmobilehub.android.maps.plugin.googlemaps.presentation.maps

import android.graphics.drawable.Drawable
import com.google.android.gms.maps.model.Marker
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhMarker
import com.openmobilehub.android.maps.core.presentation.models.OmhCoordinate
import com.openmobilehub.android.maps.core.utils.UnsupportedFeatureLogger
import com.openmobilehub.android.maps.plugin.googlemaps.utils.CoordinateConverter
import com.openmobilehub.android.maps.plugin.googlemaps.utils.MarkerIconConverter
import com.openmobilehub.android.maps.plugin.googlemaps.utils.markerLogger

@SuppressWarnings("TooManyFunctions")
internal class OmhMarkerImpl(
    private val marker: Marker,
    private val logger: UnsupportedFeatureLogger = markerLogger
) : OmhMarker {
    override fun getPosition(): OmhCoordinate {
        return CoordinateConverter.convertToOmhCoordinate(marker.position)
    }

    override fun setPosition(omhCoordinate: OmhCoordinate) {
        marker.position = CoordinateConverter.convertToLatLng(omhCoordinate)
    }

    override fun getTitle(): String? {
        return marker.title
    }

    override fun setTitle(title: String?) {
        marker.title = title
    }

    override fun getIsDraggable(): Boolean {
        return marker.isDraggable
    }

    override fun setIsDraggable(isDraggable: Boolean) {
        marker.isDraggable = isDraggable
    }

    override fun setAnchor(anchorU: Float, anchorV: Float) {
        this.marker.setAnchor(anchorU, anchorV)
    }

    override fun getAlpha(): Float {
        return marker.alpha
    }

    override fun setAlpha(alpha: Float) {
        marker.alpha = alpha
    }

    override fun getSnippet(): String? {
        return marker.snippet
    }

    override fun setSnippet(snippet: String?) {
        marker.snippet = snippet
    }

    override fun setIcon(icon: Drawable?) {
        if (icon == null) {
            marker.setIcon(null)
        } else {
            marker.setIcon(
                MarkerIconConverter.convertDrawableToBitmapDescriptor(icon)
            )
        }
    }

    override fun getIsVisible(): Boolean {
        return marker.isVisible
    }

    override fun setIsVisible(visible: Boolean) {
        marker.isVisible = visible
    }

    override fun getIsFlat(): Boolean {
        return marker.isFlat
    }

    override fun setIsFlat(flat: Boolean) {
        marker.isFlat = flat
    }

    override fun getRotation(): Float {
        return marker.rotation
    }

    override fun setRotation(rotation: Float) {
        marker.rotation = rotation
    }

    override fun setBackgroundColor(color: Int?) {
        logger.logFeatureSetterPartiallySupported(
            "setBackgroundColor",
            "only hue (H) component of HSV color representation is controllable"
        )
        marker.setIcon(MarkerIconConverter.convertColorToBitmapDescriptor(color))
    }
}
