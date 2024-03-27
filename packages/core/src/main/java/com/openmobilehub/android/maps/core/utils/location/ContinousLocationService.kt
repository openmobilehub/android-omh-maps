package com.openmobilehub.android.maps.core.utils.location

import android.Manifest
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.annotation.RequiresPermission
import com.openmobilehub.android.maps.core.presentation.models.OmhCoordinate
import com.openmobilehub.android.maps.core.presentation.models.OmhMapException
import com.openmobilehub.android.maps.core.presentation.models.OmhMapStatusCodes

class ContinousLocationService(
    private val locationManager: LocationManager,
    private val providers: List<String>
) {
    private var locationListener: LocationListener? = null
    private var currentBestLocation: Location? = null

    @SuppressWarnings("TooGenericExceptionCaught")
    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
    fun startLocationUpdates(
        onLocationUpdateSuccess: (OmhCoordinate) -> Unit,
        onLocationUpdateFailure: (Exception) -> Unit
    ) {
        locationListener =
            LocationListener { location: Location? ->
                if (location != null) {
                    val moreAccurateLocation =
                        LocationUtil.getMoreAccurateLocation(location, currentBestLocation)

                    if (moreAccurateLocation == location) {
                        currentBestLocation = location
                        onLocationUpdateSuccess(
                            OmhCoordinate(
                                location.latitude,
                                location.longitude
                            )
                        )
                    }
                } else {
                    onLocationUpdateFailure(
                        OmhMapException.NullLocation(
                            IllegalStateException(
                                OmhMapStatusCodes.getStatusCodeString(
                                    OmhMapStatusCodes.NULL_LOCATION
                                )
                            )
                        )
                    )
                }
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
    fun stopLocationUpdates() {
        currentBestLocation = null

        if (locationListener != null) {
            locationManager.removeUpdates(locationListener!!)
        }
    }
}
