package com.openmobilehub.android.maps.plugin.mapbox.presentation.maps

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import com.mapbox.bindgen.Expected
import com.mapbox.bindgen.None
import com.mapbox.maps.MapboxStyleManager
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.layers.Layer
import com.mapbox.maps.extension.style.layers.addLayer
import com.mapbox.maps.extension.style.layers.generated.SymbolLayer
import com.mapbox.maps.extension.style.layers.generated.symbolLayer
import com.mapbox.maps.extension.style.sources.addSource
import com.mapbox.maps.extension.style.sources.generated.GeoJsonSource
import com.openmobilehub.android.maps.core.presentation.models.OmhCoordinate
import com.openmobilehub.android.maps.core.utils.DrawableConverter
import com.openmobilehub.android.maps.core.utils.logging.UnsupportedFeatureLogger
import com.openmobilehub.android.maps.core.utils.uuid.DefaultUUIDGenerator
import com.openmobilehub.android.maps.plugin.mapbox.presentation.interfaces.IMapInfoWindowManagerDelegate
import com.openmobilehub.android.maps.plugin.mapbox.presentation.interfaces.IOmhInfoWindowMapViewDelegate
import com.openmobilehub.android.maps.plugin.mapbox.utils.AnchorConverter
import com.openmobilehub.android.maps.plugin.mapbox.utils.JoinTypeConverter
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.runs
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.AfterClass
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.util.UUID

class OmhMarkerImplTest {
    private val source = mockk<GeoJsonSource>(relaxed = true)
    private val symbolLayer = mockk<SymbolLayer>(relaxed = true)
    private val context = mockk<Context>(relaxed = true)
    private val logger: UnsupportedFeatureLogger =
        mockk<UnsupportedFeatureLogger>(relaxed = true)
    private val infoWindowManagerDelegate: IMapInfoWindowManagerDelegate = mockk(relaxed = true)
    private val infoWindowMapViewDelegate: IOmhInfoWindowMapViewDelegate = mockk(relaxed = true)

    private val style = mockk<Style>(relaxed = true)

    private lateinit var omhMarker: OmhMarkerImpl

    private val defaultMarkerIconDrawable = mockk<Drawable>()
    private val convertDrawableToBitmapMock = mockk<Bitmap>()

    private var markerUUID: UUID = DefaultUUIDGenerator().generate()

    @Before
    fun setUp() {
        mockkStatic("com.mapbox.maps.extension.style.sources.SourceUtils")
        mockkStatic("com.mapbox.maps.extension.style.layers.LayerUtils")
        mockkStatic("com.mapbox.maps.extension.style.layers.generated.SymbolLayerKt")

        // add a new marker, this time mocking the SymbolLayer to verify setters
        every {
            symbolLayer(
                any(),
                any(),
                any()
            )
        } answers {
            val mock = mockk<SymbolLayer>(relaxed = true)
            every { mock.iconSize } returns 1.0
            thirdArg<(Layer) -> Unit>().invoke(mock)
            mock
        }
        mockkObject(JoinTypeConverter)

        // mock for safeStyle.addImage return type to indicate a success
        val successExpectedMock = mockk<Expected<String, None>>(relaxed = true)
        every { successExpectedMock.error } returns null
        every { style.addImage(any(), any<Bitmap>(), any()) } returns successExpectedMock
        every { style.addImage(any(), any<Bitmap>()) } returns successExpectedMock
        every { style.removeStyleLayer(any()) } returns successExpectedMock
        every { style.removeStyleSource(any()) } returns successExpectedMock
        every { style.removeStyleImage(any()) } returns successExpectedMock
        every { style.isStyleLoaded() } returns true
        val res = mockk<Resources>()
        every { res.getDrawable(any<Int>()) } returns defaultMarkerIconDrawable
        every { context.resources } returns res
        every { convertDrawableToBitmapMock.width } returns 80
        every { convertDrawableToBitmapMock.height } returns 80
        every { convertDrawableToBitmapMock.density } returns 1
        mockkObject(DrawableConverter)
        every { DrawableConverter.convertDrawableToBitmap(any()) } answers { convertDrawableToBitmapMock }

        omhMarker = OmhMarkerImpl(
            markerUUID,
            context,
            symbolLayer,
            INITIAL_COORDINATE,
            IW_INITIAL_TITLE,
            IW_INITIAL_SNIPPET,
            INITIAL_DRAGGABLE,
            INITIAL_CLICKABLE,
            INITIAL_BACKGROUND_COLOR,
            infoWindowManagerDelegate = infoWindowManagerDelegate,
            infoWindowMapViewDelegate = infoWindowMapViewDelegate,
            initialIcon = INITIAL_ICON,
            logger = logger
        )
        omhMarker.setGeoJsonSource(source)
        omhMarker.omhInfoWindow.setGeoJsonSource(source)
    }

