package com.openmobilehub.android.maps.plugin.googlemaps.presentation.interfaces

import com.google.android.gms.maps.model.Polyline

interface IPolylineDelegate {
    fun removePolyline(polyline: Polyline)
}
