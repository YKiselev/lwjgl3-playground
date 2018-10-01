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

import com.github.ykiselev.services.layers.UiLayer;
import com.github.ykiselev.window.WindowEvents;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class AppConsole implements UiLayer, AutoCloseable {

    private final ConsoleOutputStream stream;

    private final WindowEvents events = new WindowEvents() {
        // todo
    };

    public AppConsole(ConsoleOutputStream stream) {
        this.stream = stream;
    }

    @Override
    public WindowEvents events() {
        return events;
    }

    @Override
    public void draw(int width, int height) {
        // todo
        final LoggerContext context = (LoggerContext) LogManager.getContext(false);
        final Configuration cfg = context.getConfiguration();
        final AppConsoleLog4j2Appender appender = cfg.getAppender("AppConsole");
        if (appender != null) {
            int g = 0;
        }
    }

    @Override
    public Kind kind() {
        return Kind.NORMAL;
    }

    @Override
    public void close() throws Exception {
        // todo
    }
}
