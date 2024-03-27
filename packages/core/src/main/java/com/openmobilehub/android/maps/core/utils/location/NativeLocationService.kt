package com.openmobilehub.android.maps.core.utils.location

import android.Manifest
import android.content.Context
import android.location.LocationManager
import androidx.annotation.RequiresPermission
import com.openmobilehub.android.maps.core.presentation.models.OmhCoordinate

class NativeLocationService(context: Context) : ILocationService {
    private var locationManager: LocationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    private val providers = listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER)

    private val currentLocationService = CurrentLocationService(
        locationManager,
        providers
    )

    private val continousLocationService = ContinousLocationService(
        locationManager,
        providers
    )

    private val lastLocationService = LastLocationService(
        locationManager,
        providers
    )

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
    override fun getCurrentLocation(
        onSuccess: (OmhCoordinate) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        currentLocationService.getCurrentLocation(onSuccess, onFailure)
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
    override fun getLastLocation(
        onSuccess: (OmhCoordinate) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        lastLocationService.getLastLocation(onSuccess, onFailure)
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
    override fun startLocationUpdates(
        onLocationUpdateSuccess: (OmhCoordinate) -> Unit,
        onLocationUpdateFailure: (Exception) -> Unit
    ) {
        continousLocationService.startLocationUpdates(
            onLocationUpdateSuccess,
            onLocationUpdateFailure
        )
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
    override fun stopLocationUpdates() {
        continousLocationService.stopLocationUpdates()
    }
}
