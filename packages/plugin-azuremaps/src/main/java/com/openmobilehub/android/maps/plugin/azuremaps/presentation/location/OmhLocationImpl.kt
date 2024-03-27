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

package com.openmobilehub.android.maps.plugin.azuremaps.presentation.location

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import androidx.annotation.RequiresPermission
import com.openmobilehub.android.maps.core.presentation.interfaces.location.OmhFailureListener
import com.openmobilehub.android.maps.core.presentation.interfaces.location.OmhLocation
import com.openmobilehub.android.maps.core.presentation.interfaces.location.OmhSuccessListener
import com.openmobilehub.android.maps.core.utils.location.NativeLocationService

internal class OmhLocationImpl(context: Context) : OmhLocation {

    private val nativeLocationService = NativeLocationService(context)

    @RequiresPermission(anyOf = [ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION])
    override fun getCurrentLocation(
        omhOnSuccessListener: OmhSuccessListener,
        omhOnFailureListener: OmhFailureListener
    ) {
        nativeLocationService.getCurrentLocation(
            onSuccess = { location ->
                omhOnSuccessListener.onSuccess(location)
            },
            onFailure = { exception ->
                omhOnFailureListener.onFailure(exception)
            }
        )
    }

    @RequiresPermission(anyOf = [ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION])
    override fun getLastLocation(
        omhOnSuccessListener: OmhSuccessListener,
        omhOnFailureListener: OmhFailureListener
    ) {
        nativeLocationService.getLastLocation(
            onSuccess = { location ->
                omhOnSuccessListener.onSuccess(location)
            },
            onFailure = { exception ->
                omhOnFailureListener.onFailure(exception)
            }
        )
    }

    internal class Builder : OmhLocation.Builder {
        override fun build(context: Context): OmhLocation {
            return OmhLocationImpl(context)
        }
    }
}
