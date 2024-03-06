package com.openmobilehub.android.maps.plugin.googlemaps.presentation.maps

import android.content.Context
import android.view.View
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polygon
import com.google.android.gms.maps.model.PolygonOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhInfoWindowViewFactory
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhMarker
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhOnInfoWindowClickListener
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhOnInfoWindowLongClickListener
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhOnInfoWindowOpenStatusChangeListener
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhOnMarkerClickListener
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhOnMarkerDragListener
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhOnPolygonClickListener
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhOnPolylineClickListener
import com.openmobilehub.android.maps.core.presentation.models.OmhCoordinate
import com.openmobilehub.android.maps.core.presentation.models.OmhMarkerOptions
import com.openmobilehub.android.maps.core.presentation.models.OmhPolygonOptions
import com.openmobilehub.android.maps.core.presentation.models.OmhPolylineOptions
import com.openmobilehub.android.maps.core.utils.logging.Logger
import com.openmobilehub.android.maps.core.utils.logging.UnsupportedFeatureLogger
import com.openmobilehub.android.maps.plugin.googlemaps.utils.Constants
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Before
import org.junit.Test
import java.io.ByteArrayInputStream

class OmhMapImplTest {

    private lateinit var omhMapImpl: OmhMapImpl
    private val context = mockk<Context>()
    private val googleMap = mockk<GoogleMap>(relaxed = true)
    private val mockedMarker = mockk<Marker>(relaxed = true)

    private val omhOnMarkerClickListener = mockk<OmhOnMarkerClickListener>(relaxed = true)
    private val omhOnMarkerDragListener = mockk<OmhOnMarkerDragListener>(relaxed = true)
    private val capturedInfoWindowAdapter = slot<GoogleMap.InfoWindowAdapter>()
    private val omhOnInfoWindowOpenStatusChangeListener =
        mockk<OmhOnInfoWindowOpenStatusChangeListener>(relaxed = true)
    private val omhOnInfoWindowClickListener = mockk<OmhOnInfoWindowClickListener>(relaxed = true)
    private val omhOnInfoWindowLongClickListener =
        mockk<OmhOnInfoWindowLongClickListener>(relaxed = true)
    private val omhOnPolylineClickListener = mockk<OmhOnPolylineClickListener>(relaxed = true)
    private val omhOnPolygonClickListener = mockk<OmhOnPolygonClickListener>(relaxed = true)

    private val logger = mockk<Logger>(relaxed = true)
    private val markerUnsupportedFeatureLogger = mockk<UnsupportedFeatureLogger>(relaxed = true)

    @Before
    fun setUp() {
        every { googleMap.addMarker(any<MarkerOptions>()) } returns mockedMarker
        every { googleMap.setOnMarkerClickListener(any()) } returns Unit
        every { googleMap.setOnMarkerDragListener(any()) } returns Unit
        every { googleMap.setInfoWindowAdapter(any()) } returns Unit
        every { googleMap.setInfoWindowAdapter(capture(capturedInfoWindowAdapter)) } answers {}
        omhMapImpl = OmhMapImpl(googleMap, context, logger, markerUnsupportedFeatureLogger)
    }

    @Test
    fun `providerName returns correct string`() {
        // Act
        val providerName = omhMapImpl.providerName

        // Assert
        assertEquals(Constants.PROVIDER_NAME, providerName)
    }

    @Test
    fun `addPolyline adds polyline to map and returns OmhPolyline`() {
        // Arrange
        val omhPolylineOptions = OmhPolylineOptions()
        omhPolylineOptions.points = listOf(
            OmhCoordinate(0.0, 0.0),
            OmhCoordinate(1.0, 1.0)
        )

        val polyline = mockk<Polyline>(relaxed = true)

        every { googleMap.addPolyline(any<PolylineOptions>()) } returns polyline

        // Act
        val result = omhMapImpl.addPolyline(omhPolylineOptions)

        // Assert
        verify { googleMap.addPolyline(any<PolylineOptions>()) }
        Assert.assertNotNull(result)
    }

    @Test
    fun `addPolygon adds polyline to map and returns OmhPolyline`() {
        // Arrange
        val omhPolygonOptions = OmhPolygonOptions()
        omhPolygonOptions.outline = listOf(
            OmhCoordinate(0.0, 0.0),
            OmhCoordinate(1.0, 1.0)
        )

        val polygon = mockk<Polygon>(relaxed = true)

        every { googleMap.addPolygon(any<PolygonOptions>()) } returns polygon

        // Act
        val result = omhMapImpl.addPolygon(omhPolygonOptions)

        // Assert
        verify { googleMap.addPolygon(any<PolygonOptions>()) }
        Assert.assertNotNull(result)
    }

