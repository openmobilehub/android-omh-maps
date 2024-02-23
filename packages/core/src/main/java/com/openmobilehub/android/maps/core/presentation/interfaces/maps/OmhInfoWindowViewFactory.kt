package com.openmobilehub.android.maps.core.presentation.interfaces.maps

import android.view.View

/**
 * Abstraction to generate customizable [View] based on an [OmhMarker].
 */
typealias OmhInfoWindowViewFactory = (marker: OmhMarker) -> View
