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

import android.view.MotionEvent
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

internal class CustomMarker(private val omhMapImpl: OmhMapImpl, mapView: MapView) :
    Marker(mapView) {

    internal fun updateInfoWindowState() {
        if (isInfoWindowOpen) {
            if (alpha != 0f) { // if the marker is visible
                val omhMarker = omhMapImpl.markers[this]

                omhMarker?.let { omhMapImpl.ignoreInfoWindowOpenCloseEvents[omhMarker] = true }
                showInfoWindow() // re-open the info window to apply marker changes (e.g. the anchor)
                omhMarker?.let { omhMapImpl.ignoreInfoWindowOpenCloseEvents[omhMarker] = false }
            } else {
                closeInfoWindow() // close the open window
            }
        }
    }

    override fun setDefaultIcon() {
        // to achieve feature parity, we need to back up the anchor before resetting the icon,
        // since omsdroid implementation sets a custom anchor inside setDefaultIcon
        val anchor = mAnchorU to mAnchorV // back up the anchor before resetting the icon

        super.setDefaultIcon()

        setAnchor(anchor.first, anchor.second) // restore the backed up anchor
    }

    override fun onLongPress(event: MotionEvent?, mapView: MapView?): Boolean {
        // to achieve feature parity, we need to ensure that the info window
        // is not closed upon start of dragging of the marker, which is the default for OSM
        // NOTE: most of this handling is done in onTouchEvent, yet this override prevents
        // the info window from flickering and closing for a brief moment when the user
        // initially long-presses the marker for dragging
        val wasInfoWindowOpen = isInfoWindowOpen

        val omhMarker = omhMapImpl.markers[this]!!
        omhMapImpl.ignoreInfoWindowOpenCloseEvents[omhMarker] = true

        val retVal = super.onLongPress(event, mapView)

        if (wasInfoWindowOpen && !isInfoWindowOpen) {
            // re-open the window after long press
            showInfoWindow()
        }

        omhMapImpl.ignoreInfoWindowOpenCloseEvents[omhMarker] = false

        return retVal
    }

    override fun showInfoWindow() {
        val wasInfoWindowShown = isInfoWindowOpen

        val omhMarker = omhMapImpl.markers[this]
        omhMarker?.let {
            if (wasInfoWindowShown) omhMapImpl.ignoreInfoWindowOpenCloseEvents[omhMarker] = true
        }
        super.showInfoWindow()
        omhMarker?.let {
            if (wasInfoWindowShown) omhMapImpl.ignoreInfoWindowOpenCloseEvents[omhMarker] = true
        }

        // to achieve feature parity, we need to ensure that the info window
        // is re-rendered if it was already open before
        if (wasInfoWindowShown) {
            omhMapImpl.reRenderInfoWindows()
        }
    }

    override fun setPosition(position: GeoPoint?) {
        // since osmdroid setPosition implementation first calls show and then hide on the info window,
        // then ignore the false-positive open state change events

        val omhMarker = omhMapImpl.markers[this]
        omhMarker?.let { omhMapImpl.ignoreInfoWindowOpenCloseEvents[omhMarker] = true }
        super.setPosition(position)
        omhMarker?.let { omhMapImpl.ignoreInfoWindowOpenCloseEvents[omhMarker] = false }
    }
}
