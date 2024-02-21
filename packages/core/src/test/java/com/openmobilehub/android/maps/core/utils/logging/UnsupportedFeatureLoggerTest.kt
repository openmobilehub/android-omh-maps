package com.openmobilehub.android.maps.core.utils.logging

import android.util.Log
import com.openmobilehub.android.maps.core.utils.Constants
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class UnsupportedFeatureLoggerTest {

    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.w(any(), any<String>()) } returns 0

        Logger.enableLogging()
    }

    @Test
    fun `logNotSupported logs correct message when logging is enabled`() {
        // Arrange
        val logger = UnsupportedFeatureLogger("TestProvider", "TestMapElement")
        val propertyName = "TestProperty"

        // Act
        logger.logNotSupported(propertyName)

        // Assert
        verify {
            Log.w(
                Constants.LOG_TAG,
                "[TestProvider]: Property $propertyName in TestMapElement not supported."
            )
        }
    }

    @Test
    fun `logGetterNotSupported logs correct message when logging is enabled`() {
        // Arrange
        val logger = UnsupportedFeatureLogger("TestProvider", "TestMapElement")
        val propertyName = "TestProperty"

        // Act
        logger.logGetterNotSupported(propertyName)

        // Assert
        verify {
            Log.w(
                Constants.LOG_TAG,
                "[TestProvider]: Getter for property $propertyName in TestMapElement not supported."
            )
        }
    }

    @Test
    fun `logSetterNotSupported logs correct message when logging is enabled`() {
        // Arrange
        val logger = UnsupportedFeatureLogger("TestProvider", "TestMapElement")
        val propertyName = "TestProperty"

        // Act
        logger.logSetterNotSupported(propertyName)

        // Assert
        verify {
            Log.w(
                Constants.LOG_TAG,
                "[TestProvider]: Setter for property $propertyName in TestMapElement not supported."
            )
        }
    }
}
