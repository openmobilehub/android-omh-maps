package com.openmobilehub.android.maps.plugin.mapbox.utils

import android.content.Context
import android.util.DisplayMetrics
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class DimensionConverterTest {
    private val context = mockk<Context>(relaxed = true)
    private val mockedDensity = 3f

    @Before
    fun setup() {
        val displayMetrics = mockk<DisplayMetrics>().apply {
            this.density = mockedDensity
        }
        every { context.resources.displayMetrics } returns displayMetrics
    }

    @Test
    fun `dpFromPx returns correct dp value for given px`() {
        // Arrange
        val pxValue = 30
        val expectedDpValue = 10

        // Act
        val result = DimensionConverter.dpFromPx(context, pxValue)

        // Assert
        Assert.assertEquals(expectedDpValue, result)
    }

    @Test
    fun `pxFromDp returns correct px value from given dp`() {
        // Arrange
        val dxValue = 10
        val expectedPxValue = 30

        // Act
        val result = DimensionConverter.pxFromDp(context, dxValue)

        // Assert
        Assert.assertEquals(expectedPxValue, result)
    }
}
