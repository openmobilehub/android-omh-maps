package com.openmobilehub.android.maps.plugin.mapbox.utils

import com.openmobilehub.android.maps.core.utils.logging.Logger
import com.openmobilehub.android.maps.core.utils.logging.UnsupportedFeatureLogger

val commonLogger = Logger(Constants.PROVIDER_NAME)
val polylineLogger = UnsupportedFeatureLogger(Constants.PROVIDER_NAME, "OmhPolyline")
