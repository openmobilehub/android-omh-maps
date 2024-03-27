package com.openmobilehub.android.maps.core.utils.location

import android.Manifest
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.annotation.RequiresPermission
import com.openmobilehub.android.maps.core.presentation.models.OmhCoordinate
import com.openmobilehub.android.maps.core.presentation.models.OmhMapException
import com.openmobilehub.android.maps.core.presentation.models.OmhMapStatusCodes

class CurrentLocationService(
    private val locationManager: LocationManager,
    private val providers: List<String>
) {
    private var locationListener: LocationListener? = null
    private val currentLocationsList = mutableListOf<Location>()

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
    fun getCurrentLocation(
        onSuccess: (OmhCoordinate) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        this.startLocationUpdates(
            onLocationUpdateSuccess = { omhCoordinate: OmhCoordinate ->
                onSuccess(omhCoordinate)
                currentLocationsList.clear()
                this.stopLocationUpdates()
            },
            onLocationUpdateFailure = { exception: Exception ->
                onFailure(exception)
                currentLocationsList.clear()
                this.stopLocationUpdates()
            }
        )
    }

    @SuppressWarnings("ReturnCount")
    private fun handleLocationUpdate(
        location: Location?,
        onLocationUpdateSuccess: (OmhCoordinate) -> Unit,
        onLocationUpdateFailure: (Exception) -> Unit
    ) {
        if (location == null) {
            onLocationUpdateFailure(
                OmhMapException.NullLocation(
                    IllegalStateException(
                        OmhMapStatusCodes.getStatusCodeString(
                            OmhMapStatusCodes.NULL_LOCATION
                        )
                    )
                )
            )
            return
        }

        val requestTime = System.currentTimeMillis()
        val requestTimeDelta = System.currentTimeMillis() - requestTime

        val isTimeout = requestTimeDelta > TIMEOUT
        val isGoodAccuracy = location.accuracy < GOOD_ACCURACY

        if (isGoodAccuracy) {
            return onLocationUpdateSuccess(
                OmhCoordinate(
                    location.latitude,
                    location.longitude
                )
            )
        }

        currentLocationsList.add(location)

        if (isTimeout) {
            val mostAccurateLocation = this.currentLocationsList.minByOrNull { it.accuracy }
                ?: return onLocationUpdateFailure(
                    OmhMapException.NullLocation(
                        IllegalStateException(
                            OmhMapStatusCodes.getStatusCodeString(
                                OmhMapStatusCodes.NULL_LOCATION
                            )
                        )
                    )
                )

            return onLocationUpdateSuccess(
                OmhCoordinate(
                    mostAccurateLocation.latitude,
                    mostAccurateLocation.longitude
                )
            )
        }
    }

    @SuppressWarnings("TooGenericExceptionCaught")
    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
    private fun startLocationUpdates(
        onLocationUpdateSuccess: (OmhCoordinate) -> Unit,
        onLocationUpdateFailure: (Exception) -> Unit
    ) {
        locationListener =
            LocationListener { location: Location? ->
                handleLocationUpdate(location, onLocationUpdateSuccess, onLocationUpdateFailure)
            }

        try {
            providers.forEach { provider ->
                locationManager.requestLocationUpdates(
                    provider,
                    0L,
                    0f,
                    locationListener!!
                )
            }
        } catch (exception: IllegalArgumentException) {
            onLocationUpdateFailure(
                OmhMapException.ApiException(
                    OmhMapStatusCodes.NULL_LISTENER,
                    exception
                )
            )
        } catch (exception: RuntimeException) {
            onLocationUpdateFailure(
                OmhMapException.ApiException(
                    OmhMapStatusCodes.INVALID_LOOPER,
                    exception
                )
            )
        } catch (exception: SecurityException) {
            onLocationUpdateFailure(OmhMapException.PermissionError(exception))
        } catch (exception: OmhMapException.ApiException) {
            onLocationUpdateFailure(exception)
        }
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
    private fun stopLocationUpdates() {
        if (locationListener != null) {
            locationManager.removeUpdates(locationListener!!)
        }
    }

    companion object {
        private const val TIMEOUT = 1000 * 10 // 10 seconds
        private const val GOOD_ACCURACY = 20 // 20 meters
    }
}
