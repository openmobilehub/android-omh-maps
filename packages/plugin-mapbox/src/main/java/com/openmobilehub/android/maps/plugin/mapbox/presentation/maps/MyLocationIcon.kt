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

package com.openmobilehub.android.maps.plugin.mapbox.presentation.maps

import android.content.Context
import android.view.Gravity
import android.widget.FrameLayout
import com.openmobilehub.android.maps.core.utils.ScreenUnitConverter
import com.openmobilehub.android.maps.plugin.mapbox.R
import com.openmobilehub.android.maps.plugin.mapbox.utils.Constants

class MyLocationIcon(context: Context) : androidx.appcompat.widget.AppCompatImageView(context) {

    init {
        setImageResource(R.drawable.recenter_camera_icon)

        val iconSize =
            ScreenUnitConverter.dpToPx(Constants.MAPBOX_ICON_SIZE.toFloat(), context).toInt()
        val iconMargin =
            ScreenUnitConverter.dpToPx(Constants.MAPBOX_ICON_MARGIN.toFloat(), context).toInt()

        val layoutParams = FrameLayout.LayoutParams(iconSize, iconSize).apply {
            setMargins(0, iconMargin, iconMargin, 0) // left, top, right, bottom
            gravity = Gravity.TOP or Gravity.END
        }

        this.layoutParams = layoutParams
    }
}
