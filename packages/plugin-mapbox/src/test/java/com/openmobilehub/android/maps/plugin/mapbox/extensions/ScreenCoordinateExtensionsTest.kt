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

package com.openmobilehub.android.maps.plugin.mapbox.extensions

import com.mapbox.maps.ScreenCoordinate
import com.openmobilehub.android.maps.plugin.mapbox.utils.cartesian.Offset2D
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotSame
import org.junit.Test

class ScreenCoordinateExtensionsTest {

    @Test
    fun `plus operator should properly offset screen coordinates`() {
        // Arrange
        val coordinate = ScreenCoordinate(5.0, 10.0)
        val offset = Offset2D(1.0, -7.1)

        // Act
        val result = coordinate + offset

        // Assert
        assertEquals(ScreenCoordinate(6.0, 2.9), result)
    }

    @Test
    fun testPlusOperatorCreatesNewInstance() {
        // Arrange
        val coordinate = ScreenCoordinate(5.0, 10.0)
        val offset = Offset2D(1.0, 7.0)

        // Act
        val result = coordinate + offset

        // Assert
        assertNotSame(coordinate, result)
    }

    @Test
    fun `distanceTo should calculate distance between two coordinates`() {
        // Arrange
        val coordinate1 = ScreenCoordinate(0.0, 0.0)
        val coordinate2 = ScreenCoordinate(3.0, -4.0)

        // Act
        val distance = coordinate1.distanceTo(coordinate2)

        // Assert
        assertEquals(5.0, distance, 1e-4)
    }

    @Test
    fun `distanceTo should handle zero coordinates`() {
        // Arrange
        val coordinate1 = ScreenCoordinate(0.0, 0.0)
        val coordinate2 = ScreenCoordinate(0.0, 0.0)

        // Act
        val distance = coordinate1.distanceTo(coordinate2)

        // Assert
        assertEquals(0.0, distance, 1e-4)
    }
}