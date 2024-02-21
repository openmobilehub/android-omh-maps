package com.openmobilehub.android.maps.plugin.googlemaps.presentation.maps

import android.content.Context
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polygon
import com.google.android.gms.maps.model.PolygonOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhMarker
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhOnMarkerClickListener
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhOnMarkerDragListener
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhOnPolygonClickListener
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhOnPolylineClickListener
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhPolygon
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhPolyline
import com.openmobilehub.android.maps.core.presentation.models.OmhCoordinate
import com.openmobilehub.android.maps.core.presentation.models.OmhMarkerOptions
import com.openmobilehub.android.maps.core.presentation.models.OmhPolygonOptions
import com.openmobilehub.android.maps.core.presentation.models.OmhPolylineOptions
import com.openmobilehub.android.maps.core.utils.logging.Logger
import com.openmobilehub.android.maps.plugin.googlemaps.utils.Constants
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.io.ByteArrayInputStream

class OmhMapImplTest {

    private lateinit var omhMapImpl: OmhMapImpl
    private val googleMap = mockk<GoogleMap>()
    private val context = mockk<Context>()
    private val omhOnMarkerClickListener = mockk<OmhOnMarkerClickListener>(relaxed = true)
    private val setOnMarkerDragListener = mockk<OmhOnMarkerDragListener>(relaxed = true)
    private val omhOnPolylineClickListener = mockk<OmhOnPolylineClickListener>(relaxed = true)
    private val omhOnPolygonClickListener = mockk<OmhOnPolygonClickListener>(relaxed = true)

    private val logger = mockk<Logger>(relaxed = true)

    @Before
    fun setUp() {
        omhMapImpl = OmhMapImpl(googleMap, context, logger)
    }

    @Test
    fun `providerName returns correct string`() {
        // Act
        val providerName = omhMapImpl.providerName

        // Assert
        Assert.assertEquals(Constants.PROVIDER_NAME, providerName)
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

        val marker = mockk<Marker>(relaxed = true)

        every { googleMap.addMarker(any<MarkerOptions>()) } returns marker

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
        // Assert
        val capturedListener = slot<GoogleMap.OnMarkerClickListener>()
        every { googleMap.setOnMarkerClickListener(capture(capturedListener)) } answers {}
        val marker = mockk<Marker>(relaxed = true)

        // Act
        omhMapImpl.setOnMarkerClickListener(omhOnMarkerClickListener)
        capturedListener.captured.onMarkerClick(marker)

        // Assert
        verify { omhOnMarkerClickListener.onMarkerClick(any<OmhMarker>()) }
    }

    @Test
    fun `setOnMarkerDragListener triggers listener on marker drag events`() {
        // Assert
        val capturedListener = slot<GoogleMap.OnMarkerDragListener>()
        every { googleMap.setOnMarkerDragListener(capture(capturedListener)) } answers {}
        val marker = mockk<Marker>(relaxed = true)

        // Act
        omhMapImpl.setOnMarkerDragListener(setOnMarkerDragListener)
        capturedListener.captured.onMarkerDragStart(marker)
        capturedListener.captured.onMarkerDrag(marker)
        capturedListener.captured.onMarkerDragEnd(marker)

        // Assert
        verify {
            setOnMarkerDragListener.onMarkerDragStart(any<OmhMarker>())
            setOnMarkerDragListener.onMarkerDrag(any<OmhMarker>())
            setOnMarkerDragListener.onMarkerDragEnd(any<OmhMarker>())
        }
    }

    @Test
    fun `setOnPolylineClickListener triggers listener on polyline click`() {
        // Assert
        val capturedListener = slot<GoogleMap.OnPolylineClickListener>()
        every { googleMap.setOnPolylineClickListener(capture(capturedListener)) } answers {}
        val polyline = mockk<Polyline>(relaxed = true)

        // Act
        omhMapImpl.setOnPolylineClickListener(omhOnPolylineClickListener)
        capturedListener.captured.onPolylineClick(polyline)

        // Assert
        verify { omhOnPolylineClickListener.onPolylineClick(any<OmhPolyline>()) }
    }

    @Test
    fun `setOnPolygonClickListener triggers listener on polygon click`() {
        // Assert
        val capturedListener = slot<GoogleMap.OnPolygonClickListener>()
        every { googleMap.setOnPolygonClickListener(capture(capturedListener)) } answers {}
        val polygon = mockk<Polygon>(relaxed = true)

        // Act
        omhMapImpl.setOnPolygonClickListener(omhOnPolygonClickListener)
        capturedListener.captured.onPolygonClick(polygon)

        // Assert
        verify { omhOnPolygonClickListener.onPolygonClick(any<OmhPolygon>()) }
    }
}
