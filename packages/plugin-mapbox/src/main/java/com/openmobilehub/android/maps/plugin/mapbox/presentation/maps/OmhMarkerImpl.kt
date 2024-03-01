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

import android.graphics.drawable.Drawable
import androidx.core.content.res.ResourcesCompat
import com.mapbox.geojson.Feature
import com.mapbox.maps.MapView
import com.mapbox.maps.extension.style.layers.generated.SymbolLayer
import com.mapbox.maps.extension.style.layers.properties.generated.IconPitchAlignment
import com.mapbox.maps.extension.style.layers.properties.generated.IconRotationAlignment
import com.mapbox.maps.extension.style.layers.properties.generated.Visibility
import com.mapbox.maps.extension.style.sources.generated.GeoJsonSource
import com.mapbox.maps.extension.style.sources.updateGeoJSONSourceFeatures
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhMarker
import com.openmobilehub.android.maps.core.presentation.models.OmhCoordinate
import com.openmobilehub.android.maps.core.utils.DrawableConverter
import com.openmobilehub.android.maps.plugin.mapbox.R
import com.openmobilehub.android.maps.plugin.mapbox.utils.AnchorConverter
import com.openmobilehub.android.maps.plugin.mapbox.utils.Constants
import com.openmobilehub.android.maps.plugin.mapbox.utils.CoordinateConverter
import java.util.UUID

@SuppressWarnings("TooManyFunctions", "LongParameterList")
internal class OmhMarkerImpl(
    private val markerUUID: UUID,
    private val mapView: MapView,
    private var position: OmhCoordinate,
    private var title: String?,
    private var snippet: String?,
    private var draggable: Boolean,
    private var clickable: Boolean,
    private var backgroundColor: Int?,
    initialIcon: Drawable?
) : OmhMarker {

    private lateinit var geoJsonSource: GeoJsonSource
    private lateinit var symbolLayer: SymbolLayer
    private var isCustomIconSet: Boolean = false

    init {
        isCustomIconSet = initialIcon != null
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
        updatePositionVar(omhCoordinate)
        geoJsonSource.updateGeoJSONSourceFeatures(
            listOf(
                Feature.fromGeometry(
                    CoordinateConverter.convertToPoint(omhCoordinate)
                )
            )
        )
    }

    fun updatePositionVar(omhCoordinate: OmhCoordinate) {
        position = omhCoordinate
        // TODO: make sure this is called from outside on drag!
    }

    override fun getTitle(): String? {
        return title
    }

    override fun setTitle(title: String?) {
        this.title = title
        // TODO: implement info window functionality
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

    override fun setDraggable(draggable: Boolean) {
        this.draggable = draggable
    }

    override fun setAnchor(anchorU: Float, anchorV: Float) {
        symbolLayer.iconAnchor(
            AnchorConverter.convertContinuousToDiscreteIconAnchor(anchorU to anchorV)
        )
    }

    override fun getAlpha(): Float {
        return symbolLayer.iconOpacity?.toFloat() ?: 1.0f
    }

    override fun setAlpha(alpha: Float) {
        symbolLayer.iconOpacity(alpha.toDouble())
    }

    override fun getSnippet(): String? {
        return snippet
    }

    override fun setSnippet(snippet: String?) {
        this.snippet = snippet
        // TODO: implement info window functionality
    }

    override fun setIcon(icon: Drawable?) {
        isCustomIconSet = icon != null

        val addedIconID = addOrUpdateMarkerIconImage(icon)
        // color the icon image using Mapbox's SDF implementation
        symbolLayer.iconColor(Constants.DEFAULT_MARKER_COLOR)
        symbolLayer.iconImage(addedIconID)
    }

    override fun getIsVisible(): Boolean {
        return symbolLayer.visibility == Visibility.VISIBLE
    }

    override fun setIsVisible(visible: Boolean) {
        symbolLayer.visibility(getIconVisibility(visible))
    }

    override fun getIsFlat(): Boolean {
        return symbolLayer.iconPitchAlignment == IconPitchAlignment.MAP
    }

    override fun setIsFlat(flat: Boolean) {
        symbolLayer.iconPitchAlignment(getIconPitchAlignment(flat))
        symbolLayer.iconRotationAlignment(getIconRotationAlignment(flat))
    }

    override fun getRotation(): Float {
        return symbolLayer.iconRotate?.toFloat() ?: 0.0f
    }

    override fun setRotation(rotation: Float) {
        symbolLayer.iconRotate(rotation.toDouble())
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

    private fun getIconID(bForCustomIcon: Boolean): String {
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
            mapView.context.resources,
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
    internal fun addOrUpdateMarkerIconImage(
        icon: Drawable?,
    ): String {
        val markerImageID = getIconID(icon != null)

        // ensure the other icon is removed for memory optimization
        mapView.mapboxMap.removeStyleImage(getIconID(!isCustomIconSet))

        val addImageResult = mapView.mapboxMap.addImage(
            markerImageID,
            DrawableConverter.convertDrawableToBitmap(
                icon ?: getDefaultIcon()
            ),
            icon == null // apply backgroundColor to the default image, only if icon is null
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
