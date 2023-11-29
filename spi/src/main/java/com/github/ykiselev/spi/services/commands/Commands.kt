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

import com.github.ykiselev.spi.services.commands.CommandException.*
import java.util.function.Consumer
import java.util.stream.Stream

/**
 * Command processor.<br></br>
 * For supported command syntax see [Tokenizer] javadoc.
 *
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
interface Commands {
    interface ExecutionContext {
        fun onException(ex: RuntimeException)
        fun onUnknownCommand(args: List<String>)
    }

    class ThrowingExecutionContext private constructor() : ExecutionContext {
        override fun onException(ex: RuntimeException) {
            throw ex
        }

        override fun onUnknownCommand(args: List<String>) {
            throw UnknownCommandException(args[0])
        }

        companion object {
            val INSTANCE: ExecutionContext = ThrowingExecutionContext()
        }
    }

    /**
     * Splits passed command line into tokens using configured tokenizer and executes separate commands.
     *
     * @param commandLine the command line to execute.
     * @param context     the execution context to deal with exceptional cases
     */
    fun execute(commandLine: String, context: ExecutionContext)

    /**
     * Splits passed command line into tokens using configured tokenizer and executes separate commands.
     *
     * @param commandLine the command line to execute.
     * @throws CommandException.CommandStackOverflowException   if current call's depth is greater than configured `maxDepth`.
     * @throws CommandException.CommandExecutionFailedException if command execution has failed.
     * @throws CommandException.UnknownCommandException         if unknown command is present in passed command line.
     */
    @Throws(
        CommandStackOverflowException::class,
        CommandExecutionFailedException::class,
        UnknownCommandException::class
    )
    fun execute(commandLine: String) {
        execute(commandLine, ThrowingExecutionContext.INSTANCE)
    }

    /**
     * @return all registered commands
     */
    fun commands(): Stream<Command>

    /**
     * Registers passed command handler for specified command. The handler will receive a list of command arguments where
     * first element is always a command itself.
     *
     * @param handler the handler to call to execute command.
     * @return the handle to unbind handler.
     * @throws CommandException.CommandAlreadyRegisteredException if specified command is already registered.
     */
    @Throws(CommandAlreadyRegisteredException::class)
    fun add(handler: Command): AutoCloseable

    /**
     * Convenient method for commands with many arguments.
     *
     * @param command the command to bind handler to.
     * @param handler the handler.
     * @return the handle to unbind handler.
     */
    fun add(command: String, handler: Consumer<List<String>>): AutoCloseable {
        return add(
            object : Command(command) {
                override fun run(args: List<String>) {
                    handler.accept(args)
                }
            }
        )
    }

    /**
     * Convenient method for commands without arguments.
     *
     * @param command the command to bind handler to.
     * @param handler the handler without arguments.
     * @return the handle to unbind handler.
     */
    fun add(command: String, handler: H0): AutoCloseable =
        add(Handlers.command(command, handler))

    /**
     * Convenient method for commands with one argument.
     *
     * @param command the command to bind handler to.
     * @param handler the handler.
     * @return the handle to unbind handler.
     */
    fun add(command: String, handler: H1): AutoCloseable =
        add(Handlers.command(command, handler))

    /**
     * Convenient method for commands with two arguments.
     *
     * @param command the command to bind handler to.
     * @param handler the handler.
     * @return the handle to unbind handler.
     */
    fun add(command: String, handler: H2): AutoCloseable =
        add(Handlers.command(command, handler))

    /**
     * Convenient method for commands with three arguments.
     *
     * @param command the command to bind handler to.
     * @param handler the handler.
     * @return the handle to unbind handler.
     */
    fun add(command: String, handler: H3): AutoCloseable =
        add(Handlers.command(command, handler))

    /**
     * Convenient method for commands with four arguments.
     *
     * @param command the command to bind handler to.
     * @param handler the handler.
     * @return the handle to unbind handler.
     */
    fun add(command: String, handler: H4): AutoCloseable =
        add(Handlers.command(command, handler))

    /**
     * Convenient method for commands with five arguments.
     *
     * @param command the command to bind handler to.
     * @param handler the handler.
     * @return the handle to unbind handler.
     */
    fun add(command: String, handler: H5): AutoCloseable =
        add(Handlers.command(command, handler))

    /**
     * Convenient method for commands with six arguments.
     *
     * @param command the command to bind handler to.
     * @param handler the handler.
     * @return the handle to unbind handler.
     */
    fun add(command: String, handler: H6): AutoCloseable =
        add(Handlers.command(command, handler))

    /**
     * Convenient method to add many commands at once.
     *
     * @return the command builder
     */
    fun add(): CommandBuilder =
        CommandBuilder(this)

    fun interface H0 {
        @Throws(Exception::class)
        fun handle()
    }

    fun interface H1 {
        @Throws(Exception::class)
        fun handle(a1: String)
    }

    fun interface H2 {
        @Throws(Exception::class)
        fun handle(a1: String, a2: String)
    }

    fun interface H3 {
        @Throws(Exception::class)
        fun handle(a1: String, a2: String, a3: String)
    }

    fun interface H4 {
        @Throws(Exception::class)
        fun handle(a1: String, a2: String, a3: String, a4: String)
    }

    fun interface H5 {
        @Throws(Exception::class)
        fun handle(a1: String, a2: String, a3: String, a4: String, a5: String)
    }

    fun interface H6 {
        @Throws(Exception::class)
        fun handle(a1: String, a2: String, a3: String, a4: String, a5: String, a6: String)
    }
}
