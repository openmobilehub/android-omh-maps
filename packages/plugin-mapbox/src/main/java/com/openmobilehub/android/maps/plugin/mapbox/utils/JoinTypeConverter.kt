package com.openmobilehub.android.maps.plugin.mapbox.utils

import com.mapbox.maps.extension.style.layers.properties.generated.LineJoin
import com.openmobilehub.android.maps.core.presentation.models.OmhJointType

object JoinTypeConverter {
    fun convertToLineJoin(joinType: Int): LineJoin {
        return when (joinType) {
            OmhJointType.BEVEL -> LineJoin.BEVEL
            OmhJointType.ROUND -> LineJoin.ROUND
            else -> LineJoin.MITER
        }
    }

    fun convertToOmhJointType(lineJoin: LineJoin?): Int {
        return when (lineJoin) {
            LineJoin.BEVEL -> OmhJointType.BEVEL
            LineJoin.ROUND -> OmhJointType.ROUND
            else -> OmhJointType.DEFAULT
        }
    }
}
