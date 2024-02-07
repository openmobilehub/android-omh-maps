/*
 * Copyright 2023 Open Mobile Hub
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.openmobilehub.android.maps.plugin.googlemaps.presentation.extensions

import android.graphics.Color
import com.google.android.gms.maps.model.Dash
import com.google.android.gms.maps.model.Dot
import com.google.android.gms.maps.model.Gap
import com.google.android.gms.maps.model.JointType
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.RoundCap
import com.google.android.gms.maps.model.SquareCap
import com.openmobilehub.android.maps.core.presentation.models.OmhCoordinate
import com.openmobilehub.android.maps.core.presentation.models.OmhDash
import com.openmobilehub.android.maps.core.presentation.models.OmhDot
import com.openmobilehub.android.maps.core.presentation.models.OmhGap
import com.openmobilehub.android.maps.core.presentation.models.OmhJointType
import com.openmobilehub.android.maps.core.presentation.models.OmhPolylineOptions
import com.openmobilehub.android.maps.core.presentation.models.OmhRoundCap
import com.openmobilehub.android.maps.core.presentation.models.OmhSquareCap
import com.openmobilehub.android.maps.core.presentation.models.OmhStyleSpanGradient
import com.openmobilehub.android.maps.core.presentation.models.OmhStyleSpanMonochromatic
import com.openmobilehub.android.maps.plugin.googlemaps.extensions.toPolylineOptions
import com.openmobilehub.android.maps.plugin.googlemaps.utils.CapConverter
import com.openmobilehub.android.maps.plugin.googlemaps.utils.CoordinateConverter
import com.openmobilehub.android.maps.plugin.googlemaps.utils.PatternConverter
import com.openmobilehub.android.maps.plugin.googlemaps.utils.SpanConverter
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

internal class OmhPolylineOptionsTest {
    private val omhPolylineOptions = OmhPolylineOptions().apply {
        points = listOf(
            OmhCoordinate(20.0, 20.0),
            OmhCoordinate(10.0, 10.0),
        )
        clickable = true
        color = Color.RED
        width = 100f
        isVisible = true
        zIndex = 10f
        jointType = OmhJointType.ROUND
        pattern = listOf(
            OmhDot(),
            OmhGap(10f),
            OmhDash(20f),
        )
        startCap = OmhRoundCap()
        endCap = OmhSquareCap()
        spans = listOf(
            OmhStyleSpanMonochromatic(Color.RED),
            OmhStyleSpanGradient(
                Color.RED,
                Color.BLUE,
            )
        )
    }

    @Before
    fun setUp() {
        // Mocking static methods of converters
        mockkObject(CoordinateConverter)
        mockkObject(PatternConverter)
        mockkObject(CapConverter)
        mockkObject(SpanConverter)
    }

    @Test
    fun `toPolylineOptions converts OmhPolylineOptions to PolylineOptions`() {
        // Arrange
        val mockLatLng = mockk<LatLng>()
        every { CoordinateConverter.convertToLatLng(any()) } returns mockLatLng

        // Act
        val polylineOptions = omhPolylineOptions.toPolylineOptions()

        // Assert
        assertEquals(2, polylineOptions.points.size)
        assertEquals(Color.RED, polylineOptions.color)
        assertEquals(100f, polylineOptions.width)
        assertEquals(true, polylineOptions.isVisible)
        assertEquals(10f, polylineOptions.zIndex)
        assertEquals(JointType.ROUND, polylineOptions.jointType)
        assertEquals(3, polylineOptions.pattern?.size)
        assertTrue(polylineOptions.pattern?.get(0) is Dot)
        assertTrue(polylineOptions.pattern?.get(1) is Gap)
        assertTrue(polylineOptions.pattern?.get(2) is Dash)
        assertTrue(polylineOptions.startCap is RoundCap)
        assertTrue(polylineOptions.endCap is SquareCap)
        assertTrue(polylineOptions.isClickable)
    }
}