    @Test
    fun `addMarker adds marker to map and returns OmhMarker`() {
        // Arrange
        val omhMarkerOptions = OmhMarkerOptions()

        // Act
        val result = omhMapImpl.addMarker(omhMarkerOptions)

        // Assert
        verify { googleMap.addMarker(any<MarkerOptions>()) }
        Assert.assertNotNull(result)
    }

    @Test
    fun `setMapStyle applies custom style when JSON resource ID provided`() {
        // Arrange
        val jsonStyleResId = 1

        every { googleMap.setMapStyle(any()) } returns true

        val inputStream = ByteArrayInputStream("{}".toByteArray())
        every { context.resources.openRawResource(jsonStyleResId) } returns inputStream

        // Act
        omhMapImpl.setMapStyle(jsonStyleResId)

        // Assert
        verify { googleMap.setMapStyle(any<MapStyleOptions>()) }
    }

    @Test
    fun `setMapStyle logs warning message when style is not applied`() {
        // Arrange
        val jsonStyleResId = 1

        every { googleMap.setMapStyle(any()) } returns false

        val inputStream = ByteArrayInputStream("{}".toByteArray())
        every { context.resources.openRawResource(jsonStyleResId) } returns inputStream

        // Act
        omhMapImpl.setMapStyle(jsonStyleResId)

        // Assert
        verify { logger.logWarning("Failed to apply custom map style. Check logs from Google Maps SDK.") }
    }

    @Test
    fun `setMapStyle sets default style when null JSON resource ID provided`() {
        // Arrange
        every { googleMap.setMapStyle(any()) } returns true

        // Act
        omhMapImpl.setMapStyle(null)

        // Assert
        verify { googleMap.setMapStyle(null) }
    }

    @Test
    fun `setOnMarkerClickListener triggers listener on marker click`() {
        // Arrange
        omhMapImpl.addMarker(
            OmhMarkerOptions().apply {
                clickable = true
                draggable = true
            }
        )
        val capturedListener = slot<GoogleMap.OnMarkerClickListener>()
        every { googleMap.setOnMarkerClickListener(capture(capturedListener)) } answers {}

        // Act
        omhMapImpl.setOnMarkerClickListener(omhOnMarkerClickListener)
        capturedListener.captured.onMarkerClick(mockedMarker)

        // Assert
        verify { omhOnMarkerClickListener.onMarkerClick(any<OmhMarker>()) }
    }

    @Test
    fun `setOnMarkerDragListener triggers listener on marker drag events`() {
        // Arrange
        omhMapImpl.addMarker(
            OmhMarkerOptions().apply {
                clickable = true
                draggable = true
            }
        )
        val capturedListener = slot<GoogleMap.OnMarkerDragListener>()
        every { googleMap.setOnMarkerDragListener(capture(capturedListener)) } answers {}

        // Act
        omhMapImpl.setOnMarkerDragListener(omhOnMarkerDragListener)
        capturedListener.captured.onMarkerDragStart(mockedMarker)
        capturedListener.captured.onMarkerDrag(mockedMarker)
        capturedListener.captured.onMarkerDragEnd(mockedMarker)

        // Assert
        verify {
            omhOnMarkerDragListener.onMarkerDragStart(any<OmhMarker>())
            omhOnMarkerDragListener.onMarkerDrag(any<OmhMarker>())
            omhOnMarkerDragListener.onMarkerDragEnd(any<OmhMarker>())
        }
    }

    @Test
    fun `setInfoWindowAdapter sets a custom InfoWindowAdapter that is handled properly`() {
        // Arrange
        omhMapImpl.addMarker(OmhMarkerOptions())

        val mockedIWView = mockk<View>()
        val mockedIWContentsView = mockk<View>()

        // Act
        omhMapImpl.setCustomInfoWindowViewFactory(object : OmhInfoWindowViewFactory {
            override fun createInfoWindowView(marker: OmhMarker): View {
                return mockedIWView
            }
        })
        omhMapImpl.setCustomInfoWindowContentsViewFactory(object : OmhInfoWindowViewFactory {
            override fun createInfoWindowView(marker: OmhMarker): View {
                return mockedIWContentsView
            }
        })

        val getInfoWindowRet = capturedInfoWindowAdapter.captured.getInfoWindow(mockedMarker)
        val getInfoContentsRet = capturedInfoWindowAdapter.captured.getInfoContents(mockedMarker)

        // Assert
        assertSame(mockedIWView, getInfoWindowRet)
        assertSame(mockedIWContentsView, getInfoContentsRet)
    }

