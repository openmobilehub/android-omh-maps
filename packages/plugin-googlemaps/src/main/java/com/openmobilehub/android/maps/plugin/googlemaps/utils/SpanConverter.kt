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

package com.openmobilehub.android.maps.plugin.googlemaps.utils

import android.graphics.Bitmap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.StrokeStyle
import com.google.android.gms.maps.model.StyleSpan
import com.google.android.gms.maps.model.TextureStyle
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhStyleSpan
import com.openmobilehub.android.maps.core.presentation.models.OmhStyleSpanGradient
import com.openmobilehub.android.maps.core.presentation.models.OmhStyleSpanMonochromatic

object SpanConverter {
    fun convertToStyleSpan(omhStyleSpan: OmhStyleSpan): StyleSpan? {
        return when (omhStyleSpan) {
            is OmhStyleSpanMonochromatic -> createMonochromaticStyleSpan(omhStyleSpan)
            is OmhStyleSpanGradient -> createGradientStyleSpan(omhStyleSpan)
            else -> null
        }
    }

    private fun createMonochromaticStyleSpan(style: OmhStyleSpanMonochromatic): StyleSpan {
        val color = style.color
        val strokeStyleBuilder = StrokeStyle.colorBuilder(color)
        val textureStyle = createTextureStyle(style.stamp)
        val strokeStyle =
            textureStyle?.let { strokeStyleBuilder.stamp(it).build() } ?: strokeStyleBuilder.build()

        return style.segments?.let { segments ->
            StyleSpan(strokeStyle, segments)
        } ?: StyleSpan(strokeStyle)
    }

    private fun createGradientStyleSpan(style: OmhStyleSpanGradient): StyleSpan {
        val strokeStyleBuilder = StrokeStyle.gradientBuilder(style.fromColor, style.toColor)
        val textureStyle = createTextureStyle(style.stamp)
        val strokeStyle =
            textureStyle?.let { strokeStyleBuilder.stamp(it).build() } ?: strokeStyleBuilder.build()

        return style.segments?.let { segments ->
            StyleSpan(strokeStyle, segments)
        } ?: StyleSpan(strokeStyle)
    }

    private fun createTextureStyle(bitmap: Bitmap?): TextureStyle? {
        return bitmap?.let {
            TextureStyle.newBuilder(BitmapDescriptorFactory.fromBitmap(it)).build()
        }
    }
}
