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

import android.content.Context
import com.openmobilehub.android.maps.core.model.MapProvider

interface IClassChecker {
    fun classExists(className: String): Boolean
}

internal object ClassChecker : IClassChecker {
    override fun classExists(className: String): Boolean {
        return try {
            Class.forName(className)
            true
        } catch (@Suppress("SwallowedException") e: ClassNotFoundException) {
            false
        }
    }
}

class MapProvidersUtils(private val classChecker: IClassChecker = ClassChecker) {
    fun getAvailableMapProviders(context: Context): List<MapProvider> {
        val mapProviders = mutableListOf(
            MapProvider("OpenStreetMap", Constants.OPEN_STREET_MAP_PATH),
            MapProvider("Mapbox", Constants.MAPBOX_PATH),
            MapProvider("AzureMaps", Constants.AZURE_MAPS_PATH)
        )
        if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(context)) {
            mapProviders.add(0, MapProvider("Google", Constants.GOOGLE_MAPS_PATH))
        }

        return mapProviders.filter { classChecker.classExists(it.path) }
    }

    fun getDefaultMapProvider(context: Context): MapProvider {
        val mapProviders = getAvailableMapProviders(context)
        return mapProviders.first()
    }
}
