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

package com.openmobilehub.android.maps.plugin.openstreetmap.extensions

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import com.openmobilehub.android.maps.core.presentation.models.Constants
import com.openmobilehub.android.maps.core.presentation.models.OmhCoordinate
import com.openmobilehub.android.maps.core.presentation.models.OmhMarkerOptions
import com.openmobilehub.android.maps.core.utils.DrawableConverter
import com.openmobilehub.android.maps.core.utils.logging.UnsupportedFeatureLogger
import com.openmobilehub.android.maps.plugin.openstreetmap.presentation.maps.OmhMapImpl
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Test
import org.osmdroid.views.MapView
import org.osmdroid.views.MapViewRepository
import org.osmdroid.views.overlay.infowindow.BasicInfoWindow
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow

internal class OmhMarkerOptionsExtensionTest {
    private val omhCoordinate = OmhCoordinate(16.9, 166.0)
    private var mockedIcon: Drawable = mockk<Drawable>()
    private val omhMarkerOptionsWithIcon: OmhMarkerOptions
    private val convertDrawableToBitmapMock = mockk<Bitmap>()

    private val omhMarkerOptionsInvisible = OmhMarkerOptions().apply {
        position = omhCoordinate
        title = "Marker Title 1"
        isVisible = false
    }

    private val mapView = mockk<MapView>(relaxed = true)
    private val omhMap = mockk<OmhMapImpl>(relaxed = true)
    private val mockLogger: UnsupportedFeatureLogger =
        mockk<UnsupportedFeatureLogger>(relaxed = true)
    private val context = mockk<Context>(relaxed = true)
    private val repository = mockk<MapViewRepository>(relaxed = true)
    private val defaultMarkerInfoWindow = mockk<MarkerInfoWindow>(relaxed = true)

    init {
        every { repository.defaultMarkerIcon } returns mockk<Drawable>()
        every { defaultMarkerInfoWindow.isOpen } returns false
        every { repository.defaultMarkerInfoWindow } returns defaultMarkerInfoWindow
        every { repository.defaultPolygonInfoWindow } returns mockk<BasicInfoWindow>()
        every { repository.defaultPolylineInfoWindow } returns mockk<BasicInfoWindow>()
        every { mapView.context } returns context
        every { mapView.repository } returns repository

        every { mockedIcon.intrinsicWidth } returns 50
        every { mockedIcon.intrinsicHeight } returns 50
        mockkObject(DrawableConverter)
        every { convertDrawableToBitmapMock.width } returns 80
        every { convertDrawableToBitmapMock.height } returns 80
        every { convertDrawableToBitmapMock.density } returns 1
        every { DrawableConverter.convertDrawableToBitmap(any()) } answers { convertDrawableToBitmapMock }

        omhMarkerOptionsWithIcon = OmhMarkerOptions().apply {
            position = omhCoordinate
            title = "Marker Title 1"
            draggable = true
            anchor = Pair(0.5f, 0.5f)
            alpha = 0.5f
            snippet = "Marker Snippet 1"
            isVisible = true
            isFlat = true
            rotation = 87.6f
            icon = mockedIcon
        }
    }

    @Test
    fun `toMarkerOptions converts OmhMarkerOptions with icon to MarkerOptions`() {
        val markerOptions = omhMarkerOptionsWithIcon.toMarkerOptions(omhMap, mapView, mockLogger)

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

        assertEquals(omhMarkerOptionsWithIcon.alpha, markerOptions.alpha)

        assertEquals(omhMarkerOptionsWithIcon.snippet, markerOptions.snippet)

        assertEquals(omhMarkerOptionsWithIcon.isFlat, markerOptions.isFlat)

        assertEquals(
            omhMarkerOptionsWithIcon.rotation,
            -markerOptions.rotation
        ) // rotation is counter-clockwise in OSM

        val res = context.resources
        verify {
            DrawableConverter.convertDrawableToBitmap(mockedIcon)
            BitmapDrawable(res, convertDrawableToBitmapMock)
        }

        assert(markerOptions.icon is BitmapDrawable)
    }

    @Test
    fun `toMarkerOptions converts OmhMarkerOptions with isVisible set to false to MarkerOptions`() {
        val markerOptions = omhMarkerOptionsInvisible.toMarkerOptions(omhMap, mapView, mockLogger)

        assertEquals(
            omhMarkerOptionsInvisible.position.latitude,
            markerOptions.position.latitude,
            0.0
        )
        assertEquals(
            omhMarkerOptionsInvisible.position.longitude,
            markerOptions.position.longitude,
            0.0
        )

        assertEquals(omhMarkerOptionsInvisible.title, markerOptions.title)

        // visible set to false results in alpha = 0f
        assertEquals(0f, markerOptions.alpha)
    }

    @Test
    fun `toMarkerOptions should return log setter not supported for backgroundColor property`() {
        OmhMarkerOptions().apply {
            backgroundColor = 0xFFFFFF
        }.toMarkerOptions(omhMap, mapView, mockLogger)

        verify { mockLogger.logSetterNotSupported("backgroundColor") }
    }

    @Test
    fun `OmhMarkerOptions has proper default constructor arguments`() {
        val defaultOmhMarkerOptions = OmhMarkerOptions()

        assertEquals(defaultOmhMarkerOptions.draggable, false)
        assertEquals(
            defaultOmhMarkerOptions.anchor,
            Pair(
                Constants.ANCHOR_CENTER,
                Constants.ANCHOR_CENTER
            )
        )
        assertEquals(
            defaultOmhMarkerOptions.infoWindowAnchor,
            Pair(
                Constants.ANCHOR_CENTER,
                Constants.ANCHOR_TOP
            )
        )
        assertEquals(defaultOmhMarkerOptions.alpha, Constants.DEFAULT_ALPHA)
        assertEquals(defaultOmhMarkerOptions.snippet, null)
        assertEquals(defaultOmhMarkerOptions.isVisible, true)
        assertEquals(defaultOmhMarkerOptions.isFlat, false)
        assertEquals(defaultOmhMarkerOptions.rotation, Constants.DEFAULT_ROTATION)
        assertEquals(defaultOmhMarkerOptions.backgroundColor, null)
        assertEquals(defaultOmhMarkerOptions.icon, null)
        assertEquals(defaultOmhMarkerOptions.clickable, true)
    }

    @Test
    fun `toMarkerOptions should log zIndex not supported when using zIndex`() {
        // Act
        OmhMarkerOptions().apply {
            zIndex = 1.0f
        }.toMarkerOptions(omhMap, mapView, mockLogger)

        // Assert
        verify { mockLogger.logNotSupported("zIndex") }
    }
}
