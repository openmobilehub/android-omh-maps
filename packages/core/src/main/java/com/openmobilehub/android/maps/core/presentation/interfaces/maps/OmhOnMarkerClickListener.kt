package com.openmobilehub.android.maps.core.presentation.interfaces.maps

/**
 * Abstraction to provide access to callback interface for when a marker has been clicked.
 */
fun interface OmhOnMarkerClickListener {
    /**
     * Listener called when a marker is clicked
     * @return true if the default behavior should be suppressed; false otherwise
     */
    fun onMarkerClick(marker: OmhMarker): Boolean
}
