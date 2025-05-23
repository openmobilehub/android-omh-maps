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

package com.openmobilehub.android.maps.plugin.googlemaps.utils

import com.openmobilehub.android.maps.core.utils.logging.Logger
import com.openmobilehub.android.maps.core.utils.logging.UnsupportedFeatureLogger

val commonLogger = Logger(Constants.PROVIDER_NAME)
val polylineLogger = UnsupportedFeatureLogger(Constants.PROVIDER_NAME, "OmhPolyline")
val markerLogger = UnsupportedFeatureLogger(Constants.PROVIDER_NAME, "OmhMarker")
