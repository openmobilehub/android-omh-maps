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

package com.openmobilehub.android.maps.plugin.azuremaps.presentation.maps

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.widget.FrameLayout
import com.openmobilehub.android.maps.core.utils.ScreenUnitConverter
import com.openmobilehub.android.maps.plugin.azuremaps.R

class MyLocationControl(context: Context) :
    androidx.appcompat.widget.AppCompatImageButton(context) {

    init {
        setImageResource(R.drawable.recenter_camera_icon)
        setBackgroundColor(Color.WHITE)

        val size =
            ScreenUnitConverter.dpToPx(BUTTON_SIZE, context).toInt()
        val topMargin =
            ScreenUnitConverter.dpToPx(BUTTON_TOP_MARGIN, context).toInt()
        val rightMargin =
            ScreenUnitConverter.dpToPx(BUTTON_RIGHT_MARGIN, context).toInt()

        val layoutParams = FrameLayout.LayoutParams(size, size).apply {
            setMargins(0, topMargin, rightMargin, 0) // left, top, right, bottom
            gravity = Gravity.TOP or Gravity.END
        }

        this.layoutParams = layoutParams
    }
    companion object {
        const val BUTTON_SIZE = 44.0f
        const val BUTTON_TOP_MARGIN = 2.0f
        const val BUTTON_RIGHT_MARGIN = 10.0f
    }
}
