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
import android.graphics.Color
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.StyleSpan
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhStyleSpan
import com.openmobilehub.android.maps.core.presentation.models.OmhStyleSpanGradient
import com.openmobilehub.android.maps.core.presentation.models.OmhStyleSpanMonochromatic
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class SpanConverterTest {

    @Before
    fun setUp() {
        mockkStatic(BitmapDescriptorFactory::class)
    }

    @Test
    fun `convertToStyleSpan returns StyleSpan for OmhStyleSpanMonochromatic`() {
        // Arrange
        val bitmap = mockk<Bitmap>()
        val bitmapDescriptor = mockk<BitmapDescriptor>()
        every { BitmapDescriptorFactory.fromBitmap(bitmap) } returns bitmapDescriptor

        val omhStyleSpanMonochromatic = OmhStyleSpanMonochromatic(
            color = Color.RED,
            stamp = bitmap,
            segments = 3.0
        )

        // Act
        val result = SpanConverter.convertToStyleSpan(omhStyleSpanMonochromatic)

        // Assert
        Assert.assertTrue(result is StyleSpan)
        Assert.assertEquals(3.0, result?.segments)
    }

    @Test
    fun `convertToStyleSpan returns StyleSpan for OmhStyleSpanGradient`() {
        // Arrange
        val bitmap = mockk<Bitmap>()
        val bitmapDescriptor = mockk<BitmapDescriptor>()
        every { BitmapDescriptorFactory.fromBitmap(bitmap) } returns bitmapDescriptor

        val omhStyleSpanGradient = OmhStyleSpanGradient(
            fromColor = Color.RED,
            toColor = Color.BLUE,
            stamp = bitmap,
            segments = 3.0
        )

        // Act
        val result = SpanConverter.convertToStyleSpan(omhStyleSpanGradient)

        // Assert
        Assert.assertTrue(result is StyleSpan)
        Assert.assertEquals(3.0, result?.segments)
    }

    @Test
    fun `convertToStyleSpan returns null for unknown OmhStyleSpan type`() {
        // Arrange
        val unknownStyleSpan = mockk<OmhStyleSpan>()

        // Act
        val result = SpanConverter.convertToStyleSpan(unknownStyleSpan)

        // Assert
        Assert.assertNull(result)
    }
}
