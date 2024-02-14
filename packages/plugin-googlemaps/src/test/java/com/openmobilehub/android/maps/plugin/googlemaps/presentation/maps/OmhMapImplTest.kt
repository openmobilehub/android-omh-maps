package com.openmobilehub.android.maps.plugin.googlemaps.presentation.maps

import android.content.Context
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Polygon
import com.google.android.gms.maps.model.PolygonOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.openmobilehub.android.maps.core.presentation.models.OmhCoordinate
import com.openmobilehub.android.maps.core.presentation.models.OmhPolygonOptions
import com.openmobilehub.android.maps.core.presentation.models.OmhPolylineOptions
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.io.ByteArrayInputStream

class OmhMapImplTest {

    private lateinit var omhMapImpl: OmhMapImpl
    private val googleMap = mockk<GoogleMap>()
    private val context = mockk<Context>()

    @Before
    fun setUp() {
        omhMapImpl = OmhMapImpl(googleMap, context)
    }

    @Test
    fun `providerName returns correct string`() {
        // Act
        val providerName = omhMapImpl.providerName

        // Assert
        Assert.assertEquals("Google", providerName)
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
    fun `setMapStyle sets default style when null JSON resource ID provided`() {
        // Arrange
        every { googleMap.setMapStyle(any()) } returns true

        // Act
        omhMapImpl.setMapStyle(null)

        // Assert
        verify { googleMap.setMapStyle(null) }
    }
}
