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

package com.github.ykiselev.services.commands;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public abstract class CommandException extends RuntimeException {

    public CommandException() {
    }

    public CommandException(String message) {
        super(message);
    }

    public CommandException(String message, Throwable cause) {
        super(message, cause);
    }

    public CommandException(Throwable cause) {
        super(cause);
    }

    public CommandException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public static final class CommandAlreadyRegisteredException extends CommandException {

        public CommandAlreadyRegisteredException(String message) {
            super(message);
        }
    }

    public static final class CommandStackOverflowException extends CommandException {

        public CommandStackOverflowException(int maxDepth) {
            super(String.format("Max depth reached: %d", maxDepth));
        }
    }

    public static final class CommandExecutionFailedException extends CommandException {

        public CommandExecutionFailedException(String commandLine, Throwable cause) {
            super(String.format("Command failed: %s", commandLine), cause);
        }
    }

    public static final class UnknownCommandException extends CommandException {

        public UnknownCommandException(String command) {
            super(String.format("Unknown command: %s", command));
        }
    }
}
