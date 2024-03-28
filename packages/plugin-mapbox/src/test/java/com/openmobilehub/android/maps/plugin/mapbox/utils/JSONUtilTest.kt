package com.openmobilehub.android.maps.plugin.mapbox.utils

import android.content.Context
import com.openmobilehub.android.maps.core.utils.logging.Logger
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.IOException

class JSONUtilTest {
    private val context = mockk<Context>(relaxed = true)
    private val logger = mockk<Logger>(relaxed = true)

    @Test
    fun `convertJSONToString returns correct string when valid resource ID provided`() {
        // Arrange
        val resourceId = 1
        val jsonContent = "test"
        every { context.resources.openRawResource(resourceId) } returns ByteArrayInputStream(
            jsonContent.toByteArray()
        )

        // Act
        val result = JSONUtil.convertJSONToString(context, resourceId, logger)

        // Assert
        Assert.assertEquals(jsonContent, result)
    }

    @Test
    fun `convertJSONToString returns null when IOException occurs`() {
        // Arrange
        val resourceId = 1
        every { context.resources.openRawResource(resourceId) } throws IOException()

        // Act
        val result = JSONUtil.convertJSONToString(context, resourceId, logger)

        // Assert
        Assert.assertNull(result)
        verify { logger.logError(any()) }
    }
}
