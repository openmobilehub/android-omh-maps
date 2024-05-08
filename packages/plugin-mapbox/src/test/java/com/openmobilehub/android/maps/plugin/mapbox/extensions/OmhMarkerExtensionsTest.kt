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
import android.view.LayoutInflater
import androidx.core.content.res.ResourcesCompat
import com.mapbox.bindgen.Expected
import com.mapbox.bindgen.None
import com.mapbox.geojson.Feature
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.layers.Layer
import com.mapbox.maps.extension.style.layers.addLayer
import com.mapbox.maps.extension.style.layers.generated.SymbolLayer
import com.mapbox.maps.extension.style.layers.generated.symbolLayer
import com.mapbox.maps.extension.style.layers.properties.generated.IconAnchor
import com.mapbox.maps.extension.style.layers.properties.generated.Visibility
import com.mapbox.maps.extension.style.sources.addSource
import com.mapbox.maps.extension.style.sources.generated.GeoJsonSource
import com.mapbox.maps.extension.style.sources.generated.geoJsonSource
import com.openmobilehub.android.maps.core.R
import com.openmobilehub.android.maps.core.presentation.models.OmhCoordinate
import com.openmobilehub.android.maps.core.presentation.models.OmhMarkerOptions
import com.openmobilehub.android.maps.core.utils.DrawableConverter
import com.openmobilehub.android.maps.core.utils.logging.UnsupportedFeatureLogger
import com.openmobilehub.android.maps.core.utils.uuid.DefaultUUIDGenerator
import com.openmobilehub.android.maps.plugin.mapbox.presentation.maps.OmhMapImpl
import com.openmobilehub.android.maps.plugin.mapbox.presentation.maps.OmhMarkerImpl
import com.openmobilehub.android.maps.plugin.mapbox.utils.CoordinateConverter
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.AfterClass
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters
import java.util.UUID
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@RunWith(Parameterized::class)
internal class OmhMarkerExtensionsTest(
    private val data: OmhMarkerOptions
) {
    private val omhMap = mockk<OmhMapImpl>(relaxed = true)
    private val mapView = mockk<MapView>()
    private val context = mockk<Context>()
    private val safeStyle = mockk<Style>(relaxed = true)
    private val mockLogger: UnsupportedFeatureLogger =
        mockk<UnsupportedFeatureLogger>(relaxed = true)
    private val defaultMarkerIconDrawable = mockk<Drawable>()
    private val convertDrawableToBitmapMock = mockk<Bitmap>()
    private lateinit var mockedViewDrawnToBitmap: Bitmap
    private val markerUUID = DefaultUUIDGenerator().generate()

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
            zIndex = 1.0f
        }

        @JvmStatic
        @Parameters
        internal fun data() = listOf(
            omhMarkerOptionsInvisible,
            omhMarkerOptionsWithIcon
        )

        @JvmStatic
        @AfterClass
        fun cleanup() {
            unmockkAll()
        }
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
        every { safeStyle.addImage(any(), any<Bitmap>()) } returns mockAddImageResult
        every { safeStyle.isStyleLoaded() } returns true
        every { convertDrawableToBitmapMock.width } returns 80
        every { convertDrawableToBitmapMock.height } returns 80

        val displayMetrics = mockk<DisplayMetrics>().apply {
            this.density = 3f
        }
        every { context.resources.displayMetrics } returns displayMetrics

        mockedViewDrawnToBitmap = mockk<Bitmap>(relaxed = true)
        every { mockedViewDrawnToBitmap.width } returns 80
        every { mockedViewDrawnToBitmap.height } returns 80

        mockkStatic(LayoutInflater::class)
        mockkStatic(Executors::class) // for mocking the executor handling View.drawToBitmap(...)
    }

    private fun createMarker(
        context: Context,
        markerUUID: UUID,
        options: OmhMarkerOptions
    ): Triple<OmhMarkerImpl, Pair<SymbolLayer, SymbolLayer>, Pair<GeoJsonSource, GeoJsonSource>> {
        val markerGeoJsonSourceID = OmhMarkerImpl.getGeoJsonSourceID(markerUUID)

        val pointOnMap = CoordinateConverter.convertToPoint(options.position)

        val markerLayer =
            symbolLayer(OmhMarkerImpl.getSymbolLayerID(markerUUID), markerGeoJsonSourceID) {}

        options.applyMarkerOptions(markerLayer, mockLogger)

        val omhMarker = OmhMarkerImpl(
            markerUUID = markerUUID,
            context = context,
            markerSymbolLayer = markerLayer,
            position = options.position,
            initialTitle = options.title,
            initialSnippet = options.snippet,
            initialInfoWindowAnchor = options.infoWindowAnchor,
            draggable = options.draggable,
            clickable = options.clickable,
            backgroundColor = options.backgroundColor,
            initialIcon = options.icon,
            bufferedAlpha = options.alpha,
            bufferedIsVisible = options.isVisible,
            bufferedAnchor = options.anchor,
            bufferedIsFlat = options.isFlat,
            bufferedRotation = options.rotation,
            infoWindowManagerDelegate = omhMap.mapMarkerManager,
            infoWindowMapViewDelegate = omhMap,
            markerDelegate = omhMap.mapMarkerManager,
        )

        val markerGeoJsonSource = geoJsonSource(markerGeoJsonSourceID) {
            feature(Feature.fromGeometry(pointOnMap))
        }
        omhMarker.setGeoJsonSource(markerGeoJsonSource)

        val infoWindowGeoJsonSourceID = omhMarker.omhInfoWindow.getGeoJsonSourceID()
        val infoWindowGeoJsonSource = geoJsonSource(infoWindowGeoJsonSourceID) {
            feature(Feature.fromGeometry(pointOnMap))
        }
        omhMarker.omhInfoWindow.setGeoJsonSource(infoWindowGeoJsonSource)

        return Triple(
            omhMarker,
            markerLayer to omhMarker.omhInfoWindow.infoWindowSymbolLayer,
            markerGeoJsonSource to infoWindowGeoJsonSource
        )
    }

    @Test
    @SuppressWarnings("LongMethod")
    fun `OmhMarkerOptions addOmhMarker creates an OmhMarker and all properties are properly handled`() {
        var (omhMarker, layers, sources) = createMarker(
            mapView.context,
            markerUUID,
            data
        )
        var (markerSymbolLayer, infoWindowSymbolLayer) = layers
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
            OmhMarkerImpl.getGeoJsonSourceID(markerUUID),
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
            symbolLayer(
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

        val newMarkerPack = createMarker(
            mapView.context,
            markerUUID,
            data
        )
        omhMarker = newMarkerPack.first
        layers = newMarkerPack.second
        val (markerIconLayer, infoWindowLayer) = layers
        sources = newMarkerPack.third
        markerGeoJsonSource = sources.first
        infoWindowGeoJsonSource = sources.second
        // mock (on the new OmhMarker instance) that the map style had been loaded
        omhMarker.onStyleLoaded(safeStyle)

        val previousMarkerIconId = omhMarker.lastMarkerIconID

        omhMarker.setIcon(someOtherMockedDrawable)

        // here we check if marker properties are valid & set properly on the layer after map style is loaded
        verify {
            markerIconLayer.iconIgnorePlacement(true) // defaults from OmhMarkerExtensions
            markerIconLayer.iconAllowOverlap(true) // defaults from OmhMarkerExtensions
            markerIconLayer.iconImage(previousMarkerIconId!!)
            markerIconLayer.iconImage(omhMarker.lastMarkerIconID!!)
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
            safeStyle.removeStyleImage(previousMarkerIconId!!)
            // ensure that the previous icon has once been added to the layer
            safeStyle.addImage(
                previousMarkerIconId,
                convertDrawableToBitmapMock,
                data.icon === null // ensure SDF coloring is only enabled for the default icon
            )
            // ensure that the new icon has been added to the layer
            safeStyle.addImage(
                omhMarker.lastMarkerIconID!!,
                convertDrawableToBitmapMock,
                false // ensure SDF coloring is only enabled for the default icon
            )
        }
    }

    @Test
    @SuppressWarnings("LongMethod")
    fun `OmhMarkerOptions addOmhMarker creates an OmhInfoWindow assigned to OmhMarker and the IW works properly`() {
        var (omhMarker, layers, sources) = createMarker(
            mapView.context,
            markerUUID,
            data
        )
        var (markerSymbolLayer, infoWindowSymbolLayer) = layers
        var (markerGeoJsonSource, infoWindowGeoJsonSource) = sources

        every { Executors.newSingleThreadExecutor() } answers {
            val executor = mockk<ExecutorService>(relaxed = true)

            every { executor.execute(any()) } answers {
                val infoWindowIconID =
                    omhMarker.omhInfoWindow.addOrUpdateMarkerIconImage(mockedViewDrawnToBitmap)
                omhMarker.omhInfoWindow.infoWindowSymbolLayer.iconImage(infoWindowIconID)

                omhMarker.omhInfoWindow.updatePosition()
            }

            executor
        }

        // assert property values
        assertEquals(
            data.infoWindowAnchor,
            omhMarker.omhInfoWindow.bufferedInfoWindowAnchor,
        )
        assertEquals(
            OmhMarkerImpl.getGeoJsonSourceID(markerUUID),
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
            symbolLayer(
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

        val newMarkerPack = createMarker(
            mapView.context,
            markerUUID,
            data
        )
        omhMarker = newMarkerPack.first
        layers = newMarkerPack.second
        val (markerIconLayer, infoWindowLayer) = layers
        sources = newMarkerPack.third
        markerGeoJsonSource = sources.first
        infoWindowGeoJsonSource = sources.second
        // mock (on the new OmhMarker instance) that the map style had been loaded
        omhMarker.onStyleLoaded(safeStyle)

        // make sure the IW is invalidated after changing visibility
        val previousIWImageID = omhMarker.omhInfoWindow.lastInfoWindowIconID
        omhMarker.omhInfoWindow.setInfoWindowVisibility(true)
        assertNotEquals(previousIWImageID, omhMarker.omhInfoWindow.lastInfoWindowIconID)
        assertNotNull(omhMarker.omhInfoWindow.lastInfoWindowIconID)
        assertEquals(mockedViewDrawnToBitmap.width, omhMarker.omhInfoWindow.iwBitmapWidth)
        assertEquals(mockedViewDrawnToBitmap.height, omhMarker.omhInfoWindow.iwBitmapHeight)

        // here we check if IW properties are valid & set properly on the layer after map style is loaded
        verify {
            infoWindowLayer.iconImage(omhMarker.omhInfoWindow.lastInfoWindowIconID!!)
            infoWindowLayer.iconSize(1.0)
            infoWindowLayer.iconImage(any<String>())
            infoWindowLayer.iconAnchor(IconAnchor.CENTER)
            infoWindowLayer.iconAllowOverlap(true)
            infoWindowLayer.iconIgnorePlacement(true)
            infoWindowLayer.visibility(Visibility.NONE)
        }

        // ensure both source & symbol layers were added to map via the style
        verify {
            safeStyle.addSource(markerGeoJsonSource)
            safeStyle.addSource(infoWindowGeoJsonSource)

            safeStyle.addLayer(markerIconLayer)
            safeStyle.addLayer(infoWindowLayer)
        }
    }

    @Test
    fun `OmhMarkerOptions should log zIndex not supported when using zIndex`() {
        // Act
        createMarker(
            mapView.context,
            markerUUID,
            data
        )

        // Assert
        if (data.zIndex != null) {
            verify { mockLogger.logNotSupported("zIndex") }
        }
    }
}
