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

import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Test

class BoundingBox2DTest {
    val centerPoint = Point2D(5.0, 5.4)
    val width = 4.5
    val height = 3.5

    @Test
    fun `test contains function for a point inside the BB, without hitBorder`() {
        // Arrange
        val boundingBox = BoundingBox2D(centerPoint, width, height, 0.0)
        val pointInside = Point2D(6.0, 5.85)

        // Act
        val result = boundingBox.contains(pointInside)

        // Assert
        assertTrue(result)
    }

    @Test
    fun `test contains function for point inside the BB, with hitBorder`() {
        // Arrange
        val boundingBox = BoundingBox2D(centerPoint, width, height, 16.0)
        val pointInside = Point2D(-1.56, 3.9)

        // Act
        val result = boundingBox.contains(pointInside)

        // Assert
        assertTrue(result)
    }

    @Test
    fun `test contains function for point outside the BB, without hitBorder`() {
        // Arrange
        val boundingBox = BoundingBox2D(centerPoint, width, height, 0.0)
        val pointOutside = Point2D(5.61, 200.0)

        // Act
        val result = boundingBox.contains(pointOutside)

        // Assert
        assertFalse(result)
    }

    @Test
    fun `test contains function for point outside the BB, with hitBorder`() {
        // Arrange
        val boundingBox = BoundingBox2D(centerPoint, width, height, 6.0)
        val pointInside = Point2D(-90.56, 7.9)

        // Act
        val result = boundingBox.contains(pointInside)

        // Assert
        assertFalse(result)
    }
}
