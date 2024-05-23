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
import com.openmobilehub.android.maps.core.utils.uuid.UUIDGenerator
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

    private lateinit var polygonManager: PolygonManager

    @Before
    fun setUp() {
        mockkStatic("com.mapbox.maps.extension.style.sources.SourceUtils")
        mockkStatic("com.mapbox.maps.extension.style.layers.LayerUtils")

        every { uuidGenerator.generate() } returns UUID.fromString(DEFAULT_UUID)

        polygonManager = PolygonManager(mapView, uuidGenerator)
    }

    @Test
    fun `maybeHandleClick does nothing if Polygon with given id does not exist`() {
        // Arrange
        // We have empty polygons map
        val clickHandler = mockk<OmhOnPolygonClickListener>(relaxed = true)
        polygonManager.clickListener = clickHandler

        // Act
        polygonManager.maybeHandleClick("id")

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
        polygonManager.maybeHandleClick("polygon-$DEFAULT_UUID")

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
        polygonManager.maybeHandleClick("polygon-$DEFAULT_UUID")

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
    fun `onStyleLoaded adds source and layers to style for each polygon`() {
        // Arrange
        every { any<MapboxStyleManager>().addSource(any()) } just runs
        every { any<MapboxStyleManager>().addLayer(any()) } just runs

        val polygonOptions = OmhPolygonOptions().apply {
            outline = DEFAULT_OUTLINE
        }

        // Act
        polygonManager.addPolygon(polygonOptions, null)
        polygonManager.onStyleLoaded(style)

        // Assert
        verify(exactly = 1) { style.addSource(any()) }
        verify(exactly = 2) { style.addLayer(any()) }
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

    @Test
    fun `removePolygon calls removeStyleSource and removeStyleLayer methods and removes polygon`() {
        // Arrange
        val id = "polygon-$DEFAULT_UUID"
        val outlineId = "outline-$id"

        every { mapView.mapboxMap.style } returns style

        every { style.styleSourceExists(id) } returns true
        every { style.styleLayerExists(id) } returns true
        every { style.styleLayerExists(outlineId) } returns true

        every { any<MapboxStyleManager>().addSource(any()) } just runs
        every { any<MapboxStyleManager>().addLayer(any()) } just runs

        // Act
        val polygonOptions = OmhPolygonOptions().apply {
            outline = DEFAULT_OUTLINE
        }
        polygonManager.addPolygon(polygonOptions, style)

        // Assert
        Assert.assertEquals(1, polygonManager.polygons.count())

        // Act
        println(polygonManager.polygons)
        polygonManager.removePolygon(id)

        // Assert
        verify { style.removeStyleSource(id) }
        verify { style.removeStyleLayer(id) }
        verify { style.removeStyleLayer(outlineId) }

        Assert.assertEquals(0, polygonManager.polygons.count())
    }

    @Test
    fun `removePolygon does not call removeStyleSource and removeStyleLayer if the source does not exist`() {
        // Arrange
        val id = "polyline-$DEFAULT_UUID"
        val outlineId = "outline-$id"

        every { mapView.mapboxMap.style } returns style

        every { style.styleSourceExists(id) } returns false
        every { style.styleLayerExists(id) } returns true
        every { style.styleLayerExists(outlineId) } returns true

        // Act
        polygonManager.removePolygon(id)

        // Assert
        verify(exactly = 0) { style.removeStyleSource(id) }
        verify(exactly = 0) { style.removeStyleLayer(id) }
        verify(exactly = 0) { style.removeStyleLayer(outlineId) }
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
