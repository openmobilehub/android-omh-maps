package com.openmobilehub.android.maps.plugin.mapbox.utils

import android.content.Context

internal object DimensionConverter {
    fun dpFromPx(context: Context, px: Int): Int {
        return (px / context.resources.displayMetrics.density).toInt()
    }

    fun pxFromDp(context: Context, dp: Int): Int {
        return (dp * context.resources.displayMetrics.density).toInt()
    }
}
