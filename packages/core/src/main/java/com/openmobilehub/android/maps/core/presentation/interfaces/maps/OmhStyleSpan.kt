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

import android.graphics.Bitmap
import android.os.Parcelable

/**
 * `OmhStyleSpan` is an interface that provides an abstraction for a style span on a map.
 * A style span is a segment of a polyline or polygon that has a specific style.
 * You can customize the appearance of the style span by changing its color, pattern, and texture.
 */
interface OmhStyleSpan : Parcelable {
    /**
     * Gets the length of the style span in screen pixels.
     * @return A double representing the length of the style span.
     */
    val segments: Double?

    /**
     * Gets the texture of the style span.
     * @return A Bitmap representing the texture of the style span.
     */
    val stamp: Bitmap?
}
