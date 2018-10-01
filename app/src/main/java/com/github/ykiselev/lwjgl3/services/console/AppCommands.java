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

package com.github.ykiselev.lwjgl3.services.console;

import com.github.ykiselev.cow.CopyOnModify;
import com.github.ykiselev.services.commands.CommandException.CommandAlreadyRegisteredException;
import com.github.ykiselev.services.commands.CommandException.CommandExecutionFailedException;
import com.github.ykiselev.services.commands.CommandException.CommandStackOverflowException;
import com.github.ykiselev.services.commands.CommandException.UnknownCommandException;
import com.github.ykiselev.services.commands.Commands;
import com.github.ykiselev.services.commands.Tokenizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class AppCommands implements Commands {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Tokenizer tokenizer;

    private final int maxDepth;

    private final CopyOnModify<Map<String, Consumer<List<String>>>> handlers = new CopyOnModify<>(Collections.emptyMap());

    private final ThreadLocal<ThreadContext> context = ThreadLocal.withInitial(ThreadContext::new);

    /**
     * Primary ctor.
     *
     * @param tokenizer the tokenizer to use.
     * @param maxDepth  the maximum depth of recursive calls.
     */
    public AppCommands(Tokenizer tokenizer, int maxDepth) {
        this.tokenizer = requireNonNull(tokenizer);
        this.maxDepth = maxDepth;
    }

    /**
     * Convenient ctor with {@code maxDepth} set to 16.
     *
     * @param tokenizer the tokenizer to use.
     */
    public AppCommands(Tokenizer tokenizer) {
        this(tokenizer, 16);
    }

    @Override
    public void execute(String commandLine) throws CommandStackOverflowException, CommandExecutionFailedException, UnknownCommandException {
        if (commandLine == null || commandLine.isEmpty()) {
            return;
        }
        final ThreadContext tc = context.get();
        tc.enter(maxDepth);
        try {
            final List<String> args = tc.args();
            args.clear();
            int fromIndex = 0;
            while (fromIndex < commandLine.length()) {
                fromIndex = execute(commandLine, fromIndex, args);
                args.clear();
            }
        } finally {
            tc.leave();
        }
    }

    private Consumer<List<String>> handler(String command) {
        return handlers.value().get(command);
    }

    private int execute(String commandLine, int fromIndex, List<String> args) throws CommandExecutionFailedException, UnknownCommandException {
        final int result = tokenizer.tokenize(commandLine, fromIndex, args);
        if (!args.isEmpty()) {
            final Consumer<List<String>> handler = handler(args.get(0));
            if (handler != null) {
                try {
                    handler.accept(args);
                } catch (CommandExecutionFailedException | CommandStackOverflowException e) {
                    throw e;
                } catch (RuntimeException e) {
                    throw new CommandExecutionFailedException(commandLine, e);
                }
            } else {
                throw new UnknownCommandException(args.get(0));
            }
        }
        return result;
    }

    @Override
    public Stream<String> commands() {
        return handlers.value().keySet().stream();
    }

    @Override
    public AutoCloseable add(String command, Consumer<List<String>> handler) throws CommandAlreadyRegisteredException {
        handlers.modify(before -> {
            final Map<String, Consumer<List<String>>> after = new HashMap<>(before);
            if (after.putIfAbsent(command, handler) != null) {
                throw new CommandAlreadyRegisteredException(command);
            }
            return after;
        });
        return () -> remove(command);
    }

    private void remove(String command) {
        handlers.modify(before -> {
            final Map<String, Consumer<List<String>>> after = new HashMap<>(before);
            if (after.remove(command) == null) {
                logger.warn("Unable to remove (not found): {}", command);
            }
            return after;
        });
    }

    private static final class ThreadContext {

        private final List<String> args = new ArrayList<>();

        private int depth;

        List<String> args() {
            return args;
        }

        void enter(int limit) {
            depth++;
            if (depth > limit) {
                throw new CommandStackOverflowException(limit);
            }
        }

        void leave() {
            depth--;
        }
    }
}