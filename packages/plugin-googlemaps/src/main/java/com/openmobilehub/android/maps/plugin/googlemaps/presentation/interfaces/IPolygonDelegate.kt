package com.openmobilehub.android.maps.plugin.googlemaps.presentation.interfaces

import com.google.android.gms.maps.model.Polygon

interface IPolygonDelegate {
    fun removePolygon(polygon: Polygon)
}
