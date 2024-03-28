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

package com.openmobilehub.android.maps.plugin.googlemaps.presentation.extensions

import android.graphics.drawable.Drawable
import com.google.android.gms.maps.model.BitmapDescriptor
import com.openmobilehub.android.maps.core.presentation.models.Constants.ANCHOR_CENTER
import com.openmobilehub.android.maps.core.presentation.models.Constants.DEFAULT_ALPHA
import com.openmobilehub.android.maps.core.presentation.models.Constants.DEFAULT_ROTATION
import com.openmobilehub.android.maps.core.presentation.models.OmhCoordinate
import com.openmobilehub.android.maps.core.presentation.models.OmhMarkerOptions
import com.openmobilehub.android.maps.core.utils.logging.UnsupportedFeatureLogger
import com.openmobilehub.android.maps.plugin.googlemaps.extensions.toMarkerOptions
import com.openmobilehub.android.maps.plugin.googlemaps.utils.MarkerIconConverter
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

internal class OmhMarkerOptionsTest {
    private lateinit var mockLogger: UnsupportedFeatureLogger
    private lateinit var colorBitmapDescriptor: BitmapDescriptor

    private val omhCoordinate = OmhCoordinate(16.9, 166.0)
    private val omhMarkerOptionsWithIcon = OmhMarkerOptions().apply {
        position = omhCoordinate
        title = "Marker Title 1"
        draggable = true
        anchor = Pair(0.5f, 0.5f)
        alpha = 0.5f
        snippet = "Marker Snippet 1"
        isVisible = false
        isFlat = true
        rotation = 87.6f
        icon = mockk<Drawable>()
    }

    private val omhMarkerOptionsWithBackgroundColor = OmhMarkerOptions().apply {
        position = omhCoordinate
        title = "Marker Title 2"
        draggable = false
        anchor = Pair(0.2f, 0.73f)
        alpha = 0.98f
        snippet = "Marker Snippet 2"
        isVisible = true
        isFlat = false
        rotation = 324.6f
        backgroundColor = 123
        icon = null
    }

    @Before
    fun setUp() {
        mockLogger = mockk<UnsupportedFeatureLogger>(relaxed = true)
        colorBitmapDescriptor = mockk<BitmapDescriptor>(relaxed = true)

        mockkObject(MarkerIconConverter)
        every { MarkerIconConverter.convertColorToBitmapDescriptor(any()) } returns colorBitmapDescriptor
    }

    @Test
    fun `toMarkerOptions converts OmhMarkerOptions with icon to MarkerOptions`() {
        val iconBitmapDescriptor = mockk<BitmapDescriptor>()
        every { MarkerIconConverter.convertDrawableToBitmapDescriptor(any()) } returns iconBitmapDescriptor

        val markerOptions = omhMarkerOptionsWithIcon.toMarkerOptions(mockLogger)

        assertEquals(
            omhMarkerOptionsWithIcon.position.latitude,
            markerOptions.position.latitude,
            0.0
        )
        assertEquals(
            omhMarkerOptionsWithIcon.position.longitude,
            markerOptions.position.longitude,
            0.0
        )

        assertEquals(omhMarkerOptionsWithIcon.title, markerOptions.title)

        assertEquals(omhMarkerOptionsWithIcon.draggable, markerOptions.isDraggable)

        assertEquals(omhMarkerOptionsWithIcon.anchor.first, markerOptions.anchorU)
        assertEquals(omhMarkerOptionsWithIcon.anchor.second, markerOptions.anchorV)

        assertEquals(omhMarkerOptionsWithIcon.alpha, markerOptions.alpha)

        assertEquals(omhMarkerOptionsWithIcon.snippet, markerOptions.snippet)

        assertEquals(omhMarkerOptionsWithIcon.isVisible, markerOptions.isVisible)

        assertEquals(omhMarkerOptionsWithIcon.isFlat, markerOptions.isFlat)

        assertEquals(omhMarkerOptionsWithIcon.rotation, markerOptions.rotation)

        assertEquals(iconBitmapDescriptor, markerOptions.icon)
    }

    @Test
    fun `toMarkerOptions converts OmhMarkerOptions with backgroundColor to MarkerOptions`() {
        val markerOptions = omhMarkerOptionsWithBackgroundColor.toMarkerOptions(mockLogger)

        assertEquals(
            omhMarkerOptionsWithBackgroundColor.position.latitude,
            markerOptions.position.latitude,
            0.0
        )
        assertEquals(
            omhMarkerOptionsWithBackgroundColor.position.longitude,
            markerOptions.position.longitude,
            0.0
        )

        assertEquals(omhMarkerOptionsWithBackgroundColor.title, markerOptions.title)

        assertEquals(omhMarkerOptionsWithBackgroundColor.draggable, markerOptions.isDraggable)

        assertEquals(omhMarkerOptionsWithBackgroundColor.anchor.first, markerOptions.anchorU)
        assertEquals(omhMarkerOptionsWithBackgroundColor.anchor.second, markerOptions.anchorV)

        assertEquals(omhMarkerOptionsWithBackgroundColor.alpha, markerOptions.alpha)

        assertEquals(omhMarkerOptionsWithBackgroundColor.snippet, markerOptions.snippet)

        assertEquals(omhMarkerOptionsWithBackgroundColor.isVisible, markerOptions.isVisible)

        assertEquals(omhMarkerOptionsWithBackgroundColor.isFlat, markerOptions.isFlat)

        assertEquals(omhMarkerOptionsWithBackgroundColor.rotation, markerOptions.rotation)

        assertEquals(colorBitmapDescriptor, markerOptions.icon)
    }

    @Test
    fun `toMarkerOptions has proper default constructor arguments`() {
        val defaultOmhMarkerOptions = OmhMarkerOptions()

        assertEquals(defaultOmhMarkerOptions.draggable, false)
        assertEquals(defaultOmhMarkerOptions.anchor, Pair(ANCHOR_CENTER, ANCHOR_CENTER))
        assertEquals(defaultOmhMarkerOptions.alpha, DEFAULT_ALPHA)
        assertEquals(defaultOmhMarkerOptions.snippet, null)
        assertEquals(defaultOmhMarkerOptions.isVisible, true)
        assertEquals(defaultOmhMarkerOptions.isFlat, false)
        assertEquals(defaultOmhMarkerOptions.rotation, DEFAULT_ROTATION)
        assertEquals(defaultOmhMarkerOptions.backgroundColor, null)
        assertEquals(defaultOmhMarkerOptions.icon, null)
        assertEquals(defaultOmhMarkerOptions.clickable, true)
    }

    @Test
    fun `toMarkerOptions should return log setter not supported for backgroundColor property`() {
        OmhMarkerOptions().apply {
            backgroundColor = 0xFFFFFF
        }.toMarkerOptions(mockLogger)

        verify { mockLogger.logFeatureSetterPartiallySupported("backgroundColor", any()) }
    }
}
