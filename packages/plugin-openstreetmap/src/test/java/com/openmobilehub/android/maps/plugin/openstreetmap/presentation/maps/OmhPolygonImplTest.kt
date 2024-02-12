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

package com.openmobilehub.android.maps.plugin.openstreetmap.presentation.maps

import android.graphics.Color
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhPatternItem
import com.openmobilehub.android.maps.core.presentation.models.OmhCoordinate
import com.openmobilehub.android.maps.core.presentation.models.OmhJointType
import com.openmobilehub.android.maps.core.utils.UnsupportedFeatureLogger
import com.openmobilehub.android.maps.plugin.openstreetmap.utils.ConverterUtils
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
import org.osmdroid.views.overlay.Polygon

class OmhPolygonImplTest {

    private lateinit var polygon: Polygon
    private lateinit var omhPolygon: OmhPolygonImpl
    private val mockLogger: UnsupportedFeatureLogger =
        mockk<UnsupportedFeatureLogger>(relaxed = true)

    @Before
    fun setUp() {
        polygon = mockk(relaxed = true)
        omhPolygon = OmhPolygonImpl(polygon, mockLogger)
        mockkObject(ConverterUtils)
    }

    @Test
    fun `getClickable returns clickable state`() {
        // Arrange
        val expectedValue = true
        every { polygon.isEnabled } returns expectedValue

        // Act
        val clickable = omhPolygon.getClickable()

        // Assert
        Assert.assertEquals(expectedValue, clickable)
    }

    @Test
    fun `setClickable sets clickable state`() {
        // Arrange
        val expectedValue = true
        every { polygon.isEnabled = any() } just runs

        // Act
        omhPolygon.setClickable(expectedValue)

        // Assert
        verify { polygon.isEnabled = expectedValue }
    }

    @Test
    fun `getStrokeColor returns polygon color`() {
        // Arrange
        val expectedColor = Color.RED
        every { polygon.outlinePaint.color } returns expectedColor

        // Act
        val color = omhPolygon.getStrokeColor()

        // Assert
        Assert.assertEquals(expectedColor, color)
    }

    @Test
    fun `setStrokeColor sets polygon color`() {
        // Arrange
        every { polygon.outlinePaint.color = any() } just runs

        // Act
        omhPolygon.setStrokeColor(Color.RED)

        // Assert
        verify { polygon.outlinePaint.color = Color.RED }
    }

    @Test
    fun `getFillColor returns polygon fill color`() {
        // Arrange
        val expectedColor = Color.RED
        every { polygon.fillPaint.color } returns expectedColor

        // Act
        val color = omhPolygon.getFillColor()

        // Assert
        Assert.assertEquals(expectedColor, color)
    }

    @Test
    fun `setFillColor sets polygon fill color`() {
        // Arrange
        every { polygon.fillPaint.color = any() } just runs

        // Act
        omhPolygon.setFillColor(Color.RED)

        // Assert
        verify { polygon.fillPaint.color = Color.RED }
    }

    @Test
    fun `getStrokeJointType should return default joint type value`() {
        // Arrange
        val defaultJointType = 0
        // Act
        val jointType = omhPolygon.getStrokeJointType()

        // Assert
        Assert.assertEquals(defaultJointType, jointType)
        verify { mockLogger.logGetterNotSupported("strokeJointType") }
    }

    @Test
    fun `setStrokeJointType should log setter not supported`() {
        // Act
        omhPolygon.setStrokeJointType(OmhJointType.ROUND)

        // Assert
        verify { mockLogger.logSetterNotSupported("strokeJointType") }
    }

    @Test
    fun `getStrokePattern should return null and log getter not supported`() {
        // Act
        val result = omhPolygon.getStrokePattern()

        // Assert
        Assert.assertNull(result)
        verify { mockLogger.logGetterNotSupported("strokePattern") }
    }

    @Test
    fun `setStrokePattern should log setter not supported`() {
        // Arrange
        val omhPatternItem = mockk<OmhPatternItem>()

        // Act
        omhPolygon.setStrokePattern(listOf(omhPatternItem))

        // Assert
        verify { mockLogger.logSetterNotSupported("strokePattern") }
    }

