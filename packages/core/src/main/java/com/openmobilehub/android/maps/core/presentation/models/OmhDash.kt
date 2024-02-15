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

import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhPatternItem
import kotlinx.parcelize.Parcelize

/**
 * `OmhDash` is a class that represents a dash pattern item on a map.
 * A dash pattern item can be used to customize the appearance of polylines and other map elements.
 * This class extends the [OmhPatternItem] interface.
 *
 * @property length The length of the dash.
 */
@Parcelize
class OmhDash(val length: Float) : OmhPatternItem
