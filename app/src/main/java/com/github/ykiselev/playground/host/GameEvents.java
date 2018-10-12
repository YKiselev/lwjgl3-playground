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

import com.github.ykiselev.api.Updateable;
import com.github.ykiselev.assets.Assets;
import com.github.ykiselev.closeables.Closeables;
import com.github.ykiselev.closeables.CompositeAutoCloseable;
import com.github.ykiselev.components.Game;
import com.github.ykiselev.services.Services;
import com.github.ykiselev.services.commands.Commands;
import com.github.ykiselev.services.commands.EventFiringHandler;
import com.github.ykiselev.services.configuration.PersistedConfiguration;
import com.github.ykiselev.services.events.Events;
import com.github.ykiselev.services.events.game.NewGameEvent;
import com.github.ykiselev.services.layers.UiLayers;
import com.github.ykiselev.spi.ClassFromName;
import com.github.ykiselev.spi.InstanceFromClass;
import com.github.ykiselev.wrap.Wrap;
import com.typesafe.config.Config;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class GameEvents implements AutoCloseable, Updateable {

    private final Services services;

    private volatile Game game;

    private final Object lock = new Object();

    private final AutoCloseable subscriptions;

    public GameEvents(Services services) {
        this.services = requireNonNull(services);
        this.subscriptions = new CompositeAutoCloseable(
                services.resolve(Events.class)
                        .subscribe(NewGameEvent.class, this::onNewGame),
                services.resolve(Commands.class)
                        .add(new EventFiringHandler<>("new-game", services, NewGameEvent.INSTANCE)),
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

    private void onNewGame() {
        synchronized (lock) {
            closeGame();
            game = new InstanceFromClass<Game>(
                    new ClassFromName(getFactoryClassName()),
                    services
            ).get();
            services.resolve(UiLayers.class).add(game);
        }
    }
}
