package cob.github.ykiselev.lwjgl3.host;

import cob.github.ykiselev.lwjgl3.events.game.NewGameEvent;
import cob.github.ykiselev.lwjgl3.layers.UiLayers;
import cob.github.ykiselev.lwjgl3.playground.Game;
import cob.github.ykiselev.lwjgl3.services.Services;
import com.github.ykiselev.assets.Assets;

import java.util.function.Consumer;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class OnNewGameEvent implements Consumer<NewGameEvent> {

    private final Host host;

    public OnNewGameEvent(Host host) {
        this.host = requireNonNull(host);
    }

    @Override
    public void accept(NewGameEvent event) {
        final Services services = host.services();
        services.tryResolve(Game.class)
                .ifPresent(g -> {
                    services.remove(Game.class, g);
                    g.close();
                });
        final Game game = new Game(
                host,
                services.resolve(Assets.class)
        );
        services.add(Game.class, game);
        services.resolve(UiLayers.class)
                .replace(game);
    }
}
