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

import com.mapbox.maps.ScreenCoordinate
import com.openmobilehub.android.maps.core.presentation.models.OmhCoordinate

/**
 * Delegate interface having capabilities to handle drag events on the map.
 */
internal interface IMapDragManagerDelegate {

    /**
     * Returns an entity (if present and in draggable state) that can possibly be dragged.
     *
     * @param screenCoordinate Coordinate on the screen for which to find the entity to possibly be dragged.
     * @param validator Predicate to validate the entity to be dragged. Returns `true` if an entity
     * can be a hit, or `false` otherwise (in which case it is ignored).
     */
    fun findInteractableEntities(
        screenCoordinate: ScreenCoordinate,
        validator: ((predicate: ITouchInteractable) -> Boolean)? = null
    ): List<ITouchInteractable>

    /**
     * Callback invoked when drag event is started.
     *
     * @param omhCoordinate Coordinate on the map where the clicked entity has been dragged to.
     * @param draggedEntity The entity that has been dragged.
     */
    fun handleDragStart(omhCoordinate: OmhCoordinate, draggedEntity: ITouchInteractable)

    /**
     * Callback invoked when drag event is in progress, called
     * sequentially as the user moves pointer on the screen.
     *
     * @param omhCoordinate Coordinate on the map where the clicked entity has been dragged to.
     * @param draggedEntity The entity that has been dragged.
     */
    fun handleDragMove(omhCoordinate: OmhCoordinate, draggedEntity: ITouchInteractable)

    /**
     * Callback invoked when drag event is finished.
     *
     * @param omhCoordinate Coordinate on the map where the clicked entity has been dragged to.
     * @param draggedEntity The entity that has been dragged.
     */
    fun handleDragEnd(omhCoordinate: OmhCoordinate, draggedEntity: ITouchInteractable)
}
