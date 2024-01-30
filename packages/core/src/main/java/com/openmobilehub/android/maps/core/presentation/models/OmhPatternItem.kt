package com.openmobilehub.android.maps.core.presentation.models

import android.os.Parcel
import android.os.Parcelable

open class OmhPatternItem() : Parcelable {
    constructor(parcel: Parcel) : this() {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        // No properties to write to parcel
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<OmhPatternItem> {
        override fun createFromParcel(parcel: Parcel): OmhPatternItem {
            return OmhPatternItem(parcel)
        }

        override fun newArray(size: Int): Array<OmhPatternItem?> {
            return arrayOfNulls(size)
        }
    }
}