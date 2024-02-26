package com.openmobilehub.android.maps.core.presentation.interfaces.maps

/**
 * Abstraction to provide access to callback interface for when a marker has been opened or closed.
 */
fun interface OmhOnInfoWindowOpenStatusChangeListener {
    /**
     * Listener called when a marker is clicked
     * @return true if the default behavior should be suppressed; false otherwise
     */
    fun onMarkerOpenStatusChange(marker: OmhMarker, isOpen: Boolean): Boolean
}
