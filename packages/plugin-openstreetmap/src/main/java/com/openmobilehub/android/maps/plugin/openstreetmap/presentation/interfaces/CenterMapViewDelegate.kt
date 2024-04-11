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

package com.openmobilehub.android.maps.plugin.openstreetmap.presentation.interfaces

import android.view.View.OnClickListener
import android.widget.Button

interface CenterMapViewDelegate {
    /**
     * Set the center map on current location button enabled status.
     * The button will be hidden when `false` is passed and shown otherwise.
     *
     * @param isEnabled `true` to show the [Button], `false` to hide it.
     */
    fun setCenterLocationButtonEnabled(isEnabled: Boolean)

    /**
     * Set the on center map on current location callback.
     *
     * @param onCenterLocation The callback to be invoked whenever the map
     * is centered on current location.
     */
    fun setCenterLocation(onCenterLocation: () -> Unit)

    /**
     * Set on click listener for the center location button.
     *
     * @param onClick [OnClickListener] to be set on the [Button].
     */
    fun setOnCenterLocationButtonClickListener(onClick: OnClickListener)
}
