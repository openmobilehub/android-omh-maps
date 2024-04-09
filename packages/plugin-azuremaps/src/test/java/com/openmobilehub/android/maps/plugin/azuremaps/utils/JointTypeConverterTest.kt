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

import com.azure.android.maps.control.options.LineJoin
import com.openmobilehub.android.maps.core.presentation.models.OmhJointType
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters

@RunWith(Parameterized::class)
internal class JointTypeConverterTest(
    private val data: Pair<Int, String>
) {

    @Test
    fun `convertToAzureMapsLineJoin returns correct String from LineJoin for various continuous joint types`() {
        // Act
        val result = JointTypeConverter.convertToAzureMapsLineJoin(data.first)

        // Assert
        Assert.assertEquals(data.second, result)
    }

    companion object {
        @JvmStatic
        @Parameters
        fun data() = listOf(
            Pair(OmhJointType.BEVEL, LineJoin.BEVEL),
            Pair(OmhJointType.ROUND, LineJoin.ROUND),
            Pair(OmhJointType.DEFAULT, LineJoin.MITER),
            Pair(-1, LineJoin.MITER) // should default to Miter when unsupported value is provided
        )
    }
}