    @Test
    fun `backgroundColor should return the background color`() {
        // Act
        val actual = omhMarker.getBackgroundColor()

        // Assert
        assertEquals(INITIAL_BACKGROUND_COLOR, actual)
    }

    @Test
    fun `backgroundColor sets background color`() {
        // Act
        omhMarker.setBackgroundColor(0xFFFFFFFF.toInt())

        // Assert
        assertEquals(0xFFFFFFFF.toInt(), omhMarker.getBackgroundColor())
    }

    @Test
    fun `getClickable returns clickable state`() {
        // Act
        val clickable = omhMarker.getClickable()

        // Assert
        assertEquals(INITIAL_CLICKABLE, clickable)
    }

    @Test
    fun `setClickable sets clickable state`() {
        // Arrange
        val expectedValue = !INITIAL_CLICKABLE

        // Act
        omhMarker.setClickable(expectedValue)

        // Assert
        assertEquals(expectedValue, omhMarker.getClickable())
    }

    @Test
    fun `getDraggable returns draggable state`() {
        // Assert
        assertEquals(INITIAL_DRAGGABLE, omhMarker.getDraggable())
    }

    @Test
    fun `setDraggable sets draggable state`() {
        // Arrange
        val expectedValue = !INITIAL_DRAGGABLE

        // Act
        omhMarker.setDraggable(expectedValue)

        // Assert
        assertEquals(expectedValue, omhMarker.getDraggable())
    }

    @Test
    fun `setAnchor sets anchor`() {
        // Arrange
        val expectedValue = Pair(0.3f, 0.75f)

        // Act
        omhMarker.setAnchor(expectedValue.first, expectedValue.second)

        // Assert
        verify(exactly = 1) {
            logger.logFeatureSetterPartiallySupported(
                "anchor",
                "only discrete anchor types are supported, continuous values are converted to discrete ones"
            )
            symbolLayer.iconAnchor(
                AnchorConverter.convertContinuousToDiscreteIconAnchor(expectedValue)
            )
        }
    }

    @Test
    fun `setInfoWindowAnchor sets info window anchor`() {
        // Arrange
        val expectedValue = Pair(0.3f, 0.75f)

        // Act
        omhMarker.setInfoWindowAnchor(expectedValue.first, expectedValue.second)

        // Assert
        assertEquals(expectedValue, omhMarker.omhInfoWindow.bufferedInfoWindowAnchor)
    }

    @Test
    fun `getAlpha returns alpha value`() {
        // Act
        val actual = omhMarker.getAlpha()

        // Assert
        assertEquals(1.0f, actual)
    }

    @Test
    fun `setAlpha sets alpha value`() {
        // Arrange
        val expected = 0.732f

        // Act
        omhMarker.setAlpha(expected)
        val actual = omhMarker.getAlpha()

        // Assert
        assertEquals(expected, actual)
    }

    @Test
    fun `getTitle returns snippet value`() {
        // Act
        val actual = omhMarker.getTitle()

        // Assert
        assertEquals(IW_INITIAL_TITLE, actual)
    }

