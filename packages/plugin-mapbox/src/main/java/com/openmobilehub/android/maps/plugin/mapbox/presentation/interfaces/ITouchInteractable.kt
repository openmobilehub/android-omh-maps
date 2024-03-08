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

package com.openmobilehub.android.maps.plugin.mapbox.presentation.interfaces

import com.openmobilehub.android.maps.plugin.mapbox.utils.cartesian.Offset2D

/**
 * Common interface for draggable entities on the map.
 */
internal interface ITouchInteractable {
    /**
     * Returns whether the entity is draggable.
     *
     * @return `true` if the entity is draggable, `false` otherwise.
     */
    fun getDraggable(): Boolean

    /**
     * Returns the offset of the handle from the position of the entity on map.
     *
     * @return The offset in pixels.
     */
    fun getHandleOffset(): Offset2D<Double>

    /**
     * Returns whether the entity is long-clickable.
     *
     * @return `true` if the entity is long-clickable, `false` otherwise.
     */
    fun getLongClickable(): Boolean
}
