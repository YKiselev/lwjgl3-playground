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
import com.github.ykiselev.common.closeables.CompositeAutoCloseable;
import com.github.ykiselev.spi.GameFactory;
import com.github.ykiselev.spi.api.Updateable;
import com.github.ykiselev.spi.components.Game;
import com.github.ykiselev.spi.services.Services;

import java.util.ServiceLoader;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class AppGame implements Updateable, AutoCloseable {

    private final Services services;

    private volatile Game game;

    private final Object lock = new Object();

    private final AutoCloseable subscriptions;

    public AppGame(Services services) {
        this.services = requireNonNull(services);
        this.subscriptions = new CompositeAutoCloseable(
                services.commands.add("new-game", this::newGame),
                services.persistedConfiguration.wire()
                        .withBoolean("game.isPresent", () -> game != null, false)
                        .build()
        );
    }

    @Override
    public void update() {
        final Game game = this.game;
        if (game != null) {
            game.update();
        }
    }

    @Override
    public void close() {
        closeGame();
        Closeables.close(subscriptions);
    }

    private void newGame() {
        synchronized (lock) {
            closeGame();
            game = ServiceLoader.load(GameFactory.class)
                    .findFirst()
                    .map(f -> f.create(services))
                    .orElseThrow(() -> new IllegalStateException("Game factory service not found!"));
            services.uiLayers.add(game);
            services.uiLayers.removePopups();
        }
    }

    private void closeGame() {
        synchronized (lock) {
            if (game != null) {
                services.uiLayers.remove(game);
                Closeables.close(game);
                game = null;
            }
        }
    }
}
