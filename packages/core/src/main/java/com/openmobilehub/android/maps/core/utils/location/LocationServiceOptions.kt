package com.openmobilehub.android.maps.core.utils.location

data class LocationServiceOptions(
    val currentLocationAccuracy: Int = GOOD_ACCURACY,
    val currentLocationTimeout: Int = TIMEOUT
) {
    companion object {
        const val TIMEOUT = 1000 * 10 // 10 seconds
        const val GOOD_ACCURACY = 20 // 20 meters
    }
}
