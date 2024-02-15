package com.openmobilehub.android.maps.core.utils.logging

import android.util.Log
import com.openmobilehub.android.maps.core.utils.Constants
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class LoggerTest {

    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.i(any(), any()) } returns 0
        every { Log.w(any(), any<String>()) } returns 0
        every { Log.e(any(), any<String>()) } returns 0

        Logger.enableLogging()
    }

    @Test
    fun `logDebug logs correct message when logging is enabled`() {
        // Arrange
        val logger = Logger("TestProvider")

        // Act
        logger.logDebug("Debug message")

        // Assert
        verify { Log.d(Constants.LOG_TAG, "[TestProvider]: Debug message") }
    }

    @Test
    fun `logInfo logs correct message when logging is enabled`() {
        // Arrange
        val logger = Logger("TestProvider")

        // Act
        logger.logInfo("Info message")

        // Assert
        verify { Log.i(Constants.LOG_TAG, "[TestProvider]: Info message") }
    }

    @Test
    fun `logWarning logs correct message when logging is enabled`() {
        // Arrange
        val logger = Logger("TestProvider")

        // Act
        logger.logWarning("Warning message")

        // Assert
        verify { Log.w(Constants.LOG_TAG, "[TestProvider]: Warning message") }
    }

    @Test
    fun `logError logs correct message when logging is enabled`() {
        // Arrange
        val logger = Logger("TestProvider")

        // Act
        logger.logError("Error message")

        // Assert
        verify { Log.e(Constants.LOG_TAG, "[TestProvider]: Error message") }
    }

    @Test
    fun `log methods do not log when logging is disabled`() {
        // Arrange
        Logger.disableLogging()
        val logger = Logger("TestProvider")

        // Act
        logger.logDebug("Debug message")
        logger.logInfo("Info message")
        logger.logWarning("Warning message")
        logger.logError("Error message")

        // Assert
        verify(exactly = 0) { Log.d(any(), any()) }
        verify(exactly = 0) { Log.i(any(), any()) }
        verify(exactly = 0) { Log.w(any(), any<String>()) }
        verify(exactly = 0) { Log.e(any(), any()) }
    }
}
