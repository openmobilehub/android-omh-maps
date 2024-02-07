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

import android.graphics.Bitmap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.ButtCap
import com.google.android.gms.maps.model.CustomCap
import com.google.android.gms.maps.model.RoundCap
import com.google.android.gms.maps.model.SquareCap
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhCap
import com.openmobilehub.android.maps.core.presentation.models.OmhButtCap
import com.openmobilehub.android.maps.core.presentation.models.OmhCustomCap
import com.openmobilehub.android.maps.core.presentation.models.OmhRoundCap
import com.openmobilehub.android.maps.core.presentation.models.OmhSquareCap
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class CapConverterTest {

    @Before
    fun setUp() {
        // Mock static methods if needed, e.g., BitmapDescriptorFactory
        mockkStatic(BitmapDescriptorFactory::class)
    }

    @Test
    fun `convertToCap returns RoundCap for OmhRoundCap`() {
        // Arrange
        val omhRoundCap = OmhRoundCap()

        // Act
        val result = CapConverter.convertToCap(omhRoundCap)

        // Assert
        Assert.assertNotNull(result)
        Assert.assertTrue(result is RoundCap)
    }

    @Test
    fun `convertToCap returns SquareCap for OmhSquareCap`() {
        // Arrange
        val omhSquareCap = OmhSquareCap()

        // Act
        val result = CapConverter.convertToCap(omhSquareCap)

        // Assert
        Assert.assertNotNull(result)
        Assert.assertTrue(result is SquareCap)
    }

    @Test
    fun `convertToCap returns ButtCap for OmhButtCap`() {
        // Arrange
        val omhButtCap = OmhButtCap()

        // Act
        val result = CapConverter.convertToCap(omhButtCap)

        // Assert
        Assert.assertNotNull(result)
        Assert.assertTrue(result is ButtCap)
    }

    @Test
    fun `convertToCap returns CustomCap for OmhCustomCap`() {
        // Arrange
        val bitmap = mockk<Bitmap>()
        val bitmapDescriptor = mockk<BitmapDescriptor>()
        every { BitmapDescriptorFactory.fromBitmap(bitmap) } returns bitmapDescriptor

        val omhCustomCap = OmhCustomCap(bitmap, refWidth = 10f)

        // Act
        val result = CapConverter.convertToCap(omhCustomCap)

        // Assert
        Assert.assertNotNull(result)
        Assert.assertTrue(result is CustomCap)
        Assert.assertEquals(10f, (result as CustomCap).refWidth)
    }

    @Test
    fun `convertToCap returns CustomCap without refWidth for OmhCustomCap with null refWidth`() {
        // Arrange
        val bitmap = mockk<Bitmap>()
        val bitmapDescriptor = mockk<BitmapDescriptor>()
        val googleMapsDefaultRefWidth = 10f

        every { BitmapDescriptorFactory.fromBitmap(bitmap) } returns bitmapDescriptor

        val omhCustomCap = OmhCustomCap(bitmap, refWidth = null)

        // Act
        val result = CapConverter.convertToCap(omhCustomCap)

        // Assert
        Assert.assertNotNull(result)
        Assert.assertTrue(result is CustomCap)
        Assert.assertEquals(googleMapsDefaultRefWidth, (result as CustomCap).refWidth)
    }

    @Test
    fun `convertToCap returns null for unknown OmhCap type`() {
        // Arrange
        val unknownCap = mockk<OmhCap>()

        // Act
        val result = CapConverter.convertToCap(unknownCap)

        // Assert
        Assert.assertNull(result)
    }
}
