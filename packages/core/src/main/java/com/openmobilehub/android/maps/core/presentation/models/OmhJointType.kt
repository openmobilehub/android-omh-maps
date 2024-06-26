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

package com.openmobilehub.android.maps.core.presentation.models

/**
 * [OmhJointType] is an object that represents the joint type of a polyline on a map.
 * The joint type can be one of the following:
 * - DEFAULT: The miter joint type.
 * - BEVEL: The bevel joint type.
 * - ROUND: The round joint type.
 */
object OmhJointType {
    const val DEFAULT = 0
    const val BEVEL = 1
    const val ROUND = 2
}
