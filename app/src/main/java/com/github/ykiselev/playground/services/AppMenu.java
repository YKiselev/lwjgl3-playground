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
import com.github.ykiselev.playground.layers.menu.Menu;
import com.github.ykiselev.spi.services.Services;

import java.util.concurrent.TimeUnit;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class AppMenu implements AutoCloseable {

    private final Services services;

    private final AutoCloseable ac;

    private volatile Menu menu;

    private final Object lock = new Object();

    public AppMenu(Services services) {
        this.services = requireNonNull(services);
        this.ac = services.commands.add("show-menu", this::showMenu);
    }

    private boolean recycle() {
        synchronized (lock) {
            final Menu m = menu;
            if (m != null && m.canBeRemoved()) {
                Closeables.close(m);
                menu = null;
                return false;
            }
        }
        return true;
    }

    @Override
    public void close() {
        synchronized (lock) {
            Closeables.closeAll(menu, ac);
            menu = null;
        }
    }

    private void showMenu() {
        synchronized (lock) {
            if (menu == null) {
                menu = new Menu(services);
                services.schedule.schedule(10, TimeUnit.SECONDS, this::recycle);
            }
            services.uiLayers.add(menu);
        }
    }
}
