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

import com.github.ykiselev.services.commands.CommandException.CommandAlreadyRegisteredException;
import com.github.ykiselev.services.commands.CommandException.CommandExecutionFailedException;
import com.github.ykiselev.services.commands.CommandException.CommandStackOverflowException;
import com.github.ykiselev.services.commands.CommandException.UnknownCommandException;
import com.github.ykiselev.services.commands.Commands;
import com.github.ykiselev.services.commands.Tokenizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class AppCommands implements Commands {

    private static final VarHandle VH;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Tokenizer tokenizer;

    private final int maxDepth;

    private final Deque<List<String>> stack = new ArrayDeque<>();

    // Note: this map should be treated as immutable!
    private volatile Map<String, Consumer<List<String>>> handlers = Collections.emptyMap();

    // accessed only from synchronized block
    private int depth;

    static {
        try {
            VH = MethodHandles.lookup().findVarHandle(AppCommands.class, "handlers", Map.class);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new Error(e);
        }
    }

    public AppCommands(Tokenizer tokenizer, int maxDepth) {
        this.tokenizer = requireNonNull(tokenizer);
        this.maxDepth = maxDepth;
    }

    private List<String> newArgs() {
        final List<String> existing = stack.poll();
        if (existing != null) {
            return existing;
        }
        return new ArrayList<>();
    }

    private void freeArgs(List<String> args) {
        args.clear();
        stack.push(args);
    }

    @Override
    public void execute(String commandLine) throws CommandStackOverflowException, CommandExecutionFailedException, UnknownCommandException {
        if (commandLine == null || commandLine.isEmpty()) {
            return;
        }
        synchronized (stack) {
            depth++;
            if (depth > maxDepth) {
                throw new CommandStackOverflowException(maxDepth);
            }
            final List<String> args = newArgs();
            try {
                int fromIndex = 0;
                while (fromIndex < commandLine.length()) {
                    fromIndex = execute(commandLine, fromIndex, args);
                    args.clear();
                }
            } finally {
                freeArgs(args);
                depth--;
            }
        }
    }

    private int execute(String commandLine, int fromIndex, List<String> args) throws CommandExecutionFailedException, UnknownCommandException {
        final int result = tokenizer.tokenize(commandLine, fromIndex, args);
        if (!args.isEmpty()) {
            final Consumer<List<String>> handler = handlers.get(args.get(0));
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
    public AutoCloseable add(String command, Consumer<List<String>> handler) throws CommandAlreadyRegisteredException {
        for (; ; ) {
            final Map<String, Consumer<List<String>>> before = this.handlers;
            final Map<String, Consumer<List<String>>> after = new HashMap<>(before);
            if (after.putIfAbsent(command, handler) != null) {
                throw new CommandAlreadyRegisteredException(command);
            }
            if (VH.compareAndSet(this, before, after)) {
                return () -> remove(command);
            }
        }
    }

    private void remove(String command) {
        for (; ; ) {
            final Map<String, Consumer<List<String>>> before = this.handlers;
            final Map<String, Consumer<List<String>>> after = new HashMap<>(before);
            if (after.remove(command) == null) {
                logger.warn("Unable to remove (not found): {}", command);
            }
            if (VH.compareAndSet(this, before, after)) {
                break;
            }
        }
    }
}
