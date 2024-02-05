package com.openmobilehub.android.maps.core.utils

import android.util.Log

class UnsupportedFeatureLogger(private val mapElement: String, private val providerName: String) {
    private val tag = Constants.LOG_TAG

    fun logNotSupported(propertyName: String) {
        Log.w(tag, "Property $propertyName in $mapElement not supported by $providerName")
    }

    fun logGetterNotSupported(propertyName: String) {
        Log.w(tag, "Getter for property $propertyName in $mapElement not supported by $providerName")
    }

    fun logSetterNotSupported(propertyName: String) {
        Log.w(tag, "Setter for property $propertyName in $mapElement not supported by $providerName")
    }
}
