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

package com.github.ykiselev.spi.services.commands;

import com.github.ykiselev.common.ThrowingRunnable;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Command processor.<br/>
 * For supported command syntax see {@link Tokenizer} javadoc.
 *
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface Commands {

    interface ExecutionContext {

        void onException(RuntimeException ex);

        void onUnknownCommand(List<String> args);
    }

    final class ThrowingExecutionContext implements ExecutionContext {

        public static final ExecutionContext INSTANCE = new ThrowingExecutionContext();

        private ThrowingExecutionContext() {
        }

        @Override
        public void onException(RuntimeException ex) {
            throw ex;
        }

        @Override
        public void onUnknownCommand(List<String> args) {
            throw new CommandException.UnknownCommandException(args.get(0));
        }
    }

    /**
     * Splits passed command line into tokens using configured tokenizer and executes separate commands.
     *
     * @param commandLine the command line to execute.
     * @param context     the execution context to deal with exceptional cases
     */
    void execute(String commandLine, ExecutionContext context);

    /**
     * Splits passed command line into tokens using configured tokenizer and executes separate commands.
     *
     * @param commandLine the command line to execute.
     * @throws CommandException.CommandStackOverflowException   if current call's depth is greater than configured {@code maxDepth}.
     * @throws CommandException.CommandExecutionFailedException if command execution has failed.
     * @throws CommandException.UnknownCommandException         if unknown command is present in passed command line.
     */
    default void execute(String commandLine) throws CommandException.CommandStackOverflowException, CommandException.CommandExecutionFailedException, CommandException.UnknownCommandException {
        execute(commandLine, ThrowingExecutionContext.INSTANCE);
    }

    /**
     * @return all registered commands
     */
    Stream<Command> commands();

    /**
     * Registers passed command handler for specified command. The handler will receive a list of command arguments where
     * first element is always a command itself.
     *
     * @param handler the handler to call to execute command.
     * @return the handle to unbind handler.
     * @throws CommandException.CommandAlreadyRegisteredException if specified command is already registered.
     */
    AutoCloseable add(Command handler) throws CommandException.CommandAlreadyRegisteredException;

    /**
     * Convenient method for commands without arguments.
     *
     * @param command the command to bind handler to.
     * @param handler the handler.
     * @return the handle to unbind handler.
     */
    default AutoCloseable add(String command, Consumer<List<String>> handler) {
        return add(
                new Command() {
                    @Override
                    public void run(List<String> args) {
                        handler.accept(args);
                    }

                    @Override
                    public String name() {
                        return command;
                    }
                }
        );
    }

    /**
     * Convenient method for commands without arguments.
     *
     * @param command the command to bind handler to.
     * @param handler the handler.
     * @return the handle to unbind handler.
     */
    default AutoCloseable add(String command, ThrowingRunnable handler) {
        return add(Handlers.command(command, handler));
    }

    /**
     * Convenient method for commands without arguments.
     *
     * @param command the command to bind handler to.
     * @param handler the handler receiving only command name.
     * @return the handle to unbind handler.
     */
    default AutoCloseable add(String command, H1 handler) {
        return add(Handlers.command(command, handler));
    }

    /**
     * Convenient method for commands with single argument.
     *
     * @param command the command to bind handler to.
     * @param handler the handler receiving only command name.
     * @return the handle to unbind handler.
     */
    default AutoCloseable add(String command, H2 handler) {
        return add(Handlers.command(command, handler));
    }

    /**
     * Convenient method for commands with two arguments.
     *
     * @param command the command to bind handler to.
     * @param handler the handler receiving only command name.
     * @return the handle to unbind handler.
     */
    default AutoCloseable add(String command, H3 handler) {
        return add(Handlers.command(command, handler));
    }

    /**
     * Convenient method for commands with three arguments.
     *
     * @param command the command to bind handler to.
     * @param handler the handler receiving only command name.
     * @return the handle to unbind handler.
     */
    default AutoCloseable add(String command, H4 handler) {
        return add(Handlers.command(command, handler));
    }

    /**
     * Convenient method for commands with four arguments.
     *
     * @param command the command to bind handler to.
     * @param handler the handler receiving only command name.
     * @return the handle to unbind handler.
     */
    default AutoCloseable add(String command, H5 handler) {
        return add(Handlers.command(command, handler));
    }

    /**
     * Convenient method for commands with five arguments.
     *
     * @param command the command to bind handler to.
     * @param handler the handler receiving only command name.
     * @return the handle to unbind handler.
     */
    default AutoCloseable add(String command, H6 handler) {
        return add(Handlers.command(command, handler));
    }

    /**
     * Convenient method to add many commands at once.
     *
     * @return the command builder
     */
    default CommandBuilder add() {
        return new CommandBuilder(this);
    }

    @FunctionalInterface
    interface H1 {

        void handle(String a1) throws Exception;
    }

    @FunctionalInterface
    interface H2 {

        void handle(String a1, String a2) throws Exception;
    }

    @FunctionalInterface
    interface H3 {

        void handle(String a1, String a2, String a3) throws Exception;
    }

    @FunctionalInterface
    interface H4 {

        void handle(String a1, String a2, String a3, String a4) throws Exception;
    }

    @FunctionalInterface
    interface H5 {

        void handle(String a1, String a2, String a3, String a4, String a5) throws Exception;
    }

    @FunctionalInterface
    interface H6 {

        void handle(String a1, String a2, String a3, String a4, String a5, String a6) throws Exception;
    }
}
