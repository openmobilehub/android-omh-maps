package com.openmobilehub.android.maps.plugin.openstreetmap.extensions

import com.openmobilehub.android.maps.core.presentation.models.OmhMarkerOptions
import com.openmobilehub.android.maps.core.utils.UnsupportedFeatureLogger
import com.openmobilehub.android.maps.plugin.openstreetmap.utils.markerLogger
import org.osmdroid.views.overlay.Marker

internal fun OmhMarkerOptions.applyToMarker(
    marker: Marker,
    logger: UnsupportedFeatureLogger = markerLogger
) {
    val invalidateInfoWindow =
        marker.snippet != snippet || marker.title != title

    marker.position = position.toGeoPoint()
    marker.title = title
    marker.isDraggable = isDraggable

    marker.setAnchor(anchor.first, anchor.second)
    marker.alpha = alpha
    marker.snippet = snippet
    marker.setVisible(isVisible)
    marker.isFlat = isFlat
    marker.rotation = rotation

    if (!isVisible && marker.isInfoWindowShown) {
        marker.closeInfoWindow()
    }

    if (icon != null) {
        marker.icon = icon
    } else if (backgroundColor != null) {
        logger.logSetterNotSupported("setBackgroundColor")
    } else {
        marker.setDefaultIcon()
    }

    if (invalidateInfoWindow) {
        if (marker.isInfoWindowShown) {
            marker.showInfoWindow() // open or close & reopen to apply the new contents
        }
    }
}
