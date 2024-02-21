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

package com.openmobilehub.android.maps.core.utils.logging

/**
 * [UnsupportedFeatureLogger] is a utility class that extends the `Logger` class
 * to provide specific logging functionality for unsupported features.
 *
 * It logs warnings when certain properties or methods in a map element are not supported.
 *
 * @property providerName The name of the provider. This is used to identify the source of the log message.
 * @property mapElement The name of the map element that contains the unsupported feature.
 */
class UnsupportedFeatureLogger(providerName: String, private val mapElement: String) : Logger(providerName) {

    /**
     * Logs a warning message indicating that a certain property in the map element is not supported.
     *
     * @param propertyName The name of the unsupported property.
     */
    fun logNotSupported(propertyName: String) {
        logWarning("Property $propertyName in $mapElement not supported.")
    }

    /**
     * Logs a warning message indicating that the getter for a certain property in the map element is not supported.
     *
     * @param propertyName The name of the property whose getter is not supported.
     */
    fun logGetterNotSupported(propertyName: String) {
        logWarning("Getter for property $propertyName in $mapElement not supported.")
    }

    /**
     * Logs a warning message indicating that the setter for a certain property in the map element is not supported.
     *
     * @param propertyName The name of the property whose setter is not supported.
     */
    fun logSetterNotSupported(propertyName: String) {
        logWarning("Setter for property $propertyName in $mapElement not supported.")
    }
}
