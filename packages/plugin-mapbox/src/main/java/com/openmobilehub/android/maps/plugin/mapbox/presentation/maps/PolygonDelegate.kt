package com.openmobilehub.android.maps.plugin.mapbox.presentation.maps

import com.openmobilehub.android.maps.core.presentation.models.OmhCoordinate

interface PolygonDelegate {
    fun updatePolygonSource(sourceId: String, outline: List<OmhCoordinate>, holes: List<List<OmhCoordinate>>?)
}
