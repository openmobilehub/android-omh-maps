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

package com.openmobilehub.android.maps.plugin.azuremaps.presentation.maps

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import androidx.core.content.res.ResourcesCompat
import com.azure.android.maps.control.Popup
import com.azure.android.maps.control.layer.SymbolLayer
import com.azure.android.maps.control.options.AnchorType
import com.azure.android.maps.control.options.IconRotationAlignment
import com.azure.android.maps.control.options.SymbolLayerOptions
import com.azure.android.maps.control.source.DataSource
import com.mapbox.geojson.Feature
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhMarker
import com.openmobilehub.android.maps.core.presentation.models.OmhCoordinate
import com.openmobilehub.android.maps.core.utils.DrawableConverter
import com.openmobilehub.android.maps.core.utils.cartesian.Offset2D
import com.openmobilehub.android.maps.core.utils.cartesian.rotateOffset
import com.openmobilehub.android.maps.plugin.azuremaps.R
import com.openmobilehub.android.maps.plugin.azuremaps.presentation.interfaces.IMapViewDelegate
import com.openmobilehub.android.maps.plugin.azuremaps.presentation.interfaces.ITouchInteractable
import com.openmobilehub.android.maps.plugin.azuremaps.utils.AnchorConverter
import com.openmobilehub.android.maps.plugin.azuremaps.utils.Constants
import com.openmobilehub.android.maps.plugin.azuremaps.utils.CoordinateConverter
import java.util.UUID
import com.openmobilehub.android.maps.core.presentation.models.Constants as OmhConstants

