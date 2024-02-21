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

/**
 * `LogLevel` is an enumeration that represents different levels of logging for packages.
 *
 * The levels include:
 * - DEBUG: Used for fine-grained informational events that are most useful for debugging packages.
 * - INFO: Used to indicate informational messages that highlight
 * the progress of the packages at a coarse-grained level.
 * - WARN: Used to indicate potentially harmful situations in packages.
 * - ERROR: Used to indicate error events in packages that might still allow the package to continue running.
 *
 * These levels can be used in conjunction with the `Logger` class to
 * log messages at different levels of severity for packages.
 */
enum class LogLevel {
    DEBUG,
    INFO,
    WARN,
    ERROR
}
