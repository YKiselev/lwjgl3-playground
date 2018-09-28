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

import com.github.ykiselev.services.commands.CommandException.CommandAlreadyRegisteredException;
import com.github.ykiselev.services.commands.CommandException.CommandExecutionFailedException;
import com.github.ykiselev.services.commands.CommandException.CommandStackOverflowException;
import com.github.ykiselev.services.commands.CommandException.TokenizerHasFailedException;
import com.github.ykiselev.services.commands.CommandException.UnknownCommandException;

import java.util.List;
import java.util.function.Consumer;

/**
 * Command processor.<br/>
 * Supported command syntax:<br/>
 * <pre>
 *     command [arg1 arg2... argN]
 * </pre>
 * To pass multiple commands on one line use ';' as delimiter (spaces before and after are optional and will be stripped by tokenizer):
 * <pre>
 *     command1 arg1 arg2; command2 arg1 arg2 arg3
 * </pre>
 * To pass argument(s) with whitespaces use quotes - (") or (')
 * <pre>
 *     command "arg with whitespaces"
 * </pre>
 * Passed script may contain single-line comments starting from (//) and lasting till the first CR or LF character is encountered.
 * <pre>
 *     // this text is ignored
 *     command arg1
 * </pre>
 *
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface Commands {

    /**
     * Splits passed command line into tokens using configured tokenizer and executes separate commands.
     *
     * @param commandLine the command line to execute.
     * @throws CommandStackOverflowException   if current call's depth is greater than configured {@code maxDepth}.
     * @throws CommandExecutionFailedException if command execution has failed.
     * @throws UnknownCommandException         if unknown command is present in passed command line.
     * @throws TokenizerHasFailedException     if configured tokenizer is unable to split passed {@code non-null} and {@code non-empty} command line to tokens.
     */
    void execute(String commandLine) throws CommandStackOverflowException, CommandExecutionFailedException, UnknownCommandException, TokenizerHasFailedException;

    /**
     * Registers passed command handler for specified command. The handler will receive a list of command arguments where
     * first element is always a command itself.
     *
     * @param command the command to bind handler to.
     * @param handler the handler to call to execute command.
     * @return the handle to unbind handler.
     * @throws CommandAlreadyRegisteredException if specified command is already registered.
     */
    AutoCloseable add(String command, Consumer<List<String>> handler) throws CommandAlreadyRegisteredException;

    /**
     * Convenient method for commands without arguments.
     *
     * @param command the command to bind handler to.
     * @param handler the handler.
     * @return the handle to unbind handler.
     */
    default AutoCloseable add(String command, Runnable handler) {
        return add(command, Handlers.consumer(handler));
    }

    /**
     * Convenient method for commands without arguments.
     *
     * @param command the command to bind handler to.
     * @param handler the handler receiving only command name.
     * @return the handle to unbind handler.
     */
    default AutoCloseable add(String command, H1 handler) {
        return add(command, Handlers.consumer(handler));
    }

    /**
     * Convenient method for commands with single argument.
     *
     * @param command the command to bind handler to.
     * @param handler the handler receiving only command name.
     * @return the handle to unbind handler.
     */
    default AutoCloseable add(String command, H2 handler) {
        return add(command, Handlers.consumer(handler));
    }

    /**
     * Convenient method for commands with two arguments.
     *
     * @param command the command to bind handler to.
     * @param handler the handler receiving only command name.
     * @return the handle to unbind handler.
     */
    default AutoCloseable add(String command, H3 handler) {
        return add(command, Handlers.consumer(handler));
    }

    /**
     * Convenient method for commands with three arguments.
     *
     * @param command the command to bind handler to.
     * @param handler the handler receiving only command name.
     * @return the handle to unbind handler.
     */
    default AutoCloseable add(String command, H4 handler) {
        return add(command, Handlers.consumer(handler));
    }

    /**
     * Convenient method for commands with four arguments.
     *
     * @param command the command to bind handler to.
     * @param handler the handler receiving only command name.
     * @return the handle to unbind handler.
     */
    default AutoCloseable add(String command, H5 handler) {
        return add(command, Handlers.consumer(handler));
    }

    /**
     * Convenient method for commands with five arguments.
     *
     * @param command the command to bind handler to.
     * @param handler the handler receiving only command name.
     * @return the handle to unbind handler.
     */
    default AutoCloseable add(String command, H6 handler) {
        return add(command, Handlers.consumer(handler));
    }

    @FunctionalInterface
    interface H1 {

        void handle(String a1);
    }

    @FunctionalInterface
    interface H2 {

        void handle(String a1, String a2);
    }

    @FunctionalInterface
    interface H3 {

        void handle(String a1, String a2, String a3);
    }

    @FunctionalInterface
    interface H4 {

        void handle(String a1, String a2, String a3, String a4);
    }

    @FunctionalInterface
    interface H5 {

        void handle(String a1, String a2, String a3, String a4, String a5);
    }

    @FunctionalInterface
    interface H6 {

        void handle(String a1, String a2, String a3, String a4, String a5, String a6);
    }
}
