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
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import org.junit.Assert
import org.junit.Test

internal class MapProvidersUtilsTest {

    private val context = mockk<Context>()

    private fun mockGMS(available: Boolean) {
        mockkStatic(GoogleApiAvailability::class)
        val googleApiAvailability = mockk<GoogleApiAvailability>()
        every { googleApiAvailability.isGooglePlayServicesAvailable(any()) } answers {
            if (available) ConnectionResult.SUCCESS else ConnectionResult.SERVICE_MISSING
        }

        every { GoogleApiAvailability.getInstance() } returns googleApiAvailability
    }

    private fun buildClassChecker(classes: Set<String>): IClassChecker {
        return object : IClassChecker {
            override fun classExists(className: String): Boolean {
                return classes.contains(className)
            }
        }
    }

    @Test
    fun `getAvailableMapProviders returns all providers if both GMS & plugins available`() {
        // Arrange
        val allProviders = hashSetOf(
            Constants.GOOGLE_MAPS_PATH,
            Constants.OPEN_STREET_MAP_PATH,
            Constants.MAPBOX_PATH,
            Constants.AZURE_MAPS_PATH
        )
        mockGMS(true)
        val classChecker = buildClassChecker(allProviders)

        // Act
        val availableProviders = MapProvidersUtils(classChecker).getAvailableMapProviders(context)

        // Assert
        Assert.assertEquals(4, availableProviders.size)
        Assert.assertEquals(
            allProviders,
            availableProviders.map { it.path }.toSet()
        )
    }

    @Test
    fun `getAvailableMapProviders returns non-GMS providers if some non-GMS plugins are unavailable and GMS missing`() {
        // Arrange
        val testProviders = hashSetOf(
            Constants.GOOGLE_MAPS_PATH,
            Constants.MAPBOX_PATH,
            Constants.AZURE_MAPS_PATH
        )
        mockGMS(false)
        val classChecker = buildClassChecker(testProviders)

        // Act
        val availableProviders = MapProvidersUtils(classChecker).getAvailableMapProviders(context)

        // Assert
        Assert.assertEquals(2, availableProviders.size)
        Assert.assertEquals(
            hashSetOf(
                Constants.MAPBOX_PATH,
                Constants.AZURE_MAPS_PATH
            ),
            availableProviders.map { it.path }.toSet()
        )
    }
}
