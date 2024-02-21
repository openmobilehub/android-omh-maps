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

package com.openmobilehub.android.maps.core

import com.openmobilehub.android.maps.core.Constants.ANOTHER_MARKER_TITLE
import com.openmobilehub.android.maps.core.Constants.LATITUDE
import com.openmobilehub.android.maps.core.Constants.LONGITUDE
import com.openmobilehub.android.maps.core.Constants.MARKER_TITLE
import com.openmobilehub.android.maps.core.presentation.models.DEFAULT_ALPHA
import com.openmobilehub.android.maps.core.presentation.models.DEFAULT_ANCHOR
import com.openmobilehub.android.maps.core.presentation.models.DEFAULT_ROTATION
import com.openmobilehub.android.maps.core.presentation.models.OmhCoordinate
import com.openmobilehub.android.maps.core.presentation.models.OmhMarkerOptions
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Test

internal class OmhMarkerOptionsTest {
    private val omhCoordinate = OmhCoordinate(LATITUDE, LONGITUDE)
    private val omhMarkerOptions = OmhMarkerOptions().apply {
        position = omhCoordinate
        title = MARKER_TITLE
    }

    @Test
    fun `given a OmhCoordinate, when is assigned in a OmhMarkerOptions, then was assigned the OmhCoordinate`() {
        val newOmhMarkerOptions = OmhMarkerOptions().apply { position = omhCoordinate }

        assertEquals(newOmhMarkerOptions.position, omhCoordinate)
    }

    @Test
    fun `given a title, when is assigned in a OmhMarkerOptions, then was assigned the title`() {
        val newOmhMarkerOptions = OmhMarkerOptions().apply {
            position = omhCoordinate
            title = MARKER_TITLE
        }

        assertEquals(newOmhMarkerOptions.title, MARKER_TITLE)
    }

    @Test
    fun `given a OmhMarkerOptions, when no title is assigned, then title is null`() {
        val newOmhMarkerOptions = OmhMarkerOptions().apply {
            position = omhCoordinate
        }

        assertNull(newOmhMarkerOptions.title)
    }

    @Test
    fun `given a OmhMarkerOptions, when compare to another with same OmhCoordinate, then is true`() {
        val newOmhMarkerOptions = OmhMarkerOptions().apply { position = omhCoordinate }

        assertEquals(newOmhMarkerOptions.position, omhMarkerOptions.position)
    }

    @Test
    fun `given a OmhMarkerOptions, when compare to another with same title, then is true`() {
        val newOmhMarkerOptions = OmhMarkerOptions().apply {
            position = omhCoordinate
            title = MARKER_TITLE
        }

        assertEquals(newOmhMarkerOptions.position, omhMarkerOptions.position)
    }

    @Test
    fun `given a OmhMarkerOptions, comparison to another with a different OmhCoordinate results in false`() {
        val compareOmhCoordinate = OmhCoordinate()
        val compareOmhMarkerOptions = OmhMarkerOptions().apply { position = compareOmhCoordinate }

        assertNotEquals(omhMarkerOptions.position, compareOmhMarkerOptions.position)
    }

    @Test
    fun `given a OmhMarkerOptions, comparison to another with a different title results in false`() {
        val compareOmhMarkerOptions = OmhMarkerOptions().apply {
            position = omhCoordinate
            title = ANOTHER_MARKER_TITLE
        }

        assertNotEquals(omhMarkerOptions.title, compareOmhMarkerOptions.title)
    }

    @Test
    fun `when initializing OmhMarkerOptions, all default constructor arguments are applied as expected`() {
        assertEquals(omhMarkerOptions.draggable, false)
        assertEquals(omhMarkerOptions.anchor, Pair(DEFAULT_ANCHOR, DEFAULT_ANCHOR))
        assertEquals(omhMarkerOptions.alpha, DEFAULT_ALPHA)
        assertEquals(omhMarkerOptions.snippet, null)
        assertEquals(omhMarkerOptions.isVisible, true)
        assertEquals(omhMarkerOptions.isFlat, false)
        assertEquals(omhMarkerOptions.rotation, DEFAULT_ROTATION)
        assertEquals(omhMarkerOptions.backgroundColor, null)
        assertEquals(omhMarkerOptions.icon, null)
    }
}
