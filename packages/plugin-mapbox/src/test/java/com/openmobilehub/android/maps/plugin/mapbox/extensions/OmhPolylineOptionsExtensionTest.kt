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

package com.openmobilehub.android.maps.plugin.mapbox.extensions

import android.graphics.Color
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
import com.openmobilehub.android.maps.core.utils.UnsupportedFeatureLogger
import com.openmobilehub.android.maps.plugin.mapbox.utils.ConverterUtils
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.verify
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.osmdroid.util.GeoPoint

class OmhPolylineOptionsExtensionTest {

    private val mockedLogger = mockk<UnsupportedFeatureLogger>(relaxed = true)

    private val omhPolylineOptions = OmhPolylineOptions().apply {
        points = listOf(
            OmhCoordinate(20.0, 20.0),
            OmhCoordinate(10.0, 10.0),
        )
        clickable = true
        color = Color.BLUE
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
        mockkObject(ConverterUtils)
    }

    @Test
    fun `toPolylineOptions converts OmhPolylineOptions to PolylineOptions`() {
        // Arrange
        val mockGeoPoint = mockk<GeoPoint>()
        every { ConverterUtils.convertToGeoPoint(any()) } returns mockGeoPoint

        // Act
        val polylineOptions = omhPolylineOptions.toPolylineOptions()
        // Assert
        Assert.assertEquals(2, polylineOptions.actualPoints.size)
        Assert.assertEquals(true, polylineOptions.isVisible)
        Assert.assertTrue(polylineOptions.isEnabled)
    }

    @Test
    fun `toPolylineOptions logs not supported properties`() {
        // Act
        omhPolylineOptions.toPolylineOptions(mockedLogger)

        // Assert
        verify { mockedLogger.logNotSupported("zIndex") }
        verify { mockedLogger.logNotSupported("jointType") }
        verify { mockedLogger.logNotSupported("pattern") }
        verify { mockedLogger.logNotSupported("startCap") }
        verify { mockedLogger.logNotSupported("endCap") }
        verify { mockedLogger.logNotSupported("spans") }
    }
}
