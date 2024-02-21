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

package com.openmobilehub.android.maps.core.utils.logging

import android.util.Log
import com.openmobilehub.android.maps.core.utils.Constants

/**
 * [Logger] is a utility class that provides logging functionality for packages.
 *
 *  @property providerName The provider name. This is used to identify the source of the log message.
 */
open class Logger(private val providerName: String) {
    companion object {
        /**
         * A flag indicating whether logging is enabled.
         */
        var isLoggingEnabled = true

        /**
         * Enables logging.
         */
        fun enableLogging() {
            isLoggingEnabled = true
        }

        /**
         * Disables logging.
         */
        fun disableLogging() {
            isLoggingEnabled = false
        }
    }

    /**
     * The tag used for logging.
     */
    protected val tag: String = Constants.LOG_TAG

    /**
     * Creates a log message by appending the `providerName` to the message.
     *
     * @param message The message to log.
     * @return The created log message.
     */
    private fun createMessage(message: String): String {
        return "[$providerName]: $message"
    }

    /**
     * Logs a message at the specified log level if logging is enabled.
     *
     * @param level The log level.
     * @param message The message to log.
     */
    private fun log(level: LogLevel, message: String) {
        if (!isLoggingEnabled) return
        when (level) {
            LogLevel.DEBUG -> Log.d(tag, message)
            LogLevel.INFO -> Log.i(tag, message)
            LogLevel.WARN -> Log.w(tag, message)
            LogLevel.ERROR -> Log.e(tag, message)
        }
    }

    /**
     * Logs a debug message.
     *
     * @param message The message to log.
     */
    fun logDebug(message: String) {
        log(LogLevel.DEBUG, createMessage(message))
    }

    /**
     * Logs an info message.
     *
     * @param message The message to log.
     */
    fun logInfo(message: String) {
        log(LogLevel.INFO, createMessage(message))
    }

    /**
     * Logs a warning message.
     *
     * @param message The message to log.
     */
    fun logWarning(message: String) {
        log(LogLevel.WARN, createMessage(message))
    }

    /**
     * Logs an error message.
     *
     * @param message The message to log.
     */
    fun logError(message: String) {
        log(LogLevel.ERROR, createMessage(message))
    }
}
