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

package com.openmobilehub.android.maps.core.utils.cartesian

import org.junit.Assert
import org.junit.Test

class Point2DTest {

    @Test
    fun `plus operator should properly offset screen coordinates for Double`() {
        // Arrange
        val coordinate = Point2D(5.0, 10.0)
        val offset = Offset2D(1.0, -7.1)

        // Act
        val result = coordinate + offset

        // Assert
        Assert.assertEquals(6.0, result.x, 1e-4)
        Assert.assertEquals(2.9, result.y, 1e-4)
    }

    @Test
    fun `test plus operator creates new instance for Double`() {
        // Arrange
        val coordinate = Point2D(5.0, 10.0)
        val offset = Offset2D(1.0, 7.0)

        // Act
        val result = coordinate + offset

        // Assert
        Assert.assertNotSame(coordinate, result)
    }

    @Test
    fun `distanceTo should calculate distance between two coordinates for Double`() {
        // Arrange
        val coordinate1 = Point2D(0.0, 0.0)
        val coordinate2 = Point2D(3.0, -4.0)

        // Act
        val distance = coordinate1.distanceTo(coordinate2)

        // Assert
        Assert.assertEquals(5.0, distance, 1e-4)
    }

    @Test
    fun `distanceTo should handle zero coordinates for Double`() {
        // Arrange
        val coordinate1 = Point2D(0.0, 0.0)
        val coordinate2 = Point2D(0.0, 0.0)

        // Act
        val distance = coordinate1.distanceTo(coordinate2)

        // Assert
        Assert.assertEquals(0.0, distance, 1e-4)
    }

    @Test
    fun `plus operator should properly offset screen coordinates for Int`() {
        // Arrange
        val coordinate = Point2D(5, 10)
        val offset = Offset2D(1, -7)

        // Act
        val result = coordinate + offset

        // Assert
        Assert.assertEquals(Point2D(6, 3), result)
    }

    @Test
    fun `test plus operator creates new instance for Int`() {
        // Arrange
        val coordinate = Point2D(5, 10)
        val offset = Offset2D(1, 7)

        // Act
        val result = coordinate + offset

        // Assert
        Assert.assertNotSame(coordinate, result)
    }

    @Test
    fun `distanceTo should calculate distance between two coordinates for Int`() {
        // Arrange
        val coordinate1 = Point2D(0, 0)
        val coordinate2 = Point2D(3, -4)

        // Act
        val distance = coordinate1.distanceTo(coordinate2)

        // Assert
        Assert.assertEquals(5.0, distance, 1e-4)
    }

    @Test
    fun `distanceTo should handle zero coordinates for Int`() {
        // Arrange
        val coordinate1 = Point2D(0, 0)
        val coordinate2 = Point2D(0, 0)

        // Act
        val distance = coordinate1.distanceTo(coordinate2)

        // Assert
        Assert.assertEquals(0.0, distance, 1e-4)
    }
}
