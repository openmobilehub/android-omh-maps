package com.openmobilehub.android.maps.plugin.mapbox.utlis

import com.mapbox.maps.extension.style.layers.properties.generated.LineJoin
import com.openmobilehub.android.maps.core.presentation.models.OmhJointType
import com.openmobilehub.android.maps.plugin.mapbox.utils.JoinTypeConverter
import org.junit.Assert
import org.junit.Test

class JoinTypeConverterTest {
    @Test
    fun `convertToLineJoin returns LineCap_BEVEL for OmhJointType_BEVEL`() {
        // Act
        val result = JoinTypeConverter.convertToLineJoin(OmhJointType.BEVEL)

        // Assert
        Assert.assertEquals(LineJoin.BEVEL, result)
    }

    @Test
    fun `convertToLineJoin returns LineCap_ROUND for OmhJointType_ROUND`() {
        // Act
        val result = JoinTypeConverter.convertToLineJoin(OmhJointType.ROUND)

        // Assert
        Assert.assertEquals(LineJoin.ROUND, result)
    }

    @Test
    fun `convertToLineJoin returns LineCap_MITER for OmhJointType_DEFAULT`() {
        // Act
        val result = JoinTypeConverter.convertToLineJoin(OmhJointType.DEFAULT)

        // Assert
        Assert.assertEquals(LineJoin.MITER, result)
    }

    @Test
    fun `convertToOmhJointType returns OmhJointType_BEVEL for LineCap_BEVEL`() {
        // Act
        val result = JoinTypeConverter.convertToOmhJointType(LineJoin.BEVEL)

        // Assert
        Assert.assertEquals(OmhJointType.BEVEL, result)
    }

    @Test
    fun `convertToOmhJointType returns OmhJointType_ROUND for LineCap_ROUND`() {
        // Act
        val result = JoinTypeConverter.convertToOmhJointType(LineJoin.BEVEL)

        // Assert
        Assert.assertEquals(OmhJointType.BEVEL, result)
    }

    @Test
    fun `convertToOmhJointType returns OmhJointType_DEFAULT for LineCap_MITER`() {
        // Act
        val result = JoinTypeConverter.convertToOmhJointType(LineJoin.BEVEL)

        // Assert
        Assert.assertEquals(OmhJointType.BEVEL, result)
    }
}
