package cob.github.ykiselev.lwjgl3.host;

import cob.github.ykiselev.lwjgl3.events.layers.ShowMenuEvent;
import cob.github.ykiselev.lwjgl3.layers.UiLayer;
import cob.github.ykiselev.lwjgl3.layers.UiLayers;
import cob.github.ykiselev.lwjgl3.layers.menu.Menu;
import cob.github.ykiselev.lwjgl3.services.Services;

import java.util.function.Consumer;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class OnShowMenuEvent implements Consumer<ShowMenuEvent> {

    private final Services services;

    public OnShowMenuEvent(Services services) {
        this.services = requireNonNull(services);
    }

    @Override
    public void accept(ShowMenuEvent showMenuEvent) {
        final UiLayer menu = services.resolveOrAdd(
                Menu.class,
                () -> new Menu(services)
        );
        services.resolve(UiLayers.class)
                .push(menu);
    }
}
