package com.openmobilehub.android.maps.sample.maps

import android.graphics.Color
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhMap
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhPolyline
import com.openmobilehub.android.maps.core.presentation.models.OmhCoordinate
import com.openmobilehub.android.maps.core.presentation.models.OmhPolylineOptions

object DebugPolylineHelper {

    fun addSinglePolyline(omhMap: OmhMap): OmhPolyline? {
        val basicPolylineOptions = OmhPolylineOptions().apply {
            points = listOf(
                OmhCoordinate(0.0, 0.0),
                OmhCoordinate(30.0, 10.0),
                OmhCoordinate(20.0, 20.0),
                OmhCoordinate(20.0, 20.0),
                OmhCoordinate(20.0, 30.0),
                OmhCoordinate(10.0, 30.0),
                OmhCoordinate(-10.0, 40.0),
                OmhCoordinate(15.0, 60.0),
            )
            width = 10f
        }

        val basicPolyline = omhMap.addPolyline(basicPolylineOptions)
        basicPolyline?.setTag("Customizable Polyline pressed")

        return basicPolyline
    }

    fun addReferencePolyline(omhMap: OmhMap): OmhPolyline? {
        val basicPolylineOptions = OmhPolylineOptions().apply {
            points = listOf(
                OmhCoordinate(50.0, 25.0),
                OmhCoordinate(-50.0, 25.0),
            )
            color = Color.LTGRAY
            clickable = true
            width = 10f
            zIndex = 50f
        }

        val basicPolyline = omhMap.addPolyline(basicPolylineOptions)
        basicPolyline?.setTag("Reference Polyline pressed")

        return basicPolyline
    }

    fun getRandomizedPoints(): List<OmhCoordinate> {
        return Array(8) {
            val baseLongitude = it * 5
            val minLongitude = baseLongitude - 5
            val maxLongitude = baseLongitude + 5
            OmhCoordinate(
                (-30..30).random().toDouble(),
                (minLongitude..maxLongitude).random().toDouble(),
            )
        }.toList()
    }
}
