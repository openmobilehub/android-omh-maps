package com.openmobilehub.android.maps.core.utils.location

import android.location.Location

internal object LocationUtil {
    private const val TIME_TRESHOLD = 1000 * 60 * 2
    private const val ACCURACY_TRESHOLD = 200

    @SuppressWarnings("ReturnCount")
    fun getMoreAccurateLocation(
        firstLocation: Location,
        secondLocation: Location?
    ): Location {
        if (secondLocation == null) {
            return firstLocation
        }

        val timeDelta = firstLocation.time - secondLocation.time
        val accuracyDelta = firstLocation.accuracy - secondLocation.accuracy

        val isNewer = timeDelta > 0
        val isSignificantlyNewer = timeDelta > TIME_TRESHOLD

        if (isSignificantlyNewer) {
            return firstLocation
        }

        val isMoreAccurate = accuracyDelta < 0
        val isLessAccurate = accuracyDelta > 0
        val isSignificantlyLessAccurate = accuracyDelta > ACCURACY_TRESHOLD

        if (isMoreAccurate) {
            return firstLocation
        }

        if (isNewer && !isLessAccurate) {
            return firstLocation
        }

        if (isNewer && !isSignificantlyLessAccurate) {
            return firstLocation
        }

        return secondLocation
    }

    fun getMostAccurateLocation(
        locationList: List<Location?>
    ): Location? {
        val safeLocationList = locationList.filterNotNull()

        if (safeLocationList.isEmpty()) {
            return null
        }

        return safeLocationList.reduce { mostAccurate, current ->
            getMoreAccurateLocation(mostAccurate, current)
        }
    }
}
