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
import androidx.annotation.Keep
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

const val DEFAULT_ANCHOR = 0.5f
const val DEFAULT_ALPHA = 1.0f
const val DEFAULT_ROTATION = 0f

/**
 * Defines [OmhMarkerOptions] for a marker.
 * You can customize marker appearance by changing its properties.
 *
 * Implements Parcelable interface to facilitate the usage.
 *
 * @property position The location for the marker.
 * @property title The title for the marker.
 * @property isDraggable Whether the marker is draggable. Default value: `false`
 * @property anchor The anchor for the marker image. Default: `Pair(0.5f, 0.5f)`
 * @property alpha The alpha (transparency) of the marker. Default: `1.0f`
 * @property snippet The text snippet to be shown below marker title.
 * @property isVisible Boolean indicating whether the marker is visible.
 * @property isFlat Boolean representing whether the marker is flat (stuck to the map)
 * or is a billboard (rotates and tilts with the camera).
 * @property rotation The rotation of the marker (degrees, clockwise) with respect to the map. Default: `0f`
 * @property backgroundColor The color of the marker or resets the color to the provider's default value if null.
 */
@Keep
@Parcelize
@SuppressWarnings("LongParameterList")
class OmhMarkerOptions(
    var position: OmhCoordinate = OmhCoordinate(),
    var title: String? = null,
    var isDraggable: Boolean = false,
    var anchor: Pair<Float, Float> = Pair(DEFAULT_ANCHOR, DEFAULT_ANCHOR),
    var alpha: Float = DEFAULT_ALPHA,
    var snippet: String? = null,
    var isVisible: Boolean = true,
    var isFlat: Boolean = false,
    var rotation: Float = DEFAULT_ROTATION,
    var backgroundColor: Int? = null
) : Parcelable {
    @IgnoredOnParcel
    var icon: Drawable? = null
}
