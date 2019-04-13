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

import com.github.ykiselev.common.closeables.CompositeAutoCloseable;
import com.github.ykiselev.common.ThrowingRunnable;

import java.util.List;
import java.util.function.Consumer;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class CommandBuilder {

    private final Commands commands;

    private final CompositeAutoCloseable ac;

    @SuppressWarnings("WeakerAccess")
    public CommandBuilder(Commands commands, CompositeAutoCloseable ac) {
        this.commands = requireNonNull(commands);
        this.ac = requireNonNull(ac);
    }

    @SuppressWarnings("WeakerAccess")
    public CommandBuilder(Commands commands) {
        this(commands, new CompositeAutoCloseable());
    }

    public CommandBuilder with(Command handler) {
        return new CommandBuilder(
                commands,
                ac.and(commands.add(handler))
        );
    }

    public CommandBuilder with(String command, Consumer<List<String>> handler) {
        return new CommandBuilder(
                commands,
                ac.and(commands.add(command, handler))
        );
    }

    public CommandBuilder with(String command, ThrowingRunnable handler) {
        return new CommandBuilder(
                commands,
                ac.and(commands.add(Handlers.command(command, handler)))
        );
    }

    public CommandBuilder with(String command, Commands.H1 handler) {
        return new CommandBuilder(
                commands,
                ac.and(commands.add(Handlers.command(command, handler)))
        );
    }

    public CommandBuilder with(String command, Commands.H2 handler) {
        return new CommandBuilder(
                commands,
                ac.and(commands.add(Handlers.command(command, handler)))
        );
    }

    public CommandBuilder with(String command, Commands.H3 handler) {
        return new CommandBuilder(
                commands,
                ac.and(commands.add(Handlers.command(command, handler)))
        );
    }

    public CommandBuilder with(String command, Commands.H4 handler) {
        return new CommandBuilder(
                commands,
                ac.and(commands.add(Handlers.command(command, handler)))
        );
    }

    public CommandBuilder with(String command, Commands.H5 handler) {
        return new CommandBuilder(
                commands,
                ac.and(commands.add(Handlers.command(command, handler)))
        );
    }

    public CommandBuilder with(String command, Commands.H6 handler) {
        return new CommandBuilder(
                commands,
                ac.and(commands.add(Handlers.command(command, handler)))
        );
    }

    public CompositeAutoCloseable build() {
        return ac;
    }
}
