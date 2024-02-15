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

import com.openmobilehub.android.maps.core.presentation.models.OmhCoordinate
import com.openmobilehub.android.maps.core.presentation.models.OmhMarkerOptions
import com.openmobilehub.android.maps.plugin.googlemaps.extensions.toMarkerOptions
import org.junit.Assert.assertEquals
import org.junit.Test

internal class OmhMarkerOptionsTest {
    private val omhCoordinate = OmhCoordinate(16.9, 166.0)
    private val omhMarkerOptions = OmhMarkerOptions().apply {
        position = omhCoordinate
        title = "Marker Title"
        isDraggable = true
        anchor = Pair(0.5f, 0.5f)
        alpha = 0.5f
        snippet = "Marker Snippet"
        isVisible = false
        isFlat = true
        rotation = 87.6f
    }

    @Test
    fun `toMarkerOptions converts OmhMarkerOptions to MarkerOptions`() {
        val markerOptions = omhMarkerOptions.toMarkerOptions()

        assertEquals(markerOptions.position.latitude, omhMarkerOptions.position.latitude, 0.0)
        assertEquals(markerOptions.position.longitude, omhMarkerOptions.position.longitude, 0.0)

        assertEquals(markerOptions.title, omhMarkerOptions.title)

        assertEquals(markerOptions.isDraggable, omhMarkerOptions.isDraggable)

        assertEquals(markerOptions.anchorU, omhMarkerOptions.anchor.first)
        assertEquals(markerOptions.anchorV, omhMarkerOptions.anchor.second)

        assertEquals(markerOptions.alpha, omhMarkerOptions.alpha)

        assertEquals(markerOptions.snippet, omhMarkerOptions.snippet)

        assertEquals(markerOptions.isVisible, omhMarkerOptions.isVisible)

        assertEquals(markerOptions.isFlat, omhMarkerOptions.isFlat)

        assertEquals(markerOptions.rotation, omhMarkerOptions.rotation)

        assertEquals(markerOptions.icon, null)
    }
}
