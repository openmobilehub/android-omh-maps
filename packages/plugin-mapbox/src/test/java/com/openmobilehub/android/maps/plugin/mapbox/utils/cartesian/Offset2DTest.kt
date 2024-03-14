package com.openmobilehub.android.maps.plugin.mapbox.utils.cartesian

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
