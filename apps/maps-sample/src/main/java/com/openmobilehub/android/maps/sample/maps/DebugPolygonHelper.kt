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
                OmhCoordinate(27.5, -27.5),
                OmhCoordinate(27.5, 27.5),
                OmhCoordinate(-27.5, 27.5),
                OmhCoordinate(-27.5, -27.5),
            )
        }

        val polygon = omhMap.addPolygon(polygonOptions)
        polygon?.setTag("Customizable Polygon Pressed")

        return polygon
    }

    fun addReferencePolygon(omhMap: OmhMap): OmhPolygon? {
        val polygonOptions = OmhPolygonOptions().apply {
            outline = listOf(
                OmhCoordinate(-20.0, 25.0),
                OmhCoordinate(-30.0, 20.0),
                OmhCoordinate(-30.0, 30.0),
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

    private fun getRandomizedValue(): Double {
        return (20..40).random().toDouble()
    }

    private fun getNegativeRandomizedValue(): Double {
        return (-40..-20).random().toDouble()
    }

    fun getRandomizedOutlinePoints(): List<OmhCoordinate> {
        val point1 = OmhCoordinate(getRandomizedValue(), getNegativeRandomizedValue())
        val point2 = OmhCoordinate(getRandomizedValue(), getRandomizedValue())
        val point3 = OmhCoordinate(getNegativeRandomizedValue(), getRandomizedValue())
        val point4 = OmhCoordinate(getNegativeRandomizedValue(), getNegativeRandomizedValue())

        return listOf(point1, point2, point3, point4)
    }
}
