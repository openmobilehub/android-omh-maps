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

package com.openmobilehub.android.maps.plugin.mapbox.presentation.maps

import android.graphics.Color
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhCap
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhPatternItem
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhStyleSpan
import com.openmobilehub.android.maps.core.presentation.models.OmhCoordinate
import com.openmobilehub.android.maps.core.presentation.models.OmhJointType
import com.openmobilehub.android.maps.core.utils.UnsupportedFeatureLogger
import com.openmobilehub.android.maps.plugin.mapbox.utils.ConverterUtils
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.runs
import io.mockk.verify
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Polyline

class OmhPolylineImplTest {

    private lateinit var polyline: Polyline
    private lateinit var omhPolyline: OmhPolylineImpl
    private val initiallyClickable = true
    private val mockMapView: MapView = mockk(relaxed = true)
    private val mockLogger: UnsupportedFeatureLogger =
        mockk<UnsupportedFeatureLogger>(relaxed = true)

    @Before
    fun setUp() {
        polyline = mockk(relaxed = true)
        omhPolyline = OmhPolylineImpl(polyline, mockMapView, initiallyClickable, mockLogger)
        mockkObject(ConverterUtils)
    }

    @Test
    fun `getClickable returns clickable state`() {
        // Act
        val clickable = omhPolyline.getClickable()

        // Assert
        Assert.assertEquals(initiallyClickable, clickable)
    }

    @Test
    fun `setClickable sets clickable state`() {
        // Arrange
        val expectedValue = true
        // Act
        omhPolyline.setClickable(expectedValue)

        // Assert
        Assert.assertEquals(expectedValue, omhPolyline.getClickable())
    }

    @Test
    fun `getColor returns polyline color`() {
        // Arrange
        val expectedColor = Color.RED
        every { polyline.outlinePaint.color } returns expectedColor

        // Act
        val color = omhPolyline.getColor()

        // Assert
        Assert.assertEquals(expectedColor, color)
    }

    @Test
    fun `setColor sets polyline color`() {
        // Arrange
        every { polyline.outlinePaint.color = any() } just runs

        // Act
        omhPolyline.setColor(Color.RED)

        // Assert
        verify { polyline.outlinePaint.color = Color.RED }
    }

    @Test
    fun `getEndCap should log getter not supported and return null`() {
        // Act
        val endCap = omhPolyline.getEndCap()

        // Assert
        Assert.assertNull(endCap)
        verify { mockLogger.logGetterNotSupported("endCap") }
    }

    @Test
    fun `setEndCap should log setter not supported`() {
        // Assert
        val omhCap = mockk<OmhCap>()

        // Act
        omhPolyline.setEndCap(omhCap)

        // Assert
        verify { mockLogger.logSetterNotSupported("endCap") }
    }

    @Test
    fun `getJointType should return default joint type value`() {
        // Arrange
        val defaultJointType = 0
        // Act
        val jointType = omhPolyline.getJointType()

        // Assert
        Assert.assertEquals(defaultJointType, jointType)
        verify { mockLogger.logGetterNotSupported("jointType") }
    }

    @Test
    fun `setJointType should log setter not supported`() {
        // Act
        omhPolyline.setJointType(OmhJointType.ROUND)

        // Assert
        verify { mockLogger.logSetterNotSupported("jointType") }
    }

    @Test
    fun `getPattern should return null and log getter not supported`() {
        // Act
        val result = omhPolyline.getPattern()

        // Assert
        Assert.assertNull(result)
        verify { mockLogger.logGetterNotSupported("pattern") }
    }

    @Test
    fun `setPattern should log setter not supported`() {
        // Arrange
        val omhPatternItem = mockk<OmhPatternItem>()

        // Act
        omhPolyline.setPattern(listOf(omhPatternItem))

        // Assert
        verify { mockLogger.logSetterNotSupported("pattern") }
    }

    @Test
    fun `getPoints returns converted polyline points`() {
        // Arrange
        val geoPoint = mockk<GeoPoint>()
        val omhCoordinate = mockk<OmhCoordinate>()
        every { polyline.actualPoints } returns listOf(geoPoint)
        every { ConverterUtils.convertToOmhCoordinate(geoPoint) } returns omhCoordinate

        // Act
        val result = omhPolyline.getPoints()

        // Assert
        Assert.assertEquals(listOf(omhCoordinate), result)
    }

    @Test
    fun `setPoints sets polyline points`() {
        // Arrange
        val omhCoordinate = mockk<OmhCoordinate>()
        val geoPoint = mockk<GeoPoint>()
        every { ConverterUtils.convertToGeoPoint(omhCoordinate) } returns geoPoint

        // Act
        omhPolyline.setPoints(listOf(omhCoordinate))

        // Assert
        verify { polyline.setPoints(listOf(geoPoint)) }
    }

    @Test
    fun `getSpans should return null and log getter not supported`() {
        // Act
        val result = omhPolyline.getSpans()

        // Assert
        Assert.assertNull(result)
        verify { mockLogger.logGetterNotSupported("spans") }
    }

    @Test
    fun `setSpans should log setter not supported`() {
        // Arrange
        val omhStyleSpan = mockk<OmhStyleSpan>()

        // Act
        omhPolyline.setSpans(listOf(omhStyleSpan))

        // Assert
        verify { mockLogger.logSetterNotSupported("spans") }
    }

    @Test
    fun `getStartCap should return null and log getter not supported`() {
        // Act
        val result = omhPolyline.getStartCap()

        // Assert
        Assert.assertNull(result)
        verify { mockLogger.logGetterNotSupported("startCap") }
    }

    @Test
    fun `setStartCap should log setter not supported`() {
        // Arrange
        val omhCap = mockk<OmhCap>()

        // Act
        omhPolyline.setStartCap(omhCap)

        // Assert
        verify { mockLogger.logSetterNotSupported("startCap") }
    }

    @Test
    fun `getWidth returns polyline width`() {
        // Arrange
        val expectedWidth = 10.0f
        every { polyline.outlinePaint.strokeWidth } returns expectedWidth

        // Act
        val width = omhPolyline.getWidth()

        // Assert
        Assert.assertEquals(expectedWidth, width)
    }

    @Test
    fun `setWidth sets polyline width`() {
        // Arrange
        val width = 10.0f

        // Act
        omhPolyline.setWidth(width)

        // Assert
        verify { polyline.outlinePaint.strokeWidth = width }
    }

    @Test
    fun `isVisible returns polyline visibility`() {
        // Arrange
        val expectedVisibility = true
        every { polyline.isVisible } returns expectedVisibility

        // Act
        val visibility = omhPolyline.isVisible()

        // Assert
        Assert.assertEquals(expectedVisibility, visibility)
    }

    @Test
    fun `setVisible sets polyline visibility`() {
        // Arrange
        val visibility = true

        // Act
        omhPolyline.setVisible(visibility)

        // Assert
        verify { polyline.isVisible = visibility }
    }

    @Test
    fun `getZIndex should return default polyline zIndex and log getter not supported`() {
        // Arrange
        val defaultZIndex = 0.0f

        // Act
        val zIndex = omhPolyline.getZIndex()

        // Assert
        Assert.assertEquals(defaultZIndex, zIndex)
        verify { mockLogger.logGetterNotSupported("zIndex") }
    }

    @Test
    fun `setZIndex should log setter not supported`() {
        // Arrange
        val zIndex = 1.0f

        // Act
        omhPolyline.setZIndex(zIndex)

        // Assert
        verify { mockLogger.logSetterNotSupported("zIndex") }
    }
}
