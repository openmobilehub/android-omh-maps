package com.openmobilehub.android.maps.plugin.googlemaps.utils

import android.graphics.Color
import android.graphics.drawable.Drawable
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.openmobilehub.android.maps.core.utils.DrawableConverter

object MarkerIconConverter {
    fun convertColorToBitmapDescriptor(color: Int?): BitmapDescriptor? {
        return if (color == null) {
            null
        } else {
            @SuppressWarnings("MagicNumber")
            val hsv = FloatArray(3)
            Color.colorToHSV(color, hsv)
            return BitmapDescriptorFactory.defaultMarker(hsv[0])
        }
    }

    fun convertDrawableToBitmapDescriptor(drawable: Drawable): BitmapDescriptor {
        return BitmapDescriptorFactory.fromBitmap(DrawableConverter.convertDrawableToBitmap(drawable))
    }
}
