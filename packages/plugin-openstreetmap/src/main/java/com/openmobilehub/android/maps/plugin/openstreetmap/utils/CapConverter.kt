package com.openmobilehub.android.maps.plugin.openstreetmap.utils

import android.graphics.Paint
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhCap
import com.openmobilehub.android.maps.core.presentation.models.OmhButtCap
import com.openmobilehub.android.maps.core.presentation.models.OmhRoundCap
import com.openmobilehub.android.maps.core.presentation.models.OmhSquareCap

object CapConverter {
    fun convertToPaintCap(omhCap: OmhCap): Paint.Cap {
        return when (omhCap) {
            is OmhRoundCap -> Paint.Cap.ROUND
            is OmhSquareCap -> Paint.Cap.SQUARE
            else -> Paint.Cap.BUTT
        }
    }

    fun convertToOmhCap(lineCap: Paint.Cap): OmhCap {
        return when (lineCap) {
            Paint.Cap.ROUND -> OmhRoundCap()
            Paint.Cap.SQUARE -> OmhSquareCap()
            else -> OmhButtCap()
        }
    }
}
