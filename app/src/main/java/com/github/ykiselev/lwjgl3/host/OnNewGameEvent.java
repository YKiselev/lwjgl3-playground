package com.github.ykiselev.lwjgl3.host;

import com.github.ykiselev.lwjgl3.events.game.NewGameEvent;
import com.github.ykiselev.lwjgl3.layers.UiLayers;
import com.github.ykiselev.lwjgl3.playground.Game;
import com.github.ykiselev.lwjgl3.services.Services;

import java.util.function.Consumer;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class OnNewGameEvent implements Consumer<NewGameEvent> {

    private final Services services;

    public OnNewGameEvent(Services services) {
        this.services = requireNonNull(services);
    }

    @Override
    public void accept(NewGameEvent event) {
        services.tryResolve(Game.class)
                .ifPresent(g -> {
                    services.remove(Game.class, g);
                    try {
                        g.close();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
        final Game game = new Game(services);
        services.add(Game.class, game);
        services.resolve(UiLayers.class)
                .replace(game);
    }
}
