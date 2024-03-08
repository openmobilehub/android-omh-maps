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
import android.util.Log
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
import com.openmobilehub.android.maps.plugin.mapbox.presentation.interfaces.IMapInfoWindowManagerDelegate
import com.openmobilehub.android.maps.plugin.mapbox.presentation.interfaces.IOmhInfoWindowMapViewDelegate
import com.openmobilehub.android.maps.plugin.mapbox.presentation.interfaces.ITouchInteractable
import com.openmobilehub.android.maps.plugin.mapbox.utils.AnchorConverter
import com.openmobilehub.android.maps.plugin.mapbox.utils.Constants
import com.openmobilehub.android.maps.plugin.mapbox.utils.CoordinateConverter
import com.openmobilehub.android.maps.plugin.mapbox.utils.cartesian.Offset2D
import com.openmobilehub.android.maps.plugin.mapbox.utils.cartesian.plus
import java.util.UUID
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import com.openmobilehub.android.maps.core.presentation.models.Constants as OmhConstants

@SuppressWarnings("TooManyFunctions", "LongParameterList")
internal class OmhMarkerImpl(
    internal val markerUUID: UUID,
    internal val context: Context,
    private var position: OmhCoordinate,
    initialTitle: String?,
    initialSnippet: String?,
    private var draggable: Boolean,
    private var clickable: Boolean,
    private var backgroundColor: Int?,
    private var bufferedAlpha: Float = OmhConstants.DEFAULT_ALPHA,
    private var bufferedIsVisible: Boolean = OmhConstants.DEFAULT_IS_VISIBLE,
    private var bufferedIsFlat: Boolean = OmhConstants.DEFAULT_IS_FLAT,
    private var bufferedRotation: Float = OmhConstants.DEFAULT_ROTATION,
    internal var bufferedAnchor: Pair<Float, Float> = OmhConstants.ANCHOR_CENTER to OmhConstants.ANCHOR_CENTER,
    initialInfoWindowAnchor: Pair<Float, Float> = OmhConstants.ANCHOR_CENTER to OmhConstants.ANCHOR_TOP,
    initialIcon: Drawable?,
    infoWindowManagerDelegate: IMapInfoWindowManagerDelegate,
    infoWindowMapViewDelegate: IOmhInfoWindowMapViewDelegate
) : OmhMarker, ITouchInteractable {

    private lateinit var geoJsonSource: GeoJsonSource
    private lateinit var markerSymbolLayer: SymbolLayer
    private lateinit var safeStyle: Style
    internal var omhInfoWindow: OmhInfoWindow
    private var isCustomIconSet: Boolean = false
    internal var iconWidth: Int = 0
    internal var iconHeight: Int = 0

    internal var bufferedIcon: Drawable? = null

    init {
        isCustomIconSet = initialIcon != null
        setIcon(initialIcon) // will be buffered if the layer has not been not added to the map yet

        omhInfoWindow = OmhInfoWindow(
            title = initialTitle,
            snippet = initialSnippet,
            iwAnchor = initialInfoWindowAnchor,
            infoWindowManagerDelegate = infoWindowManagerDelegate,
            omhMarker = this,
            mapViewDelegate = infoWindowMapViewDelegate
        )
    }

    fun setGeoJsonSource(geoJsonSource: GeoJsonSource) {
        this.geoJsonSource = geoJsonSource
    }

    fun setMarkerLayer(markerLayer: SymbolLayer) {
        this.markerSymbolLayer = markerLayer
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
        omhInfoWindow.updatePosition()
    }

    override fun getTitle(): String? {
        return omhInfoWindow.getTitle()
    }

    override fun setTitle(title: String?) {
        omhInfoWindow.setTitle(title)
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

    @SuppressWarnings("MagicNumber")
    internal fun getHandleTranslation(): Offset2D<Double> {
        val offsetCoeffX =
            -(bufferedAnchor.first - 0.5f).roundToInt() // invert the x-axis to match MapBox coordinate system
        val offsetCoeffY =
            -(bufferedAnchor.second - 1.0f).roundToInt() // invert the y-axis to match MapBox coordinate system

        val offsetX = offsetCoeffX * iconWidth
        val offsetY = offsetCoeffY * iconHeight

        return Offset2D(offsetX.toDouble(), offsetY.toDouble())
    }

    private fun rotateOffset(translation: Offset2D<Double>): Offset2D<Double> {
        // rotation angle
        val theta = Math.toRadians(getRotation().toDouble())
        Log.d("OmhMarkerImpl", "rotateOffset: theta = $theta")
        return Offset2D(
            -(cos(theta) * translation.x - sin(theta) * translation.y),
            -(sin(theta) * translation.x + cos(theta) * translation.y)
        )
    }

    override fun getHandleCenterOffset(): Offset2D<Double> {
        val translation = getHandleTranslation()

        return rotateOffset(translation)
    }

    /**
     * Returns the screen offset for position of the handle of the entity that takes into account the transforms.
     *
     * @return The offset in pixels from entity position converted to screen pixel coordinates
     * using `pixelForCoordinate(...)` to the center horizontally & **top edge vertically**.
     */
    fun getHandleTopOffset(): Offset2D<Double> {
        val translation = getHandleTranslation()

        // since we are already at the center of the marker, we want to move just by 1/4 of the IW's height
        // to get to just-above-the-top-edge of the marker
        return rotateOffset(translation + Offset2D(0.0, iconHeight / 2.0))
    }

    override fun getLongClickable(): Boolean {
        return false
    }

    override fun setDraggable(draggable: Boolean) {
        this.draggable = draggable
    }

    override fun setAnchor(anchorU: Float, anchorV: Float) {
        bufferedAnchor = anchorU to anchorV
        markerSymbolLayer.iconAnchor(
            AnchorConverter.convertContinuousToDiscreteIconAnchor(bufferedAnchor)
        )
        omhInfoWindow.updatePosition()
    }

    override fun setInfoWindowAnchor(iwAnchorU: Float, iwAnchorV: Float) {
        omhInfoWindow.setInfoWindowAnchor(iwAnchorU, iwAnchorV)
    }

    fun applyBufferedProperties(safeStyle: Style) {
        check(!this::safeStyle.isInitialized) { "Buffered properties have already been applied" }

        synchronized(this) {
            this.safeStyle = safeStyle

            this.safeStyle.addSource(geoJsonSource)
            this.safeStyle.addLayer(markerSymbolLayer)

            omhInfoWindow.applyBufferedProperties(this.safeStyle)

            setIcon(bufferedIcon)
            setAlpha(bufferedAlpha)
            setIsVisible(bufferedIsVisible)
            setIsFlat(bufferedIsFlat)
            setRotation(bufferedRotation)
            setAnchor(bufferedAnchor.first, bufferedAnchor.second)
            setBackgroundColor(backgroundColor)

            // one call to updatePosition will happen in omhInfoWindow.applyBufferedProperties(),
            // yet after buffered properties were applied, a re-invalidation is needed
            omhInfoWindow.updatePosition()

            // (possibly) clear some memory
            bufferedIcon = null
        }
    }

    override fun getAlpha(): Float {
        return if (this::safeStyle.isInitialized) {
            markerSymbolLayer.iconOpacity?.toFloat() ?: OmhConstants.DEFAULT_ALPHA
        } else {
            bufferedAlpha
        }
    }

    override fun setAlpha(alpha: Float) {
        if (this::safeStyle.isInitialized) {
            markerSymbolLayer.iconOpacity(alpha.toDouble())
        } else {
            // if the layer was not added to the map yet, buffer the alpha value to apply it later
            bufferedAlpha = alpha
        }
    }

    override fun getSnippet(): String? {
        return omhInfoWindow.getSnippet()
    }

    override fun setSnippet(snippet: String?) {
        omhInfoWindow.setSnippet(snippet)
    }

    override fun setIcon(icon: Drawable?) {
        isCustomIconSet = icon != null

        if (this::safeStyle.isInitialized) {
            val addedIconID = addOrUpdateMarkerIconImage(icon)
            // color the icon image using Mapbox's SDF implementation
            markerSymbolLayer.iconColor(Constants.DEFAULT_MARKER_COLOR)
            markerSymbolLayer.iconImage(addedIconID)
        } else {
            // if the layer was not added to the map yet, buffer the icon value to apply it later
            bufferedIcon = icon
        }
    }

    override fun getIsVisible(): Boolean {
        return if (this::safeStyle.isInitialized) {
            markerSymbolLayer.visibility == Visibility.VISIBLE
        } else {
            bufferedIsVisible
        }
    }

    override fun setIsVisible(visible: Boolean) {
        if (this::safeStyle.isInitialized) {
            markerSymbolLayer.visibility(getIconsVisibility(visible))
        } else {
            // if the layer was not added to the map yet, buffer the icon value to apply it later
            bufferedIsVisible = visible
        }

        if (!visible) {
            // close the info window if the marker is hidden, for parity
            hideInfoWindow()
        }
    }

    override fun getIsFlat(): Boolean {
        return if (this::safeStyle.isInitialized) {
            markerSymbolLayer.iconPitchAlignment == IconPitchAlignment.MAP
        } else {
            bufferedIsFlat
        }
    }

    override fun setIsFlat(flat: Boolean) {
        if (this::safeStyle.isInitialized) {
            markerSymbolLayer.iconPitchAlignment(getIconsPitchAlignment(flat))
            markerSymbolLayer.iconRotationAlignment(getIconsRotationAlignment(flat))
        } else {
            // if the layer was not added to the map yet, buffer the is flat value to apply it later
            bufferedIsFlat = flat
        }
    }

    override fun getRotation(): Float {
        return if (this::safeStyle.isInitialized) {
            markerSymbolLayer.iconRotate?.toFloat() ?: OmhConstants.DEFAULT_ROTATION
        } else {
            bufferedRotation
        }
    }

    override fun setRotation(rotation: Float) {
        if (this::safeStyle.isInitialized) {
            markerSymbolLayer.iconRotate(rotation.toDouble())

            omhInfoWindow.updatePosition()
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
        markerSymbolLayer.iconColor(
            color ?: Constants.DEFAULT_MARKER_COLOR
        )
    }

    override fun showInfoWindow() {
        omhInfoWindow.setInfoWindowVisibility(true)
    }

    override fun hideInfoWindow() {
        omhInfoWindow.setInfoWindowVisibility(false)
    }

    override fun getIsInfoWindowShown(): Boolean {
        return omhInfoWindow.getIsInfoWindowShown()
    }

    internal fun getMarkerIconID(bForCustomIcon: Boolean): String {
        return "$markerUUID-omh-marker-icon-${if (bForCustomIcon) "custom" else "default"}"
    }

    internal fun getGeoJsonSourceID(): String {
        return "$markerUUID-omh-marker-geojson-source"
    }

    internal fun getSymbolLayerID(): String {
        return "$markerUUID-omh-marker-layer"
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
     * @return the ID of the added or updated marker icon image
     * (static for a given marker, received from [getMarkerIconID]).
     */
    private fun addOrUpdateMarkerIconImage(
        icon: Drawable?,
    ): String {
        val markerImageID = getMarkerIconID(icon != null)

        // ensure the other icon is removed for memory optimization
        safeStyle.removeStyleImage(getMarkerIconID(!isCustomIconSet))

        val bitmap = DrawableConverter.convertDrawableToBitmap(icon ?: getDefaultIcon())

        iconWidth = bitmap.width
        iconHeight = bitmap.height

        omhInfoWindow.updatePosition() // iconWidth & iconHeight have an impact on the IW's position

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
        internal fun getIconsPitchAlignment(isFlat: Boolean): IconPitchAlignment {
            return if (isFlat) {
                IconPitchAlignment.MAP // flat
            } else {
                IconPitchAlignment.VIEWPORT // billboard
            }
        }

        internal fun getIconsRotationAlignment(isFlat: Boolean): IconRotationAlignment {
            return if (isFlat) {
                IconRotationAlignment.MAP // flat
            } else {
                IconRotationAlignment.VIEWPORT // billboard
            }
        }

        internal fun getIconsVisibility(isVisible: Boolean): Visibility {
            return if (isVisible) {
                Visibility.VISIBLE
            } else {
                Visibility.NONE
            }
        }
    }
}
