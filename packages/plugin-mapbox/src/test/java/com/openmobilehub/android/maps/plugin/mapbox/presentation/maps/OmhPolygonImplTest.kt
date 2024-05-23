package com.openmobilehub.android.maps.plugin.mapbox.presentation.maps

import android.graphics.Color
import com.mapbox.maps.MapboxStyleManager
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.layers.addLayer
import com.mapbox.maps.extension.style.layers.generated.FillLayer
import com.mapbox.maps.extension.style.layers.generated.LineLayer
import com.mapbox.maps.extension.style.layers.properties.generated.LineJoin
import com.mapbox.maps.extension.style.layers.properties.generated.Visibility
import com.mapbox.maps.extension.style.sources.addSource
import com.mapbox.maps.extension.style.sources.generated.GeoJsonSource
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhPatternItem
import com.openmobilehub.android.maps.core.presentation.models.OmhCoordinate
import com.openmobilehub.android.maps.core.presentation.models.OmhPolygonOptions
import com.openmobilehub.android.maps.core.utils.ScreenUnitConverter
import com.openmobilehub.android.maps.core.utils.logging.UnsupportedFeatureLogger
import com.openmobilehub.android.maps.plugin.mapbox.presentation.interfaces.IPolygonDelegate
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

class OmhPolygonImplTest {
    private val source = mockk<GeoJsonSource>(relaxed = true)
    private val lineLayer = mockk<LineLayer>(relaxed = true)
    private val fillLayer = mockk<FillLayer>(relaxed = true)
    private val polygonDelegate = mockk<IPolygonDelegate>(relaxed = true)
    private val logger: UnsupportedFeatureLogger =
        mockk<UnsupportedFeatureLogger>(relaxed = true)

    private val style = mockk<Style>(relaxed = true)

    private val initialOptions = OmhPolygonOptions().apply {
        clickable = DEFAULT_CLICKABLE_STATE
        outline = DEFAULT_OUTLINE
        holes = DEFAULT_HOLES
    }

    private lateinit var omhPolygon: OmhPolygonImpl

    @Before
    fun setUp() {
        mockkStatic("com.mapbox.maps.extension.style.sources.SourceUtils")
        mockkStatic("com.mapbox.maps.extension.style.layers.LayerUtils")

        mockkObject(JoinTypeConverter)

        mockkObject(ScreenUnitConverter)

        every { style.isStyleLoaded() } returns true

        omhPolygon = OmhPolygonImpl(
            source,
            fillLayer,
            lineLayer,
            initialOptions,
            polygonDelegate,
            logger
        )
    }

    @Test
    fun `with style - getStrokeColor returns stroke color`() {
        // Arrange
        omhPolygon.onStyleLoaded(style)

        val expectedColor = Color.RED
        every { lineLayer.lineColorAsColorInt } returns expectedColor

        // Act
        val color = omhPolygon.getStrokeColor()

        // Assert
        verify(exactly = 1) { lineLayer.lineColorAsColorInt }
        Assert.assertEquals(expectedColor, color)
    }

    @Test
    fun `with style - setStrokeColor sets stroke color`() {
        // Arrange
        omhPolygon.onStyleLoaded(style)

        val expectedColor = Color.RED
        every { lineLayer.lineColor(any<Int>()) } returns lineLayer

        // Act
        omhPolygon.setStrokeColor(expectedColor)

        // Assert
        verify { lineLayer.lineColor(expectedColor) }
    }

    @Test
    fun `without style - getStrokeColor returns null if stroke color was not set`() {
        // Act
        val color = omhPolygon.getStrokeColor()

        // Assert
        verify(exactly = 0) { lineLayer.lineColor }
        Assert.assertNull(color)
    }

    @Test
    fun `without style - getStrokeColor returns buffered stroke color if it was set`() {
        // Arrange
        val expectedColor = Color.RED

        // Act
        omhPolygon.setStrokeColor(expectedColor)
        val color = omhPolygon.getStrokeColor()

        // Assert
        verify(exactly = 0) { lineLayer.lineColor }
        Assert.assertEquals(expectedColor, color)
    }

