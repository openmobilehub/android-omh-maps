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

package com.openmobilehub.android.maps.plugin.mapbox.extensions

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.DisplayMetrics
import androidx.core.content.res.ResourcesCompat
import com.mapbox.bindgen.Expected
import com.mapbox.bindgen.None
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.layers.Layer
import com.mapbox.maps.extension.style.layers.addLayer
import com.mapbox.maps.extension.style.layers.generated.SymbolLayer
import com.mapbox.maps.extension.style.layers.properties.generated.IconAnchor
import com.mapbox.maps.extension.style.layers.properties.generated.Visibility
import com.mapbox.maps.extension.style.sources.addSource
import com.mapbox.maps.extension.style.sources.generated.GeoJsonSource
import com.openmobilehub.android.maps.core.presentation.models.OmhCoordinate
import com.openmobilehub.android.maps.core.presentation.models.OmhMarkerOptions
import com.openmobilehub.android.maps.core.utils.DrawableConverter
import com.openmobilehub.android.maps.plugin.mapbox.R
import com.openmobilehub.android.maps.plugin.mapbox.presentation.maps.OmhMapImpl
import com.openmobilehub.android.maps.plugin.mapbox.presentation.maps.OmhMarkerImpl
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.verify
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters

@RunWith(Parameterized::class)
internal class OmhMarkerExtensionsTest(
    private val data: OmhMarkerOptions
) {
    private val omhMap = mockk<OmhMapImpl>(relaxed = true)
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
            infoWindowAnchor = Pair(0.4f, 0.3f)
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
        every { convertDrawableToBitmapMock.width } returns 80
        every { convertDrawableToBitmapMock.height } returns 80

        val displayMetrics = mockk<DisplayMetrics>().apply {
            this.density = 3f
        }
        every { context.resources.displayMetrics } returns displayMetrics
    }

    @Test
    @SuppressWarnings("LongMethod")
    fun `OmhMarkerOptions addOmhMarker creates an OmhMarker and all properties are properly handled`() {
        var (omhMarker, sources, _) = data.addOmhMarker(
            mapView.context,
            infoWindowManagerDelegate = omhMap.mapMarkerManager,
            infoWindowMapViewDelegate = omhMap
        )
        var (markerGeoJsonSource, infoWindowGeoJsonSource) = sources

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

        // assert property values
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
            markerGeoJsonSource.sourceId
        )
        assertEquals(data.alpha, omhMarker.getAlpha())
        assertEquals(data.title, omhMarker.getTitle())
        assertEquals(data.snippet, omhMarker.getSnippet())
        assertEquals(data.isFlat, omhMarker.getIsFlat())
        assertEquals(data.rotation, omhMarker.getRotation())
        assertEquals(data.isVisible, omhMarker.getIsVisible())
        assertEquals(data.backgroundColor, omhMarker.getBackgroundColor())
        assertEquals(data.clickable, omhMarker.getClickable())
        assertEquals(data.draggable, omhMarker.getDraggable())

        // we will also check if we can apply setters when still in 'buffered' mode (before map style is loaded)
        val someOtherMockedDrawable = mockk<Drawable>(relaxed = true)
        val otherIconThanFromOptions =
            if (data.icon === null) someOtherMockedDrawable else data.icon
        omhMarker.setIcon(otherIconThanFromOptions)
        omhMarker.setAlpha(5.5f)
        omhMarker.setIsFlat(!data.isFlat)
        omhMarker.setIsVisible(!data.isVisible)
        omhMarker.setRotation(58.5f)
        omhMarker.setClickable(!data.clickable)
        omhMarker.setDraggable(!data.draggable)
        omhMarker.setAnchor(0.9f, 0.9f)

        // assert before the style is loaded
        verifyIconLoaderProcessing(otherIconThanFromOptions, true)
        assertEquals(5.5f, omhMarker.getAlpha())
        assertEquals(!data.isFlat, omhMarker.getIsFlat())
        assertEquals(!data.isVisible, omhMarker.getIsVisible())
        assertEquals(58.5f, omhMarker.getRotation())
        assertEquals(!data.clickable, omhMarker.getClickable())
        assertEquals(!data.draggable, omhMarker.getDraggable())
        assertEquals(0.9f to 0.9f, omhMarker.bufferedAnchor)

        // also, try to change the background color
        omhMarker.setBackgroundColor(0x12345678)
        assertEquals(0x12345678, omhMarker.getBackgroundColor())
        // if a custom icon is set, then it shall be set to null, otherwise it will remain unchanged
        if (data.icon !== null) {
            verifyIconLoaderProcessing(null, true)
        }

        // revert our temporary changes for the tests to come to pass by comparing to the original options
        omhMarker.setBackgroundColor(data.backgroundColor)
        omhMarker.setIcon(data.icon)
        omhMarker.setAlpha(data.alpha)
        omhMarker.setIsFlat(data.isFlat)
        omhMarker.setIsVisible(data.isVisible)
        omhMarker.setRotation(data.rotation)
        omhMarker.setBackgroundColor(data.backgroundColor)
        omhMarker.setClickable(data.clickable)
        omhMarker.setDraggable(data.draggable)
        omhMarker.setAnchor(data.anchor.first, data.anchor.second)

        // below, we will test the map after mocking that the map style has been loaded

        mockkStatic("com.mapbox.maps.extension.style.layers.generated.SymbolLayerKt")

        // add a new marker, this time mocking the SymbolLayer to verify setters
        every {
            com.mapbox.maps.extension.style.layers.generated.symbolLayer(
                any(),
                any(),
                any()
            )
        } answers {
            val mock = mockk<SymbolLayer>(relaxed = true)
            every { mock.iconSize } returns 1.0
            thirdArg<(Layer) -> Unit>().invoke(mock)
            mock
        }

        val newMarkerPack = data.addOmhMarker(
            mapView.context,
            infoWindowManagerDelegate = omhMap.mapMarkerManager,
            infoWindowMapViewDelegate = omhMap
        )
        omhMarker = newMarkerPack.first
        markerGeoJsonSource = newMarkerPack.second.first
        infoWindowGeoJsonSource = newMarkerPack.second.second
        val layers = newMarkerPack.third
        val (markerIconLayer, infoWindowLayer) = layers
        // mock (on the new OmhMarker instance) that the map style had been loaded
        omhMarker.applyBufferedProperties(safeStyle)

        // here we check if marker properties are valid & set properly on the layer after map style is loaded
        verify {
            markerIconLayer.iconIgnorePlacement(true) // defaults from OmhMarkerExtensions
            markerIconLayer.iconAllowOverlap(true) // defaults from OmhMarkerExtensions
            markerIconLayer.iconImage(omhMarker.getMarkerIconID(data.icon !== null))
            markerIconLayer.iconOpacity(data.alpha.toDouble())
            markerIconLayer.iconPitchAlignment(
                OmhMarkerImpl.getIconsPitchAlignment(data.isFlat)
            )
            markerIconLayer.iconRotationAlignment(
                OmhMarkerImpl.getIconsRotationAlignment(data.isFlat)
            )
            markerIconLayer.visibility(
                OmhMarkerImpl.getIconsVisibility(data.isVisible)
            )
            markerIconLayer.iconRotate(data.rotation.toDouble())
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
            safeStyle.removeStyleImage(omhMarker.getMarkerIconID(data.icon === null))
            // ensure that the new icon has been added to the layer
            safeStyle.addImage(
                omhMarker.getMarkerIconID(data.icon !== null),
                convertDrawableToBitmapMock,
                data.icon === null // ensure SDF coloring is only enabled for the default icon
            )
        }
    }

    @Test
    @SuppressWarnings("LongMethod")
    fun `OmhMarkerOptions addOmhMarker creates an OmhInfoWindow assigned to OmhMarker and the IW works properly`() {
        var (omhMarker, sources, _) = data.addOmhMarker(
            mapView.context,
            infoWindowManagerDelegate = omhMap.mapMarkerManager,
            infoWindowMapViewDelegate = omhMap
        )
        var (markerGeoJsonSource, infoWindowGeoJsonSource) = sources

        // assert property values
        assertEquals(
            data.infoWindowAnchor,
            omhMarker.omhInfoWindow.bufferedInfoWindowAnchor,
        )
        assertEquals(
            omhMarker.getGeoJsonSourceID(),
            markerGeoJsonSource.sourceId
        )
        assertEquals(false, omhMarker.omhInfoWindow.getIsInfoWindowShown())
        assertEquals(data.title, omhMarker.getTitle())
        assertEquals(data.snippet, omhMarker.getSnippet())

        // we will also check if we can apply setters when still in 'buffered' mode (before map style is loaded)
        val someOtherMockedDrawable = mockk<Drawable>(relaxed = true)
        val otherIconThanFromOptions =
            if (data.icon === null) someOtherMockedDrawable else data.icon
        omhMarker.omhInfoWindow.setTitle("ABC")
        omhMarker.omhInfoWindow.setSnippet("DEF")
        omhMarker.omhInfoWindow.setInfoWindowAnchor(5.5f, 3.5f)

        // assert before the style is loaded
        assertEquals("ABC", omhMarker.omhInfoWindow.getTitle())
        assertEquals("DEF", omhMarker.omhInfoWindow.getSnippet())
        assertEquals(5.5f to 3.5f, omhMarker.omhInfoWindow.bufferedInfoWindowAnchor)

        // revert our temporary changes for the tests to come to pass by comparing to the original options
        omhMarker.omhInfoWindow.setTitle(data.title)
        omhMarker.omhInfoWindow.setSnippet(data.snippet)
        omhMarker.omhInfoWindow.setInfoWindowAnchor(
            data.infoWindowAnchor.first,
            data.infoWindowAnchor.second
        )
        omhMarker.omhInfoWindow.setInfoWindowVisibility(data.isVisible)

        // below, we will test the map after mocking that the map style has been loaded

        mockkStatic("com.mapbox.maps.extension.style.layers.generated.SymbolLayerKt")

        // add a new marker, this time mocking the SymbolLayer to verify setters
        every {
            com.mapbox.maps.extension.style.layers.generated.symbolLayer(
                any(),
                any(),
                any()
            )
        } answers {
            val mock = mockk<SymbolLayer>(relaxed = true)
            every { mock.iconSize } returns 1.0
            thirdArg<(Layer) -> Unit>().invoke(mock)
            mock
        }

        val newMarkerPack = data.addOmhMarker(
            mapView.context,
            infoWindowManagerDelegate = omhMap.mapMarkerManager,
            infoWindowMapViewDelegate = omhMap
        )
        omhMarker = newMarkerPack.first
        markerGeoJsonSource = newMarkerPack.second.first
        infoWindowGeoJsonSource = newMarkerPack.second.second
        val layers = newMarkerPack.third
        val (markerIconLayer, infoWindowLayer) = layers
        // mock (on the new OmhMarker instance) that the map style had been loaded
        omhMarker.applyBufferedProperties(safeStyle)

        // make sure the IW is invalidated after changing visibility
        val previousIWImageID = omhMarker.omhInfoWindow.lastInfoWindowIconID
        omhMarker.omhInfoWindow.setInfoWindowVisibility(true)
        Assert.assertNotEquals(previousIWImageID, omhMarker.omhInfoWindow.lastInfoWindowIconID)

        // here we check if IW properties are valid & set properly on the layer after map style is loaded
        verify {
            infoWindowLayer.iconImage(omhMarker.omhInfoWindow.lastInfoWindowIconID!!)
            infoWindowLayer.iconSize(1.0)
            infoWindowLayer.iconImage(any<String>())
            infoWindowLayer.iconAnchor(IconAnchor.BOTTOM)
            infoWindowLayer.iconAllowOverlap(true)
            infoWindowLayer.iconIgnorePlacement(true)
            infoWindowLayer.visibility(Visibility.NONE)
        }

        // check if the icon image is loaded or unloaded based on IW state
        if (omhMarker.omhInfoWindow.getIsInfoWindowShown()) {
            assertNotNull(omhMarker.omhInfoWindow.lastInfoWindowIconID)
        } else {
            assertNull(omhMarker.omhInfoWindow.lastInfoWindowIconID)
        }

        // ensure both source & symbol layers were added to map via the style
        val capturedSource = slot<GeoJsonSource>()
        val capturedLayer = slot<SymbolLayer>()
        every { safeStyle.addSource(capture(capturedSource)) } answers {}
        every { safeStyle.addLayer(capture(capturedLayer)) } answers {}

        verify {
            safeStyle.addImage(
                omhMarker.omhInfoWindow.lastInfoWindowIconID!!,
                any<Bitmap>(),
                any()
            )
        }
    }
}
