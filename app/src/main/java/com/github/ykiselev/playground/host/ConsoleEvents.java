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

package com.github.ykiselev.playground.host;

import com.github.ykiselev.closeables.Closeables;
import com.github.ykiselev.closeables.CompositeAutoCloseable;
import com.github.ykiselev.playground.services.console.AppConsole;
import com.github.ykiselev.services.Services;
import com.github.ykiselev.services.events.Events;
import com.github.ykiselev.services.events.console.ToggleConsoleEvent;
import com.github.ykiselev.services.layers.UiLayers;

import java.util.function.UnaryOperator;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class ConsoleEvents implements AutoCloseable, UnaryOperator<CompositeAutoCloseable> {

    private final Services services;

    private final AppConsole console;

    private final Object lock = new Object();

    public ConsoleEvents(Services services) {
        this.services = requireNonNull(services);
        this.console = new AppConsole(null);
        services.resolve(UiLayers.class)
                .bringToFront(console);
    }

    @Override
    public void close() {
        Closeables.close(console);
    }

    @Override
    public CompositeAutoCloseable apply(CompositeAutoCloseable builder) {
        return builder.and(
                services.resolve(Events.class)
                        .subscribe(ToggleConsoleEvent.class, this::onToggleConsole)
        ).and(this);
    }

    private void onToggleConsole() {
        throw new UnsupportedOperationException("not implemented");
    }
}
