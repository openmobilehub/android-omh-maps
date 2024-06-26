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

package com.openmobilehub.android.maps.core.presentation.interfaces.maps

/**
 * Abstraction to provide access to callback interface for when a polygon on the map is clicked.
 */
fun interface OmhOnPolygonClickListener {
    /**
     * Called when a polygon on the map is clicked.
     * @param polygon The polygon that was clicked.
     * @return true if the default behavior should be suppressed; false otherwise
     */
    fun onPolygonClick(polygon: OmhPolygon): Boolean
}
