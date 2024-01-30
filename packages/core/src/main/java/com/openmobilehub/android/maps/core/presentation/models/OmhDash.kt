package com.openmobilehub.android.maps.core.presentation.models

import android.os.Parcel
import android.os.Parcelable
class OmhDash (val length: Float): OmhPatternItem() {
    constructor(parcel: Parcel) : this(parcel.readFloat()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        super.writeToParcel(parcel, flags)
        parcel.writeFloat(length)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<OmhDash> {
        override fun createFromParcel(parcel: Parcel): OmhDash {
            return OmhDash(parcel)
        }

        override fun newArray(size: Int): Array<OmhDash?> {
            return arrayOfNulls(size)
        }
    }
}