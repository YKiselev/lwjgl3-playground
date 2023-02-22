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
import com.github.ykiselev.opengl.OglRecipes;
import com.github.ykiselev.opengl.materials.Materials;
import com.github.ykiselev.spi.GameFactory;
import com.github.ykiselev.spi.api.Updatable;
import com.github.ykiselev.spi.components.Game;
import com.github.ykiselev.wrap.Wrap;

import java.util.ServiceLoader;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class GameBootstrap implements Updatable, AutoCloseable {

    private final AppContext context;

    private volatile Game game;

    private final AutoCloseable closeable;

    public GameBootstrap(AppContext context) {
        this.context = requireNonNull(context);
        try (var guard = Closeables.newGuard()) {
            guard.add(context.commands().add()
                    .with("new-game", this::newGame)
                    .build());

            guard.add(context.configuration().wire()
                    .withBoolean("game.isPresent", () -> game != null, false)
                    .build());

            // debug
            Materials mat = guard.add(context.assets().load("materials/materials.conf", OglRecipes.MATERIALS));
            guard.add(mat);

            this.closeable = guard.detach();
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
        Closeables.close(closeable);
    }

    private void newGame() {
        closeGame();
        game = ServiceLoader.load(GameFactory.class)
                .findFirst()
                .map(f -> f.create(context.toGameFactoryArgs()))
                .orElseThrow(() -> new IllegalStateException("Game factory service not found!"));
        context.uiLayers().add(game);
        context.uiLayers().removePopups();
    }

    private void closeGame() {
        if (game != null) {
            context.uiLayers().remove(game);
            Closeables.close(game);
            game = null;
        }
    }
}
