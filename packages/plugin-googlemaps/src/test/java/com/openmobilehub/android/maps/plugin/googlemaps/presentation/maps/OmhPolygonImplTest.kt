package com.openmobilehub.android.maps.plugin.googlemaps.presentation.maps

import android.graphics.Color
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PatternItem
import com.google.android.gms.maps.model.Polygon
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhPatternItem
import com.openmobilehub.android.maps.core.presentation.models.OmhCoordinate
import com.openmobilehub.android.maps.plugin.googlemaps.utils.CoordinateConverter
import com.openmobilehub.android.maps.plugin.googlemaps.utils.PatternConverter
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.runs
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class OmhPolygonImplTest {

    private lateinit var polygon: Polygon
    private lateinit var omhPolygon: OmhPolygonImpl

    @Before
    fun setUp() {
        polygon = mockk(relaxed = true)
        omhPolygon = OmhPolygonImpl(polygon)
        mockkObject(CoordinateConverter)
        mockkObject(PatternConverter)
    }

    @Test
    fun `getClickable returns clickable state`() {
        // Arrange
        val expectedValue = true
        every { polygon.isClickable } returns expectedValue

        // Act
        val clickable = omhPolygon.getClickable()

        // Assert
        assertEquals(expectedValue, clickable)
    }

    @Test
    fun `setClickable sets clickable state`() {
        // Arrange
        val expectedValue = true
        every { polygon.isClickable = any() } just runs

        // Act
        omhPolygon.setClickable(expectedValue)

        // Assert
        verify { polygon.isClickable = expectedValue }
    }

    @Test
    fun `getStrokeColor returns polygon stroke color`() {
        // Arrange
        val expectedColor = 123
        every { polygon.strokeColor } returns expectedColor

        // Act
        val color = omhPolygon.getStrokeColor()

        // Assert
        assertEquals(expectedColor, color)
    }

    @Test
    fun `setStrokeColor sets polygon stroke color`() {
        // Arrange
        val color = 123
        every { polygon.strokeColor = any() } just runs

        // Act
        omhPolygon.setStrokeColor(color)

        // Assert
        verify { polygon.strokeColor = color }
    }

    @Test
    fun `getFillColor returns polygon fill color`() {
        // Arrange
        val expectedColor = Color.RED
        every { polygon.fillColor } returns expectedColor

        // Act
        val color = omhPolygon.getFillColor()

        // Assert
        assertEquals(expectedColor, color)
    }

    @Test
    fun `setFillColor sets polygon fill color`() {
        // Arrange
        val color = Color.RED
        every { polygon.fillColor = any() } just runs

        // Act
        omhPolygon.setFillColor(color)

        // Assert
        verify { polygon.fillColor = color }
    }

    @Test
    fun `getStrokeJointType returns polygon stroke joint type`() {
        // Arrange
        val expectedJointType = 123
        every { polygon.strokeJointType } returns expectedJointType

        // Act
        val jointType = omhPolygon.getStrokeJointType()

        // Assert
        assertEquals(expectedJointType, jointType)
    }

    @Test
    fun `setStrokeJointType sets polygon stroke joint type`() {
        // Arrange
        val jointType = 123
        every { polygon.strokeJointType = any() } just runs

        // Act
        omhPolygon.setStrokeJointType(jointType)

        // Assert
        verify { polygon.strokeJointType = jointType }
    }

    @Test
    fun `getStrokePattern returns polygon stroke pattern`() {
        // Arrange
        val patternItem = mockk<PatternItem>()
        val omhPatternItem = mockk<OmhPatternItem>()
        every { polygon.strokePattern } returns listOf(patternItem)
        every { PatternConverter.convertToOmhPatternItem(patternItem) } returns omhPatternItem

        // Act
        val strokePattern = omhPolygon.getStrokePattern()

        // Assert
        assertEquals(listOf(omhPatternItem), strokePattern)
    }

    @Test
    fun `setStrokePattern sets polygon stroke pattern`() {
        // Arrange
        val pattern = listOf(mockk<OmhPatternItem>())
        every { PatternConverter.convertToPatternItem(any()) } returns mockk()
        every { polygon.strokePattern = any() } just runs

        // Act
        omhPolygon.setStrokePattern(pattern)

        // Assert
        verify { polygon.strokePattern = any() }
    }

    @Test
    fun `getOutline returns polygon outline`() {
        // Arrange
        val latLng = mockk<LatLng>()
        val omhCoordinate = mockk<OmhCoordinate>()
        every { polygon.points } returns listOf(latLng)
        every { CoordinateConverter.convertToOmhCoordinate(latLng) } returns omhCoordinate

        // Act
        val outline = omhPolygon.getOutline()

        // Assert
        assertEquals(listOf(omhCoordinate), outline)
    }

    @Test
    fun `setOutline sets polygon outline`() {
        // Arrange
        val omhCoordinate = mockk<OmhCoordinate>()
        val latLng = mockk<LatLng>()
        every { CoordinateConverter.convertToLatLng(omhCoordinate) } returns latLng

        // Act
        omhPolygon.setOutline(listOf(omhCoordinate))

        // Assert
        verify { polygon.points = listOf(latLng) }
    }

    @Test
    fun `getHoles returns polygon holes`() {
        // Arrange
        val latLng = mockk<LatLng>()
        val omhCoordinate = mockk<OmhCoordinate>()
        every { polygon.holes } returns listOf(listOf(latLng))
        every { CoordinateConverter.convertToOmhCoordinate(latLng) } returns omhCoordinate

        // Act
        val holes = omhPolygon.getHoles()

        // Assert
        assertEquals(listOf(listOf(omhCoordinate)), holes)
    }

    @Test
    fun `setHoles sets polygon holes`() {
        // Arrange
        val holes = listOf(listOf(mockk<OmhCoordinate>()))
        every { CoordinateConverter.convertToLatLng(any()) } returns mockk()
        every { polygon.holes = any() } just runs

        // Act
        omhPolygon.setHoles(holes)

        // Assert
        verify { polygon.holes = any() }
    }

    @Test
    fun `getTag returns polygon tag`() {
        // Arrange
        val expectedTag = "tag"
        every { polygon.tag } returns expectedTag

        // Act
        val tag = omhPolygon.getTag()

        // Assert
        assertEquals(expectedTag, tag)
    }

    @Test
    fun `setTag sets polygon tag`() {
        // Arrange
        val tag = "tag"
        every { polygon.tag = any() } just runs

        // Act
        omhPolygon.setTag(tag)

        // Assert
        verify { polygon.tag = tag }
    }

    @Test
    fun `getStrokeWidth returns polygon stroke width`() {
        // Arrange
        val expectedWidth = 123f
        every { polygon.strokeWidth } returns expectedWidth

        // Act
        val width = omhPolygon.getStrokeWidth()

        // Assert
        assertEquals(expectedWidth, width)
    }

    @Test
    fun `setStrokeWidth sets polygon stroke width`() {
        // Arrange
        val width = 123f
        every { polygon.strokeWidth = any() } just runs

        // Act
        omhPolygon.setStrokeWidth(width)

        // Assert
        verify { polygon.strokeWidth = width }
    }

    @Test
    fun `isVisible returns polygon visibility`() {
        // Arrange
        val expectedVisibility = true
        every { polygon.isVisible } returns expectedVisibility

        // Act
        val visibility = omhPolygon.isVisible()

        // Assert
        assertEquals(expectedVisibility, visibility)
    }

    @Test
    fun `setVisible sets polygon visibility`() {
        // Arrange
        val visibility = true
        every { polygon.isVisible = any() } just runs

        // Act
        omhPolygon.setVisible(visibility)

        // Assert
        verify { polygon.isVisible = visibility }
    }

    @Test
    fun `getZIndex returns polygon zIndex`() {
        // Arrange
        val expectedZIndex = 123f
        every { polygon.zIndex } returns expectedZIndex

        // Act
        val zIndex = omhPolygon.getZIndex()

        // Assert
        assertEquals(expectedZIndex, zIndex)
    }

    @Test
    fun `setZIndex sets polygon zIndex`() {
        // Arrange
        val zIndex = 123f
        every { polygon.zIndex = any() } just runs

        // Act
        omhPolygon.setZIndex(zIndex)

        // Assert
        verify { polygon.zIndex = zIndex }
    }
}
