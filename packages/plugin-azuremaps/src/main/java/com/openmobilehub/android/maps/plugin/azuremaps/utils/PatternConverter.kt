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

import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhPatternItem
import com.openmobilehub.android.maps.core.presentation.models.OmhDash
import com.openmobilehub.android.maps.core.presentation.models.OmhGap
import com.openmobilehub.android.maps.core.utils.logging.UnsupportedFeatureLogger

object PatternConverter {

    /**
     * Converts list of OmhPatternItem into list of the alternating dashes and gaps lengths.
     * Only OmhDash and OmhGap items are supported. If same consecutive items types are provided,
     * a additional item is inserted with value 0 as AzureMapsPattern expects alternating dashes and gaps.
     *
     * @return The lengths of the alternating dashes and gaps that form the dash pattern.
     */
    fun convertToAzureMapsPattern(
        omhPatternItems: List<OmhPatternItem>,
        logger: UnsupportedFeatureLogger? = null
    ): List<Float> {
        return omhPatternItems.flatMapIndexed { index, omhPatternItem ->
            val shouldBeDash = index % 2 == 0

            convertNextOmhPatternItem(omhPatternItem, shouldBeDash, logger)
        }
    }

    @SuppressWarnings("ReturnCount")
    private fun convertNextOmhPatternItem(
        omhPatternItem: OmhPatternItem,
        shouldBeDash: Boolean,
        logger: UnsupportedFeatureLogger? = null
    ): List<Float> {
        return when (omhPatternItem) {
            is OmhDash -> {
                if (shouldBeDash) {
                    return listOf(omhPatternItem.length)
                }

                listOf(0.0f, omhPatternItem.length)
            }
            is OmhGap -> {
                if (!shouldBeDash) {
                    return listOf(omhPatternItem.length)
                }

                listOf(0.0f, omhPatternItem.length)
            }
            else -> {
                logger?.logFeatureSetterPartiallySupported("pattern", "unsupported pattern item")
                return listOf(0.0f)
            }
        }
    }
}
