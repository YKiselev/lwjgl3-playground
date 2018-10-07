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

import com.github.ykiselev.circular.CircularBuffer;
import com.github.ykiselev.playground.services.console.AppConsole;
import com.github.ykiselev.playground.services.console.AppConsoleLog4j2Appender;
import com.github.ykiselev.playground.services.console.ConsoleBuffer;
import com.github.ykiselev.services.Services;
import com.github.ykiselev.services.layers.UiLayers;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class ConsoleFactory {

    private final Services services;

    public ConsoleFactory(Services services) {
        this.services = requireNonNull(services);
    }

    public AppConsole create() {
        final AppConsole console = new AppConsole(
                services,
                new ConsoleBuffer(getBuffer())
        );
        services.resolve(UiLayers.class)
                .add(console);
        return console;
    }

    private static CircularBuffer<String> getBuffer() {
        final LoggerContext context = (LoggerContext) LogManager.getContext(false);
        final Configuration cfg = context.getConfiguration();
        final AppConsoleLog4j2Appender appender = cfg.getAppender("AppConsole");
        if (appender == null) {
            throw new IllegalStateException("Appender not found: \"AppConsole\". Check log4j2 configuration!");
        }
        return appender.buffer();
    }
}
