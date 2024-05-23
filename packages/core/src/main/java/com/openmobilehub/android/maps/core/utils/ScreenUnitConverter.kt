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

import android.content.Context
import android.content.res.Resources

/**
 * Utility for converting screen units.
 */
object ScreenUnitConverter {
    /**
     * Converts pixels to dp (density independent pixels).
     *
     * @param px The value in pixels.
     * @param context The context.
     *
     * @return The value in dp.
     */
    fun pxToDp(px: Float, context: Context): Float {
        return px / context.resources.displayMetrics.density
    }

    /**
     * Converts pixels to dp (density independent pixels).
     *
     * @param px The value in pixels.
     *
     * @return The value in dp.
     */
    fun pxToDp(px: Float): Float {
        return px / Resources.getSystem().displayMetrics.density
    }

    /**
     * Converts dp (density independent pixels) to pixels.
     *
     * @param dp The value in dp.
     * @param context The context.
     *
     * @return The value in pixels.
     */
    fun dpToPx(dp: Float, context: Context): Float {
        return dp * context.resources.displayMetrics.density
    }

    /**
     * Converts dp (density independent pixels) to pixels.
     *
     * @param dp The value in dp.
     *
     * @return The value in pixels.
     */
    fun dpToPx(dp: Float): Float {
        return dp * Resources.getSystem().displayMetrics.density
    }
}
