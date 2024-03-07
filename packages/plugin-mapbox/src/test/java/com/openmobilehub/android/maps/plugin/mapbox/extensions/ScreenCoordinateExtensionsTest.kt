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
import com.openmobilehub.android.maps.plugin.mapbox.utils.Offset2D
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotSame
import org.junit.Test

class ScreenCoordinateExtensionsTest {

    @Test
    fun testPlusOperator() {
        // Arrange
        val coordinate = ScreenCoordinate(5.0, 10.0)
        val offset = Offset2D(1, 7)

        // Act
        val result = coordinate + offset

        // Assert
        assertEquals(ScreenCoordinate(6.0, 17.0), result)
    }

    @Test
    fun testPlusOperatorCreatesNewInstance() {
        // Arrange
        val coordinate = ScreenCoordinate(5.0, 10.0)
        val offset = Offset2D(1, 7)

        // Act
        val result = coordinate + offset

        // Assert
        assertNotSame(coordinate, result)
    }
}
