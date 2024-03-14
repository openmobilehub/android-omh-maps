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

package com.openmobilehub.android.maps.core.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.VectorDrawable

/**
 * Utility for converting drawables to bitmaps.
 */
object DrawableConverter {
    /**
     * Converts a drawable to a bitmap.
     *
     * @param drawable The drawable to convert.
     *
     * @return The bitmap.
     */
    fun convertDrawableToBitmap(drawable: Drawable): Bitmap {
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

            return bitmap
        } else {
            return (drawable as BitmapDrawable).bitmap
        }
    }
}
