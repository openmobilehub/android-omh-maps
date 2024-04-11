package com.openmobilehub.android.maps.plugin.azuremaps.utils

import com.azure.android.maps.control.options.AnchorType
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters

@RunWith(Parameterized::class)
internal class AnchorConverterTest(
    private val data: Pair<Pair<Float, Float>, String>
) {

    @Test
    fun `genericConvertContinuousToAnchorType returns correct String from AnchorType for various continuous anchors`() {
        // Act
        val result = AnchorConverter.convertContinuousToDiscreteAnchorType(data.first)

        // Assert
        Assert.assertEquals(data.second, result)
    }

    companion object {
        @JvmStatic
        @Parameters
        fun data() = listOf(
            Pair(Pair(0.5f, 0.5f), AnchorType.CENTER),
            Pair(Pair(0.25f + 1e-4f, 0.75f - 1e-4f), AnchorType.CENTER),
            Pair(Pair(0.1f, 0.5f), AnchorType.LEFT),
            Pair(Pair(0.75f, 0.6f), AnchorType.RIGHT),
            Pair(Pair(0.6f, 0.15f), AnchorType.TOP),
            Pair(Pair(0.6f, 0.75f), AnchorType.BOTTOM),
            Pair(Pair(0.15f, 0.15f), AnchorType.TOP_LEFT),
            Pair(Pair(0.75f, 0.15f), AnchorType.TOP_RIGHT),
            Pair(Pair(0.15f, 0.75f), AnchorType.BOTTOM_LEFT),
            Pair(Pair(0.75f, 0.75f), AnchorType.BOTTOM_RIGHT),
        )
    }
}