    @Test
    fun `without style - setStrokeColor sets buffered stroke color`() {
        // Arrange
        val expectedColor = Color.RED

        // Act
        omhPolygon.setStrokeColor(expectedColor)

        // Assert
        verify(exactly = 0) { lineLayer.lineColor(expectedColor) }
        Assert.assertEquals(expectedColor, omhPolygon.getStrokeColor())
    }

    @Test
    fun `with style - getFillColor returns fill color`() {
        // Arrange
        omhPolygon.onStyleLoaded(style)

        val expectedColor = Color.RED
        every { fillLayer.fillColorAsColorInt } returns expectedColor

        // Act
        val color = omhPolygon.getFillColor()

        // Assert
        verify(exactly = 1) { fillLayer.fillColorAsColorInt }
        Assert.assertEquals(expectedColor, color)
    }

    @Test
    fun `with style - setFillColor sets fill color`() {
        // Arrange
        omhPolygon.onStyleLoaded(style)

        val expectedColor = Color.RED
        every { fillLayer.fillColor(any<Int>()) } returns fillLayer

        // Act
        omhPolygon.setFillColor(expectedColor)

        // Assert
        verify { fillLayer.fillColor(expectedColor) }
    }

    @Test
    fun `without style - getFillColor returns null if fill color was not set`() {
        // Act
        val color = omhPolygon.getFillColor()

        // Assert
        verify(exactly = 0) { fillLayer.fillColor }
        Assert.assertNull(color)
    }

    @Test
    fun `without style - getFillColor returns buffered fill color if it was set`() {
        // Arrange
        val expectedColor = Color.RED

        // Act
        omhPolygon.setFillColor(expectedColor)
        val color = omhPolygon.getFillColor()

        // Assert
        verify(exactly = 0) { fillLayer.fillColor }
        Assert.assertEquals(expectedColor, color)
    }

    @Test
    fun `without style - setFillColor sets buffered fill color`() {
        // Arrange
        val expectedColor = Color.RED

        // Act
        omhPolygon.setFillColor(expectedColor)

        // Assert
        verify(exactly = 0) { fillLayer.fillColor(expectedColor) }
        Assert.assertEquals(expectedColor, omhPolygon.getFillColor())
    }

    @Test
    fun `with style - getStrokeJointType returns stroke joint type`() {
        // Arrange
        omhPolygon.onStyleLoaded(style)

        val omhLineJoin = mockk<Int>(relaxed = true)
        every { JoinTypeConverter.convertToOmhJointType(any<LineJoin>()) } returns omhLineJoin

        // Act
        val jointType = omhPolygon.getStrokeJointType()

        // Assert
        verify(exactly = 1) { lineLayer.lineJoin }
        Assert.assertEquals(omhLineJoin, jointType)
    }

    @Test
    fun `with style - setStrokeJointType sets stroke joint type`() {
        // Arrange
        omhPolygon.onStyleLoaded(style)

        val lineJoin = mockk<LineJoin>()
        every { JoinTypeConverter.convertToLineJoin(any<Int>()) } returns lineJoin

        // Act
        omhPolygon.setStrokeJointType(mockk<Int>(relaxed = true))

        // Assert
        verify { lineLayer.lineJoin(lineJoin) }
    }

    @Test
    fun `without style - getStrokeJointType returns null if stroke join type was not set`() {
        // Act
        val joinType = omhPolygon.getStrokeJointType()

        // Assert
        verify(exactly = 0) { lineLayer.lineJoin }
        Assert.assertNull(joinType)
    }

    @Test
    fun `without style - getStrokeJointType returns buffered stroke join type if it was set`() {
        // Arrange
        val mockedJointType = mockk<Int>(relaxed = true)

        // Act
        omhPolygon.setStrokeJointType(mockedJointType)
        val jointType = omhPolygon.getStrokeJointType()

        // Assert
        verify(exactly = 0) { lineLayer.lineJoin }
        Assert.assertEquals(mockedJointType, jointType)
    }

