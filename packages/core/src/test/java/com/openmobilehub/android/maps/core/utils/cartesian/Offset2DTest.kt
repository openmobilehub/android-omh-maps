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

import org.junit.Assert.assertEquals
import org.junit.Test

class Offset2DTest {

    @Test
    fun `test Offset2D for Int type plus operator`() {
        // Arrange
        val offset1 = Offset2D(1, 2)
        val offset2 = Offset2D(2, -3)

        // Act
        val result = offset1 + offset2

        // Assert
        assertEquals(Offset2D(3, -1), result)
    }

    @Test
    fun `test Offset2D for Double type plus operator`() {
        // Arrange
        val offset1 = Offset2D(1.4, -2.5)
        val offset2 = Offset2D(3.0, 4.0)

        // Act
        val result = offset1 + offset2

        // Assert
        assertEquals(Offset2D(4.4, 1.5), result)
    }

    @Test
    fun `test Offset2D for Float type plus operator`() {
        // Arrange
        val offset1 = Offset2D(1f, 2.4f)
        val offset2 = Offset2D(-3f, 4f)

        // Act
        val result = offset1 + offset2

        // Assert
        assertEquals(Offset2D(-2f, 6.4f), result)
    }
}
