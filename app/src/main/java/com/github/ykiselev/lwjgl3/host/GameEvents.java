package com.github.ykiselev.lwjgl3.host;

import com.github.ykiselev.closeables.Closeables;
import com.github.ykiselev.lwjgl3.events.SubscriptionsBuilder;
import com.github.ykiselev.lwjgl3.events.game.NewGameEvent;
import com.github.ykiselev.lwjgl3.layers.UiLayers;
import com.github.ykiselev.lwjgl3.playground.Game;
import com.github.ykiselev.lwjgl3.services.Services;

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
        synchronized (lock) {
            if (game != null) {
                Closeables.close(game);
                game = null;
            }
            game = new Game(services);
            services.resolve(UiLayers.class)
                    .replace(game);
        }
        return null;
    }

    @Override
    public void close() {
        synchronized (lock) {
            Closeables.close(game);
            game = null;
        }
    }

    @Override
    public SubscriptionsBuilder apply(SubscriptionsBuilder builder) {
        return builder.with(NewGameEvent.class, this::onNewGameEvent)
                .and(this);
    }
}
