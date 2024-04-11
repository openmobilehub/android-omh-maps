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

package com.openmobilehub.android.maps.plugin.googlemaps.presentation.maps

import android.graphics.Color
import com.google.android.gms.maps.model.Cap
import com.google.android.gms.maps.model.JointType
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PatternItem
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.StyleSpan
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhCap
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhPatternItem
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhStyleSpan
import com.openmobilehub.android.maps.core.presentation.models.OmhCoordinate
import com.openmobilehub.android.maps.core.presentation.models.OmhJointType
import com.openmobilehub.android.maps.core.utils.logging.UnsupportedFeatureLogger
import com.openmobilehub.android.maps.plugin.googlemaps.presentation.interfaces.IPolylineDelegate
import com.openmobilehub.android.maps.plugin.googlemaps.utils.CapConverter
import com.openmobilehub.android.maps.plugin.googlemaps.utils.CoordinateConverter
import com.openmobilehub.android.maps.plugin.googlemaps.utils.PatternConverter
import com.openmobilehub.android.maps.plugin.googlemaps.utils.SpanConverter
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.runs
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class OmhPolylineImplTest {

    private lateinit var polyline: Polyline
    private lateinit var omhPolyline: OmhPolylineImpl
    private val polylineLogger = mockk<UnsupportedFeatureLogger>(relaxed = true)
    private val delegate = mockk<IPolylineDelegate>(relaxed = true)

    @Before
    fun setUp() {
        polyline = mockk(relaxed = true)
        omhPolyline = OmhPolylineImpl(polyline, delegate, polylineLogger)
        mockkObject(CapConverter)
        mockkObject(PatternConverter)
        mockkObject(CoordinateConverter)
        mockkObject(SpanConverter)
    }

    @Test
    fun `getClickable returns clickable state`() {
        // Arrange
        val expectedValue = true
        every { polyline.isClickable } returns expectedValue

        // Act
        val clickable = omhPolyline.getClickable()

        // Assert
        assertEquals(expectedValue, clickable)
    }

    @Test
    fun `setClickable sets clickable state`() {
        // Arrange
        val expectedValue = true
        every { polyline.isClickable = any() } just runs

        // Act
        omhPolyline.setClickable(expectedValue)

        // Assert
        verify { polyline.isClickable = expectedValue }
    }

    @Test
    fun `getColor returns polyline color`() {
        // Arrange
        val expectedColor = Color.RED
        every { polyline.color } returns expectedColor

        // Act
        val color = omhPolyline.getColor()

        // Assert
        assertEquals(expectedColor, color)
    }

    @Test
    fun `setColor sets polyline color`() {
        // Arrange
        every { polyline.color = any() } just runs

        // Act
        omhPolyline.setColor(Color.RED)

        // Assert
        verify { polyline.color = Color.RED }
    }

    @Test
    fun `getEndCap logs getter not supported returns null`() {
        // Arrange
        every { polyline.endCap = any() } just runs

        // Act
        val endCap = omhPolyline.getEndCap()

        // Assert
        verify { polylineLogger.logGetterNotSupported("endCap") }
        assertNull(endCap)
    }

    @Test
    fun `setEndCap sets polyline endCap if not null`() {
        val omhCap = mockk<OmhCap>()
        val cap = mockk<Cap>()

        every { CapConverter.convertToCap(omhCap) } returns cap
        every { polyline.endCap = cap } just runs

        omhPolyline.setEndCap(omhCap)

        verify { polyline.endCap = cap }
    }

    @Test
    fun `getJointType returns joint type value`() {
        // Arrange
        every { polyline.jointType } returns JointType.DEFAULT

        // Act
        val jointType = omhPolyline.getJointType()

        // Assert
        assertEquals(OmhJointType.DEFAULT, jointType)
    }

    @Test
    fun `setJointType sets joint type value`() {
        // Arrange
        every { polyline.jointType = any() } just runs

        // Act
        omhPolyline.setJointType(OmhJointType.ROUND)

        // Assert
        verify { polyline.jointType = JointType.ROUND }
    }

    @Test
    fun `getPattern returns converted polyline pattern`() {
        // Arrange
        val patternItem = mockk<PatternItem>()
        val omhPatternItem = mockk<OmhPatternItem>()
        every { polyline.pattern } returns listOf(patternItem)
        every { PatternConverter.convertToOmhPatternItem(patternItem) } returns omhPatternItem

        // Act
        val result = omhPolyline.getPattern()

        // Assert
        assertEquals(listOf(omhPatternItem), result)
    }

    @Test
    fun `setPattern sets polyline pattern`() {
        // Arrange
        val omhPatternItem = mockk<OmhPatternItem>()
        val patternItem = mockk<PatternItem>()
        every { PatternConverter.convertToPatternItem(omhPatternItem) } returns patternItem
        every { polyline.pattern = listOf(patternItem) } just runs

        // Act
        omhPolyline.setPattern(listOf(omhPatternItem))

        // Assert
        verify { polyline.pattern = listOf(patternItem) }
    }

    @Test
    fun `getPoints returns converted polyline points`() {
        // Arrange
        val latLng = mockk<LatLng>()
        val omhCoordinate = mockk<OmhCoordinate>()
        every { polyline.points } returns listOf(latLng)
        every { CoordinateConverter.convertToOmhCoordinate(latLng) } returns omhCoordinate

        // Act
        val result = omhPolyline.getPoints()

        // Assert
        assertEquals(listOf(omhCoordinate), result)
    }

    @Test
    fun `setPoints sets polyline points`() {
        // Arrange
        val omhCoordinate = mockk<OmhCoordinate>()
        val latLng = mockk<LatLng>()
        every { CoordinateConverter.convertToLatLng(omhCoordinate) } returns latLng

        // Act
        omhPolyline.setPoints(listOf(omhCoordinate))

        // Assert
        verify { polyline.points = listOf(latLng) }
    }

    @Test
    fun `getSpans returns null`() {
        // Act
        val result = omhPolyline.getSpans()

        // Assert
        assertNull(result)
    }

    @Test
    fun `setSpans sets polyline spans if not null`() {
        // Arrange
        val omhStyleSpan = mockk<OmhStyleSpan>()
        val styleSpan = mockk<StyleSpan>()
        every { SpanConverter.convertToStyleSpan(omhStyleSpan) } returns styleSpan

        // Act
        omhPolyline.setSpans(listOf(omhStyleSpan))

        // Assert
        verify { polyline.spans = listOf(styleSpan) }
    }

    @Test
    fun `getStartCap logs getter not supported and returns null`() {
        // Act
        val result = omhPolyline.getStartCap()

        // Assert
        verify { polylineLogger.logGetterNotSupported("startCap") }
        assertNull(result)
    }

    @Test
    fun `setStartCap sets polyline startCap if not null`() {
        // Arrange
        val omhCap = mockk<OmhCap>()
        val cap = mockk<Cap>()
        every { CapConverter.convertToCap(omhCap) } returns cap

        // Act
        omhPolyline.setStartCap(omhCap)

        // Assert
        verify { polyline.startCap = cap }
    }

    @Test
    fun `getWidth returns polyline width`() {
        // Arrange
        val expectedWidth = 10.0f
        every { polyline.width } returns expectedWidth

        // Act
        val width = omhPolyline.getWidth()

        // Assert
        assertEquals(expectedWidth, width)
    }

    @Test
    fun `setWidth sets polyline width`() {
        // Arrange
        val width = 10.0f

        // Act
        omhPolyline.setWidth(width)

        // Assert
        verify { polyline.width = width }
    }

    @Test
    fun `isVisible returns polyline visibility`() {
        // Arrange
        val expectedVisibility = true
        every { polyline.isVisible } returns expectedVisibility

        // Act
        val visibility = omhPolyline.isVisible()

        // Assert
        assertEquals(expectedVisibility, visibility)
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
    fun `getZIndex returns polyline zIndex`() {
        // Arrange
        val expectedZIndex = 1.0f
        every { polyline.zIndex } returns expectedZIndex

        // Act
        val zIndex = omhPolyline.getZIndex()

        // Assert
        assertEquals(expectedZIndex, zIndex)
    }

    @Test
    fun `setZIndex sets polyline zIndex`() {
        // Arrange
        val zIndex = 1.0f

        // Act
        omhPolyline.setZIndex(zIndex)

        // Assert
        verify { polyline.zIndex = zIndex }
    }

    @Test
    fun `getCap logs getter not supported and returns null`() {
        // Act
        val result = omhPolyline.getCap()

        // Assert
        verify { polylineLogger.logGetterNotSupported("cap") }
        assertNull(result)
    }

    @Test
    fun `setCap sets polyline startCap and endCap`() {
        // Arrange
        val omhCap = mockk<OmhCap>()
        val cap = mockk<Cap>()
        every { CapConverter.convertToCap(omhCap) } returns cap

        // Act
        omhPolyline.setCap(omhCap)

        // Assert
        verify { polyline.startCap = cap }
        verify { polyline.endCap = cap }
    }

    @Test
    fun `getTag returns polyline tag`() {
        // Arrange
        val expectedTag = "tag"
        every { polyline.tag } returns expectedTag

        // Act
        val tag = omhPolyline.getTag()

        // Assert
        assertEquals(expectedTag, tag)
    }

    @Test
    fun `setTag sets polyline tag`() {
        // Arrange
        val tag = "tag"

        // Act
        omhPolyline.setTag(tag)

        // Assert
        verify { polyline.tag = tag }
    }

    @Test
    fun `remove calls delegate method`() {
        // Act
        omhPolyline.remove()

        // Assert
        verify { delegate.removePolyline(polyline) }
    }
}
