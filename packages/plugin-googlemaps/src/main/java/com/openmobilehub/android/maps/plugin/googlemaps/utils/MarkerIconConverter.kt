package com.openmobilehub.android.maps.plugin.googlemaps.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.VectorDrawable
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory

object MarkerIconConverter {
    public fun convertColorToBitmapDescriptor(color: Int?): BitmapDescriptor? {
        return if (color == null) {
            null
        } else {
            @SuppressWarnings("MagicNumber")
            val hsv = FloatArray(3)
            Color.colorToHSV(color, hsv)
            return BitmapDescriptorFactory.defaultMarker(hsv[0])
        }
    }

    public fun convertDrawableToBitmapDescriptor(drawable: Drawable): BitmapDescriptor {
        if (drawable is VectorDrawable) {
            val bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth,
                drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )

            // render the drawable to bitmap
            val renderCanvas = Canvas(bitmap)
            drawable.setBounds(0, 0, renderCanvas.width, renderCanvas.height)
            drawable.draw(renderCanvas)

            return BitmapDescriptorFactory.fromBitmap(bitmap)
        } else {
            return BitmapDescriptorFactory.fromBitmap((drawable as BitmapDrawable).bitmap)
        }
    }
}
