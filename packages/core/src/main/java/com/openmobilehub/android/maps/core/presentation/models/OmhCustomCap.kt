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

import android.graphics.Bitmap
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhCap
import kotlinx.parcelize.Parcelize

/**
 * [OmhCustomCap] is a class that represents a custom cap for a polyline on a map.
 * A custom cap is a cap that uses a custom bitmap image.
 * This class extends the [OmhCap] interface.
 *
 * @property bitmap The bitmap image used for the custom cap.
 * @property refWidth The reference width for the custom cap. The cap scales as the zoom level changes.
 */
@Parcelize
class OmhCustomCap(val bitmap: Bitmap, val refWidth: Float?) : OmhCap
