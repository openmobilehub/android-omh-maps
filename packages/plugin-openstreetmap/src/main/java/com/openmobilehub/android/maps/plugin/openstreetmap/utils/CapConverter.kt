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

package com.openmobilehub.android.maps.plugin.openstreetmap.utils

import android.graphics.Paint
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhCap
import com.openmobilehub.android.maps.core.presentation.models.OmhButtCap
import com.openmobilehub.android.maps.core.presentation.models.OmhRoundCap
import com.openmobilehub.android.maps.core.presentation.models.OmhSquareCap

object CapConverter {
    fun convertToPaintCap(omhCap: OmhCap): Paint.Cap {
        return when (omhCap) {
            is OmhRoundCap -> Paint.Cap.ROUND
            is OmhSquareCap -> Paint.Cap.SQUARE
            else -> Paint.Cap.BUTT
        }
    }

    fun convertToOmhCap(lineCap: Paint.Cap): OmhCap {
        return when (lineCap) {
            Paint.Cap.ROUND -> OmhRoundCap()
            Paint.Cap.SQUARE -> OmhSquareCap()
            else -> OmhButtCap()
        }
    }
}
