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

import android.util.Log
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class UnsupportedFeatureLoggerTest {
    private val mapElement = "MapElement"
    private val providerName = "ProviderName"
    private val propertyName = "SomeProperty"
    private val tag = Constants.LOG_TAG

    private lateinit var logger: UnsupportedFeatureLogger

    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.w(String(), String()) } returns 0
        logger = UnsupportedFeatureLogger(mapElement, providerName)
    }

    @Test
    fun `logNotSupported logs correct message`() {
        // Act
        logger.logNotSupported(propertyName)

        // Assert
        verify { Log.w(tag, "Property $propertyName in $mapElement not supported by $providerName") }
    }

    @Test
    fun `logGetterNotSupported logs correct message`() {
        // Act
        logger.logGetterNotSupported(propertyName)

        // Assert
        verify { Log.w(tag, "Getter for property $propertyName in $mapElement not supported by $providerName") }
    }

    @Test
    fun `logSetterNotSupported logs correct message`() {
        // Act
        logger.logSetterNotSupported(propertyName)

        // Assert
        verify { Log.w(tag, "Setter for property $propertyName in $mapElement not supported by $providerName") }
    }
}
