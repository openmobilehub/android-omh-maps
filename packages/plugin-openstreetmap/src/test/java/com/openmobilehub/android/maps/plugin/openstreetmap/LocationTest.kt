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

package com.openmobilehub.android.maps.plugin.openstreetmap

import android.location.Location
import com.openmobilehub.android.maps.plugin.openstreetmap.Constants.LATITUDE
import com.openmobilehub.android.maps.plugin.openstreetmap.Constants.LONGITUDE
import com.openmobilehub.android.maps.plugin.openstreetmap.Constants.OTHER_LATITUDE
import com.openmobilehub.android.maps.plugin.openstreetmap.Constants.OTHER_LONGITUDE
import com.openmobilehub.android.maps.plugin.openstreetmap.extensions.toOmhCoordinate
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test

internal class LocationTest {
    private lateinit var location: Location

    @Before
    fun setUp() {
        location = mockk()
        every { location.latitude } returns LATITUDE
        every { location.longitude } returns LONGITUDE
        every { location.accuracy } returns 10.0f
    }

    @Test
    fun `given a Location, when toOmhCoordinate, then a correct OmhCoordinate returned`() {
        val omhCoordinate = location.toOmhCoordinate()

        assertEquals(location.latitude, omhCoordinate.latitude, 0.0)
        assertEquals(location.longitude, omhCoordinate.longitude, 0.0)
    }

    @Test
    fun `given a Location, when toCoordinate and compare a different OmhCoordinate, then is not equals`() {
        val omhCoordinate = location.toOmhCoordinate()
        val otherLocation = mockk<Location>()

        every { otherLocation.latitude } returns OTHER_LATITUDE
        every { otherLocation.longitude } returns OTHER_LONGITUDE

        assertNotEquals(otherLocation.latitude, omhCoordinate.latitude, 0.0)
        assertNotEquals(otherLocation.longitude, omhCoordinate.longitude, 0.0)
    }
}
