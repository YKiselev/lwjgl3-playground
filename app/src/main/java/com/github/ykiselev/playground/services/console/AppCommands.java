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

package com.github.ykiselev.playground.services.console;

import com.github.ykiselev.cow.CopyOnModify;
import com.github.ykiselev.services.commands.Command;
import com.github.ykiselev.services.commands.CommandException.CommandAlreadyRegisteredException;
import com.github.ykiselev.services.commands.CommandException.CommandExecutionFailedException;
import com.github.ykiselev.services.commands.CommandException.CommandStackOverflowException;
import com.github.ykiselev.services.commands.Commands;
import com.github.ykiselev.services.commands.Tokenizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class AppCommands implements Commands {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Tokenizer tokenizer;

    private final CopyOnModify<Map<String, Command>> handlers = new CopyOnModify<>(Collections.emptyMap());

    private final ThreadLocal<ThreadContext> context;

    /**
     * Primary ctor.
     *
     * @param tokenizer the tokenizer to use.
     * @param maxDepth  the maximum depth of recursive calls.
     */
    public AppCommands(Tokenizer tokenizer, int maxDepth) {
        this.tokenizer = requireNonNull(tokenizer);
        this.context = ThreadLocal.withInitial(() -> new ThreadContext(maxDepth));
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
    public void execute(String commandLine, ExecutionContext ctx) {
        if (commandLine == null || commandLine.isEmpty()) {
            return;
        }
        final ThreadContext tc = context.get();
        tc.enter(ctx);
        try {
            final List<String> args = tc.args();
            args.clear();
            int fromIndex = 0;
            while (fromIndex < commandLine.length()) {
                fromIndex = execute(commandLine, fromIndex, args, tc.context());
                args.clear();
            }
        } finally {
            tc.leave();
        }
    }

    private Command handler(String command) {
        return handlers.value().get(command);
    }

    private int execute(String commandLine, int fromIndex, List<String> args, ExecutionContext ctx) {
        final int result = tokenizer.tokenize(commandLine, fromIndex, args);
        if (!args.isEmpty()) {
            final Command handler = handler(args.get(0));
            if (handler != null) {
                try {
                    handler.run(args);
                } catch (CommandStackOverflowException | CommandExecutionFailedException e) {
                    ctx.onException(e);
                } catch (Exception e) {
                    ctx.onException(new CommandExecutionFailedException(commandLine, e));
                }
            } else {
                ctx.onUnknownCommand(args);
            }
        }
        return result;
    }

    @Override
    public Stream<Command> commands() {
        return handlers.value()
                .entrySet()
                .stream()
                .map(Map.Entry::getValue);
    }

    @Override
    public AutoCloseable add(Command handler) throws CommandAlreadyRegisteredException {
        handlers.modify(before -> {
            final Map<String, Command> after = new HashMap<>(before);
            if (after.putIfAbsent(handler.name(), handler) != null) {
                throw new CommandAlreadyRegisteredException(handler.name());
            }
            return after;
        });
        return () -> remove(handler.name());
    }

    private void remove(String command) {
        handlers.modify(before -> {
            final Map<String, Command> after = new HashMap<>(before);
            if (after.remove(command) == null) {
                logger.warn("Unable to remove (not found): {}", command);
            }
            return after;
        });
    }

    private static final class ThreadContext {

        private final List<String> args = new ArrayList<>();

        private final int maxDepth;

        private final Queue<ExecutionContext> queue;

        List<String> args() {
            return args;
        }

        ExecutionContext context() {
            return queue.element();
        }

        ThreadContext(int maxDepth) {
            this.maxDepth = maxDepth;
            this.queue = new ArrayDeque<>(maxDepth);
        }

        void enter(ExecutionContext context) {
            if (queue.size() >= maxDepth) {
                throw new CommandStackOverflowException(maxDepth);
            }
            queue.add(context);
        }

        void leave() {
            queue.remove();
        }
    }
}