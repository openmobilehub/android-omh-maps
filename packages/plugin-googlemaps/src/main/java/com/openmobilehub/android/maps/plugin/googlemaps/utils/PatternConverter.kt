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

package com.openmobilehub.android.maps.plugin.googlemaps.utils

import com.google.android.gms.maps.model.Dash
import com.google.android.gms.maps.model.Dot
import com.google.android.gms.maps.model.Gap
import com.google.android.gms.maps.model.PatternItem
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhPatternItem
import com.openmobilehub.android.maps.core.presentation.models.OmhDash
import com.openmobilehub.android.maps.core.presentation.models.OmhDot
import com.openmobilehub.android.maps.core.presentation.models.OmhGap

object PatternConverter {
    fun convertToPatternItem(omhPatternItem: OmhPatternItem): PatternItem? {
        return when (omhPatternItem) {
            is OmhDot -> Dot()
            is OmhDash -> Dash(omhPatternItem.length)
            is OmhGap -> Gap(omhPatternItem.length)
            else -> null
        }
    }

    fun convertToOmhPatternItem(patternItem: PatternItem): OmhPatternItem? {
        return when (patternItem) {
            is Dot -> OmhDot()
            is Dash -> OmhDash(patternItem.length)
            is Gap -> OmhGap(patternItem.length)
            else -> null
        }
    }
}
