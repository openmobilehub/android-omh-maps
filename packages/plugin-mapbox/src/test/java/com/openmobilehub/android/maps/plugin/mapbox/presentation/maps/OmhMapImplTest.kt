package com.openmobilehub.android.maps.plugin.mapbox.presentation.maps

import android.content.Context
import android.graphics.Bitmap
import android.view.View
import com.mapbox.bindgen.Expected
import com.mapbox.common.Cancelable
import com.mapbox.geojson.Feature
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraChanged
import com.mapbox.maps.CameraChangedCallback
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapIdle
import com.mapbox.maps.MapIdleCallback
import com.mapbox.maps.MapLoaded
import com.mapbox.maps.MapLoadedCallback
import com.mapbox.maps.MapView
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.MapboxStyleManager
import com.mapbox.maps.QueriedRenderedFeature
import com.mapbox.maps.QueryRenderedFeaturesCallback
import com.mapbox.maps.ScreenCoordinate
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.sources.generated.GeoJsonSource
import com.mapbox.maps.extension.style.sources.getSource
import com.mapbox.maps.plugin.Plugin
import com.mapbox.maps.plugin.compass.CompassPlugin
import com.mapbox.maps.plugin.gestures.OnMapClickListener
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.plugin.scalebar.ScaleBarPlugin
import com.mapbox.maps.plugin.viewport.ViewportPlugin
import com.mapbox.maps.plugin.viewport.state.FollowPuckViewportState
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhMapLoadedCallback
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhOnCameraIdleListener
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhOnCameraMoveStartedListener
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhOnMyLocationButtonClickListener
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhOnPolylineClickListener
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhSnapshotReadyCallback
import com.openmobilehub.android.maps.core.presentation.models.OmhCoordinate
import com.openmobilehub.android.maps.core.presentation.models.OmhPolylineOptions
import com.openmobilehub.android.maps.core.utils.logging.Logger
import com.openmobilehub.android.maps.plugin.mapbox.utils.Constants
import com.openmobilehub.android.maps.plugin.mapbox.utils.JSONUtil
import com.openmobilehub.android.maps.plugin.mapbox.utils.uuid.UUIDGenerator
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.runs
import io.mockk.slot
import io.mockk.verify
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.util.UUID

class OmhMapImplTest {
    private lateinit var omhMapImpl: OmhMapImpl
    private val map = mockk<MapView>()
    private val scaleBarPlugin = mockk<ScaleBarPlugin>(relaxed = true)
    private val compassPlugin = mockk<CompassPlugin>(relaxed = true)
    private val viewportPlugin = mockk<ViewportPlugin>(relaxed = true)
    private val context = mockk<Context>(relaxed = true)
    private val myLocationIcon = mockk<MyLocationIcon>(relaxed = true)
    private val logger = mockk<Logger>(relaxed = true)
    private val uuidGenerator = mockk<UUIDGenerator>()

    private fun mockQueryRenderedFeatures(layerID: String) {
        val mockValue = mockk<Expected<String, List<QueriedRenderedFeature>>>(relaxed = true)
        every { mockValue.value?.get(0)?.layers?.get(0) } returns layerID

        val queryRenderedFeaturesCallbackSlot = slot<QueryRenderedFeaturesCallback>()
        every {
            map.mapboxMap.queryRenderedFeatures(
                any(),
                any(),
                capture(queryRenderedFeaturesCallbackSlot)
            )
        } answers {
            queryRenderedFeaturesCallbackSlot.captured.run(mockValue)
            mockk<Cancelable>()
        }
    }

    private fun mockPixelForCoordinate() {
        every { map.mapboxMap.pixelForCoordinate(any()) } returns mockk<ScreenCoordinate>()
    }

