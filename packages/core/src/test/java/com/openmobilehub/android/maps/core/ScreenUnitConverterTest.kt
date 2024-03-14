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

package com.openmobilehub.android.maps.core

import android.content.Context
import android.util.DisplayMetrics
import com.openmobilehub.android.maps.core.utils.ScreenUnitConverter
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

internal class ScreenUnitConverterTest {

    private lateinit var context: Context
    val screenDensity = 2.0f

    @Before
    fun setup() {
        context = mockk<Context>()
        every { context.resources.displayMetrics } returns DisplayMetrics().apply {
            widthPixels = 1080
            heightPixels = 720
            density = screenDensity
        }
    }

    @Test
    fun testPxToDpConversion() {
        // Arrange
        val pxValue = 20.0f

        // Act
        val result = ScreenUnitConverter.pxToDp(pxValue, context)

        // Assert
        assertEquals(pxValue / screenDensity, result, 0.0f)
    }

    @Test
    fun testDpToPxConversion() {
        // Arrange
        val dpValue = 15.0f

        // Act
        val result = ScreenUnitConverter.dpToPx(dpValue, context)

        // Assert
        assertEquals(dpValue * screenDensity, result, 0.0f)
    }
}
