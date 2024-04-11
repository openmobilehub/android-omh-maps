package com.openmobilehub.android.maps.core.utils.location

import android.Manifest
import android.location.Location
import android.location.LocationManager
import androidx.annotation.RequiresPermission
import com.openmobilehub.android.maps.core.presentation.models.OmhCoordinate
import com.openmobilehub.android.maps.core.presentation.models.OmhMapException
import com.openmobilehub.android.maps.core.presentation.models.OmhMapStatusCodes

class LastLocationService(
    private val locationManager: LocationManager,
    private val providers: List<String>
) {
    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
    fun getLastLocation(
        onSuccess: (OmhCoordinate) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val lastKnownLocation = this.getLastKnownLocation()
        return lastKnownLocation?.let {
            onSuccess(OmhCoordinate(it.latitude, it.longitude))
        } ?: onFailure(
            OmhMapException.NullLocation(
                IllegalStateException(
                    OmhMapStatusCodes.getStatusCodeString(
                        OmhMapStatusCodes.NULL_LOCATION
                    )
                )
            )
        )
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
    private fun getLastKnownLocation(): Location? {
        val lastLocations = providers.map { provider -> this.obtainLastKnownLocation(provider) }
        return LocationUtil.getMostAccurateLocation(lastLocations)
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
    @Throws(OmhMapException::class)
    private fun obtainLastKnownLocation(provider: String): Location? {
        return try {
            locationManager.getLastKnownLocation(provider)
        } catch (exception: SecurityException) {
            throw OmhMapException.PermissionError(exception)
        } catch (exception: IllegalArgumentException) {
            throw OmhMapException.ApiException(OmhMapStatusCodes.INVALID_PROVIDER, exception)
        }
    }
}
