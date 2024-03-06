package com.openmobilehub.android.maps.plugin.mapbox.utils

import com.mapbox.maps.extension.style.layers.properties.generated.LineCap
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhCap
import com.openmobilehub.android.maps.core.presentation.models.OmhButtCap
import com.openmobilehub.android.maps.core.presentation.models.OmhRoundCap
import com.openmobilehub.android.maps.core.presentation.models.OmhSquareCap

object CapConverter {
    fun convertToLineCap(omhCap: OmhCap): LineCap {
        return when (omhCap) {
            is OmhRoundCap -> LineCap.ROUND
            is OmhSquareCap -> LineCap.SQUARE
            else -> LineCap.BUTT
        }
    }

    fun convertToOmhCap(lineCap: LineCap?): OmhCap {
        return when (lineCap) {
            LineCap.ROUND -> OmhRoundCap()
            LineCap.SQUARE -> OmhSquareCap()
            else -> OmhButtCap()
        }
    }
}
