package com.github.ykiselev.lwjgl3.host;

import com.github.ykiselev.closeables.Closeables;
import com.github.ykiselev.lwjgl3.events.game.NewGameEvent;
import com.github.ykiselev.lwjgl3.layers.UiLayers;
import com.github.ykiselev.lwjgl3.playground.Game;
import com.github.ykiselev.lwjgl3.services.Services;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class GameEvents implements AutoCloseable {

    private final Services services;

    private volatile Game game;

    public GameEvents(Services services) {
        this.services = requireNonNull(services);
    }

    public synchronized NewGameEvent onNewGameEvent(NewGameEvent event) {
        if (game != null) {
            Closeables.close(game);
            game = null;
        }
        game = new Game(services);
        services.resolve(UiLayers.class)
                .replace(game);
        return null;
    }

    @Override
    public synchronized void close() {
        Closeables.close(game);
        game = null;
    }
}
