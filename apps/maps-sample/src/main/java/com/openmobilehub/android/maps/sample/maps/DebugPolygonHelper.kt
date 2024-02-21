package com.openmobilehub.android.maps.sample.maps

import android.graphics.Color
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhMap
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhPolygon
import com.openmobilehub.android.maps.core.presentation.models.OmhCoordinate
import com.openmobilehub.android.maps.core.presentation.models.OmhPolygonOptions

object DebugPolygonHelper {
    fun addDebugPolygon(omhMap: OmhMap): OmhPolygon? {
        val polygonOptions = OmhPolygonOptions().apply {
            outline = listOf(
                OmhCoordinate(-25.0, -15.0),
                OmhCoordinate(-25.0, 20.0),
                OmhCoordinate(-20.0, 20.0),
                OmhCoordinate(-15.0, 15.0),
                OmhCoordinate(10.0, 15.0),
                OmhCoordinate(20.0, 10.0),
                OmhCoordinate(20.0, -5.0),
                OmhCoordinate(5.0, -15.0),
            )
        }

        val polygon = omhMap.addPolygon(polygonOptions)
        polygon?.setTag("Customizable Polygon Pressed")

        return polygon
    }

    fun addReferencePolygon(omhMap: OmhMap): OmhPolygon? {
        val polygonOptions = OmhPolygonOptions().apply {
            outline = listOf(
                OmhCoordinate(-23.0, 19.5),
                OmhCoordinate(-27.0, 21.5),
                OmhCoordinate(-27.0, 17.5),
            )
            strokeColor = Color.DKGRAY
            fillColor = Color.LTGRAY
            clickable = true
            zIndex = 50f
        }

        val polygon = omhMap.addPolygon(polygonOptions)
        polygon?.setTag("Reference Polygon pressed")

        return polygon
    }
}
