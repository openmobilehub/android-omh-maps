package com.openmobilehub.android.maps.core.utils.location

import android.location.Location
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert
import org.junit.Test

class LocationUtilTest {

    private fun mockLocation(time: Long, accuracy: Float): Location {
        val location = mockk<Location>()
        every { location.time } returns time
        every { location.accuracy } returns accuracy
        return location
    }

    @Test
    fun `returns first location when second location is null`() {
        // Arrange
        val firstLocation = mockLocation(1000, 50f)

        // Act
        val result = LocationUtil.getMoreAccurateLocation(firstLocation, null)

        // Assert
        Assert.assertEquals(firstLocation, result)
    }

    @Test
    fun `returns first location when it is significantly newer`() {
        // Arrange
        val firstLocation = mockLocation(3000, 50f)
        val secondLocation = mockLocation(1000, 50f)

        // Act
        val result = LocationUtil.getMoreAccurateLocation(firstLocation, secondLocation)

        // Assert
        Assert.assertEquals(firstLocation, result)
    }

    @Test
    fun `returns first location when it is more accurate`() {
        // Arrange
        val firstLocation = mockLocation(1000, 50f)
        val secondLocation = mockLocation(1000, 100f)

        // Act
        val result = LocationUtil.getMoreAccurateLocation(firstLocation, secondLocation)

        // Assert
        Assert.assertEquals(firstLocation, result)
    }

    @Test
    fun `returns second location when it is more accurate and first is not significantly newer`() {
        // Arrange
        val firstLocation = mockLocation(1000, 100f)
        val secondLocation = mockLocation(1000, 50f)

        // Act
        val result = LocationUtil.getMoreAccurateLocation(firstLocation, secondLocation)

        // Assert
        Assert.assertEquals(secondLocation, result)
    }

    @Test
    fun `returns most accurate location from a list`() {
        // Arrange
        val firstLocation = mockLocation(1000, 100f)
        val secondLocation = mockLocation(1000, 50f)
        val thirdLocation = mockLocation(1000, 25f)

        // Act
        val result = LocationUtil.getMostAccurateLocation(
            listOf(
                firstLocation,
                secondLocation,
                thirdLocation
            )
        )

        // Assert
        Assert.assertEquals(thirdLocation, result)
    }
}