    @Test
    fun `getOutline returns polygon outline`() {
        // Arrange
        val geoPoint = mockk<GeoPoint>()
        val omhCoordinate = mockk<OmhCoordinate>()
        every { polygon.actualPoints } returns listOf(geoPoint)
        every { ConverterUtils.convertToOmhCoordinate(geoPoint) } returns omhCoordinate

        // Act
        val result = omhPolygon.getOutline()

        // Assert
        Assert.assertEquals(listOf(omhCoordinate), result)
    }

    @Test
    fun `setOutline sets polyline outline`() {
        // Arrange
        val omhCoordinate = mockk<OmhCoordinate>()
        val geoPoint = mockk<GeoPoint>()
        every { ConverterUtils.convertToGeoPoint(omhCoordinate) } returns geoPoint

        // Act
        omhPolygon.setOutline(listOf(omhCoordinate))

        // Assert
        verify { polygon.points = listOf(geoPoint) }
    }

    @Test
    fun `getHoles returns polygon holes`() {
        // Arrange
        val geoPoint = mockk<GeoPoint>()
        val omhCoordinate = mockk<OmhCoordinate>()
        every { polygon.holes } returns listOf(listOf(geoPoint))
        every { ConverterUtils.convertToOmhCoordinate(geoPoint) } returns omhCoordinate

        // Act
        val result = omhPolygon.getHoles()

        // Assert
        Assert.assertEquals(listOf(listOf(omhCoordinate)), result)
    }

    @Test
    fun `setHoles sets polygon holes`() {
        // Arrange
        val omhCoordinate = mockk<OmhCoordinate>()
        val geoPoint = mockk<GeoPoint>()
        every { ConverterUtils.convertToGeoPoint(omhCoordinate) } returns geoPoint

        // Act
        omhPolygon.setHoles(listOf(listOf(omhCoordinate)))

        // Assert
        verify { polygon.holes = listOf(listOf(geoPoint)) }
    }

    @Test
    fun `getStrokeWidth returns polygon stroke width`() {
        // Arrange
        val expectedWidth = 10.0f
        every { polygon.outlinePaint.strokeWidth } returns expectedWidth

        // Act
        val width = omhPolygon.getStrokeWidth()

        // Assert
        Assert.assertEquals(expectedWidth, width)
    }

    @Test
    fun `setStrokeWidth sets polygon stroke width`() {
        // Arrange
        val width = 10.0f

        // Act
        omhPolygon.setStrokeWidth(width)

        // Assert
        verify { polygon.outlinePaint.strokeWidth = width }
    }

    @Test
    fun `getTag returns polygon tag`() {
        // Arrange
        val expectedTag = "tag"
        every { polygon.relatedObject } returns expectedTag

        // Act
        val tag = omhPolygon.getTag()

        // Assert
        Assert.assertEquals(expectedTag, tag)
    }

    @Test
    fun `setTag sets polygon tag`() {
        // Arrange
        val tag = "tag"
        every { polygon.relatedObject = any() } just runs

        // Act
        omhPolygon.setTag(tag)

        // Assert
        verify { polygon.relatedObject = tag }
    }

    @Test
    fun `isVisible returns polygon visibility`() {
        // Arrange
        val expectedVisibility = true
        every { polygon.isVisible } returns expectedVisibility

        // Act
        val visibility = omhPolygon.isVisible()

        // Assert
        Assert.assertEquals(expectedVisibility, visibility)
    }

    @Test
    fun `setVisible sets polygon visibility`() {
        // Arrange
        val visibility = true

        // Act
        omhPolygon.setVisible(visibility)

        // Assert
        verify { polygon.isVisible = visibility }
    }

    @Test
    fun `getZIndex should return default polyline zIndex and log getter not supported`() {
        // Arrange
        val defaultZIndex = 0.0f

        // Act
        val zIndex = omhPolygon.getZIndex()

        // Assert
        Assert.assertEquals(defaultZIndex, zIndex)
        verify { mockLogger.logGetterNotSupported("zIndex") }
    }

    @Test
    fun `setZIndex should log setter not supported`() {
        // Arrange
        val zIndex = 1.0f

        // Act
        omhPolygon.setZIndex(zIndex)

        // Assert
        verify { mockLogger.logSetterNotSupported("zIndex") }
    }
}
