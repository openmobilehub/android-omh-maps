package com.openmobilehub.android.maps.plugin.openstreetmap.interfaces

import org.osmdroid.views.overlay.Polygon

interface IPolygonDelegate {
    fun removePolygon(polygon: Polygon)
}
