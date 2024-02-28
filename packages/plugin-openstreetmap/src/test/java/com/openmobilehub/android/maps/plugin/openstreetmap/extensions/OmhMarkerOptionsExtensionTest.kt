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
import android.content.res.Resources
import android.graphics.drawable.Drawable
import com.openmobilehub.android.maps.core.presentation.models.OmhCoordinate
import com.openmobilehub.android.maps.core.presentation.models.OmhMarkerOptions
import com.openmobilehub.android.maps.core.utils.logging.UnsupportedFeatureLogger
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Test
import org.osmdroid.views.MapView
import org.osmdroid.views.MapViewRepository
import org.osmdroid.views.overlay.infowindow.BasicInfoWindow
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow

internal class OmhMarkerOptionsExtensionTest {
    private val omhCoordinate = OmhCoordinate(16.9, 166.0)
    private val omhMarkerOptionsWithIcon = OmhMarkerOptions().apply {
        position = omhCoordinate
        title = "Marker Title 1"
        draggable = true
        anchor = Pair(0.5f, 0.5f)
        alpha = 0.5f
        snippet = "Marker Snippet 1"
        isVisible = true
        isFlat = true
        rotation = 87.6f
        icon = mockk<Drawable>()
    }

    private val omhMarkerOptionsInvisible = OmhMarkerOptions().apply {
        position = omhCoordinate
        title = "Marker Title 1"
        isVisible = false
    }

    private val mapView = mockk<MapView>()
    private val mockLogger: UnsupportedFeatureLogger =
        mockk<UnsupportedFeatureLogger>(relaxed = true)
    private val context = mockk<Context>()
    private val repository = mockk<MapViewRepository>()
    private val defaultMarkerInfoWindow = mockk<MarkerInfoWindow>()

    init {
        every { context.resources } returns mockk<Resources>()
        every { repository.defaultMarkerIcon } returns mockk<Drawable>()
        every { defaultMarkerInfoWindow.isOpen } returns false
        every { repository.defaultMarkerInfoWindow } returns defaultMarkerInfoWindow
        every { repository.defaultPolygonInfoWindow } returns mockk<BasicInfoWindow>()
        every { repository.defaultPolylineInfoWindow } returns mockk<BasicInfoWindow>()
        every { mapView.context } returns context
        every { mapView.repository } returns repository
    }

    @Test
    fun `toMarkerOptions converts OmhMarkerOptions with icon to MarkerOptions`() {
        val markerOptions = omhMarkerOptionsWithIcon.toMarkerOptions(mapView, mockLogger)

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

        assertEquals(omhMarkerOptionsWithIcon.icon, markerOptions.icon)
    }

    @Test
    fun `toMarkerOptions converts OmhMarkerOptions with isVisible set to false to MarkerOptions`() {
        val markerOptions = omhMarkerOptionsInvisible.toMarkerOptions(mapView, mockLogger)

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
        }.toMarkerOptions(mapView, mockLogger)

        verify { mockLogger.logSetterNotSupported("backgroundColor") }
    }
}
