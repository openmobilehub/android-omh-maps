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

package com.openmobilehub.android.maps.plugin.googlemaps.utils

import com.google.android.gms.maps.model.Dash
import com.google.android.gms.maps.model.Dot
import com.google.android.gms.maps.model.Gap
import com.google.android.gms.maps.model.PatternItem
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhPatternItem
import com.openmobilehub.android.maps.core.presentation.models.OmhDash
import com.openmobilehub.android.maps.core.presentation.models.OmhDot
import com.openmobilehub.android.maps.core.presentation.models.OmhGap
import io.mockk.mockk
import org.junit.Assert
import org.junit.Test

class PatternConverterTest {

    @Test
    fun `convertToPatternItem returns Dot for OmhDot`() {
        // Arrange
        val omhDot = OmhDot()

        // Act
        val result = PatternConverter.convertToPatternItem(omhDot)

        // Assert
        Assert.assertNotNull(result)
        Assert.assertTrue(result is Dot)
    }

    @Test
    fun `convertToPatternItem returns Dash for OmhDash`() {
        // Arrange
        val omhDash = OmhDash(length = 10f)

        // Act
        val result = PatternConverter.convertToPatternItem(omhDash)

        // Assert
        Assert.assertNotNull(result)
        Assert.assertTrue(result is Dash)
        Assert.assertEquals(10f, (result as Dash).length)
    }

    @Test
    fun `convertToPatternItem returns Gap for OmhGap`() {
        // Arrange
        val omhGap = OmhGap(length = 20f)

        // Act
        val result = PatternConverter.convertToPatternItem(omhGap)

        // Assert
        Assert.assertNotNull(result)
        Assert.assertTrue(result is Gap)
        Assert.assertEquals(20f, (result as Gap).length)
    }

    @Test
    fun `convertToPatternItem returns null for unknown OmhPatternItem type`() {
        // Arrange
        val unknownPatternItem = mockk<OmhPatternItem>()

        // Act
        val result = PatternConverter.convertToPatternItem(unknownPatternItem)

        // Assert
        Assert.assertNull(result)
    }

    @Test
    fun `convertToOmhPatternItem returns OmhDot for Dot`() {
        // Act
        val result = PatternConverter.convertToOmhPatternItem(Dot())

        // Assert
        Assert.assertNotNull(result)
        Assert.assertTrue(result is OmhDot)
    }

    @Test
    fun `convertToOmhPatternItem returns OmhDash for Dash`() {
        // Arrange
        val dash = Dash(30f)

        // Act
        val result = PatternConverter.convertToOmhPatternItem(dash)

        // Assert
        Assert.assertNotNull(result)
        Assert.assertTrue(result is OmhDash)
        Assert.assertEquals(30f, (result as OmhDash).length)
    }

    @Test
    fun `convertToOmhPatternItem returns OmhGap for Gap`() {
        // Arrange
        val gap = Gap(40f)

        // Act
        val result = PatternConverter.convertToOmhPatternItem(gap)

        // Assert
        Assert.assertNotNull(result)
        Assert.assertTrue(result is OmhGap)
        Assert.assertEquals(40f, (result as OmhGap).length)
    }

    @Test
    fun `convertToOmhPatternItem returns null for unknown PatternItem type`() {
        // Arrange
        val unknownPatternItem = mockk<PatternItem>()

        // Act
        val result = PatternConverter.convertToOmhPatternItem(unknownPatternItem)

        // Assert
        Assert.assertNull(result)
    }
}
