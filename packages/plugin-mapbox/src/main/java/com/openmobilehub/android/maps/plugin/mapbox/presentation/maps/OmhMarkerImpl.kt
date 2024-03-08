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
import android.graphics.drawable.Drawable
import androidx.core.content.res.ResourcesCompat
import com.mapbox.geojson.Feature
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.layers.addLayer
import com.mapbox.maps.extension.style.layers.generated.SymbolLayer
import com.mapbox.maps.extension.style.layers.properties.generated.IconPitchAlignment
import com.mapbox.maps.extension.style.layers.properties.generated.IconRotationAlignment
import com.mapbox.maps.extension.style.layers.properties.generated.Visibility
import com.mapbox.maps.extension.style.sources.addSource
import com.mapbox.maps.extension.style.sources.generated.GeoJsonSource
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhMarker
import com.openmobilehub.android.maps.core.presentation.models.OmhCoordinate
import com.openmobilehub.android.maps.core.utils.DrawableConverter
import com.openmobilehub.android.maps.plugin.mapbox.R
import com.openmobilehub.android.maps.plugin.mapbox.presentation.interfaces.IDraggable
import com.openmobilehub.android.maps.plugin.mapbox.utils.AnchorConverter
import com.openmobilehub.android.maps.plugin.mapbox.utils.Constants
import com.openmobilehub.android.maps.plugin.mapbox.utils.CoordinateConverter
import com.openmobilehub.android.maps.plugin.mapbox.utils.Offset2D
import java.util.UUID
import kotlin.math.roundToInt
import com.openmobilehub.android.maps.core.presentation.models.Constants as OmhConstants