    @Test
    fun `without style - setStrokeJointType sets buffered stroke join type`() {
        // Arrange
        val mockedJointType = mockk<Int>(relaxed = true)

        // Act
        omhPolygon.setStrokeJointType(mockedJointType)

        // Assert
        verify(exactly = 0) { lineLayer.lineJoin(any<LineJoin>()) }
        Assert.assertEquals(mockedJointType, omhPolygon.getStrokeJointType())
    }

    @Test
    fun `with style - getStrokeWidth returns stroke width`() {
        // Arrange
        omhPolygon.onStyleLoaded(style)

        val density = 2.0f
        every { ScreenUnitConverter.dpToPx(any<Float>()) } answers { firstArg<Float>() * density }

        val nativeWidth = 10.0
        val expectedWidth = (nativeWidth * density).toFloat()
        every { lineLayer.lineWidth } returns nativeWidth

        // Act
        val width = omhPolygon.getStrokeWidth()

        // Assert
        verify(exactly = 1) { lineLayer.lineWidth }
        Assert.assertEquals(expectedWidth, width)
    }

    @Test
    fun `with style - setStrokeWidth sets stroke width`() {
        // Arrange
        omhPolygon.onStyleLoaded(style)

        val density = 2.0f
        every { ScreenUnitConverter.pxToDp(any<Float>()) } answers { firstArg<Float>() / density }

        val width = 30.0f
        val nativeWidth = (width / density).toDouble()

        // Act
        omhPolygon.setStrokeWidth(width)

        // Assert
        verify { lineLayer.lineWidth(nativeWidth) }
    }

    @Test
    fun `without style - getStrokeWidth returns null if stroke width was not set`() {
        // Act
        val width = omhPolygon.getStrokeWidth()

        // Assert
        verify(exactly = 0) { lineLayer.lineWidth }
        Assert.assertNull(width)
    }

    //
    @Test
    fun `without style - getStrokeWidth returns buffered stroke width if it was set`() {
        // Arrange
        val expectedWidth = 10f

        // Act
        omhPolygon.setStrokeWidth(expectedWidth)
        val width = omhPolygon.getStrokeWidth()

        // Assert
        verify(exactly = 0) { lineLayer.lineWidth }
        Assert.assertEquals(expectedWidth, width)
    }

    @Test
    fun `without style - setStrokeWidth sets buffered stroke width`() {
        // Arrange
        val expectedWidth = 10f

        // Act
        omhPolygon.setStrokeWidth(expectedWidth)

        // Assert
        verify(exactly = 0) { lineLayer.lineWidth(any<Double>()) }
        Assert.assertEquals(expectedWidth, omhPolygon.getStrokeWidth())
    }

    @Test
    fun `with style - isVisible returns polygon visibility`() {
        // Arrange
        omhPolygon.onStyleLoaded(style)

        val expectedVisibility = true
        every { lineLayer.visibility } returns Visibility.VISIBLE
        every { fillLayer.visibility } returns Visibility.VISIBLE

        // Act
        val visibility = omhPolygon.isVisible()

        // Assert
        verify(exactly = 1) { lineLayer.visibility }
        verify(exactly = 1) { fillLayer.visibility }
        Assert.assertEquals(expectedVisibility, visibility)
    }

    @Test
    fun `with style - setVisible sets polygon visibility`() {
        // Arrange
        omhPolygon.onStyleLoaded(style)

        val visibility = true

        // Act
        omhPolygon.setVisible(visibility)

        // Assert
        verify { lineLayer.visibility(Visibility.VISIBLE) }
        verify { fillLayer.visibility(Visibility.VISIBLE) }
    }

    @Test
    fun `without style - isVisible returns initial visibility if it was not set`() {
        // Act
        val visibility = omhPolygon.isVisible()

        // Assert
        verify(exactly = 0) { lineLayer.visibility }
        verify(exactly = 0) { fillLayer.visibility }
        Assert.assertTrue(visibility)
    }

