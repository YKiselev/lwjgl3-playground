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
import com.github.ykiselev.closeables.Closeables;
import com.github.ykiselev.closeables.CompositeAutoCloseable;
import com.github.ykiselev.components.Game;
import com.github.ykiselev.services.GameFactory;
import com.github.ykiselev.services.Services;
import com.github.ykiselev.services.commands.Commands;
import com.github.ykiselev.services.configuration.PersistedConfiguration;
import com.github.ykiselev.services.layers.UiLayers;
import com.github.ykiselev.spi.ClassFromName;
import com.github.ykiselev.spi.InstanceFromClass;
import com.github.ykiselev.wrap.Wrap;
import com.typesafe.config.Config;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class AppGameFactory implements GameFactory {

    private final Services services;

    private volatile Game game;

    private final Object lock = new Object();

    private final AutoCloseable subscriptions;

    public AppGameFactory(Services services) {
        this.services = requireNonNull(services);
        this.subscriptions = new CompositeAutoCloseable(
                services.resolve(Commands.class)
                        .add("new-game", this::newGame),
                services.resolve(PersistedConfiguration.class)
                        .wire()
                        .withBoolean("game.isPresent", () -> game != null, false)
                        .build()
        );
    }

    private String getFactoryClassName() {
        final Wrap<Config> wrap = services.resolve(Assets.class)
                .load("game.conf", Config.class);
        try (wrap) {
            return wrap.value().getString("game.factory");
        }
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

    @Override
    public void newGame() {
        synchronized (lock) {
            closeGame();
            game = new InstanceFromClass<Game>(
                    new ClassFromName(getFactoryClassName()),
                    services
            ).get();
            final UiLayers uiLayers = services.resolve(UiLayers.class);
            uiLayers.add(game);
            uiLayers.removePopups();
        }
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
}