@SuppressWarnings("TooManyFunctions", "LongParameterList")
internal class OmhMarkerImpl(
    private val markerUUID: UUID,
    private val context: Context,
    private var position: OmhCoordinate,
    private var title: String?,
    private var snippet: String?,
    private var draggable: Boolean,
    private var clickable: Boolean,
    private var backgroundColor: Int?,
    private var bufferedAlpha: Float = OmhConstants.DEFAULT_ALPHA,
    private var bufferedIsVisible: Boolean = OmhConstants.DEFAULT_IS_VISIBLE,
    private var bufferedIsFlat: Boolean = OmhConstants.DEFAULT_IS_FLAT,
    private var bufferedRotation: Float = OmhConstants.DEFAULT_ROTATION,
    internal var bufferedAnchor: Pair<Float, Float> = OmhConstants.DEFAULT_ANCHOR to OmhConstants.DEFAULT_ANCHOR,
    initialIcon: Drawable?
) : OmhMarker, IDraggable {

    private lateinit var geoJsonSource: GeoJsonSource
    private lateinit var symbolLayer: SymbolLayer
    private lateinit var safeStyle: Style
    private var isCustomIconSet: Boolean = false
    private var iconWidth: Int = 0
    private var iconHeight: Int = 0

    internal var bufferedIcon: Drawable? = null

    init {
        isCustomIconSet = initialIcon != null
        setIcon(initialIcon) // will be buffered if the layer has not been not added to the map yet
    }

    fun setGeoJsonSource(geoJsonSource: GeoJsonSource) {
        this.geoJsonSource = geoJsonSource
    }

    fun setSymbolLayer(symbolLayer: SymbolLayer) {
        this.symbolLayer = symbolLayer
    }

    fun getMarkerUUID(): UUID {
        return markerUUID
    }

    override fun getPosition(): OmhCoordinate {
        return position
    }

    override fun setPosition(omhCoordinate: OmhCoordinate) {
        position = omhCoordinate
        geoJsonSource.feature(
            Feature.fromGeometry(
                CoordinateConverter.convertToPoint(omhCoordinate)
            )
        )
    }

    override fun getTitle(): String? {
        return title
    }

    override fun setTitle(title: String?) {
        this.title = title
        // TODO implement info window functionality
    }

    override fun getClickable(): Boolean {
        return clickable
    }

    override fun setClickable(clickable: Boolean) {
        this.clickable = clickable
    }

    override fun getDraggable(): Boolean {
        return draggable
    }

    override fun getHandleOffset(): Offset2D {
        val offsetX = -(bufferedAnchor.first - 1.0f).roundToInt()
        val offsetY = (bufferedAnchor.second - 1.0f).roundToInt()

        return Offset2D(offsetX * iconWidth, offsetY * iconHeight)
    }

    override fun setDraggable(draggable: Boolean) {
        this.draggable = draggable
    }

    override fun setAnchor(anchorU: Float, anchorV: Float) {
        bufferedAnchor = anchorU to anchorV
        symbolLayer.iconAnchor(
            AnchorConverter.convertContinuousToDiscreteIconAnchor(bufferedAnchor)
        )
    }

    fun applyBufferedProperties(safeStyle: Style) {
        check(!this::safeStyle.isInitialized) { "Buffered properties have already been applied" }

        synchronized(this) {
            this.safeStyle = safeStyle

            this.safeStyle.addSource(geoJsonSource)

            setIcon(bufferedIcon)
            setAlpha(bufferedAlpha)
            setIsVisible(bufferedIsVisible)
            setIsFlat(bufferedIsFlat)
            setRotation(bufferedRotation)
            setAnchor(bufferedAnchor.first, bufferedAnchor.second)

            this.safeStyle.addLayer(symbolLayer)

            // (possibly) clear some memory
            bufferedIcon = null
        }
    }

    override fun getAlpha(): Float {
        return if (this::safeStyle.isInitialized) {
            symbolLayer.iconOpacity?.toFloat() ?: OmhConstants.DEFAULT_ALPHA
        } else {
            bufferedAlpha
        }
    }

    override fun setAlpha(alpha: Float) {
        if (this::safeStyle.isInitialized) {
            symbolLayer.iconOpacity(alpha.toDouble())
        } else {
            // if the layer was not added to the map yet, buffer the alpha value to apply it later
            bufferedAlpha = alpha
        }
    }

    override fun getSnippet(): String? {
        return snippet
    }

    override fun setSnippet(snippet: String?) {
        this.snippet = snippet
        // TODO implement info window functionality
    }

    override fun setIcon(icon: Drawable?) {
        isCustomIconSet = icon != null

        if (this::safeStyle.isInitialized) {
            val addedIconID = addOrUpdateMarkerIconImage(icon)
            // color the icon image using Mapbox's SDF implementation
            symbolLayer.iconColor(Constants.DEFAULT_MARKER_COLOR)
            symbolLayer.iconImage(addedIconID)
        } else {
            // if the layer was not added to the map yet, buffer the icon value to apply it later
            bufferedIcon = icon
        }
    }

    override fun getIsVisible(): Boolean {
        return if (this::safeStyle.isInitialized) {
            symbolLayer.visibility == Visibility.VISIBLE
        } else {
            bufferedIsVisible
        }
    }

    override fun setIsVisible(visible: Boolean) {
        if (this::safeStyle.isInitialized) {
            symbolLayer.visibility(getIconVisibility(visible))
        } else {
            // if the layer was not added to the map yet, buffer the icon value to apply it later
            bufferedIsVisible = visible
        }
    }

    override fun getIsFlat(): Boolean {
        return if (this::safeStyle.isInitialized) {
            symbolLayer.iconPitchAlignment == IconPitchAlignment.MAP
        } else {
            bufferedIsFlat
        }
    }

    override fun setIsFlat(flat: Boolean) {
        if (this::safeStyle.isInitialized) {
            symbolLayer.iconPitchAlignment(getIconPitchAlignment(flat))
            symbolLayer.iconRotationAlignment(getIconRotationAlignment(flat))
        } else {
            // if the layer was not added to the map yet, buffer the is flat value to apply it later
            bufferedIsFlat = flat
        }
    }

    override fun getRotation(): Float {
        return if (this::safeStyle.isInitialized) {
            symbolLayer.iconRotate?.toFloat() ?: OmhConstants.DEFAULT_ROTATION
        } else {
            bufferedRotation
        }
    }

    override fun setRotation(rotation: Float) {
        if (this::safeStyle.isInitialized) {
            symbolLayer.iconRotate(rotation.toDouble())
        } else {
            // if the layer was not added to the map yet, buffer the is flat value to apply it later
            bufferedRotation = rotation
        }
    }

    override fun getBackgroundColor(): Int? {
        return backgroundColor
    }

    override fun setBackgroundColor(color: Int?) {
        backgroundColor = color

        // set the default icon, also setting isCustomIconSet to keep track
        // of current state for rebuilding the icon
        setIcon(null)

        // color the icon image using Mapbox's SDF implementation
        symbolLayer.iconColor(
            color ?: Constants.DEFAULT_MARKER_COLOR
        )
    }

    internal fun getIconID(bForCustomIcon: Boolean): String {
        return "$markerUUID-omh-marker-icon-${if (bForCustomIcon) "custom" else "default"}"
    }

    internal fun getGeoJsonSourceID(): String {
        return "$markerUUID-omh-marker-geojson-source"
    }

    internal fun getSymbolLayerID(): String {
        return "$markerUUID-omh-marker-symbol-layer"
    }

    private fun getDefaultIcon(): Drawable {
        return ResourcesCompat.getDrawable(
            context.resources,
            R.drawable.marker_pin,
            null
        )!!
    }

    /**
     * Adds or updates the marker icon image on the map.
     * If the [icon] is null, the default icon is used.
     * Optionally, tints the [icon] to [backgroundColor].
     *
     * @param icon the icon [Drawable] for the marker.
     *
     * @return the ID of the added or updated marker icon image (static for a given marker, received from [getIconID]).
     */
    private fun addOrUpdateMarkerIconImage(
        icon: Drawable?,
    ): String {
        val markerImageID = getIconID(icon != null)

        // ensure the other icon is removed for memory optimization
        safeStyle.removeStyleImage(getIconID(!isCustomIconSet))

        val bitmap = DrawableConverter.convertDrawableToBitmap(icon ?: getDefaultIcon())

        iconWidth = bitmap.width
        iconHeight = bitmap.height

        val addImageResult = safeStyle.addImage(
            markerImageID,
            bitmap,
            icon === null // apply backgroundColor to the default image, only if icon is null
        )

        addImageResult.error?.let { error ->
            throw IllegalStateException("Failed to add image to map: $error")
        }

        return markerImageID
    }

    internal companion object {
        internal fun getIconPitchAlignment(isFlat: Boolean): IconPitchAlignment {
            return if (isFlat) {
                IconPitchAlignment.MAP // flat
            } else {
                IconPitchAlignment.VIEWPORT // billboard
            }
        }

        internal fun getIconRotationAlignment(isFlat: Boolean): IconRotationAlignment {
            return if (isFlat) {
                IconRotationAlignment.MAP // flat
            } else {
                IconRotationAlignment.VIEWPORT // billboard
            }
        }

        internal fun getIconVisibility(isVisible: Boolean): Visibility {
            return if (isVisible) {
                Visibility.VISIBLE
            } else {
                Visibility.NONE
            }
        }
    }
}
