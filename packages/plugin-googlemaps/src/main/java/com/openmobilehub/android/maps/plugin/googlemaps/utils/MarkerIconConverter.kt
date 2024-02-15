package com.openmobilehub.android.maps.plugin.googlemaps.utils

import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.PinConfig

object MarkerIconConverter {
    public fun convertColorToBitmapDescriptor(color: Int?): BitmapDescriptor? {
        return if (color == null) {
            null
        } else {
            BitmapDescriptorFactory.fromPinConfig(
                PinConfig.builder().setBackgroundColor(color).build()
            )
        }
    }
}
