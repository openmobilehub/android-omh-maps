package com.openmobilehub.android.maps.plugin.mapbox.presentation.maps

import android.graphics.Color
import com.mapbox.maps.MapboxStyleManager
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.layers.addLayer
import com.mapbox.maps.extension.style.layers.generated.LineLayer
import com.mapbox.maps.extension.style.layers.properties.generated.LineCap
import com.mapbox.maps.extension.style.layers.properties.generated.LineJoin
import com.mapbox.maps.extension.style.layers.properties.generated.Visibility
import com.mapbox.maps.extension.style.sources.addSource
import com.mapbox.maps.extension.style.sources.generated.GeoJsonSource
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhCap
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhPatternItem
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhStyleSpan
import com.openmobilehub.android.maps.core.presentation.models.OmhCoordinate
import com.openmobilehub.android.maps.core.presentation.models.OmhPolylineOptions
import com.openmobilehub.android.maps.core.utils.logging.UnsupportedFeatureLogger
import com.openmobilehub.android.maps.plugin.mapbox.presentation.interfaces.IPolylineDelegate
import com.openmobilehub.android.maps.plugin.mapbox.utils.CapConverter
import com.openmobilehub.android.maps.plugin.mapbox.utils.JoinTypeConverter
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.runs
import io.mockk.verify
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class OmhPolylineImplTest {
    private val source = mockk<GeoJsonSource>(relaxed = true)
    private val lineLayer = mockk<LineLayer>(relaxed = true)
    private val scaleFactor = 3.0f
    private val polylineDelegate = mockk<IPolylineDelegate>(relaxed = true)
    private val logger: UnsupportedFeatureLogger =
        mockk<UnsupportedFeatureLogger>(relaxed = true)

    private val style = mockk<Style>(relaxed = true)

    private val initialOptions = OmhPolylineOptions().apply {
        clickable = DEFAULT_CLICKABLE_STATE
        points = DEFAULT_POINTS
    }

    private lateinit var omhPolyline: OmhPolylineImpl

    @Before
    fun setUp() {
        mockkStatic("com.mapbox.maps.extension.style.sources.SourceUtils")
        mockkStatic("com.mapbox.maps.extension.style.layers.LayerUtils")

        mockkObject(CapConverter)
        mockkObject(JoinTypeConverter)

        every { style.isStyleLoaded() } returns true

        omhPolyline = OmhPolylineImpl(
            source,
            lineLayer,
            initialOptions,
            scaleFactor,
            polylineDelegate,
            logger
        )
    }

    @Test
    fun `with style - getColor returns color`() {
        // Arrange
        omhPolyline.onStyleLoaded(style)

        val expectedColor = Color.RED
        every { lineLayer.lineColorAsColorInt } returns expectedColor

        // Act
        val color = omhPolyline.getColor()

        // Assert
        verify(exactly = 1) { lineLayer.lineColorAsColorInt }
        Assert.assertEquals(expectedColor, color)
    }

    @Test
    fun `with style - setColor sets color`() {
        // Arrange
        omhPolyline.onStyleLoaded(style)

        val expectedColor = Color.RED
        every { lineLayer.lineColor(any<Int>()) } returns lineLayer

        // Act
        omhPolyline.setColor(expectedColor)

        // Assert
        verify { lineLayer.lineColor(expectedColor) }
    }

    @Test
    fun `without style - getColor returns null if color was not set`() {
        // Act
        val color = omhPolyline.getColor()

        // Assert
        verify(exactly = 0) { lineLayer.lineColor }
        Assert.assertNull(color)
    }

    @Test
    fun `without style - getColor returns buffered color if it was set`() {
        // Arrange
        val expectedColor = Color.RED

        // Act
        omhPolyline.setColor(expectedColor)
        val color = omhPolyline.getColor()

        // Assert
        verify(exactly = 0) { lineLayer.lineColor }
        Assert.assertEquals(expectedColor, color)
    }

    @Test
    fun `without style - setColor sets buffered color`() {
        // Arrange
        val expectedColor = Color.RED

        // Act
        omhPolyline.setColor(expectedColor)

        // Assert
        verify(exactly = 0) { lineLayer.lineColor(expectedColor) }
        Assert.assertEquals(expectedColor, omhPolyline.getColor())
    }

    @Test
    fun `with style - getJointType returns joint type`() {
        // Arrange
        omhPolyline.onStyleLoaded(style)

        val omhLineJoin = mockk<Int>(relaxed = true)
        every { JoinTypeConverter.convertToOmhJointType(any<LineJoin>()) } returns omhLineJoin

        // Act
        val jointType = omhPolyline.getJointType()

        // Assert
        verify(exactly = 1) { lineLayer.lineJoin }
        Assert.assertEquals(omhLineJoin, jointType)
    }

    @Test
    fun `with style - setJointType sets joint type`() {
        // Arrange
        omhPolyline.onStyleLoaded(style)

        val lineJoin = mockk<LineJoin>()
        every { JoinTypeConverter.convertToLineJoin(any<Int>()) } returns lineJoin

        // Act
        omhPolyline.setJointType(mockk<Int>(relaxed = true))

        // Assert
        verify { lineLayer.lineJoin(lineJoin) }
    }

    @Test
    fun `without style - getJointType returns null if join type was not set`() {
        // Act
        val joinType = omhPolyline.getJointType()

        // Assert
        verify(exactly = 0) { lineLayer.lineJoin }
        Assert.assertNull(joinType)
    }

    @Test
    fun `without style - getJointType returns buffered join type if it was set`() {
        // Arrange
        val omhJointType = mockk<Int>(relaxed = true)

        // Act
        omhPolyline.setJointType(omhJointType)
        val jointType = omhPolyline.getJointType()

        // Assert
        verify(exactly = 0) { lineLayer.lineJoin }
        Assert.assertEquals(omhJointType, jointType)
    }

    @Test
    fun `without style - setJointType sets buffered join type`() {
        // Arrange
        val omhJointType = mockk<Int>(relaxed = true)

        // Act
        omhPolyline.setJointType(omhJointType)

        // Assert
        verify(exactly = 0) { lineLayer.lineJoin(any<LineJoin>()) }
        Assert.assertEquals(omhJointType, omhPolyline.getJointType())
    }

    @Test
    fun `with style - getWidth returns width`() {
        // Arrange
        omhPolyline.onStyleLoaded(style)

        val nativeWidth = 10.0
        val expectedWidth = (nativeWidth * scaleFactor).toFloat()
        every { lineLayer.lineWidth } returns nativeWidth

        // Act
        val width = omhPolyline.getWidth()

        // Assert
        verify(exactly = 1) { lineLayer.lineWidth }
        Assert.assertEquals(expectedWidth, width)
    }

    @Test
    fun `with style - setWidth sets width`() {
        // Arrange
        omhPolyline.onStyleLoaded(style)

        val width = 30.0f
        val nativeWidth = (width / scaleFactor).toDouble()

        // Act
        omhPolyline.setWidth(width)

        // Assert
        verify { lineLayer.lineWidth(nativeWidth) }
    }

    @Test
    fun `without style - getWidth returns null if width was not set`() {
        // Act
        val width = omhPolyline.getWidth()

        // Assert
        verify(exactly = 0) { lineLayer.lineWidth }
        Assert.assertNull(width)
    }

    @Test
    fun `without style - getWidth returns buffered width if it was set`() {
        // Arrange
        val expectedWidth = 10f

        // Act
        omhPolyline.setWidth(expectedWidth)
        val width = omhPolyline.getWidth()

        // Assert
        verify(exactly = 0) { lineLayer.lineWidth }
        Assert.assertEquals(expectedWidth, width)
    }

    @Test
    fun `without style - setWidth sets buffered width`() {
        // Arrange
        val expectedWidth = 10f

        // Act
        omhPolyline.setWidth(expectedWidth)

        // Assert
        verify(exactly = 0) { lineLayer.lineWidth(any<Double>()) }
        Assert.assertEquals(expectedWidth, omhPolyline.getWidth())
    }

    @Test
    fun `with style - isVisible returns polyline visibility`() {
        // Arrange
        omhPolyline.onStyleLoaded(style)

        val expectedVisibility = true
        every { lineLayer.visibility } returns Visibility.VISIBLE

        // Act
        val visibility = omhPolyline.isVisible()

        // Assert
        verify(exactly = 1) { lineLayer.visibility }
        Assert.assertEquals(expectedVisibility, visibility)
    }

    @Test
    fun `with style - setVisible sets polyline visibility`() {
        // Arrange
        omhPolyline.onStyleLoaded(style)

        val visibility = true

        // Act
        omhPolyline.setVisible(visibility)

        // Assert
        verify { lineLayer.visibility(Visibility.VISIBLE) }
    }

    @Test
    fun `without style - isVisible returns initial visibility if it was not set`() {
        // Act
        val visibility = omhPolyline.isVisible()

        // Assert
        verify(exactly = 0) { lineLayer.visibility }
        Assert.assertTrue(visibility)
    }

    @Test
    fun `without style - isVisible returns buffered visibility if it was set`() {
        // Arrange
        val expectedVisibility = false

        // Act
        omhPolyline.setVisible(expectedVisibility)
        val visibility = omhPolyline.isVisible()

        // Assert
        verify(exactly = 0) { lineLayer.visibility }
        Assert.assertEquals(expectedVisibility, visibility)
    }

    @Test
    fun `without style - setVisible sets buffered visibility`() {
        // Arrange
        val expectedVisibility = false

        // Act
        omhPolyline.setVisible(expectedVisibility)

        // Assert
        verify(exactly = 0) { lineLayer.visibility(any<Visibility>()) }
        Assert.assertEquals(expectedVisibility, omhPolyline.isVisible())
    }

    @Test
    fun `with style - getCap returns cap`() {
        // Arrange
        omhPolyline.onStyleLoaded(style)

        val omhCap = mockk<OmhCap>(relaxed = true)
        every { CapConverter.convertToOmhCap(any<LineCap>()) } returns omhCap

        // Act
        val cap = omhPolyline.getCap()

        // Assert
        verify(exactly = 1) { lineLayer.lineCap }
        Assert.assertEquals(omhCap, cap)
    }

    @Test
    fun `with style - setCap sets cap`() {
        // Arrange
        omhPolyline.onStyleLoaded(style)

        val lineCap = mockk<LineCap>()
        every { CapConverter.convertToLineCap(any<OmhCap>()) } returns lineCap

        // Act
        omhPolyline.setCap(mockk<OmhCap>(relaxed = true))

        // Assert
        verify { lineLayer.lineCap(lineCap) }
    }

    @Test
    fun `without style - getCap returns null if cap was not set`() {
        // Act
        val cap = omhPolyline.getCap()

        // Assert
        verify(exactly = 0) { lineLayer.lineCap }
        Assert.assertNull(cap)
    }

    @Test
    fun `without style - getCap returns buffered cap if it was set`() {
        // Arrange
        val omhCap = mockk<OmhCap>(relaxed = true)

        // Act
        omhPolyline.setCap(omhCap)
        val cap = omhPolyline.getCap()

        // Assert
        verify(exactly = 0) { lineLayer.lineCap }
        Assert.assertEquals(omhCap, cap)
    }

    @Test
    fun `without style - setCap sets buffered cap`() {
        // Arrange
        val omhCap = mockk<OmhCap>(relaxed = true)

        // Act
        omhPolyline.setCap(omhCap)

        // Assert
        verify(exactly = 0) { lineLayer.lineCap(any<LineCap>()) }
        Assert.assertEquals(omhCap, omhPolyline.getCap())
    }

    @Test
    fun `getClickable returns initial clickable state`() {
        // Act
        val clickable = omhPolyline.getClickable()

        // Assert
        Assert.assertEquals(DEFAULT_CLICKABLE_STATE, clickable)
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
    fun `getTag returns null if tag was not set`() {
        // Act
        val tag = omhPolyline.getTag()

        // Assert
        Assert.assertNull(tag)
    }

    @Test
    fun `getTag returns tag if tag was set`() {
        // Arrange
        val expectedTag = "tag"

        // Act
        omhPolyline.setTag(expectedTag)
        val tag = omhPolyline.getTag()

        // Assert
        Assert.assertEquals(expectedTag, tag)
    }

    @Test
    fun `getPoints returns initial points if it was not updated`() {
        // Act
        val result = omhPolyline.getPoints()

        // Assert
        Assert.assertEquals(DEFAULT_POINTS, result)
    }

    @Test
    fun `getPoints returns updated points if it was updated`() {
        // Arrange
        val updatedPoints = listOf(
            OmhCoordinate(0.0, 0.0),
            OmhCoordinate(0.0, 10.0),
            OmhCoordinate(10.0, 10.0),
        )

        // Act
        omhPolyline.setPoints(updatedPoints)
        val result = omhPolyline.getPoints()

        // Assert
        Assert.assertEquals(updatedPoints, result)
    }

    @Test
    fun `setPoints calls delegate method`() {
        // Arrange
        val updatedPoints = listOf(
            OmhCoordinate(0.0, 0.0),
            OmhCoordinate(0.0, 10.0),
            OmhCoordinate(10.0, 10.0),
        )

        // Act
        omhPolyline.setPoints(updatedPoints)

        // Assert
        verify {
            polylineDelegate.updatePolylinePoints(
                lineLayer.sourceId,
                updatedPoints,
            )
        }
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
    fun `onStyleLoaded sets buffered properties and adds source and layer to map`() {
        // Arrange
        every { any<MapboxStyleManager>().addSource(any()) } just runs
        every { any<MapboxStyleManager>().addLayer(any()) } just runs

        val lineJoin = mockk<LineJoin>()
        every { JoinTypeConverter.convertToLineJoin(any<Int>()) } returns lineJoin

        val lineCap = mockk<LineCap>()
        every { CapConverter.convertToLineCap(any<OmhCap>()) } returns lineCap

        val expectedStrokeColor = Color.RED
        val expectedStrokeWidth = 30f
        val expectedNativeStrokeWidth = 10.0
        val expectedVisibility = false

        // Act
        omhPolyline.setCap(mockk<OmhCap>(relaxed = true))
        omhPolyline.setColor(expectedStrokeColor)
        omhPolyline.setJointType(mockk<Int>(relaxed = true))
        omhPolyline.setWidth(expectedStrokeWidth)
        omhPolyline.setVisible(expectedVisibility)

        omhPolyline.onStyleLoaded(style)

        // Assert
        verify { lineLayer.lineColor(expectedStrokeColor) }
        verify { lineLayer.lineJoin(lineJoin) }
        verify { lineLayer.lineWidth(expectedNativeStrokeWidth) }
        verify { lineLayer.visibility(Visibility.NONE) }

        verify { style.addSource(source) }
        verify { style.addLayer(lineLayer) }
    }

    @Test
    fun `remove calls delegate method`() {
        // Arrange
        val id = "test-id"
        every { source.sourceId } returns id

        // Act
        omhPolyline.remove()

        // Assert
        verify { polylineDelegate.removePolyline(id) }
    }

    companion object {
        private val DEFAULT_POINTS = listOf(
            OmhCoordinate(-20.0, 25.0),
            OmhCoordinate(-30.0, 20.0),
            OmhCoordinate(-30.0, 30.0),
        )
        private const val DEFAULT_CLICKABLE_STATE = true
    }
}
