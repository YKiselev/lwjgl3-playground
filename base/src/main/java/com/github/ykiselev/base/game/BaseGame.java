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

package com.github.ykiselev.base.game;

import com.github.ykiselev.base.game.client.GameClient;
import com.github.ykiselev.base.game.server.GameServer;
import com.github.ykiselev.common.closeables.Closeables;
import com.github.ykiselev.spi.GameFactoryArgs;
import com.github.ykiselev.spi.components.Game;
import com.github.ykiselev.spi.services.layers.DrawingContext;
import com.github.ykiselev.spi.window.Window;
import com.github.ykiselev.spi.window.WindowEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class BaseGame implements Game {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final AutoCloseable closeable;

    private final Window window;

    private final GameServer server;

    private final GameClient client;

    private final WindowEvents events;

    public BaseGame(GameFactoryArgs args) {
        this.window = args.window();

        try (var guard = Closeables.newGuard()) {
            this.server = new GameServer();
            guard.add(server);

            this.client = new GameClient(args);
            guard.add(client);

            closeable = guard.detach();
        }
        this.events = client;
    }

    @Override
    public void onActivation(boolean active) {
        if (active) {
            window.hideCursor();
        } else {
            window.showCursor();
        }
    }

    @Override
    public WindowEvents events() {
        return events;
    }


    @Override
    public void update() {
        server.update();
        client.update();
    }

    @Override
    public void close() throws Exception {
        closeable.close();
    }

    @Override
    public void draw(int width, int height, DrawingContext context) {
        client.draw(width, height, context);
    }
}