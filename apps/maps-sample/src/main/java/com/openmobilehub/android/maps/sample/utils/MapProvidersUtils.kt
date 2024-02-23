package com.openmobilehub.android.maps.sample.utils

import android.content.Context
import com.openmobilehub.android.maps.sample.model.MapProvider

internal object MapProvidersUtils {
    fun getAvailableMapProviders(context: Context): MutableList<MapProvider> {
        val mapProviders = mutableListOf(
            MapProvider("OpenStreetMap", Constants.OPEN_STREET_MAP_PATH),
            MapProvider("Mapbox", Constants.MAPBOX_PATH)
        )
        if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(context)) {
            mapProviders.add(0, MapProvider("Google", Constants.GOOGLE_MAPS_PATH))
        }
        return mapProviders
    }

    fun getDefaultMapProvider(context: Context): MapProvider {
        val mapProviders = getAvailableMapProviders(context)
        return mapProviders.first()
    }
}
