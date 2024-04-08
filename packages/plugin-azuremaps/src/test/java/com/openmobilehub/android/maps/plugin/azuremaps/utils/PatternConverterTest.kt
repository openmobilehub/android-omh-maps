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

package com.openmobilehub.android.maps.plugin.azuremaps.utils

import com.openmobilehub.android.maps.core.presentation.models.OmhDash
import com.openmobilehub.android.maps.core.presentation.models.OmhDot
import com.openmobilehub.android.maps.core.presentation.models.OmhGap
import org.junit.Assert
import org.junit.Test

internal class PatternConverterTest {

    private val patternValues = listOf(5.0f, 10.0f, 2.0f, 3.0f)

    @Test
    fun `convertToAzureMapsPattern converts OmhPatternItems to list of floats of alternating dashes and gaps`() {
        // Arrange
        val omhPatternItems =
            listOf(OmhDash(patternValues[0]), OmhGap(patternValues[1]), OmhDash(patternValues[2]))

        // Act
        val result = PatternConverter.convertToAzureMapsPattern(omhPatternItems)

        // Assert
        Assert.assertEquals(omhPatternItems.size, result.size)
        Assert.assertEquals(patternValues[0], result[0])
        Assert.assertEquals(patternValues[1], result[1])
        Assert.assertEquals(patternValues[2], result[2])
    }

    @Test
    fun `convertToAzureMapsPattern adds empty items when same consecutive items types are provided`() {
        // Arrange
        val omhPatternItems = listOf(
            OmhDash(patternValues[0]),
            OmhDash(patternValues[1]),
            OmhGap(patternValues[2]),
            OmhGap(patternValues[3])
        )

        // Act
        val result = PatternConverter.convertToAzureMapsPattern(omhPatternItems)

        // Assert
        Assert.assertEquals(omhPatternItems.size + 2, result.size)
        Assert.assertEquals(patternValues[0], result[0])
        Assert.assertEquals(0.0f, result[1]) // inserted gap item
        Assert.assertEquals(patternValues[1], result[2])
        Assert.assertEquals(0.0f, result[3]) // inserted dash item
        Assert.assertEquals(patternValues[2], result[4])
        Assert.assertEquals(patternValues[3], result[5])
    }

    @Test
    fun `convertToAzureMapsPattern converts unsupported pattern items to 0`() {
        // Arrange
        val omhPatternItems = listOf(
            OmhDash(patternValues[0]),
            OmhGap(patternValues[1]),
            OmhDot(),
            OmhGap(patternValues[2])
        )

        // Act
        val result = PatternConverter.convertToAzureMapsPattern(omhPatternItems)

        // Assert
        Assert.assertEquals(omhPatternItems.size, result.size)
        Assert.assertEquals(patternValues[0], result[0])
        Assert.assertEquals(patternValues[1], result[1])
        Assert.assertEquals(0.0f, result[2]) // skipped unsupported items
        Assert.assertEquals(patternValues[2], result[3])
    }

    @Test
    fun `convertToAzureMapsPattern converts unsupported pattern items to 0 and adds empty items`() {
        // Arrange
        val omhPatternItems = listOf(OmhDot(), OmhDash(patternValues[0]), OmhDot())

        // Act
        val result = PatternConverter.convertToAzureMapsPattern(omhPatternItems)

        // Assert
        Assert.assertEquals(omhPatternItems.size + 1, result.size)
        Assert.assertEquals(0.0f, result[0]) // skipped unsupported items
        Assert.assertEquals(0.0f, result[1]) // inserted gap item
        Assert.assertEquals(patternValues[0], result[2]) // dash
        Assert.assertEquals(0.0f, result[3]) // skipped unsupported items
    }
}
