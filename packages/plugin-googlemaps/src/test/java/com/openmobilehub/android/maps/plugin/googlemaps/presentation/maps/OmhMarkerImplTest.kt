package com.openmobilehub.android.maps.plugin.googlemaps.presentation.maps

import com.google.android.gms.maps.model.Marker
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

class OmhMarkerImplTest {

    private lateinit var marker: Marker
    private lateinit var omhMarker: OmhMarkerImpl

    @Before
    fun setUp() {
        marker = mockk(relaxed = true)
        omhMarker = OmhMarkerImpl(marker)
        mockkObject(CoordinateConverter)
        mockkObject(PatternConverter)
    }

    @Test
    fun `getDraggable returns draggable state`() {
        // Arrange
        val expected = listOf(true, false)
        every { marker.draggable } returnsMany expected

        // Act
        val actual = expected.map { omhMarker.getDraggable() }

        // Assert
        assertEquals(expected, actual)
    }

    @Test
    fun `setDraggable sets draggable state`() {
        // Arrange
        val expectedValue = true
        every { marker.draggable = any() } just runs

        // Act
        omhMarker.setDraggable(expectedValue)

        // Assert
        verify { marker.draggable = expectedValue }
    }

    @Test
    fun `setAnchor sets anchor`() {
        // Arrange
        val expectedValue = Pair(0.3f, 0.75f)
        every { marker.setAnchor(any(), any()) } just runs

        // Act
        omhMarker.setAnchor(expectedValue.first, expectedValue.second)

        // Assert
        verify { marker.setAnchor(expectedValue.first, expectedValue.second) }
    }

    @Test
    fun `getAlpha returns alpha value`() {
        // Arrange
        val expected = 0.35f
        every { marker.alpha } returns expected

        // Act
        val actual = omhMarker.getAlpha()

        // Assert
        assertEquals(expected, actual)
    }

    @Test
    fun `setAlpha sets alpha value`() {
        // Arrange
        val expected = 0.732f
        every { marker.alpha = any() } just runs

        // Act
        omhMarker.setAlpha(expected)

        // Assert
        verify { marker.alpha = expected }
    }

    @Test
    fun `getSnippet returns snippet value`() {
        // Arrange
        val expected = "Snippet example"
        every { marker.snippet } returns expected

        // Act
        val actual = omhMarker.getSnippet()

        // Assert
        assertEquals(expected, actual)
    }

    @Test
    fun `setSnippet sets snippet value`() {
        // Arrange
        val expected = "Snippet example"
        every { marker.snippet = any() } just runs

        // Act
        omhMarker.setSnippet(expected)

        // Assert
        verify { marker.snippet = expected }
    }

    @Test
    fun `getIsVisible gets isVisible value`() {
        // Arrange
        val expected = false
        every { marker.isVisible } returns expected

        // Act
        val actual = omhMarker.getIsVisible()

        // Assert
        assertEquals(expected, actual)
    }

    @Test
    fun `setIsVisible sets isVisible value`() {
        // Arrange
        val expected = false
        every { marker.isVisible = any() } just runs

        // Act
        omhMarker.setIsVisible(expected)

        // Assert
        verify { marker.isVisible = expected }
    }

    @Test
    fun `getIsFlat gets isFlat value`() {
        // Arrange
        val expected = true
        every { marker.isFlat } returns expected

        // Act
        val actual = omhMarker.getIsFlat()

        // Assert
        assertEquals(expected, actual)
    }

    @Test
    fun `setIsFlat sets isFlat value`() {
        // Arrange
        val expected = false
        every { marker.isFlat = any() } just runs

        // Act
        omhMarker.setIsFlat(expected)

        // Assert
        verify { marker.isFlat = expected }
    }

    @Test
    fun `getRotation gets rotation value`() {
        // Arrange
        val expected = 89.23f
        every { marker.rotation } returns expected

        // Act
        val actual = omhMarker.getRotation()

        // Assert
        assertEquals(expected, actual)
    }

    @Test
    fun `setRotation sets rotation value`() {
        // Arrange
        val expected = 45.78f
        every { marker.rotation = any() } just runs

        // Act
        omhMarker.setRotation(expected)

        // Assert
        verify { marker.rotation = expected }
    }
}
