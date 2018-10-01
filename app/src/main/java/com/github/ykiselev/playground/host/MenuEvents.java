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
import com.github.ykiselev.playground.layers.menu.Menu;
import com.github.ykiselev.services.Services;
import com.github.ykiselev.services.events.Events;
import com.github.ykiselev.services.events.menu.ShowMenuEvent;
import com.github.ykiselev.services.layers.UiLayers;
import com.github.ykiselev.services.schedule.Schedule;

import java.util.concurrent.TimeUnit;
import java.util.function.UnaryOperator;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class MenuEvents implements AutoCloseable, UnaryOperator<CompositeAutoCloseable> {

    private final Services services;

    private volatile Menu menu;

    private final Object lock = new Object();

    public MenuEvents(Services services) {
        this.services = requireNonNull(services);
    }

    private void onShowMenuEvent() {
        synchronized (lock) {
            if (menu == null) {
                menu = new Menu(services);
                services.resolve(Schedule.class)
                        .schedule(10, TimeUnit.SECONDS, this::recycle);
            }
            services.resolve(UiLayers.class).add(menu);
        }
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
            Closeables.close(menu);
            menu = null;
        }
    }

    @Override
    public CompositeAutoCloseable apply(CompositeAutoCloseable builder) {
        return builder.and(
                services.resolve(Events.class)
                        .subscribe(ShowMenuEvent.class, this::onShowMenuEvent)
        ).and(this);
    }
}
