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

package com.openmobilehub.android.maps.plugin.mapbox.presentation.maps.managers

import com.mapbox.geojson.Point
import com.mapbox.maps.ScreenCoordinate
import com.openmobilehub.android.maps.plugin.mapbox.extensions.toPoint2D
import com.openmobilehub.android.maps.plugin.mapbox.presentation.interfaces.IMapDragManagerDelegate
import com.openmobilehub.android.maps.plugin.mapbox.presentation.interfaces.IMapLongClickManagerDelegate
import com.openmobilehub.android.maps.plugin.mapbox.presentation.interfaces.ITouchInteractable
import com.openmobilehub.android.maps.plugin.mapbox.utils.Constants
import com.openmobilehub.android.maps.plugin.mapbox.utils.CoordinateConverter
import com.openmobilehub.android.maps.plugin.mapbox.utils.TimestampHelper

internal class MapTouchInteractionManager(
    private val mapDragManagerDelegate: IMapDragManagerDelegate,
    private val mapLongClickManagerDelegate: IMapLongClickManagerDelegate
) {
    private var interactingEntity: ITouchInteractable? = null
    private var interactionTouchStartTimestamp: Long? = null
    private var interactionTouchStartPoint: ScreenCoordinate? = null
    private var didDragMoveOccur: Boolean = false

    fun resetDragState() {
        interactionTouchStartTimestamp = null
        interactingEntity = null
        interactionTouchStartPoint = null
        didDragMoveOccur = false
    }

    @SuppressWarnings("NestedBlockDepth", "ReturnCount", "LongMethod")
    fun handleOnTouch(
        eventActionMasked: Int,
        screenCoordinate: ScreenCoordinate,
        point: Point
    ): Boolean {
        val omhCoordinate = CoordinateConverter.convertToOmhCoordinate(point)

        if (eventActionMasked in Constants.ACTIVE_MOTION_EVENTS) {
            if (interactingEntity === null) {
                // drag is not occurring at the moment

                if (interactionTouchStartTimestamp === null) {
                    // start counting time to check whether this will actually be a drag
                    // don't consume the event this time, as it may be a click

                    interactionTouchStartTimestamp = TimestampHelper.getNow()
                    interactionTouchStartPoint = screenCoordinate
                } else {
                    // first, check if we are still at the same point - otherwise, reset the timer
                    // allow a tiny tolerance
                    val distancePx =
                        interactionTouchStartPoint!!.toPoint2D()
                            .distanceTo(screenCoordinate.toPoint2D())
                    if (distancePx > Constants.MAP_TOUCH_SAME_COORDINATES_THRESHOLD_PX) {
                        interactionTouchStartTimestamp = TimestampHelper.getNow()
                        interactionTouchStartPoint = screenCoordinate
                    } else {
                        val deltaTime =
                            TimestampHelper.getNow() - interactionTouchStartTimestamp!!

                        if (deltaTime > Constants.MAP_TOUCH_DRAG_TOUCHDOWN_THRESHOLD_MS) {
                            // the touch interaction time allows for treating this
                            // as a drag or long click, if applicable
                            val interactableEntities =
                                mapDragManagerDelegate.findInteractableEntities(screenCoordinate)

                            for (interactableEntity in interactableEntities) {
                                if (interactableEntity.getDraggable()) {
                                    // drag or long click start
                                    interactingEntity = interactableEntity

                                    mapDragManagerDelegate.handleDragStart(
                                        omhCoordinate,
                                        interactingEntity!!
                                    )

                                    return true
                                } else if (interactableEntity.getLongClickable()) {
                                    // just long click
                                    interactingEntity = interactableEntity

                                    mapLongClickManagerDelegate.handleLongClick(interactingEntity!!)

                                    interactingEntity = null
                                    interactionTouchStartTimestamp = null
                                    didDragMoveOccur = false

                                    return true
                                } else if (interactingEntity !== null) {
                                    // drag end (draggable entity ceased to be draggable)
                                    mapDragManagerDelegate.handleDragEnd(
                                        omhCoordinate,
                                        interactingEntity!!
                                    )

                                    resetDragState()

                                    return true
                                }
                            }

                            return false
                        }
                    }
                }
            } else {
                // drag move
                mapDragManagerDelegate.handleDragMove(omhCoordinate, interactingEntity!!)

                didDragMoveOccur = true

                return true
            }
        } else {
            // inactive touch event (pointer up)
            var eventConsumed = false

            if (interactingEntity !== null && didDragMoveOccur) {
                // drag end (user finished interaction)
                mapDragManagerDelegate.handleDragEnd(omhCoordinate, interactingEntity!!)

                eventConsumed = true
            }

            resetDragState()

            return eventConsumed
        }

        return false
    }
}
