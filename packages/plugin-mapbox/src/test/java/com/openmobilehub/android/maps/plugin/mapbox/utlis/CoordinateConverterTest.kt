package com.openmobilehub.android.maps.plugin.mapbox.utlis

import com.mapbox.geojson.Point
import com.openmobilehub.android.maps.core.presentation.models.OmhCoordinate
import com.openmobilehub.android.maps.plugin.mapbox.utils.CoordinateConverter
import org.junit.Assert
import org.junit.Test

class CoordinateConverterTest {
    @Test
    fun `convertToOmhCoordinate returns correct OmhCoordinate for given Point`() {
        // Arrange
        val point = Point.fromLngLat(10.0, 20.0)
        val expectedCoordinate = OmhCoordinate(20.0, 10.0)

        // Act
        val result = CoordinateConverter.convertToOmhCoordinate(point)

        // Assert
        Assert.assertEquals(expectedCoordinate, result)
    }

    @Test
    fun `convertToPoint returns correct Point for given OmhCoordinate`() {
        // Arrange
        val coordinate = OmhCoordinate(20.0, 10.0)
        val expectedPoint = Point.fromLngLat(10.0, 20.0)

        // Act
        val result = CoordinateConverter.convertToPoint(coordinate)

        // Assert
        Assert.assertEquals(expectedPoint, result)
    }
}
