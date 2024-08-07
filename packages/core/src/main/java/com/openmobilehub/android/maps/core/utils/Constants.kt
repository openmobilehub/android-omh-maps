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

package com.openmobilehub.android.maps.core.utils

internal object Constants {
    // Network
    const val NO_INTERNET_CONNECTION = "No internet connection"
    const val LOST_INTERNET_CONNECTION = "Lost internet connection"

    // Paths
    const val GOOGLE_MAPS_PATH =
        "com.openmobilehub.android.maps.plugin.googlemaps.presentation.OmhMapFactoryImpl"
    const val OPEN_STREET_MAP_PATH =
        "com.openmobilehub.android.maps.plugin.openstreetmap.presentation.OmhMapFactoryImpl"
    const val MAPBOX_PATH =
        "com.openmobilehub.android.maps.plugin.mapbox.presentation.OmhMapFactoryImpl"
    const val AZURE_MAPS_PATH =
        "com.openmobilehub.android.maps.plugin.azuremaps.presentation.OmhMapFactoryImpl"

    // Log
    const val MAX_NAME_LENGTH = 23
    const val MIN_NAME_LENGTH = 0
    const val LOG_TAG = "OmhMaps"
}
