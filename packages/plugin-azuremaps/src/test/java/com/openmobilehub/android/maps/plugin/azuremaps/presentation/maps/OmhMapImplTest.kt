package com.openmobilehub.android.maps.plugin.azuremaps.presentation.maps

import android.content.Context
import com.azure.android.maps.control.AzureMap
import com.azure.android.maps.control.MapControl
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhMapLoadedCallback
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhOnCameraIdleListener
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhOnCameraMoveStartedListener
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhOnMyLocationButtonClickListener
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhSnapshotReadyCallback
import com.openmobilehub.android.maps.core.presentation.models.OmhCoordinate
import com.openmobilehub.android.maps.core.utils.logging.UnsupportedFeatureLogger
import com.openmobilehub.android.maps.plugin.azuremaps.presentation.maps.managers.CameraManager
import com.openmobilehub.android.maps.plugin.azuremaps.presentation.maps.managers.MyLocationManager
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class OmhMapImplTest {

    private val context = mockk<Context>(relaxed = true)
    private val mapControl = mockk<MapControl>(relaxed = true)
    private val mapView = mockk<AzureMap>(relaxed = true)
    private val cameraManager = mockk<CameraManager>(relaxed = true)
    private val myLocationManager = mockk<MyLocationManager>(relaxed = true)
    private val logger = mockk<UnsupportedFeatureLogger>(relaxed = true)

    private lateinit var omhMapImpl: OmhMapImpl

    @Before
    fun setUp() {
        omhMapImpl =
            OmhMapImpl(
                context,
                mapControl,
                mapView,
                cameraManager,
                myLocationManager,
                logger = logger,
                bRunningInTest = true
            )
    }

    @Test
    fun `getCameraPositionCoordinate calls cameraManager getCameraPositionCoordinate`() {
        // Arrange
        val latitude = 10.0
        val longitude = 15.0

        every { cameraManager.getCameraPositionCoordinate() } returns OmhCoordinate(
            latitude,
            longitude
        )

        // Act
        val result = omhMapImpl.getCameraPositionCoordinate()

        // Assert
        Assert.assertEquals(OmhCoordinate(latitude, longitude), result)
    }

    @Test
    fun `moveCamera calls cameraManager moveCamera`() {
        // Arrange
        val latitude = 10.0
        val longitude = 15.0
        val zoomLevel = 5.0f

        // Act
        omhMapImpl.moveCamera(OmhCoordinate(latitude, longitude), zoomLevel)

        // Assert
        verify { cameraManager.moveCamera(OmhCoordinate(latitude, longitude), zoomLevel) }
    }

    @Test
    fun `setZoomGesturesEnabled calls cameraManager setZoomGesturesEnabled`() {
        // Arrange
        val enableZoomGestures = true

        // Act
        omhMapImpl.setZoomGesturesEnabled(enableZoomGestures)

        // Assert
        verify { cameraManager.setZoomGesturesEnabled(enableZoomGestures) }
    }

    @Test
    fun `setRotateGesturesEnabled calls logger logSetterNotSupported`() {
        // Arrange
        val enableRotateGestures = true

        // Act
        omhMapImpl.setRotateGesturesEnabled(enableRotateGestures)

        // Assert
        verify { logger.logSetterNotSupported("rotateGestures") }
    }

    @Test
    fun `snapshot calls logger logNotSupported`() {
        // Arrange
        val callback = mockk<OmhSnapshotReadyCallback>(relaxed = true)

        // Act
        omhMapImpl.snapshot(callback)

        // Assert
        verify { logger.logNotSupported("snapshot") }
    }

    @Test
    fun `setOnCameraMoveStartedListener calls cameraManager setOnCameraMoveStartedListener`() {
        // Arrange
        val listener = mockk<OmhOnCameraMoveStartedListener>(relaxed = true)

        // Act
        omhMapImpl.setOnCameraMoveStartedListener(listener)

        // Assert
        verify { cameraManager.setOnCameraMoveStartedListener(listener) }
    }

    @Test
    fun `setOnCameraIdleListener calls cameraManager setOnCameraIdleListener`() {
        // Arrange
        val listener = mockk<OmhOnCameraIdleListener>(relaxed = true)

        // Act
        omhMapImpl.setOnCameraIdleListener(listener)

        // Assert
        verify { cameraManager.setOnCameraIdleListener(listener) }
    }

    @Test
    fun `setOnMapLoadedCallback calls cameraManager setOnMapLoadedCallback`() {
        // Arrange
        val callback = mockk<OmhMapLoadedCallback>(relaxed = true)

        // Act
        omhMapImpl.setOnMapLoadedCallback(callback)

        // Assert
        verify { cameraManager.setOnMapLoadedCallback(callback) }
    }

    @Test
    fun `setMyLocationEnabled calls myLocationManager setMyLocationEnabled`() {
        // Arrange
        val enable = true

        // Act
        omhMapImpl.setMyLocationEnabled(enable)

        // Assert
        verify { myLocationManager.setMyLocationEnabled(enable) }
    }

    @Test
    fun `isMyLocationEnabled calls myLocationManager isMyLocationEnabled`() {
        // Arrange
        every { myLocationManager.isMyLocationEnabled() } returns true

        // Act
        val result = omhMapImpl.isMyLocationEnabled()

        // Assert
        Assert.assertTrue(result)
    }

    @Test
    fun `setMyLocationButtonClickListener calls myLocationManager setMyLocationButtonClickListener`() {
        // Arrange
        val listener = mockk<OmhOnMyLocationButtonClickListener>(relaxed = true)

        // Act
        omhMapImpl.setMyLocationButtonClickListener(listener)

        // Assert
        verify { myLocationManager.setMyLocationButtonClickListener(listener) }
    }

    @Test
    fun `setMapStyle (json) calls logger logSetterNotSupported`() {
        // Arrange
        val mapStyle = 0

        // Act
        omhMapImpl.setMapStyle(mapStyle)

        // Assert
        verify { logger.logSetterNotSupported("mapStyle") }
    }

    @Test
    fun `setMapStyle (jsonString) calls logger logSetterNotSupported`() {
        // Arrange
        val mapStyle = ""

        // Act
        omhMapImpl.setMapStyle(mapStyle)

        // Assert
        verify { logger.logSetterNotSupported("mapStyle") }
    }

    @Test
    fun `setScaleFactor updates polylineManager and polygonManager scaleFactor`() {
        // Arrange
        val scaleFactor = 2.0f

        // Act
        omhMapImpl.setScaleFactor(scaleFactor)

        // Assert
        Assert.assertEquals(scaleFactor, omhMapImpl.polygonManager.scaleFactor)
        Assert.assertEquals(scaleFactor, omhMapImpl.polylineManager.scaleFactor)
    }
}