    @Test
    fun `without style - isVisible returns buffered visibility if it was set`() {
        // Arrange
        val expectedVisibility = false

        // Act
        omhPolygon.setVisible(expectedVisibility)
        val visibility = omhPolygon.isVisible()

        // Assert
        verify(exactly = 0) { lineLayer.visibility }
        verify(exactly = 0) { fillLayer.visibility }
        Assert.assertEquals(expectedVisibility, visibility)
    }

    @Test
    fun `without style - setVisible sets buffered visibility`() {
        // Arrange
        val expectedVisibility = false

        // Act
        omhPolygon.setVisible(expectedVisibility)

        // Assert
        verify(exactly = 0) { lineLayer.visibility(any<Visibility>()) }
        verify(exactly = 0) { fillLayer.visibility(any<Visibility>()) }
        Assert.assertEquals(expectedVisibility, omhPolygon.isVisible())
    }

    @Test
    fun `getClickable returns initial clickable state`() {
        // Act
        val clickable = omhPolygon.getClickable()

        // Assert
        Assert.assertEquals(DEFAULT_CLICKABLE_STATE, clickable)
    }

    @Test
    fun `setClickable sets clickable state`() {
        // Arrange
        val expectedValue = true

        // Act
        omhPolygon.setClickable(expectedValue)

        // Assert
        Assert.assertEquals(expectedValue, omhPolygon.getClickable())
    }

    @Test
    fun `getTag returns null if tag was not set`() {
        // Act
        val tag = omhPolygon.getTag()

        // Assert
        Assert.assertNull(tag)
    }

    @Test
    fun `getTag returns tag if tag was set`() {
        // Arrange
        val expectedTag = "tag"

        // Act
        omhPolygon.setTag(expectedTag)
        val tag = omhPolygon.getTag()

        // Assert
        Assert.assertEquals(expectedTag, tag)
    }

    @Test
    fun `getOutline returns initial outline if it was not updated`() {
        // Act
        val result = omhPolygon.getOutline()

        // Assert
        Assert.assertEquals(DEFAULT_OUTLINE, result)
    }

    @Test
    fun `getOutline returns updated outline if it was updated`() {
        // Arrange
        val updatedOutline = listOf(
            OmhCoordinate(0.0, 0.0),
            OmhCoordinate(0.0, 10.0),
            OmhCoordinate(10.0, 10.0),
        )

        // Act
        omhPolygon.setOutline(updatedOutline)
        val result = omhPolygon.getOutline()

        // Assert
        Assert.assertEquals(updatedOutline, result)
    }

    @Test
    fun `setOutline calls delegate method`() {
        // Arrange
        val updatedOutline = listOf(
            OmhCoordinate(0.0, 0.0),
            OmhCoordinate(0.0, 10.0),
            OmhCoordinate(10.0, 10.0),
        )

        // Act
        omhPolygon.setOutline(updatedOutline)

        // Assert
        verify {
            polygonDelegate.updatePolygonSource(
                fillLayer.sourceId,
                updatedOutline,
                DEFAULT_HOLES
            )
        }
    }

    @Test
    fun `getHoles returns initial holes if it was not updated`() {
        // Act
        val result = omhPolygon.getHoles()

        // Assert
        Assert.assertEquals(DEFAULT_HOLES, result)
    }

    @Test
    fun `getHoles returns updated holes if it was updated`() {
        // Arrange
        val updatedHoles = listOf(
            listOf(
                OmhCoordinate(0.0, 5.0),
                OmhCoordinate(0.0, 10.0),
                OmhCoordinate(10.0, 7.5),
            )
        )

        // Act
        omhPolygon.setHoles(updatedHoles)
        val result = omhPolygon.getHoles()

        // Assert
        Assert.assertEquals(updatedHoles, result)
    }

