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

package com.openmobilehub.android.maps.plugin.azuremaps.utils

import androidx.annotation.ColorInt

internal object Constants {
    // Marker style
    @ColorInt
    const val DEFAULT_MARKER_COLOR: Int = 0xFFEA393F.toInt()

    // Common
    const val PROVIDER_NAME = "AzureMaps"

    // Defaults
    const val DEFAULT_MIN_ZOOM = 1.0
    const val DEFAULT_MAX_ZOOM = 20.0

    // Map interaction
    const val MARKER_FEATURE_UUID_BINDING = "omh-marker-id-binding"

    // Camera
    /**
     * According to the official documentation, to achieve the same zoom level as in Google Maps,
     * the zoom level should be decreased by 1.0.
     * https://learn.microsoft.com/en-us/azure/azure-maps/migrate-from-google-maps-android-app?pivots=programming-language-kotlin#before-google-maps-2
     */
    const val ZOOM_LEVEL_SUBTRAHEND = 1.0
}
