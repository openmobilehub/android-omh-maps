package com.openmobilehub.android.maps.plugin.mapbox.presentation.maps.managers

import com.mapbox.geojson.Feature
import com.mapbox.maps.MapView
import com.mapbox.maps.MapboxStyleManager
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.layers.addLayer
import com.mapbox.maps.extension.style.sources.addSource
import com.mapbox.maps.extension.style.sources.generated.GeoJsonSource
import com.mapbox.maps.extension.style.sources.getSource
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhOnPolylineClickListener
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhPolyline
import com.openmobilehub.android.maps.core.presentation.models.OmhCoordinate
import com.openmobilehub.android.maps.core.presentation.models.OmhPolylineOptions
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

class PolylineManagerTest {
    private val mapView = mockk<MapView>(relaxed = true)
    private val style = mockk<Style>(relaxed = true)
    private val uuidGenerator = mockk<UUIDGenerator>()

    private lateinit var polylineManager: PolylineManager

    @Before
    fun setUp() {
        mockkStatic("com.mapbox.maps.extension.style.sources.SourceUtils")
        mockkStatic("com.mapbox.maps.extension.style.layers.LayerUtils")

        every { uuidGenerator.generate() } returns UUID.fromString(DEFAULT_UUID)
        polylineManager = PolylineManager(mapView, uuidGenerator)
    }

    @Test
    fun `maybeHandleClick does nothing if LineString with given id does not exist`() {
        // Arrange
        // We have empty polylines map
        val clickHandler = mockk<OmhOnPolylineClickListener>(relaxed = true)
        polylineManager.clickListener = clickHandler

        // Act
        polylineManager.maybeHandleClick("id")

        // Assert
        verify(exactly = 0) { clickHandler.onPolylineClick(any<OmhPolyline>()) }
    }

    @Test
    fun `maybeHandleClick does nothing if the added Polyline is not clickable`() {
        // Arrange
        val clickHandler = mockk<OmhOnPolylineClickListener>(relaxed = true)
        polylineManager.clickListener = clickHandler

        val polylineOptions = OmhPolylineOptions().apply {
            points = DEFAULT_POINTS
            clickable = false
        }

        // Act
        polylineManager.addPolyline(polylineOptions, null)
        polylineManager.maybeHandleClick("polyline-$DEFAULT_UUID")

        // Assert
        verify(exactly = 0) { clickHandler.onPolylineClick(any<OmhPolyline>()) }
    }

    @Test
    fun `maybeHandleClick calls onPolylineClick callback if Polyline was clicked`() {
        // Arrange
        val clickHandler = mockk<OmhOnPolylineClickListener>(relaxed = true)
        polylineManager.clickListener = clickHandler

        val polylineOptions = OmhPolylineOptions().apply {
            points = DEFAULT_POINTS
            clickable = true
        }

        // Act
        polylineManager.addPolyline(polylineOptions, null)
        polylineManager.maybeHandleClick("polyline-$DEFAULT_UUID")

        // Assert
        verify(exactly = 1) { clickHandler.onPolylineClick(any<OmhPolyline>()) }
    }

    @Test
    fun `addPolyline returns OmhPolyline and calls style methods if style was initialized`() {
        // Arrange
        every { any<MapboxStyleManager>().addSource(any()) } just runs
        every { any<MapboxStyleManager>().addLayer(any()) } just runs

        val polylineOptions = OmhPolylineOptions().apply {
            points = DEFAULT_POINTS
        }

        // Act
        val polyline = polylineManager.addPolyline(polylineOptions, style)

        // Assert
        verify(exactly = 1) { style.addSource(any()) }
        verify(exactly = 1) { style.addLayer(any()) }
        Assert.assertNotNull(polyline)
    }

    @Test
    fun `onStyleLoaded adds source and layers to style for each polyline`() {
        // Arrange
        every { any<MapboxStyleManager>().addSource(any()) } just runs
        every { any<MapboxStyleManager>().addLayer(any()) } just runs

        val polylineOptions = OmhPolylineOptions().apply {
            points = DEFAULT_POINTS
        }

        // Act
        polylineManager.addPolyline(polylineOptions, null)
        polylineManager.onStyleLoaded(style)

        // Assert
        verify(exactly = 1) { style.addSource(any()) }
        verify(exactly = 1) { style.addLayer(any()) }
    }

    @Test
    fun `updatePolylinePoints updates the polyline source`() {
        // Arrange
        val sourceId = "sourceId"
        val geoJsonSource = mockk<GeoJsonSource>(relaxed = true)

        every { any<MapboxStyleManager>().getSource(any()) } returns geoJsonSource

        // Act
        polylineManager.updatePolylinePoints(sourceId, DEFAULT_POINTS)

        // Assert
        verify { geoJsonSource.feature(any<Feature>()) }
    }

    @Test
    fun `removePolyline calls removeStyleSource and removeStyleLayer methods and removes polyline`() {
        // Arrange
        val id = "polyline-$DEFAULT_UUID"

        every { mapView.mapboxMap.style } returns style

        every { style.styleSourceExists(id) } returns true
        every { style.styleLayerExists(id) } returns true

        every { any<MapboxStyleManager>().addSource(any()) } just runs
        every { any<MapboxStyleManager>().addLayer(any()) } just runs

        // Act
        val polylineOptions = OmhPolylineOptions()
        polylineManager.addPolyline(polylineOptions, style)

        // Assert
        Assert.assertEquals(1, polylineManager.polylines.count())

        // Act
        polylineManager.removePolyline(id)

        // Assert
        verify { style.removeStyleSource(id) }
        verify { style.removeStyleLayer(id) }

        Assert.assertEquals(0, polylineManager.polylines.count())
    }

    @Test
    fun `removePolyline does not call removeStyleSource and removeStyleLayer if the source does not exist`() {
        // Arrange
        val id = "polyline-$DEFAULT_UUID"

        every { mapView.mapboxMap.style } returns style

        every { style.styleSourceExists(id) } returns false
        every { style.styleLayerExists(id) } returns true

        // Act
        polylineManager.removePolyline(id)

        // Assert
        verify(exactly = 0) { style.removeStyleSource(id) }
        verify(exactly = 0) { style.removeStyleLayer(id) }
    }

    companion object {
        private const val DEFAULT_UUID = "00000000-0000-0000-0000-000000000000"
        private val DEFAULT_POINTS = listOf(
            OmhCoordinate(-20.0, 25.0),
            OmhCoordinate(-30.0, 20.0),
            OmhCoordinate(-30.0, 30.0),
        )
    }
}
