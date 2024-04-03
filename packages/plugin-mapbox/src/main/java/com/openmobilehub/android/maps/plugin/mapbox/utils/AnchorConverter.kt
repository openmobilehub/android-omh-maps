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

import com.mapbox.maps.extension.style.layers.properties.generated.IconAnchor
import com.openmobilehub.android.maps.core.utils.DiscreteAnchor
import com.openmobilehub.android.maps.core.utils.AnchorConverter as CoreAnchorConverter

internal object AnchorConverter {
    /**
     * Converts a continuous anchor in the form of `(x, y)` coordinates to an [IconAnchor].
     */
    fun convertContinuousToDiscreteIconAnchor(anchorContinuous: Pair<Float, Float>): IconAnchor {
        return when (CoreAnchorConverter.genericConvertContinuousToDiscreteAnchor(anchorContinuous)) {
            DiscreteAnchor.CENTER -> IconAnchor.CENTER
            DiscreteAnchor.LEFT -> IconAnchor.LEFT
            DiscreteAnchor.RIGHT -> IconAnchor.RIGHT
            DiscreteAnchor.TOP -> IconAnchor.TOP
            DiscreteAnchor.BOTTOM -> IconAnchor.BOTTOM
            DiscreteAnchor.TOP_LEFT -> IconAnchor.TOP_LEFT
            DiscreteAnchor.TOP_RIGHT -> IconAnchor.TOP_RIGHT
            DiscreteAnchor.BOTTOM_LEFT -> IconAnchor.BOTTOM_LEFT
            DiscreteAnchor.BOTTOM_RIGHT -> IconAnchor.BOTTOM_RIGHT
        }
    }
}
