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

package com.openmobilehub.android.maps.plugin.mapbox.utils

/**
 * Utility class helpful for mocking time in tests, so that there is no need to mock [System].
 */
object TimestampHelper {
    /**
     * Returns the current time from `System.currentTimeMillis()`.
     *
     * @return the current time in milliseconds
     */
    fun getNow() = System.currentTimeMillis()
}
