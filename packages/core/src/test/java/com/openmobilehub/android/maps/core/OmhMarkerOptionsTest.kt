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

import android.os.Parcel
import com.openmobilehub.android.maps.core.Constants.ANOTHER_MARKER_TITLE
import com.openmobilehub.android.maps.core.Constants.LATITUDE
import com.openmobilehub.android.maps.core.Constants.LONGITUDE
import com.openmobilehub.android.maps.core.Constants.MARKER_TITLE
import com.openmobilehub.android.maps.core.presentation.models.OmhCoordinate
import com.openmobilehub.android.maps.core.presentation.models.OmhMarkerOptions
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
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
    fun `given a OmhMarkerOptions, when compared to another with a different OmhCoordinate, then is false`() {
        val compareOmhCoordinate = OmhCoordinate()
        val compareOmhMarkerOptions = OmhMarkerOptions().apply { position = compareOmhCoordinate }

        assertNotEquals(omhMarkerOptions.position, compareOmhMarkerOptions.position)
    }

    @Test
    fun `given a OmhMarkerOptions, when compared to another with a different title, then is false`() {
        val compareOmhMarkerOptions = OmhMarkerOptions().apply {
            position = omhCoordinate
            title = ANOTHER_MARKER_TITLE
        }

        assertNotEquals(omhMarkerOptions.title, compareOmhMarkerOptions.title)
    }

    @Test
    fun `given a Parcel, when createFromParcel and compared to another with same properties, then is true`() {
        val parcel = createOmhMarkerOptionsParcel(omhMarkerOptions)
        val createdFromParcel = OmhMarkerOptions.CREATOR.createFromParcel(parcel)

        assertEquals(omhMarkerOptions.position, createdFromParcel.position)
    }

    @Test
    fun `given a Parcel, when createFromParcel and compared to another with different OmhCoordinate, then is false`() {
        val compareOmhCoordinate = OmhCoordinate()
        val compareOmhMarkerOptions = OmhMarkerOptions().apply { position = compareOmhCoordinate }
        val parcel = createOmhMarkerOptionsParcel(omhMarkerOptions)
        val createdFromParcel = OmhMarkerOptions.CREATOR.createFromParcel(parcel)

        assertNotEquals(compareOmhMarkerOptions.position, createdFromParcel.position)
    }

    @Test
    fun `given a Parcel, when createFromParcel and compared to another with a different title, then is false`() {
        val compareOmhMarkerOptions = OmhMarkerOptions().apply {
            position = omhCoordinate
            title = ANOTHER_MARKER_TITLE
        }
        val parcel = createOmhMarkerOptionsParcel(omhMarkerOptions)
        val createdFromParcel = OmhMarkerOptions.CREATOR.createFromParcel(parcel)

        assertNotEquals(compareOmhMarkerOptions.title, createdFromParcel.title)
    }

    @Test
    fun `given a Parcel, when createFromParcel and compared to another without title, then is false`() {
        val compareOmhMarkerOptions = OmhMarkerOptions().apply {
            position = omhCoordinate
        }
        val parcel = createOmhMarkerOptionsParcel(omhMarkerOptions)
        val createdFromParcel = OmhMarkerOptions.CREATOR.createFromParcel(parcel)

        assertNotEquals(compareOmhMarkerOptions.title, createdFromParcel.title)
    }

    @Test
    fun `given a Parcel without title, when createFromParcel, then title is null`() {
        val omhMarkerOptions = OmhMarkerOptions().apply {
            position = omhCoordinate
        }
        val parcel = createOmhMarkerOptionsParcel(omhMarkerOptions)
        val createdFromParcel = OmhMarkerOptions.CREATOR.createFromParcel(parcel)

        assertNull(createdFromParcel.title)
    }

    @Test
    fun `given a Parcel without isDraggable, when createFromParcel, then isDraggable is false`() {
        val omhMarkerOptions = OmhMarkerOptions().apply {
            position = omhCoordinate
        }
        val parcel = createOmhMarkerOptionsParcel(omhMarkerOptions)
        val createdFromParcel = OmhMarkerOptions.CREATOR.createFromParcel(parcel)

        assertFalse(createdFromParcel.isDraggable)
    }

    @Test
    fun `given a Parcel with isDraggable that is true, when createFromParcel, then isDraggable is true`() {
        val omhMarkerOptions = OmhMarkerOptions().apply {
            position = omhCoordinate
            isDraggable = true
        }
        val parcel = createOmhMarkerOptionsParcel(omhMarkerOptions)
        val createdFromParcel = OmhMarkerOptions.CREATOR.createFromParcel(parcel)

        assertTrue(createdFromParcel.isDraggable)
    }

    private fun createOmhMarkerOptionsParcel(omhMarkerOptions: OmhMarkerOptions): Parcel {
        val parcel = mockk<Parcel>()

        every { parcel.writeParcelable(any(), any()) } returns Unit
        every { parcel.writeString(any()) } returns Unit
        every { parcel.writeByte(any()) } returns Unit
        every {
            parcel.readParcelable<OmhCoordinate>(OmhCoordinate::class.java.classLoader)
        } returns omhMarkerOptions.position
        every { parcel.readString() } returns omhMarkerOptions.title
        every { parcel.readByte() } returns (if (omhMarkerOptions.isDraggable) 1 else 0).toByte()

        omhMarkerOptions.writeToParcel(parcel, omhMarkerOptions.describeContents())

        every { parcel.setDataPosition(any()) } returns Unit
        every { parcel.dataPosition() } returns 0

        return parcel
    }
}
