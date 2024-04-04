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
import com.openmobilehub.android.maps.core.presentation.models.OmhDot
import com.openmobilehub.android.maps.core.presentation.models.OmhGap
import com.openmobilehub.android.maps.core.utils.logging.UnsupportedFeatureLogger

object PatternConverter {
    fun convertToAzureMapsPattern(
        omhPatternItems: List<OmhPatternItem>,
        logger: UnsupportedFeatureLogger
    ): List<Float> {
        return omhPatternItems.mapIndexedNotNull { index, omhPatternItem ->
            val shouldBeDash = index % 2 == 0

            convertToAzureMapsPattern(omhPatternItem, shouldBeDash, logger)
        }
    }

    @SuppressWarnings("ReturnCount")
    private fun convertToAzureMapsPattern(
        omhPatternItem: OmhPatternItem,
        shouldBeDash: Boolean,
        logger: UnsupportedFeatureLogger
    ): Float? {
        return when (omhPatternItem) {
            is OmhDot -> {
                logger.logFeatureSetterPartiallySupported("pattern", "Dot item is not supported")
                null
            }

            is OmhDash -> {
                if (shouldBeDash) {
                    return omhPatternItem.length
                }

                logger.logFeatureSetterPartiallySupported("pattern", "unsupported pattern order")
                return null
            }

            is OmhGap -> {
                if (!shouldBeDash) {
                    return omhPatternItem.length
                }

                logger.logFeatureSetterPartiallySupported("pattern", "unsupported pattern order")
                return null
            }

            else -> {
                logger.logFeatureSetterPartiallySupported("pattern", "unsupported patter item")
                return null
            }
        }
    }
}
