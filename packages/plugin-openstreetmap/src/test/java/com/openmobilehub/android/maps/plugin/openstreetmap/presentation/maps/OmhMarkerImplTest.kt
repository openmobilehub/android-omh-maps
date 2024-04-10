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

import com.openmobilehub.android.maps.core.utils.logging.UnsupportedFeatureLogger
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.osmdroid.views.MapView

class OmhMarkerImplTest {

    private lateinit var marker: CustomMarker
    private lateinit var omhMarker: OmhMarkerImpl
    private lateinit var mockMapView: MapView
    private lateinit var mockLogger: UnsupportedFeatureLogger
    private val initiallyClickable = true

    @Before
    fun setUp() {
        marker = mockk(relaxed = true)
        mockMapView = mockk(relaxed = true)
        mockLogger = mockk<UnsupportedFeatureLogger>(relaxed = true)
        omhMarker = OmhMarkerImpl(marker, mockMapView, initiallyClickable, mockLogger)
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
    fun `backgroundColor should log setter not supported`() {
        // Act
        omhMarker.setBackgroundColor(255)

        // Assert
        verify { mockLogger.logSetterNotSupported("backgroundColor") }
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
    fun `setInfoWindowAnchor sets info window anchor`() {
        // Arrange
        val expectedValue = Pair(0.3f, 0.75f)
        every { marker.setInfoWindowAnchor(any(), any()) } just runs

        // Act
        omhMarker.setInfoWindowAnchor(expectedValue.first, expectedValue.second)

        // Assert
        verify { marker.setInfoWindowAnchor(expectedValue.first, expectedValue.second) }
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
        every { marker.alpha } returns 0f // OSM implementation utilizes alpha under the hood instead of a boolean flag

        // Act
        val actual = omhMarker.getIsVisible()

        // Assert
        assertEquals(expected, actual)
    }

    @Test
    fun `setIsVisible sets isVisible value`() {
        // Arrange
        val expected = false
        every { marker.setVisible(any()) } just runs

        // Act
        omhMarker.setIsVisible(expected)

        // Assert
        verify { marker.setVisible(expected) }
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
        every { marker.rotation } returns -expected // rotation is counter-clockwise in OSM

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
        verify {
            marker.rotation = -expected // rotation is counter-clockwise in OSM
        }
    }

    @Test
    fun `remove removes the marker and closes info window`() {
        // Arrange
        every { marker.closeInfoWindow() } just runs

        // Act
        omhMarker.remove()

        // Assert
        assertEquals(omhMarker.isRemoved, true)
        verify(exactly = 1) {
            marker.closeInfoWindow()
            marker.remove(mockMapView)
            mockMapView.invalidate()
        }
        assertEquals(marker.isInfoWindowShown, false)
    }
}
