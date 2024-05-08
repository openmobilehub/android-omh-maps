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
import com.mapbox.maps.extension.style.layers.properties.generated.IconAnchor
import com.mapbox.maps.extension.style.layers.properties.generated.IconPitchAlignment
import com.mapbox.maps.extension.style.layers.properties.generated.IconRotationAlignment
import com.mapbox.maps.extension.style.layers.properties.generated.Visibility
import com.mapbox.maps.extension.style.sources.addSource
import com.mapbox.maps.extension.style.sources.generated.GeoJsonSource
import com.openmobilehub.android.maps.core.R
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhMarker
import com.openmobilehub.android.maps.core.presentation.models.OmhCoordinate
import com.openmobilehub.android.maps.core.utils.DrawableConverter
import com.openmobilehub.android.maps.core.utils.cartesian.Offset2D
import com.openmobilehub.android.maps.core.utils.logging.UnsupportedFeatureLogger
import com.openmobilehub.android.maps.core.utils.uuid.DefaultUUIDGenerator
import com.openmobilehub.android.maps.core.utils.uuid.UUIDGenerator
import com.openmobilehub.android.maps.plugin.mapbox.presentation.interfaces.IMapInfoWindowManagerDelegate
import com.openmobilehub.android.maps.plugin.mapbox.presentation.interfaces.IMarkerDelegate
import com.openmobilehub.android.maps.plugin.mapbox.presentation.interfaces.IOmhInfoWindowMapViewDelegate
import com.openmobilehub.android.maps.plugin.mapbox.presentation.interfaces.ITouchInteractable
import com.openmobilehub.android.maps.plugin.mapbox.utils.AnchorConverter
import com.openmobilehub.android.maps.plugin.mapbox.utils.Constants
import com.openmobilehub.android.maps.plugin.mapbox.utils.CoordinateConverter
import com.openmobilehub.android.maps.plugin.mapbox.utils.markerLogger
import java.util.UUID
import com.openmobilehub.android.maps.core.presentation.models.Constants as OmhConstants

