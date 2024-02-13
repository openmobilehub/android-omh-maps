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
import com.openmobilehub.android.maps.core.presentation.models.OmhCoordinate
import com.openmobilehub.android.maps.core.presentation.models.OmhDash
import com.openmobilehub.android.maps.core.presentation.models.OmhDot
import com.openmobilehub.android.maps.core.presentation.models.OmhGap
import com.openmobilehub.android.maps.core.presentation.models.OmhJointType
import com.openmobilehub.android.maps.core.presentation.models.OmhPolygonOptions
import com.openmobilehub.android.maps.plugin.googlemaps.extensions.toPolygonOptions
import com.openmobilehub.android.maps.plugin.googlemaps.utils.CoordinateConverter
import com.openmobilehub.android.maps.plugin.googlemaps.utils.PatternConverter
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

internal class OmhPolygonOptionsTest {
    private val omhPolygonOption = OmhPolygonOptions().apply {
        outline = listOf(
            OmhCoordinate(0.0, 0.0),
            OmhCoordinate(0.0, 20.0),
            OmhCoordinate(20.0, 20.0),
            OmhCoordinate(0.0, 20.0),
        )
        holes = listOf(
            listOf(
                OmhCoordinate(5.0, 5.0),
                OmhCoordinate(5.0, 10.0),
                OmhCoordinate(10.0, 10.0),
                OmhCoordinate(5.0, 10.0),
            )
        )
        clickable = true
        strokeColor = Color.RED
        fillColor = Color.BLUE
        strokeWidth = 100f
        isVisible = true
        zIndex = 10f
        strokeJointType = OmhJointType.ROUND
        strokePattern = listOf(
            OmhDot(),
            OmhGap(20f),
            OmhDash(30f)
        )
    }

    @Before
    fun setUp() {
        mockkObject(CoordinateConverter)
        mockkObject(PatternConverter)
    }

    @Test
    fun `toPolygon converts OmhPolygonOptions to Polygon`() {
        // Arrange
        val mockLatLng = mockk<LatLng>()
        every { CoordinateConverter.convertToLatLng(any()) } returns mockLatLng

        // Act
        val polygonOptions = omhPolygonOption.toPolygonOptions()

        // Assert
        assertEquals(4, polygonOptions.points.size)
        assertEquals(1, polygonOptions.holes.size)
        assertTrue(polygonOptions.isClickable)
        assertEquals(Color.RED, polygonOptions.strokeColor)
        assertEquals(Color.BLUE, polygonOptions.fillColor)
        assertEquals(100f, polygonOptions.strokeWidth)
        assertTrue(polygonOptions.isVisible)
        assertEquals(JointType.ROUND, polygonOptions.strokeJointType)
        assertEquals(10f, polygonOptions.zIndex)
        assertEquals(3, polygonOptions.strokePattern?.size)
        assertEquals(Dot(), polygonOptions.strokePattern?.get(0))
        assertEquals(Gap(20f), polygonOptions.strokePattern?.get(1))
        assertEquals(Dash(30f), polygonOptions.strokePattern?.get(2))
    }
}
