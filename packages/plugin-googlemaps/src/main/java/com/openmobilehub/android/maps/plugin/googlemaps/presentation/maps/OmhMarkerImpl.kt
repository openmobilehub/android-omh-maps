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
import com.openmobilehub.android.maps.core.presentation.models.Constants
import com.openmobilehub.android.maps.core.presentation.models.OmhCoordinate
import com.openmobilehub.android.maps.core.utils.logging.UnsupportedFeatureLogger
import com.openmobilehub.android.maps.plugin.googlemaps.presentation.interfaces.IMarkerDelegate
import com.openmobilehub.android.maps.plugin.googlemaps.utils.CoordinateConverter
import com.openmobilehub.android.maps.plugin.googlemaps.utils.MarkerIconConverter
import com.openmobilehub.android.maps.plugin.googlemaps.utils.markerLogger

@SuppressWarnings("TooManyFunctions")
internal class OmhMarkerImpl(
    private val marker: Marker,
    private var clickable: Boolean = true,
    private val logger: UnsupportedFeatureLogger = markerLogger,
    private val markerDelegate: IMarkerDelegate
) : OmhMarker {

    private var lastInfoWindowAnchor: Pair<Float, Float> =
        Pair(Constants.ANCHOR_CENTER, Constants.ANCHOR_TOP)

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
        val shouldInvalidateInfoWindow = marker.title != title

        marker.title = title

        if (shouldInvalidateInfoWindow) {
            invalidateInfoWindow()
        }
    }

    override fun getClickable(): Boolean {
        return clickable
    }

    override fun setClickable(clickable: Boolean) {
        this.clickable = clickable
    }

    override fun getDraggable(): Boolean {
        return marker.isDraggable
    }

    override fun setDraggable(draggable: Boolean) {
        marker.isDraggable = draggable
    }

    override fun setAnchor(anchorU: Float, anchorV: Float) {
        this.marker.setAnchor(anchorU, anchorV)
    }

    override fun setInfoWindowAnchor(iwAnchorU: Float, iwAnchorV: Float) {
        this.marker.setInfoWindowAnchor(iwAnchorU, iwAnchorV)
        lastInfoWindowAnchor = Pair(iwAnchorU, iwAnchorV)
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
        val shouldInvalidateInfoWindow = marker.snippet != snippet

        marker.snippet = snippet

        if (shouldInvalidateInfoWindow) {
            invalidateInfoWindow()
        }
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

    override fun getBackgroundColor(): Int? {
        logger.logGetterNotSupported("backgroundColor")

        return null
    }

    override fun setBackgroundColor(color: Int?) {
        if (color != null) {
            logger.logFeatureSetterPartiallySupported(
                "backgroundColor",
                "only hue (H) component of HSV color representation is controllable, alpha channel is unsupported"
            )
        }

        marker.setIcon(MarkerIconConverter.convertColorToBitmapDescriptor(color))
    }

    override fun showInfoWindow() {
        marker.showInfoWindow()
    }

    override fun hideInfoWindow() {
        marker.hideInfoWindow()
    }

    override fun getIsInfoWindowShown(): Boolean {
        return marker.isInfoWindowShown
    }

    override fun invalidateInfoWindow() {
        if (marker.isInfoWindowShown) {
            marker.showInfoWindow() // open or close-and-reopen to apply the new contents
        }
    }

    override fun remove() {
        markerDelegate.removeMarker(marker)

        marker.remove()
    }

    override fun getZIndex(): Float {
        return marker.zIndex
    }

    override fun setZIndex(zIndex: Float) {
        marker.zIndex = zIndex
    }
}
