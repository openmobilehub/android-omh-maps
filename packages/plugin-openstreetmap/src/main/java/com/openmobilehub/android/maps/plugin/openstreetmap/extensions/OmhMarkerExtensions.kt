package com.openmobilehub.android.maps.plugin.openstreetmap.extensions

import com.openmobilehub.android.maps.core.presentation.models.OmhMarkerOptions
import com.openmobilehub.android.maps.core.utils.logging.UnsupportedFeatureLogger
import com.openmobilehub.android.maps.plugin.openstreetmap.presentation.maps.CustomMarker
import com.openmobilehub.android.maps.plugin.openstreetmap.utils.markerLogger
import org.osmdroid.views.MapView

internal fun OmhMarkerOptions.toMarkerOptions(
    mapView: MapView,
    logger: UnsupportedFeatureLogger = markerLogger
): CustomMarker {
    val marker = CustomMarker(mapView)

    marker.position = position.toGeoPoint()
    marker.title = title
    marker.isDraggable = draggable

    marker.setAnchor(anchor.first, anchor.second)
    marker.alpha = alpha

    // since setVisible controls the alpha in OSM implementation, it needs separate handling after alpha
    if (!isVisible) {
        marker.setVisible(isVisible)
    }

    marker.snippet = snippet
    marker.isFlat = isFlat
    marker.rotation =
        -rotation // counter-clockwise -> clockwise to be consistent with GoogleMaps implementation

    if (!isVisible && marker.isInfoWindowShown) {
        marker.closeInfoWindow()
    }

    if (icon != null) {
        marker.icon = icon
    } else if (backgroundColor != null) {
        logger.logSetterNotSupported("backgroundColor")
    } else {
        marker.setDefaultIcon()
    }

    return marker
}
