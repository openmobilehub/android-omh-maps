package com.openmobilehub.android.maps.core.presentation.interfaces.maps

import android.view.View

/**
 * Abstraction to generate customizable [View] based on an [OmhMarker].
 */
interface OmhInfoWindowViewFactory {
    fun createInfoWindowView(marker: OmhMarker): View
}
