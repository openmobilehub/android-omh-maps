package com.openmobilehub.android.maps.plugin.azuremaps.presentation.maps.managers

import com.azure.android.maps.control.AzureMap
import com.azure.android.maps.control.data.Position
import com.azure.android.maps.control.options.CameraOptions
import com.azure.android.maps.control.options.Option
import com.openmobilehub.android.maps.core.presentation.models.OmhCoordinate
import com.openmobilehub.android.maps.plugin.azuremaps.presentation.utils.Constants
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.slot
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class CameraManagerTest {
    private val mapView = mockk<AzureMap>(relaxed = true)
    private val camera = mockk<CameraOptions>()
    private lateinit var cameraManager: CameraManager

    @Before
    fun setUp() {
        cameraManager = CameraManager(mapView)
    }

    @Test
    fun `getCameraPositionCoordinate returns camera center coordinates`() {
        // Arrange
        val latitude = 10.0
        val longitude = 15.0

        camera.apply {
            center = Position(longitude, latitude)
        }
        every { mapView.camera } returns camera

        // Act
        val result = cameraManager.getCameraPositionCoordinate()

        // Assert
        Assert.assertEquals(OmhCoordinate(latitude, longitude), result)
    }

    @Test
    fun `moveCamera sets camera center and zoom level decreased by 1`() {
        // Arrange
        val latitude = 10.0
        val longitude = 15.0
        val zoomLevel = 5.0f

        val centerSlot = slot<Option<Position>>()
        val zoomSlot = slot<Option<Double>>()
        every { mapView.setCamera(capture(centerSlot), capture(zoomSlot)) } just runs

        // Act
        cameraManager.moveCamera(OmhCoordinate(latitude, longitude), zoomLevel)

        val center = centerSlot.captured.value
        // Assert
        Assert.assertEquals(zoomLevel.toDouble() - 1, zoomSlot.captured.value, 0.0)
        Assert.assertEquals(latitude, center.latitude, 0.0)
        Assert.assertEquals(longitude, center.longitude, 0.0)
    }

    @Test
    fun `setZoomGesturesEnabled sets camera's minZoom and maxZoom to default levels if enabled`() {
        // Arrange
        val minZoomSlot = slot<Option<Double>>()
        val maxZoomSlot = slot<Option<Double>>()

        every { mapView.setCamera(capture(minZoomSlot), capture(maxZoomSlot)) } just runs

        // Act
        cameraManager.setZoomGesturesEnabled(true)

        // Assert
        Assert.assertEquals(Constants.DEFAULT_MIN_ZOOM, minZoomSlot.captured.value, 0.0)
        Assert.assertEquals(Constants.DEFAULT_MAX_ZOOM, maxZoomSlot.captured.value, 0.0)
    }

    @Test
    fun `setZoomGesturesEnabled sets camera's minZoom and maxZoom to current zoom level if disabled`() {
        // Arrange
        val currentZoomLevel = 5.0

        val minZoomSlot = slot<Option<Double>>()
        val maxZoomSlot = slot<Option<Double>>()

        every { mapView.setCamera(capture(minZoomSlot), capture(maxZoomSlot)) } just runs

        camera.apply {
            zoom = currentZoomLevel
        }
        every { mapView.camera } returns camera

        // Act
        cameraManager.setZoomGesturesEnabled(false)

        // Assert
        Assert.assertEquals(currentZoomLevel, minZoomSlot.captured.value, 0.0)
        Assert.assertEquals(currentZoomLevel, maxZoomSlot.captured.value, 0.0)
    }
}