    @Test
    fun `setTitle sets snippet value`() {
        // Arrange
        val expected = "Title example"

        // Act
        omhMarker.setTitle(expected)
        val actual = omhMarker.getTitle()

        // Assert
        assertEquals(expected, actual)
    }

    @Test
    fun `getSnippet returns snippet value`() {
        // Act
        val actual = omhMarker.getSnippet()

        // Assert
        assertEquals(IW_INITIAL_SNIPPET, actual)
    }

    @Test
    fun `setSnippet sets snippet value`() {
        // Arrange
        val expected = "Snippet example"

        // Act
        omhMarker.setSnippet(expected)
        val actual = omhMarker.getSnippet()

        // Assert
        assertEquals(expected, actual)
    }

    @Test
    fun `getIsVisible gets isVisible value`() {
        // Act
        val actual = omhMarker.getIsVisible()

        // Assert
        assertEquals(true, actual)
    }

    @Test
    fun `setIsVisible sets isVisible value`() {
        // Arrange
        val expected = false

        // Act
        omhMarker.setIsVisible(expected)

        // Assert
        assertEquals(expected, omhMarker.getIsVisible())
    }

    @Test
    fun `getIsFlat gets isFlat value`() {
        // Act
        val actual = omhMarker.getIsFlat()

        // Assert
        assertEquals(false, actual)
    }

    @Test
    fun `setIsFlat sets isFlat value`() {
        // Arrange
        val expected = true

        // Act
        omhMarker.setIsFlat(expected)
        val actual = omhMarker.getIsFlat()

        // Assert
        assertEquals(expected, actual)
    }

    @Test
    fun `getRotation gets rotation value`() {
        // Act
        val actual = omhMarker.getRotation()

        // Assert
        assertEquals(0f, actual)
    }

    @Test
    fun `setRotation sets rotation value`() {
        // Arrange
        val expected = 45.78f

        // Act
        omhMarker.setRotation(expected)
        val actual = omhMarker.getRotation()

        // Assert
        assertEquals(expected, actual)
    }

    @Test
    fun `remove() removes the marker and closes info window after styles are loaded`() {
        // Arrange
        // every { omhMarker.hideInfoWindow() } just runs
        every { any<MapboxStyleManager>().addSource(any()) } just runs
        every { any<MapboxStyleManager>().addLayer(any()) } just runs
        every {
            symbolLayer.iconSize
        } returns 1.0
        every {
            omhMarker.omhInfoWindow.infoWindowSymbolLayer.iconSize
        } returns 1.0

        // Act
        omhMarker.onStyleLoaded(style)
        omhMarker.remove()

        // Assert
        assertEquals(omhMarker.isRemoved, true)
        verify(exactly = 1) {
            // marker cleanup
            style.removeStyleLayer(OmhMarkerImpl.getSymbolLayerID(markerUUID))
            style.removeStyleSource(OmhMarkerImpl.getGeoJsonSourceID(markerUUID))
            style.removeStyleImage(omhMarker.getMarkerIconID(omhMarker.isCustomIconSet))

            // IW cleanup
            style.removeStyleLayer(omhMarker.omhInfoWindow.getSymbolLayerID())
            style.removeStyleSource(omhMarker.omhInfoWindow.getGeoJsonSourceID())
        }
        verify {
            omhMarker.omhInfoWindow.remove()
        }
        assertEquals(omhMarker.getIsInfoWindowShown(), false)
    }

    companion object {
        private val INITIAL_COORDINATE = OmhCoordinate(0.0, 0.0)
        private const val IW_INITIAL_TITLE = "Some initial title"
        private const val IW_INITIAL_SNIPPET = "Some initial snippet"

        private const val INITIAL_DRAGGABLE = true
        private const val INITIAL_CLICKABLE = true
        private val INITIAL_ICON: Drawable? = null

        @ColorInt
        private const val INITIAL_BACKGROUND_COLOR: Int = 0x77FE4A63

        @JvmStatic
        @AfterClass
        fun cleanup() {
            unmockkAll()
        }
    }
}
