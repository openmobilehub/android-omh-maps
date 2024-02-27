package com.openmobilehub.android.maps.plugin.mapbox.presentation.location

import android.content.Context
import com.mapbox.bindgen.Expected
import com.mapbox.common.Cancelable
import com.mapbox.common.location.DeviceLocationProvider
import com.mapbox.common.location.GetLocationCallback
import com.mapbox.common.location.Location
import com.mapbox.common.location.LocationError
import com.mapbox.common.location.LocationObserver
import com.mapbox.common.location.LocationProviderRequest
import com.mapbox.common.location.LocationService
import com.mapbox.common.location.LocationServiceFactory
import com.openmobilehub.android.maps.core.presentation.interfaces.location.OmhFailureListener
import com.openmobilehub.android.maps.core.presentation.interfaces.location.OmhSuccessListener
import com.openmobilehub.android.maps.core.presentation.models.OmhCoordinate
import com.openmobilehub.android.maps.core.presentation.models.OmhMapException
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.runs
import io.mockk.slot
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class OmhLocationImplTest {

    private val context = mockk<Context>()
    private val locationService = mockk<LocationService>()
    private val deviceLocationProviderExpected =
        mockk<Expected<LocationError, DeviceLocationProvider>>()

    private val onSuccessListener = mockk<OmhSuccessListener>(relaxed = true)
    private val onFailureListener = mockk<OmhFailureListener>(relaxed = true)

    @Before
    fun setup() {
        mockkStatic(LocationServiceFactory::class)
        every { LocationServiceFactory.getOrCreate() } returns locationService

        every {
            locationService.getDeviceLocationProvider(any<LocationProviderRequest>())
        } returns deviceLocationProviderExpected
    }

    private fun mockInvalidProvider() {
        every { deviceLocationProviderExpected.isValue } returns false
    }

    private fun mockValidProvider(currentLocation: Location?, lastLocation: Location?) {
        val locationProvider = mockk<DeviceLocationProvider>()

        // Last location
        val getLocationCallbackSlot = slot<GetLocationCallback>()
        every { locationProvider.getLastLocation(capture(getLocationCallbackSlot)) } answers {
            getLocationCallbackSlot.captured.run(lastLocation)
            mockk<Cancelable>()
        }

        // Current location
        val locationObserverSlot = slot<LocationObserver>()
        every { locationProvider.addLocationObserver(capture(locationObserverSlot)) } answers {
            locationObserverSlot.captured.onLocationUpdateReceived(mutableListOf(currentLocation).filterNotNull())
        }
        every { locationProvider.removeLocationObserver(any()) } just runs

        every { deviceLocationProviderExpected.isValue } returns true
        every { deviceLocationProviderExpected.value } returns locationProvider
    }

    @Test
    fun `getCurrentLocation calls onFailure with the ApiException when device location provider is invalid`() {
        // Arrange
        mockInvalidProvider()

        // Act
        val location = OmhLocationImpl(context)
        location.getCurrentLocation(onSuccessListener, onFailureListener)

        // Assert
        verify {
            onFailureListener.onFailure(any<OmhMapException.ApiException>())
        }
    }

    @Test
    fun `getCurrentLocation calls onFailure with NullLocation exception when provider returns no location`() {
        // Arrange
        mockValidProvider(null, null)

        // Act
        val location = OmhLocationImpl(context)
        location.getCurrentLocation(onSuccessListener, onFailureListener)

        // Assert
        verify { onFailureListener.onFailure(any<OmhMapException.NullLocation>()) }
    }

    @Test
    fun `getCurrentLocation calls onFailure with the NullLocation exception when provider returns no location`() {
        // Arrange
        mockValidProvider(null, null)

        // Act
        val location = OmhLocationImpl(context)
        location.getCurrentLocation(onSuccessListener, onFailureListener)

        // Assert
        verify {
            onFailureListener.onFailure(any<OmhMapException.NullLocation>())
        }
    }

    @Test
    fun `getCurrentLocation calls onSuccess when provider returns locations`() {
        // Arrange
        val latitude = 10.0
        val longitude = 15.0
        mockValidProvider(Location.Builder().latitude(latitude).longitude(longitude).build(), null)

        // Act
        val location = OmhLocationImpl(context)
        location.getCurrentLocation(onSuccessListener, onFailureListener)

        // Assert
        verify {
            onSuccessListener.onSuccess(OmhCoordinate(10.0, 15.0))
        }
    }

    @Test
    fun `getLastLocation calls onFailure with the ApiException when device location provider is invalid`() {
        // Arrange
        mockInvalidProvider()

        // Act
        val location = OmhLocationImpl(context)
        location.getLastLocation(onSuccessListener, onFailureListener)

        // Assert
        verify {
            onFailureListener.onFailure(any<OmhMapException.ApiException>())
        }
    }

    @Test
    fun `getLastLocation calls onFailure with the NullLocation exception when provider returns no location`() {
        // Arrange
        mockValidProvider(null, null)

        // Act
        val location = OmhLocationImpl(context)
        location.getLastLocation(onSuccessListener, onFailureListener)

        // Assert
        verify {
            onFailureListener.onFailure(any<OmhMapException.NullLocation>())
        }
    }

    @Test
    fun `getLastLocation calls onSuccess when provider returns location`() {
        // Arrange
        val latitude = 10.0
        val longitude = 15.0
        mockValidProvider(null, Location.Builder().latitude(latitude).longitude(longitude).build())

        // Act
        val location = OmhLocationImpl(context)
        location.getLastLocation(onSuccessListener, onFailureListener)

        // Assert
        verify {
            onSuccessListener.onSuccess(OmhCoordinate(10.0, 15.0))
        }
    }
}
