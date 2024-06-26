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

package com.openmobilehub.android.maps.core.presentation.models

import android.graphics.drawable.Drawable
import android.os.Parcelable
import androidx.annotation.ColorInt
import androidx.annotation.Keep
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

object Constants {
    const val ANCHOR_CENTER = 0.5f
    const val ANCHOR_BOTTOM = 1.0f
    const val ANCHOR_TOP = 0.0f
    const val DEFAULT_ALPHA = 1.0f
    const val DEFAULT_ROTATION = 0f
    const val DEFAULT_IS_VISIBLE = true
    const val DEFAULT_IS_FLAT = false
}

/**
 * Defines [OmhMarkerOptions] for a marker.
 * You can customize marker appearance by changing its properties.
 *
 * Implements Parcelable interface to facilitate the usage.
 *
 * @property position The location for the marker.
 * @property title The title for the marker.
 * @property clickable Whether the marker is clickable. Default value: `true`
 * @property draggable Whether the marker is draggable. Default value: `false`
 * @property anchor The anchor for the marker image. Default: `Pair(0.5f, 0.5f)`
 * @property infoWindowAnchor The anchor for the info window. Default: `Pair(0.5f, 0.0f)`
 * @property alpha The alpha (transparency) of the marker. Default: `1.0f`
 * @property snippet The text snippet to be shown below marker title.
 * @property isVisible Boolean indicating whether the marker is visible.
 * @property isFlat Boolean representing whether the marker is flat (stuck to the map)
 * or is a billboard (rotates and tilts with the camera).
 * @property rotation The rotation of the marker (degrees, clockwise) with respect to the map. Default: `0f`
 * @property backgroundColor The ARGB color of the marker or resets the color to the provider's default value if null.
 *
 * **NOTE:** please remember to pass color integers with set bytes corresponding
 * to alpha channel (e.g. of shape `0xAARRGGBB`).
 * @property icon The icon [Drawable] for the marker. Overrides [backgroundColor] if not null.
 * @property zIndex The z-index of the marker. Default: `null`
 */
@Keep
@Parcelize
@SuppressWarnings("LongParameterList")
data class OmhMarkerOptions(
    var position: OmhCoordinate = OmhCoordinate(),
    var title: String? = null,
    var clickable: Boolean = true,
    var draggable: Boolean = false,
    var anchor: Pair<Float, Float> = Pair(Constants.ANCHOR_CENTER, Constants.ANCHOR_CENTER),
    var infoWindowAnchor: Pair<Float, Float> = Pair(
        Constants.ANCHOR_CENTER,
        Constants.ANCHOR_TOP
    ),
    var alpha: Float = Constants.DEFAULT_ALPHA,
    var snippet: String? = null,
    var isVisible: Boolean = Constants.DEFAULT_IS_VISIBLE,
    var isFlat: Boolean = Constants.DEFAULT_IS_FLAT,
    var rotation: Float = Constants.DEFAULT_ROTATION,
    @ColorInt var backgroundColor: Int? = null,
    @IgnoredOnParcel var icon: Drawable? = null,
    var zIndex: Float? = null
) : Parcelable