@SuppressWarnings("TooManyFunctions", "LongParameterList")
internal class OmhMarkerImpl(
    internal val markerUUID: UUID,
    internal val context: Context,
    internal val markerSymbolLayer: SymbolLayer,
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
    infoWindowMapViewDelegate: IOmhInfoWindowMapViewDelegate,
    private val logger: UnsupportedFeatureLogger = markerLogger,
    private val markerDelegate: IMarkerDelegate,
    private val iconUUIDGenerator: UUIDGenerator = DefaultUUIDGenerator()
) : OmhMarker, ITouchInteractable {
    internal var isRemoved: Boolean = false

    private lateinit var geoJsonSource: GeoJsonSource
    private lateinit var style: Style
    internal var omhInfoWindow: OmhInfoWindow
    internal var iconWidth: Int = 0
    internal var iconHeight: Int = 0

    internal var bufferedIcon: Drawable? = null
    internal var lastMarkerIconID: String? = null

    init {
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
        val discreteAnchor = AnchorConverter.convertContinuousToDiscreteIconAnchor(bufferedAnchor)
        val (offsetCoeffX, offsetCoeffY) = when (discreteAnchor) {
            IconAnchor.CENTER -> 0.0 to 0.0
            IconAnchor.LEFT -> -0.5 to 0.0
            IconAnchor.RIGHT -> 0.5 to 0.0
            IconAnchor.TOP -> 0.0 to -1.0
            IconAnchor.BOTTOM -> 0.0 to 1.0
            IconAnchor.TOP_LEFT -> -0.5 to -1.0
            IconAnchor.TOP_RIGHT -> 0.5 to -1.0
            IconAnchor.BOTTOM_LEFT -> -0.5 to 1.0
            IconAnchor.BOTTOM_RIGHT -> 0.5 to 1.0
            else -> 0.0 to 0.0 // for safety
        }

        val offsetX = offsetCoeffX * iconWidth
        val offsetY = offsetCoeffY * iconHeight / 2.0

        return Offset2D(offsetX, offsetY)
    }

    override fun getHandleCenterOffset(): Offset2D<Double> {
        val translation = getHandleTranslation()

        return translation.rotateOffset(getRotation().toDouble())
    }

    /**
     * Returns the screen offset for position of the handle of the entity that takes into account the transforms.
     *
     * @return The offset in pixels from entity position converted to screen pixel coordinates
     * using `pixelForCoordinate(...)` to the center horizontally & **top edge vertically**.
     */
    fun getHandleTopOffset(): Offset2D<Double> {
        val translation = getHandleTranslation() + Offset2D(
            0.0,
            iconHeight / 2.0
        )

        // since we are already at the center of the marker, we want to move just by 1/4 of the IW's height
        // to get to just-above-the-top-edge of the marker
        return translation.rotateOffset(getRotation().toDouble())
    }

    override fun getLongClickable(): Boolean {
        return false
    }

    override fun setDraggable(draggable: Boolean) {
        this.draggable = draggable
    }

    override fun setAnchor(anchorU: Float, anchorV: Float) {
        logger.logFeatureSetterPartiallySupported(
            "anchor",
            "only discrete anchor types are supported, continuous values are converted to discrete ones"
        )

        bufferedAnchor = anchorU to anchorV
        markerSymbolLayer.iconAnchor(
            AnchorConverter.convertContinuousToDiscreteIconAnchor(bufferedAnchor)
        )
        omhInfoWindow.updatePosition()
    }

    override fun setInfoWindowAnchor(iwAnchorU: Float, iwAnchorV: Float) {
        omhInfoWindow.setInfoWindowAnchor(iwAnchorU, iwAnchorV)
    }

    private fun addSourceAndLayerToStyle() {
        this.style.addSource(geoJsonSource)
        this.style.addLayer(markerSymbolLayer)
    }

    private fun applyBufferedProperties() {
        setAlpha(bufferedAlpha)
        setIsVisible(bufferedIsVisible)
        setIsFlat(bufferedIsFlat)
        setRotation(bufferedRotation)
        setAnchor(bufferedAnchor.first, bufferedAnchor.second)
        if (backgroundColor == null) {
            setIcon(bufferedIcon)
        } else {
            setBackgroundColor(backgroundColor)
        }

        // one call to updatePosition will happen in omhInfoWindow.applyBufferedProperties(),
        // yet after buffered properties were applied, a re-invalidation is needed
        omhInfoWindow.updatePosition()

        // (possibly) clear some memory
        bufferedIcon = null

        omhInfoWindow.applyBufferedProperties()
    }

    private fun isStyleReady(): Boolean {
        return this::style.isInitialized && this.style.isStyleLoaded()
    }

    internal fun onStyleLoaded(safeStyle: Style) {
        check(!isStyleReady()) { "Buffered properties have already been applied" }

        synchronized(this) {
            this.style = safeStyle

            addSourceAndLayerToStyle()
            applyBufferedProperties()

            omhInfoWindow.onStyleLoaded(safeStyle)
        }
    }

    override fun getAlpha(): Float {
        return if (isStyleReady()) {
            markerSymbolLayer.iconOpacity?.toFloat() ?: OmhConstants.DEFAULT_ALPHA
        } else {
            bufferedAlpha
        }
    }

    override fun setAlpha(alpha: Float) {
        if (isStyleReady()) {
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
        if (isStyleReady()) {
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
        return if (isStyleReady()) {
            markerSymbolLayer.visibility == Visibility.VISIBLE
        } else {
            bufferedIsVisible
        }
    }

    override fun setIsVisible(visible: Boolean) {
        if (isStyleReady()) {
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
        return if (isStyleReady()) {
            markerSymbolLayer.iconPitchAlignment == IconPitchAlignment.MAP
        } else {
            bufferedIsFlat
        }
    }

    override fun setIsFlat(flat: Boolean) {
        if (isStyleReady()) {
            markerSymbolLayer.iconPitchAlignment(getIconsPitchAlignment(flat))
            markerSymbolLayer.iconRotationAlignment(getIconsRotationAlignment(flat))
        } else {
            // if the layer was not added to the map yet, buffer the is flat value to apply it later
            bufferedIsFlat = flat
        }
    }

    override fun getRotation(): Float {
        return if (isStyleReady()) {
            markerSymbolLayer.iconRotate?.toFloat() ?: OmhConstants.DEFAULT_ROTATION
        } else {
            bufferedRotation
        }
    }

    override fun setRotation(rotation: Float) {
        if (isStyleReady()) {
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

        // set the default icon
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

    private fun getMarkerIconID(bForCustomIcon: Boolean): String {
        val suffix = if (bForCustomIcon) {
            // a new UUID each time is required for Mapbox to rerender the icon
            // when a custom icon is changed to another custom icon
            "custom-${iconUUIDGenerator.generate()}"
        } else {
            "default"
        }

        return "$markerUUID-omh-marker-icon-$suffix"
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

        // ensure the old icon is removed for memory optimization
        if (lastMarkerIconID != null) style.removeStyleImage(lastMarkerIconID!!)

        val bitmap = DrawableConverter.convertDrawableToBitmap(icon ?: getDefaultIcon())

        iconWidth = bitmap.width
        iconHeight = bitmap.height

        omhInfoWindow.updatePosition() // iconWidth & iconHeight have an impact on the IW's position

        val addImageResult = style.addImage(
            markerImageID,
            bitmap,
            icon === null // apply backgroundColor to the default image, only if icon is null
        )

        addImageResult.error?.let { error ->
            throw IllegalStateException("Failed to add image to map: $error")
        }

        lastMarkerIconID = markerImageID

        return markerImageID
    }

    override fun remove() {
        isRemoved = true

        markerDelegate.removeMarker(markerSymbolLayer.layerId)

        if (isStyleReady()) {
            val removeLayerResult = style.removeStyleLayer(getSymbolLayerID(markerUUID))
            removeLayerResult.error?.let { error ->
                throw IllegalStateException("Failed to remove SymbolLayer from map: $error")
            }

            val removeSourceResult = style.removeStyleSource(getGeoJsonSourceID(markerUUID))
            removeSourceResult.error?.let { error ->
                throw IllegalStateException("Failed to remove GeoJsonSource from map: $error")
            }

            if (lastMarkerIconID != null) {
                val removeImageResult = style.removeStyleImage(lastMarkerIconID!!)
                removeImageResult.error?.let { error ->
                    throw IllegalStateException("Failed to remove image from map: $error")
                }
            }
        }

        omhInfoWindow.remove()
    }

    override fun getZIndex(): Float? {
        logger.logGetterNotSupported("zIndex")
        return null
    }

    override fun setZIndex(zIndex: Float) {
        logger.logSetterNotSupported("zIndex")
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

        internal fun getSymbolLayerID(markerUUID: UUID): String {
            return "$markerUUID-omh-marker-layer"
        }

        internal fun getGeoJsonSourceID(markerUUID: UUID): String {
            return "$markerUUID-omh-marker-geojson-source"
        }
    }
}
