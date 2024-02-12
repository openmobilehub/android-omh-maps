package com.openmobilehub.android.maps.sample.maps

import android.content.res.Resources
import android.graphics.BitmapFactory
import android.graphics.Color
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhMap
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhPolyline
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
            clickable = true
            width = 10f
        }

        val basicPolyline = omhMap.addPolyline(basicPolylineOptions)
        basicPolyline?.setTag("Customizable polyline press")

        return basicPolyline
    }

    fun addReferencePolyline(omhMap: OmhMap): OmhPolyline? {
        val basicPolylineOptions = OmhPolylineOptions().apply {
            points = listOf(
                OmhCoordinate(30.0, 25.0),
                OmhCoordinate(10.0, 25.0),
            )
            color = Color.LTGRAY
            clickable = true
            width = 10f
            zIndex = 50f
        }

        val basicPolyline = omhMap.addPolyline(basicPolylineOptions)
        basicPolyline?.setTag("Reference polyline press")

        return basicPolyline
    }

    fun addDebugPolylines(omhMap: OmhMap, resources: Resources) {
        val basicPolylineOptions = OmhPolylineOptions().apply {
            points = listOf(
                OmhCoordinate(55.0, 15.0),
                OmhCoordinate(56.0, 19.0),
                OmhCoordinate(55.0, 24.0),
            )
            clickable = true
        }

        val colorPolylineOptions = OmhPolylineOptions().apply {
            points = listOf(
                OmhCoordinate(54.0, 15.0),
                OmhCoordinate(55.0, 19.0),
                OmhCoordinate(54.0, 24.0),
            )
            color = Color.BLUE
            clickable = true
        }

        val customWidthPolylineOptions = OmhPolylineOptions().apply {
            points = listOf(
                OmhCoordinate(53.0, 15.0),
                OmhCoordinate(54.0, 19.0),
                OmhCoordinate(53.0, 24.0),
            )
            color = Color.BLUE
            width = 50f
            clickable = true
        }

        val customPatternPolylineOptions = OmhPolylineOptions().apply {
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
            clickable = true
        }

        val roundedJointTypePolylineOptions = OmhPolylineOptions().apply {
            points = listOf(
                OmhCoordinate(51.0, 15.0),
                OmhCoordinate(52.0, 19.0),
                OmhCoordinate(51.0, 24.0),
            )
            color = Color.BLUE
            jointType = OmhJointType.ROUND
            clickable = true
        }

        val customCapPolylineOptions = OmhPolylineOptions().apply {
            points = listOf(
                OmhCoordinate(50.0, 15.0),
                OmhCoordinate(51.0, 19.0),
                OmhCoordinate(50.0, 24.0),
            )
            color = Color.BLUE
            startCap =
                OmhCustomCap(BitmapFactory.decodeResource(resources, R.drawable.soccer_ball), 35f)
            endCap =
                OmhCustomCap(BitmapFactory.decodeResource(resources, R.drawable.soccer_ball), 35f)
            clickable = true
        }

        val roundedCapPolylineOptions = OmhPolylineOptions().apply {
            points = listOf(
                OmhCoordinate(49.0, 15.0),
                OmhCoordinate(50.0, 19.0),
                OmhCoordinate(49.0, 24.0),
            )
            color = Color.BLUE
            startCap = OmhRoundCap()
            endCap = OmhRoundCap()
            clickable = true
        }

        val customSpansPolylineOptions = OmhPolylineOptions().apply {
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
                OmhStyleSpanMonochromatic(
                    Color.GREEN,
                    null,
                    BitmapFactory.decodeResource(resources, R.drawable.soccer_ball)
                ),
            )
            width = 30f
            clickable = true
        }

        val increasedZIndexPolylineOptions = OmhPolylineOptions().apply {
            points = listOf(
                OmhCoordinate(57.0, 16.0),
                OmhCoordinate(45.0, 18.0),
            )
            color = Color.RED
            zIndex = 10f
            clickable = true
        }

        val basicPolyline = omhMap.addPolyline(basicPolylineOptions)
        basicPolyline?.setTag("Basic Polyline")

        val colorPolyline = omhMap.addPolyline(colorPolylineOptions)
        colorPolyline?.setTag("Custom Color Polyline")

        val customWidthPolyline = omhMap.addPolyline(customWidthPolylineOptions)
        customWidthPolyline?.setTag("Custom Width Polyline")

        val customPatternPolyline = omhMap.addPolyline(customPatternPolylineOptions)
        customPatternPolyline?.setTag("Custom Pattern Polyline")

        val roundedJointTypePolyline = omhMap.addPolyline(roundedJointTypePolylineOptions)
        roundedJointTypePolyline?.setTag("Rounded Joint Type Polyline")

        val customCapPolyline = omhMap.addPolyline(customCapPolylineOptions)
        customCapPolyline?.setTag("Custom Cap Polyline")

        val roundedCapPolyline = omhMap.addPolyline(roundedCapPolylineOptions)
        roundedCapPolyline?.setTag("Rounded Cap Polyline")

        val increasedZIndexPolyline = omhMap.addPolyline(increasedZIndexPolylineOptions)
        increasedZIndexPolyline?.setTag("Increased Z-Index Polyline")

        val customSpansPolyline = omhMap.addPolyline(customSpansPolylineOptions)
        customSpansPolyline?.setTag("Custom Spans Polyline")
    }
}
