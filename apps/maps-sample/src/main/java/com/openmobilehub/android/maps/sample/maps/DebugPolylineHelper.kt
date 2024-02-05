package com.openmobilehub.android.maps.sample.maps

import android.content.res.Resources
import android.graphics.BitmapFactory
import android.graphics.Color
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhMap
import com.openmobilehub.android.maps.core.presentation.models.OmhCoordinate
import com.openmobilehub.android.maps.core.presentation.models.OmhCustomCap
import com.openmobilehub.android.maps.core.presentation.models.OmhDash
import com.openmobilehub.android.maps.core.presentation.models.OmhDot
import com.openmobilehub.android.maps.core.presentation.models.OmhGap
import com.openmobilehub.android.maps.core.presentation.models.OmhJointType
import com.openmobilehub.android.maps.core.presentation.models.OmhPolylineOptions
import com.openmobilehub.android.maps.core.presentation.models.OmhRoundCap
import com.openmobilehub.android.maps.core.presentation.models.OmhStyleSpanGradient
import com.openmobilehub.android.maps.core.presentation.models.OmhStyleSpanMonochromatic
import com.openmobilehub.android.maps.sample.R

object DebugPolylineHelper {
    fun addDebugPolylines(omhMap: OmhMap, resources: Resources) {
        val basicPolyline = OmhPolylineOptions().apply {
            points = listOf(
                OmhCoordinate(55.0, 15.0),
                OmhCoordinate(56.0, 19.0),
                OmhCoordinate(55.0, 24.0),
            )
        }

        val colorPolyline = OmhPolylineOptions().apply {
            points = listOf(
                OmhCoordinate(54.0, 15.0),
                OmhCoordinate(55.0, 19.0),
                OmhCoordinate(54.0, 24.0),
            )
            color = Color.BLUE
        }

        val customWidthPolyline = OmhPolylineOptions().apply {
            points = listOf(
                OmhCoordinate(53.0, 15.0),
                OmhCoordinate(54.0, 19.0),
                OmhCoordinate(53.0, 24.0),
            )
            color = Color.BLUE
            width = 50f
        }

        val customPatternPolyline = OmhPolylineOptions().apply {
            points = listOf(
                OmhCoordinate(52.0, 15.0),
                OmhCoordinate(53.0, 19.0),
                OmhCoordinate(52.0, 24.0),
            )
            color = Color.BLUE
            pattern = listOf(
                OmhDot(),
                OmhGap(10f),
                OmhDash(20f),
                OmhGap(10f),
                OmhDot(),
                OmhGap(10f),
            )
        }

        val roundedJointTypePolyline = OmhPolylineOptions().apply {
            points = listOf(
                OmhCoordinate(51.0, 15.0),
                OmhCoordinate(52.0, 19.0),
                OmhCoordinate(51.0, 24.0),
            )
            color = Color.BLUE
            jointType = OmhJointType.ROUND
        }

        val customCapPolyline = OmhPolylineOptions().apply {
            points = listOf(
                OmhCoordinate(50.0, 15.0),
                OmhCoordinate(51.0, 19.0),
                OmhCoordinate(50.0, 24.0),
            )
            color = Color.BLUE
            startCap = OmhCustomCap(BitmapFactory.decodeResource(resources, R.drawable.soccer_ball), 35f)
            endCap = OmhCustomCap(BitmapFactory.decodeResource(resources, R.drawable.soccer_ball), 35f)
        }

        val roundedCapPolyline = OmhPolylineOptions().apply {
            points = listOf(
                OmhCoordinate(49.0, 15.0),
                OmhCoordinate(50.0, 19.0),
                OmhCoordinate(49.0, 24.0),
            )
            color = Color.BLUE
            startCap = OmhRoundCap()
            endCap = OmhRoundCap()
        }

        val customSpansPolyline = OmhPolylineOptions().apply {
            points = listOf(
                OmhCoordinate(48.0, 15.0),
                OmhCoordinate(49.0, 19.0),
                OmhCoordinate(48.0, 24.0),
                OmhCoordinate(48.5, 27.0),
                OmhCoordinate(47.5, 30.0),
                OmhCoordinate(49.0, 33.0),
            )
            color = Color.BLUE
            spans = listOf(
                OmhStyleSpanMonochromatic(Color.GREEN),
                OmhStyleSpanGradient(Color.BLUE, Color.RED),
                OmhStyleSpanMonochromatic(Color.RED, 2.0),
                OmhStyleSpanMonochromatic(Color.GREEN, null, BitmapFactory.decodeResource(resources, R.drawable.soccer_ball)),
            )
            width = 30f
        }

        val increasedZIndexPolyline = OmhPolylineOptions().apply {
            points = listOf(
                OmhCoordinate(57.0, 16.0),
                OmhCoordinate(45.0, 18.0),
            )
            color = Color.RED
            zIndex = 10f
        }

        omhMap.addPolyline(colorPolyline)
        omhMap.addPolyline(customWidthPolyline)
        omhMap.addPolyline(customPatternPolyline)
        omhMap.addPolyline(roundedJointTypePolyline)
        omhMap.addPolyline(customCapPolyline)
        omhMap.addPolyline(roundedCapPolyline)
        omhMap.addPolyline(increasedZIndexPolyline)
        omhMap.addPolyline(customSpansPolyline)
    }
}