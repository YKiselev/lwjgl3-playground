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

package com.github.ykiselev.lwjgl3.host;

import com.github.ykiselev.closeables.Closeables;
import com.github.ykiselev.components.Game;
import com.github.ykiselev.services.events.SubscriptionsBuilder;
import com.github.ykiselev.services.PersistedConfiguration;
import com.github.ykiselev.services.Services;
import com.github.ykiselev.services.events.game.NewGameEvent;
import com.github.ykiselev.services.layers.UiLayers;
import com.github.ykiselev.spi.ClassFromName;
import com.github.ykiselev.spi.InstanceFromClass;

import java.util.function.UnaryOperator;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class GameEvents implements AutoCloseable, UnaryOperator<SubscriptionsBuilder> {

    private final Services services;

    private volatile Game game;

    private final Object lock = new Object();

    public GameEvents(Services services) {
        this.services = requireNonNull(services);
    }

    private NewGameEvent onNewGameEvent(NewGameEvent event) {
        final String factoryClassName = services.resolve(PersistedConfiguration.class)
                .root()
                .getString("game.factory");
        synchronized (lock) {
            closeGame();
            game = new InstanceFromClass<Game>(
                    new ClassFromName(factoryClassName),
                    services
            ).get();
            services.resolve(UiLayers.class)
                    .bringToFront(game);
        }
        return null;
    }

    @Override
    public void close() {
        closeGame();
    }

    private void closeGame() {
        synchronized (lock) {
            if (game != null) {
                services.resolve(UiLayers.class)
                        .remove(game);
                Closeables.close(game);
                game = null;
            }
        }
    }

    @Override
    public SubscriptionsBuilder apply(SubscriptionsBuilder builder) {
        return builder.with(NewGameEvent.class, this::onNewGameEvent)
                .and(this);
    }
}
