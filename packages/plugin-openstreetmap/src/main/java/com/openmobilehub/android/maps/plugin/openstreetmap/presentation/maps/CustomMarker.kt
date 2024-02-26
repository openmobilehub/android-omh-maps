package com.openmobilehub.android.maps.plugin.openstreetmap.presentation.maps

import android.view.MotionEvent
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

internal class CustomMarker(private val omhMapImpl: OmhMapImpl, mapView: MapView) :
    Marker(mapView) {
    private var draggingInProgress = false

    internal fun updateOpenState() {
        if (isInfoWindowShown) {
            if (alpha != 0f) {
                showInfoWindow() // re-open the info window to apply marker changes (e.g. the anchor)
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
        val wasInfoWindowOpen = isInfoWindowShown

        val retVal = super.onLongPress(event, mapView)

        if (wasInfoWindowOpen && !isInfoWindowShown) {
            // re-open the window after long press
            showInfoWindow()

            draggingInProgress = true
        }

        return retVal
    }

    override fun showInfoWindow() {
        val wasInfoWindowShown = isInfoWindowShown

        super.showInfoWindow()

        // to achieve feature parity, we need to ensure that the info window
        // is re-rendered if it was already open before
        if (wasInfoWindowShown) {
            omhMapImpl.reRenderInfoWindows()
        }
    }
}
