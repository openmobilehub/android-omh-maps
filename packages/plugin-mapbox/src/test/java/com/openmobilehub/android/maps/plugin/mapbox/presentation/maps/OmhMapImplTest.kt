package com.openmobilehub.android.maps.plugin.mapbox.presentation.maps

import android.content.Context
import android.graphics.Bitmap
import com.mapbox.common.Cancelable
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraChanged
import com.mapbox.maps.CameraChangedCallback
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapIdle
import com.mapbox.maps.MapIdleCallback
import com.mapbox.maps.MapLoaded
import com.mapbox.maps.MapLoadedCallback
import com.mapbox.maps.MapView
import com.mapbox.maps.plugin.Plugin
import com.mapbox.maps.plugin.compass.CompassPlugin
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.scalebar.ScaleBarPlugin
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhMapLoadedCallback
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhOnCameraIdleListener
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhOnCameraMoveStartedListener
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhSnapshotReadyCallback
import com.openmobilehub.android.maps.core.presentation.models.OmhCoordinate
import com.openmobilehub.android.maps.plugin.mapbox.utils.Constants
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.slot
import io.mockk.verify
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class OmhMapImplTest {

    private lateinit var omhMapImpl: OmhMapImpl
    private val map = mockk<MapView>()
    private val scaleBarPlugin = mockk<ScaleBarPlugin>(relaxed = true)
    private val compassPlugin = mockk<CompassPlugin>(relaxed = true)
    private val context = mockk<Context>(relaxed = true)

    @Before
    fun setUp() {
        every { map.getPlugin<ScaleBarPlugin>(Plugin.MAPBOX_SCALEBAR_PLUGIN_ID) } returns scaleBarPlugin
        every { map.getPlugin<CompassPlugin>(Plugin.MAPBOX_COMPASS_PLUGIN_ID) } returns compassPlugin

        omhMapImpl = OmhMapImpl(map, context)
    }

    @Test
    fun `providerName returns correct string`() {
        // Act
        val providerName = omhMapImpl.providerName

        // Assert
        Assert.assertEquals(Constants.PROVIDER_NAME, providerName)
    }

    @Test
    fun `getCameraPositionCoordinate returns correct coordinate`() {
        // Arrange
        val latitude = 10.0
        val longitude = 15.0

        every { map.mapboxMap.cameraState.center.latitude() } returns latitude
        every { map.mapboxMap.cameraState.center.longitude() } returns longitude

        // Act
        val result = omhMapImpl.getCameraPositionCoordinate()

        // Assert
        Assert.assertEquals(OmhCoordinate(latitude, longitude), result)
    }

    @Test
    fun `moveCamera updates camera position with given coordinate and zoom level`() {
        // Arrange
        val coordinate = OmhCoordinate(10.0, 15.0)
        val zoomLevel = 5.0f

        val expectedCameraSettings = CameraOptions.Builder()
            .zoom(zoomLevel.toDouble())
            .center(Point.fromLngLat(coordinate.longitude, coordinate.latitude))
            .build()

        every { map.mapboxMap.setCamera(any<CameraOptions>()) } just runs

        // Act
        omhMapImpl.moveCamera(coordinate, zoomLevel)

        // Assert
        verify { map.mapboxMap.setCamera(expectedCameraSettings) }
    }

    @Test
    fun `setZoomGesturesEnabled enables pinchToZoom and doubleTapToZoomIn when true is passed`() {
        // Arrange
        val zoomGesturesEnabled = true

        every { map.gestures.pinchToZoomEnabled = any() } just runs
        every { map.gestures.doubleTapToZoomInEnabled = any() } just runs

        // Act
        omhMapImpl.setZoomGesturesEnabled(zoomGesturesEnabled)

        // Assert
        verify {
            map.gestures.pinchToZoomEnabled = zoomGesturesEnabled
            map.gestures.doubleTapToZoomInEnabled = zoomGesturesEnabled
        }
    }

    @Test
    fun `setRotateGesturesEnabled enables rotation when true is passed`() {
        // Arrange
        val rotateGestureEnabled = true

        every { map.gestures.rotateEnabled = any() } just runs

        // Act
        omhMapImpl.setRotateGesturesEnabled(rotateGestureEnabled)

        // Assert
        verify {
            map.gestures.rotateEnabled = rotateGestureEnabled
        }
    }

    @Test
    fun `snapshot triggers onSnapshotReady with the correct bitmap`() {
        // Arrange
        val omhSnapshotReadyCallback = mockk<OmhSnapshotReadyCallback>(relaxed = true)

        val bitmap = mockk<Bitmap>()
        val slot = slot<MapView.OnSnapshotReady>()

        every { map.snapshot(capture(slot)) } answers {}

        // Act
        omhMapImpl.snapshot(omhSnapshotReadyCallback)

        slot.captured.onSnapshotReady(bitmap)

        // Assert
        verify { omhSnapshotReadyCallback.onSnapshotReady(bitmap) }
    }

    @Test
    fun `setOnCameraMoveStartedListener triggers callback when camera changes`() {
        // Arrange
        val slot = slot<CameraChangedCallback>()
        val cameraChanged = mockk<CameraChanged>(relaxed = true)
        val listener = mockk<OmhOnCameraMoveStartedListener>(relaxed = true)

        every { map.mapboxMap.subscribeCameraChanged(capture(slot)) } answers {
            mockk<Cancelable>()
        }

        // Act
        omhMapImpl.setOnCameraMoveStartedListener(listener)

        slot.captured.run(cameraChanged)

        // Assert
        verify { listener.onCameraMoveStarted(null) }
    }

    @Test
    fun `setOnCameraMoveStartedListener triggers only 1 callback when camera changes multiple times`() {
        // Arrange
        val slot = slot<CameraChangedCallback>()
        val cameraChanged = mockk<CameraChanged>(relaxed = true)
        val listener = mockk<OmhOnCameraMoveStartedListener>(relaxed = true)

        every { map.mapboxMap.subscribeCameraChanged(capture(slot)) } answers {
            mockk<Cancelable>()
        }

        // Act
        omhMapImpl.setOnCameraMoveStartedListener(listener)

        slot.captured.run(cameraChanged)
        slot.captured.run(cameraChanged)
        slot.captured.run(cameraChanged)

        // Assert
        verify(exactly = 1) { listener.onCameraMoveStarted(null) }
    }

    @Test
    fun `setOnCameraMoveStartedListener triggers callback again after camera gets idle`() {
        // Arrange
        val cameraChangedCallbackSlot = slot<CameraChangedCallback>()
        val cameraChanged = mockk<CameraChanged>(relaxed = true)
        val moveStartedListener = mockk<OmhOnCameraMoveStartedListener>(relaxed = true)

        val mapIdleCallbackSlot = slot<MapIdleCallback>()
        val mapIdle = mockk<MapIdle>(relaxed = true)
        val idleListener = mockk<OmhOnCameraIdleListener>(relaxed = true)

        every { map.mapboxMap.subscribeCameraChanged(capture(cameraChangedCallbackSlot)) } answers {
            mockk<Cancelable>()
        }

        every { map.mapboxMap.subscribeMapIdle(capture(mapIdleCallbackSlot)) } answers {
            mockk<Cancelable>()
        }

        // Act
        omhMapImpl.setOnCameraMoveStartedListener(moveStartedListener)
        omhMapImpl.setOnCameraIdleListener(idleListener)

        cameraChangedCallbackSlot.captured.run(cameraChanged)
        // Assert
        verify(exactly = 1) { moveStartedListener.onCameraMoveStarted(null) }

        // Act
        mapIdleCallbackSlot.captured.run(mapIdle)
        cameraChangedCallbackSlot.captured.run(cameraChanged)

        // Assert
        verify(exactly = 1) { idleListener.onCameraIdle() }
        verify(exactly = 2) { moveStartedListener.onCameraMoveStarted(null) }
    }

    @Test
    fun `setOnCameraIdleListener triggers callback when camera is idle`() {
        // Arrange
        val slot = slot<MapIdleCallback>()
        val mapIdle = mockk<MapIdle>(relaxed = true)
        val listener = mockk<OmhOnCameraIdleListener>(relaxed = true)

        every { map.mapboxMap.subscribeMapIdle(capture(slot)) } answers {
            mockk<Cancelable>()
        }

        // Act
        omhMapImpl.setOnCameraIdleListener(listener)

        slot.captured.run(mapIdle)

        // Assert
        verify { listener.onCameraIdle() }
    }

    @Test
    fun `setOnMapLoadedCallback triggers callback when map is loaded`() {
        // Arrange
        val slot = slot<MapLoadedCallback>()
        val mapLoaded = mockk<MapLoaded>(relaxed = true)
        val listener = mockk<OmhMapLoadedCallback>(relaxed = true)

        every { map.mapboxMap.subscribeMapLoaded(capture(slot)) } answers {
            mockk<Cancelable>()
        }

        // Act
        omhMapImpl.setOnMapLoadedCallback(listener)

        slot.captured.run(mapLoaded)

        // Assert
        verify { listener.onMapLoaded() }
    }
}
