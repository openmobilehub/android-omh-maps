package com.openmobilehub.android.maps.plugin.mapbox.utils

import com.mapbox.maps.extension.style.layers.properties.generated.IconAnchor
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
    fun `genericConvertContinuousToIconAnchor returns correct String from IconAnchor for various continuous anchors`() {
        // Act
        val result = AnchorConverter.convertContinuousToDiscreteIconAnchor(data.first)

        // Assert
        Assert.assertEquals(data.second, result)
    }

    companion object {
        @JvmStatic
        @Parameters
        fun data() = listOf(
            Pair(Pair(0.5f, 0.5f), IconAnchor.CENTER),
            Pair(Pair(0.25f + 1e-4f, 0.75f - 1e-4f), IconAnchor.CENTER),
            Pair(Pair(0.1f, 0.5f), IconAnchor.LEFT),
            Pair(Pair(0.75f, 0.6f), IconAnchor.RIGHT),
            Pair(Pair(0.6f, 0.15f), IconAnchor.TOP),
            Pair(Pair(0.6f, 0.75f), IconAnchor.BOTTOM),
            Pair(Pair(0.15f, 0.15f), IconAnchor.TOP_LEFT),
            Pair(Pair(0.75f, 0.15f), IconAnchor.TOP_RIGHT),
            Pair(Pair(0.15f, 0.75f), IconAnchor.BOTTOM_LEFT),
            Pair(Pair(0.75f, 0.75f), IconAnchor.BOTTOM_RIGHT),
        )
    }
}
