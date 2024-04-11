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

import android.graphics.Bitmap
import com.azure.android.maps.control.options.LineCap
import com.openmobilehub.android.maps.core.presentation.models.OmhButtCap
import com.openmobilehub.android.maps.core.presentation.models.OmhCustomCap
import com.openmobilehub.android.maps.core.presentation.models.OmhRoundCap
import com.openmobilehub.android.maps.core.presentation.models.OmhSquareCap
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Test

internal class CapConverterTest {

    @Test
    fun `convertToAzureMapsLineCap returns LineCap_Round for OmhRoundCap`() {
        // Act
        val result = CapConverter.convertToAzureMapsLineCap(OmhRoundCap())

        // Assert
        assertEquals(LineCap.ROUND, result)
    }

    @Test
    fun `convertToAzureMapsLineCap returns LineCap_Square for OmhSquareCap`() {
        // Act
        val result = CapConverter.convertToAzureMapsLineCap(OmhSquareCap())

        // Assert
        assertEquals(LineCap.SQUARE, result)
    }

    @Test
    fun `convertToAzureMapsLineCap returns LineCap_Butt for OmhButtCap`() {
        // Act
        val result = CapConverter.convertToAzureMapsLineCap(OmhButtCap())

        // Assert
        assertEquals(LineCap.BUTT, result)
    }

    @Test
    fun `convertToAzureMapsLineCap returns LineCap_Round as default`() {
        // Arrange
        val bitmap = mockk<Bitmap>()
        val omhCustomCap = OmhCustomCap(bitmap, refWidth = 10f)

        // Act
        val result = CapConverter.convertToAzureMapsLineCap(omhCustomCap)

        // Assert
        assertEquals(LineCap.ROUND, result)
    }
}
