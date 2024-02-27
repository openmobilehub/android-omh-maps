/*
 * Copyright 2023 Open Mobile Hub
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.openmobilehub.android.maps.plugin.mapbox.presentation.location

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import androidx.annotation.RequiresPermission
import com.mapbox.common.location.AccuracyLevel
import com.mapbox.common.location.DeviceLocationProvider
import com.mapbox.common.location.IntervalSettings
import com.mapbox.common.location.Location
import com.mapbox.common.location.LocationObserver
import com.mapbox.common.location.LocationProviderRequest
import com.mapbox.common.location.LocationService
import com.mapbox.common.location.LocationServiceFactory
import com.openmobilehub.android.maps.core.presentation.interfaces.location.OmhFailureListener
import com.openmobilehub.android.maps.core.presentation.interfaces.location.OmhLocation
import com.openmobilehub.android.maps.core.presentation.interfaces.location.OmhSuccessListener
import com.openmobilehub.android.maps.core.presentation.models.OmhCoordinate
import com.openmobilehub.android.maps.core.presentation.models.OmhMapException
import com.openmobilehub.android.maps.core.presentation.models.OmhMapStatusCodes

internal class OmhLocationImpl(context: Context) : OmhLocation {

    private val locationService: LocationService = LocationServiceFactory.getOrCreate()
    private var locationProvider: DeviceLocationProvider? = null

    init {
        val request = LocationProviderRequest.Builder()
            .interval(
                IntervalSettings.Builder().interval(0L).minimumInterval(0L).maximumInterval(0L)
                    .build()
            )
            .displacement(0F)
            .accuracy(AccuracyLevel.HIGHEST)
            .build()

        val result = locationService.getDeviceLocationProvider(request)
        if (result.isValue) {
            locationProvider = result.value!!
            // Null location provider is handled in the methods
        }
    }

    @RequiresPermission(anyOf = [ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION])
    override fun getCurrentLocation(
        omhOnSuccessListener: OmhSuccessListener,
        omhOnFailureListener: OmhFailureListener
    ) {
        val safeLocationProvider = locationProvider ?: return omhOnFailureListener.onFailure(
            INVALID_PROVIDER_EXCEPTION
        )

        val locationObserver = object : LocationObserver {
            override fun onLocationUpdateReceived(locations: MutableList<Location>) {
                if (locations.isNotEmpty()) {
                    safeLocationProvider.removeLocationObserver(this)
                    omhOnSuccessListener.onSuccess(
                        OmhCoordinate(
                            locations[0].latitude,
                            locations[0].longitude
                        )
                    )
                } else {
                    omhOnFailureListener.onFailure(
                        NULL_LOCATION_EXCEPTION
                    )
                }
            }
        }

        safeLocationProvider.addLocationObserver(locationObserver)
    }

    @RequiresPermission(anyOf = [ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION])
    override fun getLastLocation(
        omhOnSuccessListener: OmhSuccessListener,
        omhOnFailureListener: OmhFailureListener
    ) {
        val safeLocationProvider = locationProvider ?: return omhOnFailureListener.onFailure(
            INVALID_PROVIDER_EXCEPTION
        )

        safeLocationProvider.getLastLocation { location ->
            if (location !== null) {
                omhOnSuccessListener.onSuccess(OmhCoordinate(location.latitude, location.longitude))
            } else {
                omhOnFailureListener.onFailure(NULL_LOCATION_EXCEPTION)
            }
        }
    }

    companion object {
        private val INVALID_PROVIDER_EXCEPTION = OmhMapException.ApiException(
            OmhMapStatusCodes.INVALID_PROVIDER,
            IllegalStateException(
                OmhMapStatusCodes.getStatusCodeString(
                    OmhMapStatusCodes.INVALID_PROVIDER
                )
            )
        )

        private val NULL_LOCATION_EXCEPTION = OmhMapException.NullLocation(
            IllegalStateException(
                OmhMapStatusCodes.getStatusCodeString(
                    OmhMapStatusCodes.NULL_LOCATION
                )
            )
        )
    }

    internal class Builder : OmhLocation.Builder {
        override fun build(context: Context): OmhLocation {
            return OmhLocationImpl(context)
        }
    }
}
