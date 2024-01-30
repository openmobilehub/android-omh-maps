package com.openmobilehub.android.maps.core.presentation.models

import android.os.Parcel
import android.os.Parcelable
class OmhDot() : OmhPatternItem() {
    constructor(parcel: Parcel) : this() {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        super.writeToParcel(parcel, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<OmhDot> {
        override fun createFromParcel(parcel: Parcel): OmhDot {
            return OmhDot(parcel)
        }

        override fun newArray(size: Int): Array<OmhDot?> {
            return arrayOfNulls(size)
        }
    }
}