    @Test
    fun `setHoles calls delegate method`() {
        // Arrange
        val updatedHoles = listOf(
            listOf(
                OmhCoordinate(0.0, 5.0),
                OmhCoordinate(0.0, 10.0),
                OmhCoordinate(10.0, 7.5),
            )
        )

        // Act
        omhPolygon.setHoles(updatedHoles)

        // Assert
        verify {
            polygonDelegate.updatePolygonSource(
                fillLayer.sourceId,
                DEFAULT_OUTLINE,
                updatedHoles
            )
        }
    }

    @Test
    fun `getStrokePattern logs getter not supported and returns null`() {
        // Act
        val result = omhPolygon.getStrokePattern()

        // Assert
        Assert.assertNull(result)
        verify { logger.logGetterNotSupported("strokePattern") }
    }

    @Test
    fun `setStrokePattern logs setter not supported`() {
        // Arrange
        val omhPatternItem = mockk<OmhPatternItem>()

        // Act
        omhPolygon.setStrokePattern(listOf(omhPatternItem))

        // Assert
        verify { logger.logSetterNotSupported("strokePattern") }
    }

    @Test
    fun `getZIndex logs getter not supported and return null`() {
        // Act
        val zIndex = omhPolygon.getZIndex()

        // Assert
        Assert.assertNull(zIndex)
        verify { logger.logGetterNotSupported("zIndex") }
    }

    @Test
    fun `setZIndex logs setter not supported`() {
        // Arrange
        val zIndex = 1.0f

        // Act
        omhPolygon.setZIndex(zIndex)

        // Assert
        verify { logger.logSetterNotSupported("zIndex") }
    }

    @Test
    fun `onStyleLoaded sets buffered properties and adds source and layers to map`() {
        // Arrange
        every { any<MapboxStyleManager>().addSource(any()) } just runs
        every { any<MapboxStyleManager>().addLayer(any()) } just runs

        val lineJoin = mockk<LineJoin>()
        every { JoinTypeConverter.convertToLineJoin(any<Int>()) } returns lineJoin

        val density = 2.0f
        every { ScreenUnitConverter.pxToDp(any<Float>()) } answers { firstArg<Float>() / density }

        val expectedStrokeColor = Color.RED
        val expectedFillColor = Color.BLUE
        val expectedStrokeJoinType = 1
        val expectedStrokeWidth = 30f
        val expectedNativeStrokeWidth = (expectedStrokeWidth / density).toDouble()
        val expectedVisibility = false

        // Act
        omhPolygon.setStrokeColor(expectedStrokeColor)
        omhPolygon.setFillColor(expectedFillColor)
        omhPolygon.setStrokeJointType(expectedStrokeJoinType)
        omhPolygon.setStrokeWidth(expectedStrokeWidth)
        omhPolygon.setVisible(expectedVisibility)

        omhPolygon.onStyleLoaded(style)

        // Assert
        verify { lineLayer.lineColor(expectedStrokeColor) }
        verify { fillLayer.fillColor(expectedFillColor) }
        verify { lineLayer.lineJoin(lineJoin) }
        verify { lineLayer.lineWidth(expectedNativeStrokeWidth) }
        verify { lineLayer.visibility(Visibility.NONE) }
        verify { fillLayer.visibility(Visibility.NONE) }

        verify { style.addSource(source) }
        verify { style.addLayer(fillLayer) }
        verify { style.addLayer(lineLayer) }
    }

    @Test
    fun `remove calls delegate method`() {
        // Arrange
        val id = "test-id"
        every { source.sourceId } returns id

        // Act
        omhPolygon.remove()

        // Assert
        verify { polygonDelegate.removePolygon(id) }
    }

    companion object {
        private val DEFAULT_OUTLINE = listOf(
            OmhCoordinate(-20.0, 25.0),
            OmhCoordinate(-30.0, 20.0),
            OmhCoordinate(-30.0, 30.0),
        )
        private val DEFAULT_HOLES = listOf(
            listOf(
                OmhCoordinate(0.0, 5.0),
                OmhCoordinate(0.0, 10.0),
                OmhCoordinate(10.0, 7.5),
            )
        )
        private const val DEFAULT_CLICKABLE_STATE = true
    }
}
