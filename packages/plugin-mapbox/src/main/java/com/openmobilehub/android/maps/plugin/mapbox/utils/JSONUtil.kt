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

import android.content.Context
import com.openmobilehub.android.maps.core.utils.logging.Logger
import java.io.IOException
import java.nio.charset.Charset

object JSONUtil {
    fun convertJSONToString(context: Context, resourceId: Int, logger: Logger): String? {
        val content: String
        return try {
            context.resources.openRawResource(resourceId).use { inputStream ->
                val size = inputStream.available()
                val buffer = ByteArray(size)
                inputStream.read(buffer)
                content = String(buffer, Charset.forName("UTF-8"))
            }
            content
        } catch (e: IOException) {
            logger.logError(e.stackTraceToString())
            null
        }
    }
}
