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

import android.graphics.Bitmap
import android.os.Parcelable
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhStyleSpan
import kotlinx.parcelize.Parcelize

/**
 * [OmhStyleSpanMonochromatic] is a class that represents a monochromatic style span on a map.
 * A style span can be used to customize the appearance of polylines.
 * This class implements the [OmhStyleSpan] interface.
 *
 * @property color The color of the style span.
 * @property segments The number of segments in the style span.
 * @property stamp The bitmap used for the style span.
 */
@Parcelize
class OmhStyleSpanMonochromatic(
    val color: Int,
    override val segments: Double? = null,
    override val stamp: Bitmap? = null
) : OmhStyleSpan, Parcelable
