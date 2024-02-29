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
