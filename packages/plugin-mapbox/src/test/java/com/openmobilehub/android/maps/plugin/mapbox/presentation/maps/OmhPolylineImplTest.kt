package com.openmobilehub.android.maps.plugin.mapbox.presentation.maps

import android.graphics.Color
import com.mapbox.maps.extension.style.layers.generated.LineLayer
import com.mapbox.maps.extension.style.layers.properties.generated.LineCap
import com.mapbox.maps.extension.style.layers.properties.generated.LineJoin
import com.mapbox.maps.extension.style.layers.properties.generated.Visibility
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhCap
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhPatternItem
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhStyleSpan
import com.openmobilehub.android.maps.core.presentation.models.OmhCoordinate
import com.openmobilehub.android.maps.core.utils.logging.UnsupportedFeatureLogger
import com.openmobilehub.android.maps.plugin.mapbox.presentation.interfaces.PolylineDelegate
import com.openmobilehub.android.maps.plugin.mapbox.utils.CapConverter
import com.openmobilehub.android.maps.plugin.mapbox.utils.JoinTypeConverter
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.verify
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class OmhPolylineImplTest {
    private val lineLayer = mockk<LineLayer>(relaxed = true)
    private lateinit var omhPolyline: OmhPolylineImpl
    private val initiallyClickable = true
    private val scaleFactor = 3.0f
    private val polylineDelegate = mockk<PolylineDelegate>(relaxed = true)
    private val logger: UnsupportedFeatureLogger =
        mockk<UnsupportedFeatureLogger>(relaxed = true)

    @Before
    fun setUp() {
        mockkObject(CapConverter)
        mockkObject(JoinTypeConverter)

        omhPolyline =
            OmhPolylineImpl(lineLayer, initiallyClickable, scaleFactor, polylineDelegate, logger)
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
        every { lineLayer.lineColorAsColorInt } returns expectedColor

        // Act
        val color = omhPolyline.getColor()

        // Assert
        Assert.assertEquals(expectedColor, color)
    }

    @Test
    fun `setColor sets polyline color`() {
        // Arrange
        val expectedColor = Color.RED
        every { lineLayer.lineColor(any<Int>()) } returns lineLayer

        // Act
        omhPolyline.setColor(expectedColor)

        // Assert
        verify { lineLayer.lineColor(expectedColor) }
    }

    @Test
    fun `getEndCap logs getter not supported and returns null`() {
        // Act
        val endCap = omhPolyline.getEndCap()

        // Assert
        Assert.assertNull(endCap)
        verify { logger.logGetterNotSupported("endCap") }
    }

    @Test
    fun `setEndCap logs setter not supported`() {
        // Assert
        val omhCap = mockk<OmhCap>()

        // Act
        omhPolyline.setEndCap(omhCap)

        // Assert
        verify { logger.logSetterNotSupported("endCap") }
    }

    @Test
    fun `getJointType returns join type value`() {
        // Arrange
        val omhLineJoin = mockk<Int>(relaxed = true)
        every { JoinTypeConverter.convertToOmhJointType(any<LineJoin>()) } returns omhLineJoin

        // Act
        val jointType = omhPolyline.getJointType()

        // Assert
        Assert.assertEquals(omhLineJoin, jointType)
    }

    @Test
    fun `setJointType sets join type value`() {
        // Arrange
        val omhJointType = mockk<Int>(relaxed = true)
        val lineJoin = mockk<LineJoin>()
        every { JoinTypeConverter.convertToLineJoin(omhJointType) } returns lineJoin

        // Act
        omhPolyline.setJointType(omhJointType)

        // Assert
        verify { lineLayer.lineJoin(lineJoin) }
    }

    @Test
    fun `getPattern logs getter not supported and returns null`() {
        // Act
        val result = omhPolyline.getPattern()

        // Assert
        Assert.assertNull(result)
        verify { logger.logGetterNotSupported("pattern") }
    }

    @Test
    fun `setPattern logs setter not supported`() {
        // Arrange
        val omhPatternItem = mockk<OmhPatternItem>()

        // Act
        omhPolyline.setPattern(listOf(omhPatternItem))

        // Assert
        verify { logger.logSetterNotSupported("pattern") }
    }

    @Test
    fun `getPoints logs getter not supported and return null`() {
        // Act
        val result = omhPolyline.getPoints()

        // Assert
        Assert.assertNull(result)
        verify { logger.logGetterNotSupported("points") }
    }

    @Test
    fun `setPoints calls delegate method`() {
        // Arrange
        val omhCoordinate1 = mockk<OmhCoordinate>()
        val omhCoordinate2 = mockk<OmhCoordinate>()
        val points = listOf(omhCoordinate1, omhCoordinate2)

        // Act
        omhPolyline.setPoints(points)

        // Assert
        verify { polylineDelegate.updatePolylinePoints(lineLayer.sourceId, points) }
    }

    @Test
    fun `getSpans logs getter not supported and returns null`() {
        // Act
        val result = omhPolyline.getSpans()

        // Assert
        Assert.assertNull(result)
        verify { logger.logGetterNotSupported("spans") }
    }

    @Test
    fun `setSpans logs setter not supported`() {
        // Arrange
        val omhStyleSpan = mockk<OmhStyleSpan>()

        // Act
        omhPolyline.setSpans(listOf(omhStyleSpan))

        // Assert
        verify { logger.logSetterNotSupported("spans") }
    }

    @Test
    fun `getStartCap logs getter not supported and returns null`() {
        // Act
        val result = omhPolyline.getStartCap()

        // Assert
        Assert.assertNull(result)
        verify { logger.logGetterNotSupported("startCap") }
    }

    @Test
    fun `setStartCap logs setter not supported`() {
        // Arrange
        val omhCap = mockk<OmhCap>()

        // Act
        omhPolyline.setStartCap(omhCap)

        // Assert
        verify { logger.logSetterNotSupported("startCap") }
    }

    @Test
    fun `getWidth returns polyline width multiplied by scale factor`() {
        // Arrange
        val nativeWidth = 10.0
        val expectedWidth = (nativeWidth * scaleFactor).toFloat()
        every { lineLayer.lineWidth } returns nativeWidth

        // Act
        val width = omhPolyline.getWidth()

        // Assert
        Assert.assertEquals(expectedWidth, width)
    }

    @Test
    fun `setWidth sets polyline width divided by scale factor`() {
        // Arrange
        val width = 30.0f
        val nativeWidth = (width / scaleFactor).toDouble()

        // Act
        omhPolyline.setWidth(width)

        // Assert
        verify { lineLayer.lineWidth(nativeWidth) }
    }

    @Test
    fun `isVisible returns polyline visibility`() {
        // Arrange
        val expectedVisibility = true
        every { lineLayer.visibility } returns Visibility.VISIBLE

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
        verify { lineLayer.visibility(Visibility.VISIBLE) }
    }

    @Test
    fun `getZIndex logs getter not supported and return null`() {
        // Act
        val zIndex = omhPolyline.getZIndex()

        // Assert
        Assert.assertNull(zIndex)
        verify { logger.logGetterNotSupported("zIndex") }
    }

    @Test
    fun `setZIndex logs setter not supported`() {
        // Arrange
        val zIndex = 1.0f

        // Act
        omhPolyline.setZIndex(zIndex)

        // Assert
        verify { logger.logSetterNotSupported("zIndex") }
    }

    @Test
    fun `getCap returns correct cap`() {
        // Arrange
        val omhCap = mockk<OmhCap>()
        every { CapConverter.convertToOmhCap(any<LineCap>()) } returns omhCap

        // Act
        val cap = omhPolyline.getCap()

        // Assert
        Assert.assertEquals(omhCap, cap)
    }

    @Test
    fun `setCap sets cap value`() {
        val omhCap = mockk<OmhCap>()
        val lineCap = mockk<LineCap>()
        every { CapConverter.convertToLineCap(omhCap) } returns lineCap

        // Act
        omhPolyline.setCap(omhCap)

        // Assert
        verify { lineLayer.lineCap(lineCap) }
    }

    @Test
    fun `getTag returns tag`() {
        // Arrange
        val tag = "tag"
        omhPolyline.setTag(tag)

        // Act
        val result = omhPolyline.getTag()

        // Assert
        Assert.assertEquals(tag, result)
    }

    @Test
    fun `setTag sets tag`() {
        // Arrange
        val tag = "tag"

        // Act
        omhPolyline.setTag(tag)

        // Assert
        Assert.assertEquals(tag, omhPolyline.getTag())
    }
}
