package com.rnd.aedss_android.viewmodel

import android.os.Parcel
import android.os.Parcelable

data class SectionImage(val dateSection: String, val imgList: String):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(dateSection)
        parcel.writeString(imgList)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SectionImage> {
        override fun createFromParcel(parcel: Parcel): SectionImage {
            return SectionImage(parcel)
        }

        override fun newArray(size: Int): Array<SectionImage?> {
            return arrayOfNulls(size)
        }
    }


}
