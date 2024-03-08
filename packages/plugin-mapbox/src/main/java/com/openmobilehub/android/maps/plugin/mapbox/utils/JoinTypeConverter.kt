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

import com.mapbox.maps.extension.style.layers.properties.generated.LineJoin
import com.openmobilehub.android.maps.core.presentation.models.OmhJointType

object JoinTypeConverter {
    fun convertToLineJoin(joinType: Int): LineJoin {
        return when (joinType) {
            OmhJointType.BEVEL -> LineJoin.BEVEL
            OmhJointType.ROUND -> LineJoin.ROUND
            else -> LineJoin.MITER
        }
    }

    fun convertToOmhJointType(lineJoin: LineJoin?): Int {
        return when (lineJoin) {
            LineJoin.BEVEL -> OmhJointType.BEVEL
            LineJoin.ROUND -> OmhJointType.ROUND
            else -> OmhJointType.DEFAULT
        }
    }
}
