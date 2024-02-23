package com.openmobilehub.android.maps.plugin.mapbox.utils

import com.mapbox.geojson.Point
import com.openmobilehub.android.maps.core.presentation.models.OmhCoordinate

internal object CoordinateConverter {
    fun convertToOmhCoordinate(point: Point): OmhCoordinate {
        return OmhCoordinate(point.latitude(), point.longitude())
    }

    fun convertToPoint(coordinate: OmhCoordinate): Point {
        return Point.fromLngLat(coordinate.longitude, coordinate.latitude)
    }
}
