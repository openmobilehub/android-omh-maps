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

package com.openmobilehub.android.maps.plugin.mapbox.utils

import android.graphics.Color
import com.mapbox.maps.extension.style.layers.properties.generated.LineCap

internal object Constants {
    // Common
    const val PROVIDER_NAME = "Mapbox"

    // To have parity with Google Maps
    const val INITIAL_REGION_LATITUDE = 0.0
    const val INITIAL_REGION_LONGITUDE = 0.0
    const val INITIAL_REGION_ZOOM = 0.0

    // UI Controls
    const val MAPBOX_ICON_SIZE = 48
    const val MAPBOX_ICON_MARGIN = 8

    val DEFAULT_LINE_CAP = LineCap.BUTT
    val DEFAULT_POLYLINE_COLOR = Color.BLACK
}
