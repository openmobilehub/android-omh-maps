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

internal class GooglePlayServicesUtilTest {

    private val gmsOk = ConnectionResult.SUCCESS
    private val gmsMissing = ConnectionResult.SERVICE_MISSING
    private val gmsPermissionMissing = ConnectionResult.SERVICE_MISSING_PERMISSION

    @Test
    fun `isGooglePlayServicesAvailable works properly`() {
        // Arrange
        val context = mockk<Context>()
        mockkStatic(GoogleApiAvailability::class)
        val googleApiAvailability = mockk<GoogleApiAvailability>()
        every {
            googleApiAvailability.isGooglePlayServicesAvailable(any())
        } returns gmsOk andThen gmsMissing andThen gmsPermissionMissing
        every { GoogleApiAvailability.getInstance() } returns googleApiAvailability

        // Act
        val result1 = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context)
        val result2 = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context)
        val result3 = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context)

        // Assert
        Assert.assertEquals(true, result1)
        Assert.assertEquals(false, result2)
        Assert.assertEquals(false, result3)
    }
}