@SuppressWarnings("TooManyFunctions", "LongParameterList")
internal class OmhMarkerImpl(
    internal val source: DataSource,
    internal val markerUUID: UUID,
    internal val context: Context,
    private val markerSymbolLayer: SymbolLayer,
    private var position: OmhCoordinate,
    initialTitle: String?,
    initialSnippet: String?,
    infoWindowPopup: Popup,
    private var draggable: Boolean,
    private var clickable: Boolean,
    private var backgroundColor: Int?,
    private var alpha: Float = OmhConstants.DEFAULT_ALPHA,
    private var isVisible: Boolean = OmhConstants.DEFAULT_IS_VISIBLE,
    private var isFlat: Boolean = OmhConstants.DEFAULT_IS_FLAT,
    private var rotation: Float = OmhConstants.DEFAULT_ROTATION,
    internal var anchor: Pair<Float, Float> = OmhConstants.ANCHOR_CENTER to OmhConstants.ANCHOR_CENTER,
    initialInfoWindowAnchor: Pair<Float, Float> = OmhConstants.ANCHOR_CENTER to OmhConstants.ANCHOR_TOP,
    initialIcon: Drawable?,
    private val mapViewDelegate: IMapViewDelegate,
) : OmhMarker, ITouchInteractable {

    internal var omhInfoWindow: OmhInfoWindow
    private var isCustomIconSet: Boolean = false
    internal var iconWidth: Int = 0
    internal var iconHeight: Int = 0

    init {
        isCustomIconSet = initialIcon != null

        omhInfoWindow = OmhInfoWindow(
            title = initialTitle,
            snippet = initialSnippet,
            infoWindowAnchor = initialInfoWindowAnchor,
            mapViewDelegate = mapViewDelegate,
            popup = infoWindowPopup,
            omhMarker = this,
        )

        setIcon(initialIcon)
    }

    override fun getPosition(): OmhCoordinate {
        return position
    }

    override fun setPosition(omhCoordinate: OmhCoordinate) {
        position = omhCoordinate
        source.setShapes(
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
        return isVisible && clickable
    }

    override fun setClickable(clickable: Boolean) {
        this.clickable = clickable
    }

    override fun getDraggable(): Boolean {
        return isVisible && draggable
    }

    @SuppressWarnings("MagicNumber")
    internal fun getHandleTranslation(): Offset2D<Double> {
        val discreteAnchor = AnchorConverter.convertContinuousToDiscreteAnchorType(anchor)
        val (offsetCoeffX, offsetCoeffY) = when (discreteAnchor) {
            AnchorType.CENTER -> 0.0 to 0.0
            AnchorType.LEFT -> -0.5 to 0.0
            AnchorType.RIGHT -> 0.5 to 0.0
            AnchorType.TOP -> 0.0 to -1.0
            AnchorType.BOTTOM -> 0.0 to 1.0
            AnchorType.TOP_LEFT -> -0.5 to -1.0
            AnchorType.TOP_RIGHT -> 0.5 to -1.0
            AnchorType.BOTTOM_LEFT -> -0.5 to 1.0
            AnchorType.BOTTOM_RIGHT -> 0.5 to 1.0
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
        anchor = anchorU to anchorV
        markerSymbolLayer.setOptions(
            SymbolLayerOptions.iconAnchor(
                AnchorConverter.convertContinuousToDiscreteAnchorType(anchor)
            )
        )
        omhInfoWindow.updatePosition()
    }

    override fun setInfoWindowAnchor(iwAnchorU: Float, iwAnchorV: Float) {
        omhInfoWindow.setInfoWindowAnchor(iwAnchorU, iwAnchorV)
    }

    override fun getAlpha(): Float {
        return alpha
    }

    override fun setAlpha(alpha: Float) {
        this.alpha = alpha
        reapplyAlpha()
    }

    override fun getSnippet(): String? {
        return omhInfoWindow.getSnippet()
    }

    override fun setSnippet(snippet: String?) {
        omhInfoWindow.setSnippet(snippet)
    }

    override fun setIcon(icon: Drawable?) {
        isCustomIconSet = icon != null

        val addedIconID = addOrUpdateMarkerIconImage(icon)
        // color the icon image using Mapbox's SDF implementation
        markerSymbolLayer.setOptions(
            SymbolLayerOptions.iconImage(addedIconID)
        )
    }

    override fun getIsVisible(): Boolean {
        return isVisible
    }

    internal fun reapplyAlpha() {
        markerSymbolLayer.setOptions(
            SymbolLayerOptions.iconOpacity(if (isVisible) alpha else 0f)
        )
    }

    override fun setIsVisible(visible: Boolean) {
        isVisible = visible
        reapplyAlpha()

        if (!visible) {
            // close the info window if the marker is hidden, for parity
            hideInfoWindow()
        }
    }

    override fun getIsFlat(): Boolean {
        return isFlat
    }

    override fun setIsFlat(flat: Boolean) {
        this.isFlat = flat
        markerSymbolLayer.setOptions(
            SymbolLayerOptions.iconRotationAlignment(getIconsRotationAlignment(flat))
        )
    }

    override fun getRotation(): Float {
        return rotation
    }

    override fun setRotation(rotation: Float) {
        this.rotation = rotation
        markerSymbolLayer.setOptions(
            SymbolLayerOptions.iconRotation(rotation)
        )

        omhInfoWindow.updatePosition()
    }

    override fun getBackgroundColor(): Int? {
        return backgroundColor
    }

    override fun setBackgroundColor(color: Int?) {
        backgroundColor = color

        // set the default icon, also setting isCustomIconSet to keep track
        // of current state for rebuilding the icon
        setIcon(null)
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

        // ensure the other custom icon is removed for memory optimization (if present)
        mapViewDelegate.removeImage(getMarkerIconID(!isCustomIconSet))

        val bitmap = DrawableConverter.convertDrawableToBitmap(icon ?: getDefaultIcon())

        // manually re-color the default icon
        if (!isCustomIconSet) {
            val paint = Paint()
            val filter = PorterDuffColorFilter(
                backgroundColor ?: Constants.DEFAULT_MARKER_COLOR,
                PorterDuff.Mode.SRC_IN
            )
            paint.setColorFilter(filter)

            val canvas = Canvas(bitmap)
            canvas.drawBitmap(bitmap, 0f, 0f, paint)
        }

        iconWidth = bitmap.width
        iconHeight = bitmap.height

        omhInfoWindow.updatePosition() // iconWidth & iconHeight have an impact on the IW's position

        mapViewDelegate.addImage(
            markerImageID,
            bitmap
        )

        return markerImageID
    }

    internal companion object {
        internal fun getIconsRotationAlignment(isFlat: Boolean): String {
            return if (isFlat) {
                IconRotationAlignment.MAP // flat
            } else {
                IconRotationAlignment.VIEWPORT // billboard
            }
        }

        internal fun getSymbolLayerID(markerUUID: UUID): String {
            return "$markerUUID-omh-marker-layer"
        }

        internal fun getSourceID(markerUUID: UUID): String {
            return "$markerUUID-omh-marker-source"
        }
    }
}
