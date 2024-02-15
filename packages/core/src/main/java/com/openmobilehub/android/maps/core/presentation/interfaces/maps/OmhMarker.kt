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
     * Sets the title of the marker.
     *
     * @param title sets the title. If null, the title is cleared.
     */
    fun setTitle(title: String?)

    /**
     * Checks whether the marker is draggable.
     * Default: `false`.
     *
     * @return a boolean indicating whether the marker is draggable.
     */
    fun getIsDraggable(): Boolean

    /**
     * Sets whether the marker is draggable.
     *
     * @param isDraggable sets whether the marker is draggable.
     */
    fun setIsDraggable(isDraggable: Boolean)

    /**
     * Sets the anchor point of marker image.
     *
     * @param anchorU the normalized (`0` - `1`) icon X coordinate specifier; default: `0.5`.
     * @param anchorV the normalized (`0` - `1`) icon Y coordinate specifier; default: `0.5`.
     */
    fun setAnchor(anchorU: Float, anchorV: Float)

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
     * Sets the color of the marker or resets the color to the provider's default value if null.
     * Note: this overrides [setIcon].
     *
     * @param color the color of the marker or null.
     */
    fun setBackgroundColor(color: Int?)
}
