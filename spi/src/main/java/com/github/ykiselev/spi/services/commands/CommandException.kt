/*
 * Copyright 2017 Yuriy Kiselev (uze@yandex.ru)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.ykiselev.spi.services.commands

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
abstract class CommandException : RuntimeException {
    constructor()
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable) : super(message, cause)
    constructor(cause: Throwable) : super(cause)
    constructor(message: String, cause: Throwable, enableSuppression: Boolean, writableStackTrace: Boolean) : super(
        message,
        cause,
        enableSuppression,
        writableStackTrace
    )

    class CommandAlreadyRegisteredException(message: String) : CommandException(message)
    class CommandStackOverflowException(maxDepth: Int) :
        CommandException(String.format("Max depth reached: %d", maxDepth))

    class CommandExecutionFailedException(commandLine: String, cause: Throwable) :
        CommandException(String.format("Command failed: %s", commandLine), cause)

    class UnknownCommandException(command: String) : CommandException(String.format("Unknown command: %s", command))
}
