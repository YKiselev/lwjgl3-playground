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

package com.github.ykiselev.playground.services;

import com.github.ykiselev.common.closeables.Closeables;
import com.github.ykiselev.playground.services.console.AppConsole;
import com.github.ykiselev.playground.services.console.ConsoleFactory;
import com.github.ykiselev.spi.services.GameContainer;
import com.github.ykiselev.spi.services.MenuFactory;
import com.github.ykiselev.spi.services.Services;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru)
 * @since 27.04.2019
 */
public final class Components implements AutoCloseable {

    public final Services services;

    public final AppConsole console;

    public final MenuFactory menuFactory;

    public final GameContainer gameContainer;

    public Components(Services services) {
        this.services = requireNonNull(services);
        this.console = ConsoleFactory.create(services);
        this.menuFactory = new AppMenuFactory(services);
        this.gameContainer = new AppGameContainer(services);
    }

    @Override
    public void close() {
        Closeables.closeAll(menuFactory, gameContainer, console);
    }
}
