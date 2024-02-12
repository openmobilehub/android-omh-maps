package com.openmobilehub.android.maps.sample.maps

import android.graphics.Color
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhMap
import com.openmobilehub.android.maps.core.presentation.models.OmhCoordinate
import com.openmobilehub.android.maps.core.presentation.models.OmhDash
import com.openmobilehub.android.maps.core.presentation.models.OmhGap
import com.openmobilehub.android.maps.core.presentation.models.OmhJointType
import com.openmobilehub.android.maps.core.presentation.models.OmhPolygonOptions

object DebugPolygonHelper {
    fun addDebugPolygons(omhMap: OmhMap) {
        val polygonOptions = OmhPolygonOptions().apply {
            outline = listOf(
                OmhCoordinate(0.0, 20.0),
                OmhCoordinate(20.0, 20.0),
                OmhCoordinate(20.0, 0.0),
                OmhCoordinate(0.0, 0.0),
            )
            holes = listOf(
                listOf(
                    OmhCoordinate(5.0, 15.0),
                    OmhCoordinate(15.0, 15.0),
                    OmhCoordinate(15.0, 5.0),
                    OmhCoordinate(5.0, 5.0),
                )
            )
            clickable = true
            strokeColor = Color.RED
            fillColor = Color.BLUE
            strokeJointType = OmhJointType.ROUND
            strokeWidth = 30f
            strokePattern = listOf(OmhGap(10f), OmhDash(30f))
        }

        val polygon = omhMap.addPolygon(polygonOptions)
        polygon?.setTag("Debug Polygon")
    }
}
