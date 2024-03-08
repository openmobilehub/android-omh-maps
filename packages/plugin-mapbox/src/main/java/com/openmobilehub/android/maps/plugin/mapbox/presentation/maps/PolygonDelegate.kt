package com.openmobilehub.android.maps.plugin.mapbox.presentation.maps

import com.openmobilehub.android.maps.core.presentation.models.OmhCoordinate

interface PolygonDelegate {
    fun updatePolygonHoles(sourceId: String, outline: List<OmhCoordinate>, holes: List<List<OmhCoordinate>>)
    fun updatePolygonOutline(sourceId: String, outline: List<OmhCoordinate>, holes: List<List<OmhCoordinate>>?)
}
