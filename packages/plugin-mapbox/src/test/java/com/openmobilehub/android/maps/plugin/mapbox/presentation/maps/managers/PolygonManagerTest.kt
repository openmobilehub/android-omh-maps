package com.openmobilehub.android.maps.plugin.mapbox.presentation.maps.managers

import com.mapbox.geojson.Feature
import com.mapbox.maps.MapView
import com.mapbox.maps.MapboxStyleManager
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.layers.addLayer
import com.mapbox.maps.extension.style.sources.addSource
import com.mapbox.maps.extension.style.sources.generated.GeoJsonSource
import com.mapbox.maps.extension.style.sources.getSource
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhOnPolygonClickListener
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhPolygon
import com.openmobilehub.android.maps.core.presentation.models.OmhCoordinate
import com.openmobilehub.android.maps.core.presentation.models.OmhPolygonOptions
import com.openmobilehub.android.maps.plugin.mapbox.presentation.maps.OmhPolygonImpl
import com.openmobilehub.android.maps.plugin.mapbox.utils.uuid.UUIDGenerator
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.runs
import io.mockk.verify
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.util.UUID

class PolygonManagerTest {
    private val mapView = mockk<MapView>(relaxed = true)
    private val style = mockk<Style>(relaxed = true)
    private val uuidGenerator = mockk<UUIDGenerator>()
    private val scaleFactor = 1.0f

    private lateinit var polygonManager: PolygonManager

    @Before
    fun setUp() {
        mockkStatic("com.mapbox.maps.extension.style.sources.SourceUtils")
        mockkStatic("com.mapbox.maps.extension.style.layers.LayerUtils")

        every { uuidGenerator.generate() } returns UUID.fromString(DEFAULT_UUID)

        polygonManager = PolygonManager(mapView, scaleFactor, uuidGenerator)
    }

    @Test
    fun `maybeHandleClick does nothing if Polygon with given id does not exist`() {
        // Arrange
        val clickHandler = mockk<OmhOnPolygonClickListener>(relaxed = true)
        polygonManager.clickListener = clickHandler

        // Act
        polygonManager.maybeHandleClick("Polyline", "id")

        // Assert
        verify(exactly = 0) { clickHandler.onPolygonClick(any<OmhPolygon>()) }
    }

    @Test
    fun `maybeHandleClick does nothing if type does not match Polygon type`() {
        // Arrange
        // We have empty polygons map
        val clickHandler = mockk<OmhOnPolygonClickListener>(relaxed = true)
        polygonManager.clickListener = clickHandler

        // Act
        polygonManager.maybeHandleClick("Polygon", "id")

        // Assert
        verify(exactly = 0) { clickHandler.onPolygonClick(any<OmhPolygon>()) }
    }

    @Test
    fun `maybeHandleClick does nothing if the added Polygon is not clickable`() {
        // Arrange
        val clickHandler = mockk<OmhOnPolygonClickListener>(relaxed = true)
        polygonManager.clickListener = clickHandler

        val polygonOptions = OmhPolygonOptions().apply {
            outline = DEFAULT_OUTLINE
            clickable = false
        }

        // Act
        polygonManager.addPolygon(polygonOptions, null)
        polygonManager.maybeHandleClick("Polygon", "polygon-$DEFAULT_UUID")

        // Assert
        verify(exactly = 0) { clickHandler.onPolygonClick(any<OmhPolygon>()) }
    }

    @Test
    fun `maybeHandleClick calls onPolygonClick callback if Polygon was clicked`() {
        // Arrange
        val clickHandler = mockk<OmhOnPolygonClickListener>(relaxed = true)
        polygonManager.clickListener = clickHandler

        val polygonOptions = OmhPolygonOptions().apply {
            outline = DEFAULT_OUTLINE
            clickable = true
        }

        // Act
        polygonManager.addPolygon(polygonOptions, null)
        polygonManager.maybeHandleClick("Polygon", "polygon-$DEFAULT_UUID")

        // Assert
        verify(exactly = 1) { clickHandler.onPolygonClick(any<OmhPolygon>()) }
    }

    @Test
    fun `addPolygon returns OmhPolygon and calls style methods if style was initialized`() {
        // Arrange
        every { any<MapboxStyleManager>().addSource(any()) } just runs
        every { any<MapboxStyleManager>().addLayer(any()) } just runs

        val polygonOptions = OmhPolygonOptions().apply {
            outline = DEFAULT_OUTLINE
            holes = EXAMPLE_HOLES
        }

        // Act
        val polygon = polygonManager.addPolygon(polygonOptions, style)

        // Assert
        verify(exactly = 1) { style.addSource(any()) }
        verify(exactly = 2) { style.addLayer(any()) }
        Assert.assertNotNull(polygon)
    }

    @Test
    fun `addPolygon returns OmhPolygon and add polygon source and layers to queue if style was initialized`() {
        // Arrange
        val polygonOptions = OmhPolygonOptions().apply {
            outline = DEFAULT_OUTLINE
            holes = EXAMPLE_HOLES
        }

        // Act
        val polygon = polygonManager.addPolygon(polygonOptions, null)

        // Assert
        Assert.assertTrue(polygonManager.getQueue().isNotEmpty())
        Assert.assertNotNull(polygon)
    }

    @Test
    fun `onStyleLoaded adds source and layers to style and applies buffered properties for each polygon`() {
        // Arrange
        every { any<MapboxStyleManager>().addSource(any()) } just runs
        every { any<MapboxStyleManager>().addLayer(any()) } just runs

        val polygonOptions = OmhPolygonOptions().apply {
            outline = DEFAULT_OUTLINE
        }

        // Act
        val polygon = polygonManager.addPolygon(polygonOptions, null) as OmhPolygonImpl
        polygonManager.onStyleLoaded(style)

        // Assert
        verify(exactly = 1) { style.addSource(any()) }
        verify(exactly = 2) { style.addLayer(any()) }
        verify(exactly = 1) { polygon.applyBufferedProperties(style) }
        Assert.assertTrue(polygonManager.getQueue().isEmpty())
    }

    @Test
    fun `updatePolygonSource updates the polygons source`() {
        // Arrange
        val sourceId = "sourceId"
        val geoJsonSource = mockk<GeoJsonSource>(relaxed = true)

        every { any<MapboxStyleManager>().getSource(any()) } returns geoJsonSource

        // Act
        polygonManager.updatePolygonSource(sourceId, DEFAULT_OUTLINE, EXAMPLE_HOLES)

        // Assert
        verify { geoJsonSource.feature(any<Feature>()) }
    }

    companion object {
        private const val DEFAULT_UUID = "00000000-0000-0000-0000-000000000000"
        private val DEFAULT_OUTLINE = listOf(
            OmhCoordinate(-20.0, 25.0),
            OmhCoordinate(-30.0, 20.0),
            OmhCoordinate(-30.0, 30.0),
        )
        private val EXAMPLE_HOLES = listOf(
            listOf(
                OmhCoordinate(0.0, 5.0),
                OmhCoordinate(0.0, 10.0),
                OmhCoordinate(10.0, 7.5),
            )
        )
    }
}
