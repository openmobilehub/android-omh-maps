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

package com.openmobilehub.android.maps.plugin.azuremaps.utils

import com.azure.android.maps.control.options.AnchorType
import com.openmobilehub.android.maps.core.utils.DiscreteAnchor
import com.openmobilehub.android.maps.core.utils.AnchorConverter as CoreAnchorConverter

internal object AnchorConverter {
    /**
     * Converts a continuous anchor to an [String] constant defined in Azure Maps' [AnchorType].
     */
    fun convertContinuousToDiscreteAnchorType(anchorContinuous: Pair<Float, Float>): String {
        return when (CoreAnchorConverter.genericConvertContinuousToDiscreteAnchor(anchorContinuous)) {
            DiscreteAnchor.CENTER -> AnchorType.CENTER
            DiscreteAnchor.LEFT -> AnchorType.LEFT
            DiscreteAnchor.RIGHT -> AnchorType.RIGHT
            DiscreteAnchor.TOP -> AnchorType.TOP
            DiscreteAnchor.BOTTOM -> AnchorType.BOTTOM
            DiscreteAnchor.TOP_LEFT -> AnchorType.TOP_LEFT
            DiscreteAnchor.TOP_RIGHT -> AnchorType.TOP_RIGHT
            DiscreteAnchor.BOTTOM_LEFT -> AnchorType.BOTTOM_LEFT
            DiscreteAnchor.BOTTOM_RIGHT -> AnchorType.BOTTOM_RIGHT
        }
    }
}
