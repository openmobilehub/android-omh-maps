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

import android.os.Parcelable
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhPatternItem
import kotlinx.parcelize.Parcelize

/**
 * [OmhPolygonOptions] is a class that represents the options for a polygon on a map.
 * A polygon is a shape with multiple edges and fill on a map.
 * You can customize the appearance of the polygon by changing its properties.
 *
 * @property clickable The clickable state of the polygon.
 * @property fillColor The color of the polygon.
 * @property holes The holes in the polygon.
 * @property isVisible The visibility of the polygon.
 * @property outline The points that make up the polygon.
 * @property strokeColor The color of the polygon's stroke.
 * @property strokeJointType The joint type of the polygon's stroke.
 * @property strokePattern The pattern of the polygon's stroke.
 * @property strokeWidth The width of the polygon's stroke.
 * @property zIndex The z-index of the polygon.
 */
@SuppressWarnings("LongParameterList")
@Parcelize
class OmhPolygonOptions(
    var outline: List<OmhCoordinate> = listOf(),
    var clickable: Boolean? = null,
    var fillColor: Int? = null,
    var holes: List<List<OmhCoordinate>>? = null,
    var isVisible: Boolean? = null,
    var strokeColor: Int? = null,
    var strokeJointType: Int? = null,
    var strokePattern: List<OmhPatternItem>? = null,
    var strokeWidth: Float? = null,
    var zIndex: Float? = null
) : Parcelable
