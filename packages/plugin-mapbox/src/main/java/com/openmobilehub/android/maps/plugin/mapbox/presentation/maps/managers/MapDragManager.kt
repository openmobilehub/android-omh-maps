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
import com.openmobilehub.android.maps.plugin.mapbox.extensions.distanceTo
import com.openmobilehub.android.maps.plugin.mapbox.presentation.interfaces.IMapDragManagerDelegate
import com.openmobilehub.android.maps.plugin.mapbox.utils.Constants
import com.openmobilehub.android.maps.plugin.mapbox.utils.CoordinateConverter
import com.openmobilehub.android.maps.plugin.mapbox.utils.TimestampHelper

internal class MapDragManager(private val delegate: IMapDragManagerDelegate) {
    private var currentlyDraggedEntity: Any? = null
    private var mapDragTouchStartTimestamp: Long? = null
    private var mapDragTouchStartPoint: ScreenCoordinate? = null

    @SuppressWarnings("NestedBlockDepth", "ReturnCount")
    fun handleOnTouch(
        eventActionMasked: Int,
        screenCoordinate: ScreenCoordinate,
        point: Point
    ): Boolean {
        val omhCoordinate = CoordinateConverter.convertToOmhCoordinate(point)

        if (eventActionMasked in Constants.ACTIVE_MOTION_EVENTS) {
            if (currentlyDraggedEntity === null) {
                // drag is not occurring at the moment

                if (mapDragTouchStartTimestamp === null) {
                    // start counting time to check whether this will actually be a drag
                    // don't consume the event this time, as it may be a click

                    mapDragTouchStartTimestamp = TimestampHelper.getNow()
                    mapDragTouchStartPoint = screenCoordinate
                } else {
                    // first, check if we are still at the same point - otherwise, reset the timer
                    // allow a tiny tolerance
                    val distancePx = mapDragTouchStartPoint!!.distanceTo(screenCoordinate)
                    if (distancePx > Constants.MAP_TOUCH_SAME_COORDINATES_THRESHOLD_PX) {
                        mapDragTouchStartTimestamp = TimestampHelper.getNow()
                        mapDragTouchStartPoint = screenCoordinate
                    } else {
                        val deltaTime =
                            TimestampHelper.getNow() - mapDragTouchStartTimestamp!!

                        if (deltaTime > Constants.MAP_TOUCH_DRAG_TOUCHDOWN_THRESHOLD_MS) {
                            // the touch interaction time allows for treating this as a drag, if applicable
                            val draggableEntity = delegate.findDraggableEntity(screenCoordinate)
                            if (draggableEntity !== null) {
                                if (draggableEntity.getDraggable()) {
                                    // drag start
                                    currentlyDraggedEntity = draggableEntity

                                    delegate.handleDragStart(
                                        omhCoordinate,
                                        currentlyDraggedEntity!!
                                    )
                                } else {
                                    // drag end (draggable entity ceased to be draggable or to exist)
                                    delegate.handleDragEnd(omhCoordinate, currentlyDraggedEntity!!)

                                    currentlyDraggedEntity = null
                                }
                            }

                            return true
                        }
                    }
                }
            } else {
                // drag continuation
                delegate.handleDragContinuation(omhCoordinate, currentlyDraggedEntity!!)

                return true
            }
        } else {
            if (currentlyDraggedEntity !== null) {
                // drag end (user finished interaction)
                mapDragTouchStartTimestamp = null

                delegate.handleDragEnd(omhCoordinate, currentlyDraggedEntity!!)

                currentlyDraggedEntity = null

                return true
            }
        }

        return false
    }
}
