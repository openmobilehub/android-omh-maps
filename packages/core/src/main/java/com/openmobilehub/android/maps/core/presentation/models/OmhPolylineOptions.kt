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

package com.openmobilehub.android.maps.core.presentation.models

import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.Keep
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhCap

/**
 * Defines OmhMarkerOptions for a marker.
 *
 * Implements Parcelable interface to facilitate the usage.
 */
@Keep
class OmhPolylineOptions() : Parcelable {
    var points: Array<OmhCoordinate> = arrayOf()
    var color: Int? = null
    var width: Float? = null
    var isVisible: Boolean? = null
    var zIndex: Float? = null
    var jointType: Int? = OmhJointType.DEFAULT
    var pattern: List<OmhPatternItem>? = null
    var startCap: OmhCap? = null
    var endCap: OmhCap? = null

    /**
     * Constructs a OmhMarkerOptions with the given Parcel.
     *
     * @param parcel container for the OmhCoordinate.
     */
    constructor(parcel: Parcel) : this() {
        val pointsFromParcel: Array<OmhCoordinate>? = parcel.createTypedArray(OmhCoordinate.CREATOR)
        if (pointsFromParcel != null) {
            points = pointsFromParcel
        }
        width = parcel.readFloat()
        color = parcel.readInt()
        isVisible = parcel.readByte() != 0.toByte()
        zIndex = parcel.readFloat()
        jointType = parcel.readInt()
        pattern = parcel.createTypedArrayList(OmhPatternItem.CREATOR)
        startCap = parcel.readParcelable(OmhCap::class.java.classLoader)
        endCap = parcel.readParcelable(OmhCap::class.java.classLoader)
    }
    /**
     * Describe the kinds of special objects contained in this Parcelable instance's marshaled representation.
     * Value is either 0 or CONTENTS_FILE_DESCRIPTOR
     *
     * @return a bitmask indicating the set of special object types marshaled by this Parcelable object instance.
     */
    override fun describeContents(): Int {
        return 0
    }

    /**
     * Flatten this object in to a Parcel.
     *
     * @param parcel the Parcel in which the object should be written. This value cannot be null.
     * @param flags additional flags about how the object should be written.
     */
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeTypedArray(points, flags)
        parcel.writeFloat(width ?: 0f)
        parcel.writeInt(color ?: 0)
        parcel.writeByte(if (isVisible == true) 1 else 0)
        parcel.writeFloat(zIndex ?: 0f)
        parcel.writeInt(jointType ?: OmhJointType.DEFAULT)
        parcel.writeTypedList(pattern)
//        parcel.writeParcelable(startCap, flags)
//        parcel.writeParcelable(endCap, flags)
    }

    /**
     * public CREATOR field that generates instances of your Parcelable class from a Parcel.
     */
    companion object CREATOR : Parcelable.Creator<OmhPolylineOptions> {
        /**
         * Create a new instance of the Parcelable class,
         * instantiating it from the given Parcel whose data had previously been written by Parcelable.writeToParcel().
         *
         * @param parcel the Parcel to read the object's data from.
         * @return a new instance of the Parcelable class.
         */
        override fun createFromParcel(parcel: Parcel): OmhPolylineOptions {
            return OmhPolylineOptions(parcel)
        }

        /**
         * Create a new array of the Parcelable class.
         *
         * @param size size of the array.
         * @return an array of the Parcelable class, with every entry initialized to null.
         */
        override fun newArray(size: Int): Array<OmhPolylineOptions?> {
            return arrayOfNulls(size)
        }
    }
}
