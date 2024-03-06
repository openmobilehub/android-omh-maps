package com.openmobilehub.android.maps.plugin.openstreetmap.utils

import android.graphics.Paint
import com.openmobilehub.android.maps.core.presentation.models.OmhButtCap
import com.openmobilehub.android.maps.core.presentation.models.OmhRoundCap
import com.openmobilehub.android.maps.core.presentation.models.OmhSquareCap
import org.junit.Assert
import org.junit.Test

class CapConverterTest {
    @Test
    fun `convertToPaintCap returns Paint_Cap_ROUND for OmhRoundCap`() {
        // Act
        val result = CapConverter.convertToPaintCap(OmhRoundCap())

        // Assert
        Assert.assertEquals(Paint.Cap.ROUND, result)
    }

    @Test
    fun `convertToPaintCap returns Paint_Cap_SQUARE for OmhSquareCap`() {
        // Act
        val result = CapConverter.convertToPaintCap(OmhSquareCap())

        // Assert
        Assert.assertEquals(Paint.Cap.SQUARE, result)
    }

    @Test
    fun `convertToPaintCap returns Paint_Cap_BUTT for OmhButtCap`() {
        // Act
        val result = CapConverter.convertToPaintCap(OmhButtCap())

        // Assert
        Assert.assertEquals(Paint.Cap.BUTT, result)
    }

    @Test
    fun `convertToOmhCap returns OmhRoundCap for Paint_Cap_ROUND`() {
        // Act
        val result = CapConverter.convertToOmhCap(Paint.Cap.ROUND)

        // Assert
        Assert.assertTrue(result is OmhRoundCap)
    }

    @Test
    fun `convertToOmhCap returns OmhSquareCap for Paint_Cap_SQUARE`() {
        // Act
        val result = CapConverter.convertToOmhCap(Paint.Cap.SQUARE)

        // Assert
        Assert.assertTrue(result is OmhSquareCap)
    }

    @Test
    fun `convertToOmhCap returns OmhButtCap for Paint_Cap_BUTT`() {
        // Act
        val result = CapConverter.convertToOmhCap(Paint.Cap.BUTT)

        // Assert
        Assert.assertTrue(result is OmhButtCap)
    }
}
