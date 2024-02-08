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
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhCap
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhPatternItem
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhStyleSpan
import kotlinx.parcelize.Parcelize

/**
 * [OmhPolylineOptions] is a class that represents the options for a polyline on a map.
 * A polyline is a series of connected line segments that can form any shape you want on a map.
 * You can customize the appearance of the polyline by changing its color, width, pattern, joint type, and caps.
 *
 * @property points The points that make up the polyline.
 * @property clickable The clickable state of the polyline.
 * @property color The color of the polyline.
 * @property width The width of the polyline.
 * @property isVisible The visibility of the polyline.
 * @property zIndex The z-index of the polyline.
 * @property jointType The joint type of the polyline.
 * @property pattern The pattern of the polyline.
 * @property startCap The start cap of the polyline.
 * @property endCap The end cap of the polyline.
 * @property spans The spans of the polyline.
 */
@SuppressWarnings("LongParameterList")
@Parcelize
class OmhPolylineOptions(
    var points: List<OmhCoordinate> = listOf(),
    var clickable: Boolean? = null,
    var color: Int? = null,
    var width: Float? = null,
    var isVisible: Boolean? = null,
    var zIndex: Float? = null,
    var jointType: Int? = null,
    var pattern: List<OmhPatternItem>? = null,
    var startCap: OmhCap? = null,
    var endCap: OmhCap? = null,
    var spans: List<OmhStyleSpan>? = null,
) : Parcelable
