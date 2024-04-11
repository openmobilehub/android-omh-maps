package com.openmobilehub.android.maps.core.utils.location

import com.openmobilehub.android.maps.core.presentation.models.OmhCoordinate

interface ILocationService {
    fun getCurrentLocation(onSuccess: (OmhCoordinate) -> Unit, onFailure: (Exception) -> Unit)
    fun getLastLocation(onSuccess: (OmhCoordinate) -> Unit, onFailure: (Exception) -> Unit)
    fun startLocationUpdates(
        onLocationUpdateSuccess: (OmhCoordinate) -> Unit,
        onLocationUpdateFailure: (Exception) -> Unit
    )
    fun stopLocationUpdates()
}
