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

package com.openmobilehub.android.maps.core.presentation.interfaces.maps

import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import com.openmobilehub.android.maps.core.presentation.models.OmhCoordinate

/**
 * Abstraction to provide access to [OmhMarker].
 * [OmhMarker] is an icon placed at a particular point on the map's surface.
 */
@SuppressWarnings("TooManyFunctions")
interface OmhMarker {
    /**
     * The [OmhCoordinate] value for the marker's position on the map.
     *
     * @return [OmhCoordinate] object specifying the marker's current position.
     */
    fun getPosition(): OmhCoordinate

    /**
     * Sets the location of the marker.
     *
     * @param omhCoordinate sets the location.
     */
    fun setPosition(omhCoordinate: OmhCoordinate)

    /**
     * A text string that's displayed in an info window when the user taps the marker.
     *
     * @return a string containing the marker's title.
     */
    fun getTitle(): String?

    /**
     * Sets the title of the marker. When null, the info window associated with this marker is disabled.
     *
     * @param title sets the title. If null, the title is cleared & info window is disabled.
     */
    fun setTitle(title: String?)

    /**
     * Checks whether the marker is clickable.
     * If the marker is not clickable, the info window associated with this marker
     * will be disabled and no click events will be propagated.
     * Default: `true`.
     *
     * @return a boolean indicating whether the marker is clickable.
     */
    fun getClickable(): Boolean

    /**
     * Sets whether the marker is clickable.
     *
     * @param clickable sets whether the marker is clickable.
     */
    fun setClickable(clickable: Boolean)

    /**
     * Checks whether the marker is draggable.
     * Default: `false`.
     *
     * @return a boolean indicating whether the marker is draggable.
     */
    fun getDraggable(): Boolean

    /**
     * Sets whether the marker is draggable.
     *
     * @param draggable sets whether the marker is draggable.
     */
    fun setDraggable(draggable: Boolean)

    /**
     * Sets the anchor point of marker image.
     *
     * @param anchorU the normalized (`0` - `1`) icon X coordinate specifier; default: `0.5`.
     * @param anchorV the normalized (`0` - `1`) icon Y coordinate specifier; default: `0.5`.
     */
    fun setAnchor(anchorU: Float, anchorV: Float)

    /**
     * Sets the anchor point of marker info window.
     *
     * @param iwAnchorU the normalized (`0` - `1`) icon X coordinate specifier; default: `0.5`.
     * @param iwAnchorV the normalized (`0` - `1`) icon Y coordinate specifier; default: `0.5`.
     */
    fun setInfoWindowAnchor(iwAnchorU: Float, iwAnchorV: Float)

    /**
     * Gets the alpha (transparency) of the marker.
     *
     * @return the normalized (`0` - `1`) alpha value of the marker.
     */
    fun getAlpha(): Float

    /**
     * Gets the alpha (transparency) of the marker.
     *
     * @param alpha the normalized (`0` - `1`) alpha value of the marker; alpha is `1.0` by default.
     */
    fun setAlpha(alpha: Float)

    /**
     * Gets the text snippet to be shown below marker title.
     *
     * @return a string being the current text snippet or null if snippet is disabled.
     */
    fun getSnippet(): String?

    /**
     * Sets the text snippet to be shown below marker title.
     *
     * @param snippet a string being the text snippet to be set or null to disable the snippet.
     */
    fun setSnippet(snippet: String?)

    /**
     * Sets the icon to display for the marker.
     * Note: this overrides [setBackgroundColor].
     *
     * @param icon Drawable representing the icon or null to use
     * the provider's default icon.
     */
    fun setIcon(icon: Drawable?)

    /**
     * Gets whether the marker is visible.
     *
     * @return a boolean indicating whether the marker is visible.
     */
    fun getIsVisible(): Boolean

    /**
     * Sets whether the marker is visible.
     * If the marker is not visible, the info window associated with this marker
     * will be disabled and no interaction events will be propagated.
     *
     * @param visible sets whether the marker is visible.
     */
    fun setIsVisible(visible: Boolean)

    /**
     * Gets whether the marker is flat (stuck to the map) or is a billboard (rotates and tilts with the camera).
     * Default: `false` (billboard).
     *
     * @return a boolean indicating whether the marker is flat (`true`) or a billboard (`false`).
     */
    fun getIsFlat(): Boolean

    /**
     * Sets whether the marker is flat (stuck to the map) or is a billboard (rotates and tilts with the camera).
     *
     * @param flat sets whether the marker is flat (`true`) or a billboard (`false`).
     */
    fun setIsFlat(flat: Boolean)

    /**
     * Gets the rotation of the marker (degrees, clockwise) with respect to the map; default: `0`.
     *
     * @return the rotation of the marker.
     */
    fun getRotation(): Float

    /**
     * Sets the rotation of the marker (degrees, clockwise) with respect to the map; default: `0`.
     *
     * @param rotation the rotation of the marker.
     */
    fun setRotation(rotation: Float)

    /**
     * Gets the ARGB background color of the marker represented as an integer.
     *
     * @return the ARGB color of the marker or null if not set.
     */
    @ColorInt
    fun getBackgroundColor(): Int?

    /**
     * Sets the color of the marker or resets the color to the provider's default value if null.
     * Note: this overrides [setIcon].
     *
     * @param color ARGB color of the marker or `null`.
     *
     * **NOTE:** please remember to pass color integers with set bytes corresponding
     * to alpha channel (e.g. of shape `0xAARRGGBB`).
     */
    fun setBackgroundColor(@ColorInt color: Int?)

    /**
     * Imperatively shows the info window associated with this marker.
     * If custom info window is set and the window is already shown, it will be re-rendered.
     * Note: this overrides the info-window-disabling behaviour based on the value of [setIsVisible] and [setClickable].
     */
    fun showInfoWindow()

    /**
     * Imperatively hides the info window associated with this marker.
     * Note: this overrides the info-window-disabling behaviour based on the value of [setIsVisible] and [setClickable].
     */
    fun hideInfoWindow()

    /**
     * Gets whether the info window associated with this marker is currently being shown.
     *
     * @return a boolean indicating whether the info window is currently being shown.
     */
    fun getIsInfoWindowShown(): Boolean

    /**
     * Invalidates the info window associated with this marker, causing the re-render of the info window.
     */
    fun invalidateInfoWindow()

    /**
     * Removes the marker from the map.
     */
    fun remove()

    /**
     * Gets the zIndex of the marker, which specifies the order in which the marker is drawn on the map.
     *
     * @return the zIndex of the marker or null if not set.
     */
    fun getZIndex(): Float?

    /**
     * Sets the zIndex of the marker, which specifies the order in which the marker is drawn on the map.
     *
     * @param zIndex the zIndex of the marker.
     */
    fun setZIndex(zIndex: Float)
}
