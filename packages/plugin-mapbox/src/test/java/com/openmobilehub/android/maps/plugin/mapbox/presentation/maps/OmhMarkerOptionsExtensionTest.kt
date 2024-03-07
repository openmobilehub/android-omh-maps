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

package com.openmobilehub.android.maps.plugin.mapbox.presentation.maps

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.core.content.res.ResourcesCompat
import com.mapbox.bindgen.Expected
import com.mapbox.bindgen.None
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.layers.Layer
import com.mapbox.maps.extension.style.layers.addLayer
import com.mapbox.maps.extension.style.layers.generated.SymbolLayer
import com.mapbox.maps.extension.style.sources.addSource
import com.mapbox.maps.extension.style.sources.generated.GeoJsonSource
import com.openmobilehub.android.maps.core.presentation.models.OmhCoordinate
import com.openmobilehub.android.maps.core.presentation.models.OmhMarkerOptions
import com.openmobilehub.android.maps.core.utils.DrawableConverter
import com.openmobilehub.android.maps.plugin.mapbox.R
import com.openmobilehub.android.maps.plugin.mapbox.extensions.addOmhMarker
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters

@RunWith(Parameterized::class)
internal class OmhMarkerOptionsExtensionTest(
    private val data: OmhMarkerOptions
) {
    private val mapView = mockk<MapView>()
    private val context = mockk<Context>()
    private val safeStyle = mockk<Style>(relaxed = true)
    private val defaultMarkerIconDrawable = mockk<Drawable>()
    private val convertDrawableToBitmapMock = mockk<Bitmap>()

    companion object {
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

        @JvmStatic
        @Parameters
        internal fun data() = listOf(
            omhMarkerOptionsInvisible,
            omhMarkerOptionsWithIcon
        )
    }

    @Before
    fun setup() {
        every { context.resources } returns mockk<Resources>()
        every { mapView.context } returns context
        mockkStatic(ResourcesCompat::class)
        mockkObject(DrawableConverter)
        every {
            ResourcesCompat.getDrawable(
                context.resources,
                any<Int>(),
                any<Resources.Theme>()
            )
        } answers { defaultMarkerIconDrawable }
        every { DrawableConverter.convertDrawableToBitmap(any()) } answers { convertDrawableToBitmapMock }
        mockkStatic(Style::addSource)
        mockkStatic(Style::addLayer)
        every { safeStyle.addSource(any<GeoJsonSource>()) } returns Unit
        every { safeStyle.addLayer(any<SymbolLayer>()) } returns Unit
        // mock for safeStyle.addImage return type to indicate a success
        val mockAddImageResult = mockk<Expected<String, None>>(relaxed = true)
        every { mockAddImageResult.error } returns null
        every { safeStyle.addImage(any(), any<Bitmap>(), any()) } returns mockAddImageResult
    }

    @Test
    @SuppressWarnings("LongMethod")
    fun `OmhMarkerOptions addOmhMarker creates an OmhMarker and all properties are properly handled`() {
        var (omhMarker, geoJsonSource, symbolLayer) = data.addOmhMarker(mapView)

        // we will check marker properties both before map style is loaded (to check marker property buffering validity)
        // and then after the map style is loaded (to check if the buffered properties are applied correctly)
        fun assertMarkerProperties() {
            assertEquals(
                data.position.latitude,
                omhMarker.getPosition().latitude,
                0.0
            )
            assertEquals(
                data.position.longitude,
                omhMarker.getPosition().longitude,
                0.0
            )
            assertEquals(
                omhMarker.getGeoJsonSourceID(),
                geoJsonSource.sourceId
            )

            assertEquals(data.title, omhMarker.getTitle())

            assertEquals(data.draggable, omhMarker.getDraggable())

            assertEquals(data.alpha, omhMarker.getAlpha())

            assertEquals(data.snippet, omhMarker.getSnippet())

            assertEquals(data.isFlat, omhMarker.getIsFlat())

            assertEquals(
                data.rotation,
                omhMarker.getRotation()
            )
        }

        fun verifyIconLoaderProcessing(icon: Drawable?, bMarkerBufferedMode: Boolean) {
            if (bMarkerBufferedMode) {
                // check if the icon is buffered
                assertEquals(icon, omhMarker.bufferedIcon)
            } else {
                if (icon === null) {
                    // check if valid default icon was loaded
                    verify {
                        ResourcesCompat.getDrawable(
                            context.resources,
                            R.drawable.marker_pin,
                            any(),
                        )
                    }
                } else {
                    // checked if for real the custom icon has been processed for loading
                    verify {
                        DrawableConverter.convertDrawableToBitmap(icon)
                    }
                }
            }
        }

        // we will also check if we can apply setters when still in 'buffered' mode (before map style is loaded)
        val someOtherMockedDrawable = mockk<Drawable>(relaxed = true)
        val otherIconThanFromOptions =
            if (data.icon === null) someOtherMockedDrawable else data.icon
        omhMarker.setIcon(otherIconThanFromOptions)
        omhMarker.setAlpha(5.5f)
        omhMarker.setIsFlat(!data.isFlat)
        omhMarker.setIsVisible(!data.isVisible)
        omhMarker.setRotation(58.5f)

        // assert
        verifyIconLoaderProcessing(otherIconThanFromOptions, true)
        assertEquals(5.5f, omhMarker.getAlpha())
        assertEquals(!data.isFlat, omhMarker.getIsFlat())
        assertEquals(!data.isVisible, omhMarker.getIsVisible())
        assertEquals(58.5f, omhMarker.getRotation())

        // revert our temporary changes for the tests to come to pass by comparing to the original options
        omhMarker.setIcon(data.icon)
        omhMarker.setAlpha(data.alpha)
        omhMarker.setIsFlat(data.isFlat)
        omhMarker.setIsVisible(data.isVisible)
        omhMarker.setRotation(data.rotation)

        // finally we mock that the map style has been loaded
        omhMarker.applyBufferedProperties(safeStyle)

        // add a new marker, this time mocking the SymbolLayer to verify setters
        mockkStatic("com.mapbox.maps.extension.style.layers.generated.SymbolLayerKt")
        every {
            com.mapbox.maps.extension.style.layers.generated.symbolLayer(
                any(),
                any(),
                any()
            )
        } answers {
            val mock = mockk<SymbolLayer>(relaxed = true)
            thirdArg<(Layer) -> Unit>().invoke(mock)
            mock
        }
        val newMarkerPack = data.addOmhMarker(mapView)
        omhMarker = newMarkerPack.first
        geoJsonSource = newMarkerPack.second
        symbolLayer = newMarkerPack.third
        // mock (on the new OmhMarker instance) that the map style had been loaded
        omhMarker.applyBufferedProperties(safeStyle)

        // here we check if marker properties are valid & set properly on the layer after map style is loaded
        verify {
            symbolLayer.iconIgnorePlacement(true) // defaults from OmhMarkerExtensions
            symbolLayer.iconAllowOverlap(true) // defaults from OmhMarkerExtensions
            symbolLayer.iconImage(omhMarker.getIconID(data.icon !== null))
            symbolLayer.iconOpacity(data.alpha.toDouble())
            symbolLayer.iconPitchAlignment(
                OmhMarkerImpl.getIconPitchAlignment(data.isFlat)
            )
            symbolLayer.iconRotationAlignment(
                OmhMarkerImpl.getIconRotationAlignment(data.isFlat)
            )
            symbolLayer.visibility(
                OmhMarkerImpl.getIconVisibility(data.isVisible)
            )
            symbolLayer.iconRotate(data.rotation.toDouble())
        }

        // ensure proper assets have been loaded
        verifyIconLoaderProcessing(data.icon, false)

        // ensure both source & symbol layers were added to map via the style
        val capturedSource = slot<GeoJsonSource>()
        val capturedLayer = slot<SymbolLayer>()
        every { safeStyle.addSource(capture(capturedSource)) } answers {}
        every { safeStyle.addLayer(capture(capturedLayer)) } answers {}

        verify {
            // ensure that the previous icon (from the in-between scenario) has been
            // deleted from the layer to free allocated memory
            safeStyle.removeStyleImage(omhMarker.getIconID(data.icon === null))
            // ensure that the new icon has been added to the layer
            safeStyle.addImage(
                omhMarker.getIconID(data.icon !== null),
                convertDrawableToBitmapMock,
                data.icon === null // ensure SDF coloring is only enabled for the default icon
            )
        }
    }
}
