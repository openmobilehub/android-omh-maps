package com.openmobilehub.android.maps.core.presentation.interfaces.maps

/**
 * Abstraction to provide access to callback interface for when a marker is being dragged.
 */
interface OmhOnMarkerDragListener {
    /**
     * Listener called continuously when the marker is being dragged
     */
    fun onMarkerDrag(marker: OmhMarker)

    /**
     * Listener called when the marker ended being dragged
     */
    fun onMarkerDragEnd(marker: OmhMarker)

    /**
     * Listener called when the marker began being dragged
     */
    fun onMarkerDragStart(marker: OmhMarker)
}
