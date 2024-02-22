package com.openmobilehub.android.maps.plugin.googlemaps.presentation.maps

import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.Marker
import com.openmobilehub.android.maps.core.utils.logging.UnsupportedFeatureLogger
import com.openmobilehub.android.maps.plugin.googlemaps.utils.CoordinateConverter
import com.openmobilehub.android.maps.plugin.googlemaps.utils.MarkerIconConverter
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
    private lateinit var mockLogger: UnsupportedFeatureLogger
    private val initiallyClickable = true

    @Before
    fun setUp() {
        marker = mockk(relaxed = true)
        mockLogger = mockk<UnsupportedFeatureLogger>(relaxed = true)
        omhMarker = OmhMarkerImpl(marker, initiallyClickable, mockLogger)
        mockkObject(CoordinateConverter)
        mockkObject(PatternConverter)
        mockkObject(MarkerIconConverter)
    }

    @Test
    fun `backgroundColor should return null and log getter not supported`() {
        // Act
        val actual = omhMarker.getBackgroundColor()

        // Assert
        verify { mockLogger.logGetterNotSupported("backgroundColor") }
        assertEquals(null, actual)
    }

    @Test
    fun `backgroundColor should log setter partially supported`() {
        // Arrange
        val colorBitmapDescriptor = mockk<BitmapDescriptor>()
        every { MarkerIconConverter.convertColorToBitmapDescriptor(any()) } returns colorBitmapDescriptor

        val color = 255

        // Act
        omhMarker.setBackgroundColor(color)

        // Assert
        verify { mockLogger.logFeatureSetterPartiallySupported("backgroundColor", any()) }
        verify { marker.setIcon(colorBitmapDescriptor) }
    }

    @Test
    fun `getClickable returns clickable state`() {
        // Act
        val clickable = omhMarker.getClickable()

        // Assert
        assertEquals(initiallyClickable, clickable)
    }

    @Test
    fun `setClickable sets clickable state`() {
        // Arrange
        val expectedValue = false

        // Act
        omhMarker.setClickable(expectedValue)

        // Assert
        assertEquals(expectedValue, omhMarker.getClickable())
    }

    @Test
    fun `getDraggable returns draggable state`() {
        // Arrange
        val expected = listOf(true, false)
        every { marker.isDraggable } returnsMany expected

        // Act
        val actual = expected.map { omhMarker.getDraggable() }

        // Assert
        assertEquals(expected, actual)
    }

    @Test
    fun `setDraggable sets draggable state`() {
        // Arrange
        val expectedValue = true
        every { marker.isDraggable = any() } just runs

        // Act
        omhMarker.setDraggable(expectedValue)

        // Assert
        verify { marker.isDraggable = expectedValue }
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
