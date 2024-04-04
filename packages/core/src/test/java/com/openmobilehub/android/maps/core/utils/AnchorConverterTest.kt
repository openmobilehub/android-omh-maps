/*
 * Copyright 2023 Open Mobile Hub
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.openmobilehub.android.maps.core.utils

import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters

@RunWith(Parameterized::class)
internal class AnchorConverterTest(
    private val data: Pair<Pair<Float, Float>, DiscreteAnchor>
) {

    @Test
    fun `genericConvertContinuousToDiscreteAnchor returns correct DiscreteAnchor for various continuous anchors`() {
        // Act
        val result = AnchorConverter.genericConvertContinuousToDiscreteAnchor(data.first)

        // Assert
        Assert.assertEquals(data.second, result)
    }

    companion object {
        @JvmStatic
        @Parameters
        fun data() = listOf(
            Pair(Pair(0.5f, 0.5f), DiscreteAnchor.CENTER),
            Pair(Pair(0.7f, 0.3f), DiscreteAnchor.CENTER),
            Pair(Pair(0.3f, 0.7f), DiscreteAnchor.CENTER),
            Pair(Pair(0.75f - 1e-4f, 0.25f + 1e-4f), DiscreteAnchor.CENTER),
            Pair(Pair(0.25f + 1e-4f, 0.75f - 1e-4f), DiscreteAnchor.CENTER),
            Pair(Pair(0.1f, 0.5f), DiscreteAnchor.LEFT),
            Pair(Pair(0.15f, 0.6f), DiscreteAnchor.LEFT),
            Pair(Pair(0.9f, 0.5f), DiscreteAnchor.RIGHT),
            Pair(Pair(0.75f, 0.6f), DiscreteAnchor.RIGHT),
            Pair(Pair(0.5f, 0.1f), DiscreteAnchor.TOP),
            Pair(Pair(0.6f, 0.15f), DiscreteAnchor.TOP),
            Pair(Pair(0.5f, 0.9f), DiscreteAnchor.BOTTOM),
            Pair(Pair(0.6f, 0.75f), DiscreteAnchor.BOTTOM),
            Pair(Pair(0.1f, 0.1f), DiscreteAnchor.TOP_LEFT),
            Pair(Pair(0.15f, 0.15f), DiscreteAnchor.TOP_LEFT),
            Pair(Pair(0.9f, 0.1f), DiscreteAnchor.TOP_RIGHT),
            Pair(Pair(0.75f, 0.15f), DiscreteAnchor.TOP_RIGHT),
            Pair(Pair(0.1f, 0.9f), DiscreteAnchor.BOTTOM_LEFT),
            Pair(Pair(0.15f, 0.75f), DiscreteAnchor.BOTTOM_LEFT),
            Pair(Pair(0.9f, 0.9f), DiscreteAnchor.BOTTOM_RIGHT),
            Pair(Pair(0.75f, 0.75f), DiscreteAnchor.BOTTOM_RIGHT),
        )
    }
}