    @Before
    fun setUp() {
        every { map.getPlugin<ScaleBarPlugin>(Plugin.MAPBOX_SCALEBAR_PLUGIN_ID) } returns scaleBarPlugin
        every { map.getPlugin<CompassPlugin>(Plugin.MAPBOX_COMPASS_PLUGIN_ID) } returns compassPlugin
        every { map.getPlugin<ViewportPlugin>(Plugin.MAPBOX_VIEWPORT_PLUGIN_ID) } returns viewportPlugin
        every { map.mapboxMap } returns mockk<MapboxMap>(relaxed = true)
        every { uuidGenerator.generate() } returns UUID.fromString(DEFAULT_UUID)

        mockkObject(JSONUtil)
        // Required for style.getSource
        mockkStatic("com.mapbox.maps.extension.style.sources.SourceUtils")

        omhMapImpl = OmhMapImpl(map, context, myLocationIcon, logger, uuidGenerator)
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

    @Test
    fun `isMyLocationEnabled returns correct value`() {
        // Arrange
        val isMyLocationEnabled = true
        every { map.location.enabled } returns isMyLocationEnabled

        // Act
        val result = omhMapImpl.isMyLocationEnabled()

        // Assert
        Assert.assertEquals(isMyLocationEnabled, result)
    }

    @Test
    fun `setMyLocationEnabled enables location and adds myLocationIcon view when true is passed`() {
        // Arrange
        val isMyLocationEnabled = true
        every { map.location.enabled = any() } just runs
        every { map.addView(any()) } just runs

        // Act
        omhMapImpl.setMyLocationEnabled(isMyLocationEnabled)

        // Assert
        verify { map.location.enabled = isMyLocationEnabled }
        verify { map.addView(myLocationIcon) }
    }

    @Test
    fun `setMyLocationEnabled disables location and removes myLocationIcon view when false is passed`() {
        // Arrange
        val isMyLocationEnabled = false
        every { map.location.enabled = any() } just runs
        every { map.removeView(any()) } just runs

        // Act
        omhMapImpl.setMyLocationEnabled(isMyLocationEnabled)

        // Assert
        verify { map.location.enabled = isMyLocationEnabled }
        verify { map.removeView(myLocationIcon) }
    }

    @Test
    fun `setMyLocationButtonClickListener triggers callback and recenter map when button is clicked`() {
        // Arrange
        val slot = slot<View.OnClickListener>()
        val listener = mockk<OmhOnMyLocationButtonClickListener>(relaxed = true)

        val followPuckViewportState = mockk<FollowPuckViewportState>()
        every { viewportPlugin.makeFollowPuckViewportState(any()) } returns followPuckViewportState

        every { myLocationIcon.setOnClickListener(capture(slot)) } answers {}

        // Act
        omhMapImpl.setMyLocationButtonClickListener(listener)
        slot.captured.onClick(myLocationIcon)

        // Assert
        verify(exactly = 1) { listener.onMyLocationButtonClick() }
        verify(exactly = 1) { viewportPlugin.transitionTo(followPuckViewportState) }
    }

    @Test
    fun `setMapStyle applies custom style when JSON resource ID provided with no errors`() {
        // Arrange
        val jsonStyleResId = 1
        every {
            JSONUtil.convertJSONToString(
                any<Context>(),
                any<Int>(),
                any<Logger>()
            )
        } returns "{validJSON: 1}"

        val style = mockk<Style>(relaxed = true)
        every { style.isValid() } returns true

        val slot = slot<Style.OnStyleLoaded>()
        every { map.mapboxMap.loadStyle(any<String>(), capture(slot)) } answers {
            slot.captured.onStyleLoaded(style)
        }

        // Act
        omhMapImpl.setMapStyle(jsonStyleResId)

        // Assert
        verify(exactly = 0) { logger.logWarning(any()) }
        verify(exactly = 0) { logger.logError(any()) }
        verify { map.mapboxMap.loadStyle("{validJSON: 1}", any()) }
    }

    @Test
    fun `setMapStyle logs warning message when JSON was not parsed`() {
        // Arrange
        val jsonStyleResId = 1
        every {
            JSONUtil.convertJSONToString(
                any<Context>(),
                any<Int>(),
                any<Logger>()
            )
        } returns null

        // Act
        omhMapImpl.setMapStyle(jsonStyleResId)

        // Assert
        verify { logger.logError("Failed to load style from resource with id: $jsonStyleResId") }
    }

    @Test
    fun `setMapStyle logs warning message when style was not applied correctly`() {
        // Arrange
        val jsonStyleResId = 1
        every {
            JSONUtil.convertJSONToString(
                any<Context>(),
                any<Int>(),
                any<Logger>()
            )
        } returns "{validJSON: 0}"

        val style = mockk<Style>(relaxed = true)
        every { style.isValid() } returns false

        val slot = slot<Style.OnStyleLoaded>()
        every { map.mapboxMap.loadStyle(any<String>(), capture(slot)) } answers {
            slot.captured.onStyleLoaded(style)
        }

        // Act
        omhMapImpl.setMapStyle(jsonStyleResId)

        // Assert
        verify { logger.logWarning("Failed to apply custom map style. Check logs from Mapbox SDK.") }
    }

    @Test
    fun `setMapStyle sets default style when null JSON resource ID provided`() {
        // Arrange
        every { map.mapboxMap.loadStyle(any<String>()) } just runs

        // Act
        omhMapImpl.setMapStyle(null)

        // Assert
        verify { map.mapboxMap.loadStyle(Style.STANDARD) }
    }

    @Test
    fun `addPolyline adds polyline to map and returns OmhPolyline`() {
        // Arrange
        val omhPolylineOptions = OmhPolylineOptions()
        omhPolylineOptions.points = listOf(
            OmhCoordinate(0.0, 0.0),
            OmhCoordinate(1.0, 1.0)
        )

        // Act
        val result = omhMapImpl.addPolyline(omhPolylineOptions)

        // Assert
        Assert.assertNotNull(result)
    }

    @Test
    fun `click listener gets triggered when polyline is clicked`() {
        // Arrange
        val point = Point.fromLngLat(0.0, 0.0)
        val listener = mockk<OmhOnPolylineClickListener>(relaxed = true)

        val onMapClickListenerSlot = slot<OnMapClickListener>()
        every { map.gestures.addOnMapClickListener(capture(onMapClickListenerSlot)) } just runs

        mockQueryRenderedFeatures("polyline-$DEFAULT_UUID")
        mockPixelForCoordinate()

        // Act
        omhMapImpl.setOnPolylineClickListener(listener)
        val addedPolyline = omhMapImpl.addPolyline(OmhPolylineOptions().apply { clickable = true })
        onMapClickListenerSlot.captured.onMapClick(point)

        // Assert
        verify { listener.onPolylineClick(addedPolyline) }
    }

    @Test
    fun `click listener does not get triggered when polyline is not clickable`() {
        // Arrange
        val point = Point.fromLngLat(0.0, 0.0)
        val listener = mockk<OmhOnPolylineClickListener>(relaxed = true)

        val onMapClickListenerSlot = slot<OnMapClickListener>()
        every { map.gestures.addOnMapClickListener(capture(onMapClickListenerSlot)) } just runs

        mockQueryRenderedFeatures("polyline-$DEFAULT_UUID")
        mockPixelForCoordinate()

        // Act
        omhMapImpl.setOnPolylineClickListener(listener)
        val addedPolyline = omhMapImpl.addPolyline(OmhPolylineOptions().apply { clickable = false })
        onMapClickListenerSlot.captured.onMapClick(point)

        // Assert
        verify(exactly = 0) { listener.onPolylineClick(addedPolyline) }
    }

    @Test
    fun `updatePolylinePoints updates the polyline points on the map`() {
        // Given
        val sourceId = "sourceId"
        val points = listOf(OmhCoordinate(0.0, 0.0), OmhCoordinate(1.0, 1.0))
        val geoJsonSource = mockk<GeoJsonSource>(relaxed = true)

        every { any<MapboxStyleManager>().getSource(any()) } returns geoJsonSource

        // When
        omhMapImpl.updatePolylinePoints(sourceId, points)

        // Then
        verify { geoJsonSource.feature(any<Feature>()) }
    }

    companion object {
        private const val DEFAULT_UUID = "00000000-0000-0000-0000-000000000000"
    }
}
