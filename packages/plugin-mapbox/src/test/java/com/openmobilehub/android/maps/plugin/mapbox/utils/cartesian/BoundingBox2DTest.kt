package com.openmobilehub.android.maps.plugin.mapbox.utils.cartesian

import com.mapbox.maps.ScreenCoordinate
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Test

class BoundingBox2DTest {
    val centerPoint = ScreenCoordinate(5.0, 5.4)
    val width = 4.5
    val height = 3.5

    @Test
    fun `test contains function for a point inside the BB, without hitBorder`() {
        // Arrange
        val boundingBox = BoundingBox2D(centerPoint, width, height, 0.0)
        val pointInside = ScreenCoordinate(6.0, 5.85)

        // Act
        val result = boundingBox.contains(pointInside)

        // Assert
        assertTrue(result)
    }

    @Test
    fun `test contains function for point inside the BB, with hitBorder`() {
        // Arrange
        val boundingBox = BoundingBox2D(centerPoint, width, height, 16.0)
        val pointInside = ScreenCoordinate(-1.56, 3.9)

        // Act
        val result = boundingBox.contains(pointInside)

        // Assert
        assertTrue(result)
    }

    @Test
    fun `test contains function for point outside the BB, without hitBorder`() {
        // Arrange
        val boundingBox = BoundingBox2D(centerPoint, width, height, 0.0)
        val pointOutside = ScreenCoordinate(5.61, 200.0)

        // Act
        val result = boundingBox.contains(pointOutside)

        // Assert
        assertFalse(result)
    }

    @Test
    fun `test contains function for point outside the BB, with hitBorder`() {
        // Arrange
        val boundingBox = BoundingBox2D(centerPoint, width, height, 6.0)
        val pointInside = ScreenCoordinate(-90.56, 7.9)

        // Act
        val result = boundingBox.contains(pointInside)

        // Assert
        assertFalse(result)
    }
}
