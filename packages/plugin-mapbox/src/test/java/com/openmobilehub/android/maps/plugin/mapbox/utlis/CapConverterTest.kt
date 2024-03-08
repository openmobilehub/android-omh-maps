package com.openmobilehub.android.maps.plugin.mapbox.utlis

import com.mapbox.maps.extension.style.layers.properties.generated.LineCap
import com.openmobilehub.android.maps.core.presentation.models.OmhButtCap
import com.openmobilehub.android.maps.core.presentation.models.OmhRoundCap
import com.openmobilehub.android.maps.core.presentation.models.OmhSquareCap
import com.openmobilehub.android.maps.plugin.mapbox.utils.CapConverter
import org.junit.Assert
import org.junit.Test

class CapConverterTest {
    @Test
    fun `convertToLineCap returns LineCap_Round for OmhRoundCap`() {
        // Act
        val result = CapConverter.convertToLineCap(OmhRoundCap())

        // Assert
        Assert.assertEquals(LineCap.ROUND, result)
    }

    @Test
    fun `convertToLineCap returns LineCap_Square for OmhSquareCap`() {
        // Act
        val result = CapConverter.convertToLineCap(OmhSquareCap())

        // Assert
        Assert.assertEquals(LineCap.SQUARE, result)
    }

    @Test
    fun `convertToLineCap returns LineCap_Butt for OmhButtCap`() {
        // Act
        val result = CapConverter.convertToLineCap(OmhButtCap())

        // Assert
        Assert.assertEquals(LineCap.BUTT, result)
    }

    @Test
    fun `convertToOmhCap returns OmhRoundCap for LineCap_Round`() {
        // Act
        val result = CapConverter.convertToOmhCap(LineCap.ROUND)

        // Assert
        Assert.assertTrue(result is OmhRoundCap)
    }

    @Test
    fun `convertToOmhCap returns OmhSquareCap for LineCap_Square`() {
        // Act
        val result = CapConverter.convertToOmhCap(LineCap.SQUARE)

        // Assert
        Assert.assertTrue(result is OmhSquareCap)
    }

    @Test
    fun `convertToOmhCap returns OmhButtCap for LineCap_Butt`() {
        // Act
        val result = CapConverter.convertToOmhCap(LineCap.BUTT)

        // Assert
        Assert.assertTrue(result is OmhButtCap)
    }
}