    @Test
    fun `setOnInfoWindowOpenStatusChangeListener sets a listener invoked on InfoWindow close`() {
        // Arrange
        omhMapImpl.addMarker(OmhMarkerOptions())
        val capturedListener = slot<GoogleMap.OnInfoWindowCloseListener>()
        every { googleMap.setOnInfoWindowCloseListener(capture(capturedListener)) } answers {}

        // Act
        omhMapImpl.setOnInfoWindowOpenStatusChangeListener(omhOnInfoWindowOpenStatusChangeListener)
        capturedListener.captured.onInfoWindowClose(mockedMarker) // only close listener is supported by GoogleMaps

        // Assert
        verify {
            markerUnsupportedFeatureLogger.logFeatureSetterPartiallySupported(
                "onInfoWindowOpenStatusChangeListener",
                "only the onInfoWindowClose event is supported"
            )
        }
        verify {
            omhOnInfoWindowOpenStatusChangeListener.onInfoWindowClose(any<OmhMarker>())
        }
    }

    @Test
    fun `setOnInfoWindowClickListener sets a listener invoked on InfoWindow click`() {
        // Arrange
        omhMapImpl.addMarker(OmhMarkerOptions())
        val capturedListener = slot<GoogleMap.OnInfoWindowClickListener>()
        every { googleMap.setOnInfoWindowClickListener(capture(capturedListener)) } answers {}

        // Act
        omhMapImpl.setOnInfoWindowClickListener(omhOnInfoWindowClickListener)
        capturedListener.captured.onInfoWindowClick(mockedMarker)

        // Assert
        verify {
            omhOnInfoWindowClickListener.onInfoWindowClick(any<OmhMarker>())
        }
    }

    @Test
    fun `setOnInfoWindowLongClickListener sets a listener invoked on InfoWindow click`() {
        // Arrange
        omhMapImpl.addMarker(OmhMarkerOptions())
        val capturedListener = slot<GoogleMap.OnInfoWindowLongClickListener>()
        every { googleMap.setOnInfoWindowLongClickListener(capture(capturedListener)) } answers {}

        // Act
        omhMapImpl.setOnInfoWindowLongClickListener(omhOnInfoWindowLongClickListener)
        capturedListener.captured.onInfoWindowLongClick(mockedMarker)

        // Assert
        verify {
            omhOnInfoWindowLongClickListener.onInfoWindowLongClick(any<OmhMarker>())
        }
    }

    @Test
    fun `setOnPolylineClickListener triggers listener on polyline click`() {
        // Arrange
        val capturedListener = slot<GoogleMap.OnPolylineClickListener>()
        every { googleMap.setOnPolylineClickListener(capture(capturedListener)) } answers {}

        val polyline = mockk<Polyline>(relaxed = true)
        every { googleMap.addPolyline(any<PolylineOptions>()) } returns polyline

        // Act
        omhMapImpl.setOnPolylineClickListener(omhOnPolylineClickListener)
        val omhPolyline = omhMapImpl.addPolyline(OmhPolylineOptions().apply { clickable = true })
        capturedListener.captured.onPolylineClick(polyline)

        // Assert
        verify { omhOnPolylineClickListener.onPolylineClick(omhPolyline) }
    }

    @Test
    fun `setOnPolygonClickListener triggers listener on polygon click`() {
        // Arrange
        val capturedListener = slot<GoogleMap.OnPolygonClickListener>()
        every { googleMap.setOnPolygonClickListener(capture(capturedListener)) } answers {}

        val polygon = mockk<Polygon>(relaxed = true)
        every { googleMap.addPolygon(any<PolygonOptions>()) } returns polygon
        // Act
        omhMapImpl.setOnPolygonClickListener(omhOnPolygonClickListener)
        val omhPolygon = omhMapImpl.addPolygon(OmhPolygonOptions().apply { clickable = true })
        capturedListener.captured.onPolygonClick(polygon)

        // Assert
        verify { omhOnPolygonClickListener.onPolygonClick(omhPolygon) }
    }
}
