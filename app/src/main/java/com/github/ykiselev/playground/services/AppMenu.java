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

import com.github.ykiselev.assets.Assets;
import com.github.ykiselev.common.closeables.Closeables;
import com.github.ykiselev.opengl.sprites.SpriteBatch;
import com.github.ykiselev.playground.layers.menu.Menu;
import com.github.ykiselev.spi.services.commands.Commands;
import com.github.ykiselev.spi.services.configuration.PersistedConfiguration;
import com.github.ykiselev.spi.services.layers.UiLayers;
import com.github.ykiselev.spi.services.schedule.Schedule;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class AppMenu implements AutoCloseable {

    private final AutoCloseable ac;

    private final Supplier<Menu> menuSupplier;

    private final Schedule schedule;

    private final UiLayers uiLayers;

    private Menu menu;

    public AppMenu(Assets assets, SpriteBatch spriteBatch, PersistedConfiguration configuration, Commands commands, UiLayers uiLayers, Schedule schedule) {
        this.schedule = requireNonNull(schedule);
        this.uiLayers = requireNonNull(uiLayers);
        this.ac = commands.add("show-menu", this::showMenu);
        this.menuSupplier = () -> new Menu(assets, spriteBatch,
                configuration, commands, uiLayers);
    }

    private boolean recycle() {
        final Menu m = menu;
        if (m != null && m.canBeRemoved()) {
            Closeables.close(m);
            menu = null;
            return false;
        }
        return true;
    }

    @Override
    public void close() {
        Closeables.closeAll(menu, ac);
        menu = null;
    }

    private void showMenu() {
        if (menu == null) {
            menu = menuSupplier.get();
            schedule.schedule(10, TimeUnit.SECONDS, this::recycle);
        }
        uiLayers.add(menu);
    }
}
