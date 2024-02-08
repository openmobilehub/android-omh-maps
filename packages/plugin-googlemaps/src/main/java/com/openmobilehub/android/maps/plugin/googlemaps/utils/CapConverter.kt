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

package com.openmobilehub.android.maps.plugin.googlemaps.utils

import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.ButtCap
import com.google.android.gms.maps.model.Cap
import com.google.android.gms.maps.model.CustomCap
import com.google.android.gms.maps.model.RoundCap
import com.google.android.gms.maps.model.SquareCap
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhCap
import com.openmobilehub.android.maps.core.presentation.models.OmhButtCap
import com.openmobilehub.android.maps.core.presentation.models.OmhCustomCap
import com.openmobilehub.android.maps.core.presentation.models.OmhRoundCap
import com.openmobilehub.android.maps.core.presentation.models.OmhSquareCap

object CapConverter {
    fun convertToCap(omhCap: OmhCap): Cap? {
        return when (omhCap) {
            is OmhRoundCap -> RoundCap()
            is OmhSquareCap -> SquareCap()
            is OmhButtCap -> ButtCap()
            is OmhCustomCap -> createCustomCap(omhCap)
            else -> null
        }
    }

    private fun createCustomCap(omhCap: OmhCustomCap): CustomCap {
        val bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(omhCap.bitmap)
        return omhCap.refWidth?.let { CustomCap(bitmapDescriptor, it) } ?: CustomCap(
            bitmapDescriptor
        )
    }
}
