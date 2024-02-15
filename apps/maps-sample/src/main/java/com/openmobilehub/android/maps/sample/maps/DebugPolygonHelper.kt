package com.openmobilehub.android.maps.sample.maps

import android.graphics.Color
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhMap
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhPolygon
import com.openmobilehub.android.maps.core.presentation.models.OmhCoordinate
import com.openmobilehub.android.maps.core.presentation.models.OmhDash
import com.openmobilehub.android.maps.core.presentation.models.OmhGap
import com.openmobilehub.android.maps.core.presentation.models.OmhJointType
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
            strokeColor = Color.RED
            fillColor = Color.BLUE
            strokeWidth = 10f
        }

        val polygon = omhMap.addPolygon(polygonOptions)
        polygon?.setTag("Customizable Polygon Pressed")

        return polygon
    }
}
