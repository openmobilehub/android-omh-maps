package com.openmobilehub.android.maps.plugin.mapbox.presentation.maps.managers

import com.mapbox.geojson.Point
import com.mapbox.maps.MapView
import com.mapbox.maps.ScreenCoordinate
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhMarker
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhOnInfoWindowClickListener
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhOnMarkerClickListener
import com.openmobilehub.android.maps.core.presentation.models.OmhCoordinate
import com.openmobilehub.android.maps.core.presentation.models.OmhMarkerOptions
import com.openmobilehub.android.maps.core.utils.uuid.UUIDGenerator
import com.openmobilehub.android.maps.plugin.mapbox.presentation.interfaces.IOmhInfoWindowMapViewDelegate
import com.openmobilehub.android.maps.plugin.mapbox.presentation.maps.OmhMarkerImpl
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import java.util.UUID

class MapMarkerManagerTest {
    private val mapView = mockk<MapView>(relaxed = true)
    private val uuidGenerator = mockk<UUIDGenerator>()

    private lateinit var mapMarkerManager: MapMarkerManager

    @Before
    fun setUp() {
        mockkStatic("com.mapbox.maps.extension.style.sources.SourceUtils")
        mockkStatic("com.mapbox.maps.extension.style.layers.LayerUtils")

        every { uuidGenerator.generate() } returns UUID.fromString(DEFAULT_UUID)

        mapMarkerManager =
            MapMarkerManager(
                mapView.context,
                object : IOmhInfoWindowMapViewDelegate {
                    override fun getMapWidth(): Int {
                        return mapView.width
                    }

                    override fun getMapHeight(): Int {
                        return mapView.height
                    }

                    override fun coordinateForPixel(screenCoordinate: ScreenCoordinate): Point {
                        return mapView.mapboxMap.coordinateForPixel(screenCoordinate)
                    }

                    override fun pixelForCoordinate(point: Point): ScreenCoordinate {
                        return mapView.mapboxMap.pixelForCoordinate(point)
                    }
                }
            )
    }

    @Test
    fun `maybeHandleClick does nothing if OmhMarker Point with given id does not exist`() {
        // Arrange
        // We have empty markers map
        val clickHandler = mockk<OmhOnMarkerClickListener>(relaxed = true)
        mapMarkerManager.markerClickListener = clickHandler

        // Act
        mapMarkerManager.maybeHandleClick("id") { /* do nothing in this callback */ }

        // Assert
        verify(exactly = 0) { clickHandler.onMarkerClick(any<OmhMarker>()) }
    }

    @Test
    fun `maybeHandleClick does nothing if the added OmhMarker is not clickable`() {
        // Arrange
        val clickHandler = mockk<OmhOnMarkerClickListener>(relaxed = true)
        mapMarkerManager.markerClickListener = clickHandler

        val markerOptions = OmhMarkerOptions().apply {
            position = DEFAULT_POSITION
            clickable = false
        }

        // Act
        val marker = mapMarkerManager.addMarker(markerOptions, null)
        mapMarkerManager.maybeHandleClick(OmhMarkerImpl.getSymbolLayerID(marker.markerUUID)) {
            /* do nothing in this callback */
        }

        // Assert
        verify(exactly = 0) { clickHandler.onMarkerClick(any<OmhMarker>()) }
    }

    @Test
    fun `maybeHandleClick calls onMarkerClick if OmhMarker was clicked`() {
        // Arrange
        val clickHandler = mockk<OmhOnMarkerClickListener>(relaxed = true)
        mapMarkerManager.markerClickListener = clickHandler

        val markerOptions = OmhMarkerOptions().apply {
            position = DEFAULT_POSITION
            clickable = true
        }

        // Act
        val marker = mapMarkerManager.addMarker(markerOptions, null)
        mapMarkerManager.maybeHandleClick(OmhMarkerImpl.getSymbolLayerID(marker.markerUUID)) {
            /* do nothing in this callback */
        }

        // Assert
        verify(exactly = 1) { clickHandler.onMarkerClick(marker) }
    }

    @Test
    fun `maybeHandleClick does nothing if OmhInfoWindow Point with given id does not exist`() {
        // Arrange
        // We have empty markers map
        val clickHandler = mockk<OmhOnInfoWindowClickListener>(relaxed = true)
        mapMarkerManager.infoWindowClickListener = clickHandler

        // Act
        mapMarkerManager.maybeHandleClick("id") { /* do nothing in this callback */ }

        // Assert
        verify(exactly = 0) { clickHandler.onInfoWindowClick(any<OmhMarker>()) }
    }

    @Test
    fun `maybeHandleClick does nothing if the added OmhInfoWindow is not opened`() {
        // Arrange
        val clickHandler = mockk<OmhOnInfoWindowClickListener>(relaxed = true)
        mapMarkerManager.infoWindowClickListener = clickHandler

        val markerOptions = OmhMarkerOptions().apply {
            position = DEFAULT_POSITION
        }

        // Act
        val marker = mapMarkerManager.addMarker(markerOptions, null)
        mapMarkerManager.maybeHandleClick(marker.omhInfoWindow.getSymbolLayerID()) {
            /* do nothing in this callback */
        }

        // Assert
        verify(exactly = 0) { clickHandler.onInfoWindowClick(any<OmhMarker>()) }
    }

    @Test
    fun `maybeHandleClick does nothing if the added OmhInfoWindow's parent marker is not visible`() {
        // Arrange
        val clickHandler = mockk<OmhOnInfoWindowClickListener>(relaxed = true)
        mapMarkerManager.infoWindowClickListener = clickHandler

        val markerOptions = OmhMarkerOptions().apply {
            position = DEFAULT_POSITION
        }

        // Act
        val marker = mapMarkerManager.addMarker(markerOptions, null)
        marker.showInfoWindow()
        marker.setIsVisible(false)
        mapMarkerManager.maybeHandleClick(marker.omhInfoWindow.getSymbolLayerID()) {
            /* do nothing in this callback */
        }

        // Assert
        verify(exactly = 0) { clickHandler.onInfoWindowClick(any<OmhMarker>()) }
    }

    @Test
    fun `maybeHandleClick calls onInfoWindowClick if OmhInfoWindow is clicked`() {
        // Arrange
        val clickHandler = mockk<OmhOnInfoWindowClickListener>(relaxed = true)
        mapMarkerManager.infoWindowClickListener = clickHandler

        val markerOptions = OmhMarkerOptions().apply {
            position = DEFAULT_POSITION
        }

        // Act
        val marker = mapMarkerManager.addMarker(markerOptions, null)
        marker.showInfoWindow()
        mapMarkerManager.maybeHandleClick(marker.omhInfoWindow.getSymbolLayerID()) {
            /* do nothing in this callback */
        }

        // Assert
        verify(exactly = 1) { clickHandler.onInfoWindowClick(marker) }
    }

    companion object {
        private const val DEFAULT_UUID = "00000000-0000-0000-0000-000000000000"
        private val DEFAULT_POSITION = OmhCoordinate(-20.0, 25.0)
    }
}
