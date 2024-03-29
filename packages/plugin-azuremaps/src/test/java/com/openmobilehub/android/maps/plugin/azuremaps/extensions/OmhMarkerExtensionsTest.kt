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

package com.openmobilehub.android.maps.plugin.azuremaps.extensions

import android.graphics.drawable.Drawable
import com.azure.android.maps.control.options.AnchorType
import com.azure.android.maps.control.options.IconRotationAlignment
import com.openmobilehub.android.maps.core.presentation.models.OmhCoordinate
import com.openmobilehub.android.maps.core.presentation.models.OmhMarkerOptions
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Test

internal class OmhMarkerExtensionsTest {

    private val omhCoordinate = OmhCoordinate(16.9, 166.0)

    private val defaultOmhMarkerOptions = OmhMarkerOptions()

    private val customOmhMarkerOptionsWithIcon = OmhMarkerOptions().apply {
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
        infoWindowAnchor = Pair(0.4f, 0.3f)
    }

    private val omhMarkerOptionsInvisible = OmhMarkerOptions().apply {
        position = omhCoordinate
        title = "Marker Title 1"
        isVisible = false
    }

    @Test
    @SuppressWarnings("LongMethod")
    fun `OmhMarkerOptions with default values toSymbolLayerOptionsList creates a valid list of OmhMarker properties`() {
        for (option in defaultOmhMarkerOptions.toSymbolLayerOptionsList()) {
            assertEquals(
                when (option.name) {
                    "iconSize" -> 1f
                    "iconIgnorePlacement" -> true
                    "iconAllowOverlap" -> true
                    "iconAnchor" -> AnchorType.CENTER
                    "iconOpacity" -> defaultOmhMarkerOptions.alpha
                    "iconRotation" -> 0f
                    "iconRotationAlignment" -> IconRotationAlignment.VIEWPORT
                    else -> null
                },
                option.value
            )
        }
    }

    @Test
    @SuppressWarnings("LongMethod")
    fun `OmhMarkerOptions with custom values toSymbolLayerOptionsList creates a valid list of OmhMarker properties`() {
        for (option in customOmhMarkerOptionsWithIcon.toSymbolLayerOptionsList()) {
            assertEquals(
                when (option.name) {
                    "iconSize" -> 1f
                    "iconIgnorePlacement" -> true
                    "iconAllowOverlap" -> true
                    "iconAnchor" -> AnchorType.CENTER
                    "iconOpacity" -> customOmhMarkerOptionsWithIcon.alpha
                    "iconRotation" -> 87.6f
                    "iconRotationAlignment" -> IconRotationAlignment.MAP
                    else -> null
                },
                option.value
            )
        }
    }

    @Test
    @SuppressWarnings("LongMethod")
    fun `invisible OmhMarkerOptions toSymbolLayerOptionsList creates a valid list of OmhMarker properties`() {
        for (option in omhMarkerOptionsInvisible.toSymbolLayerOptionsList()) {
            assertEquals(
                when (option.name) {
                    "iconSize" -> 1f
                    "iconIgnorePlacement" -> true
                    "iconAllowOverlap" -> true
                    "iconAnchor" -> AnchorType.CENTER
                    "iconOpacity" -> 0f
                    "iconRotation" -> 0f
                    "iconRotationAlignment" -> IconRotationAlignment.VIEWPORT
                    else -> null
                },
                option.value
            )
        }
    }
}
