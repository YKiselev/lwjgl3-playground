package com.github.ykiselev.lwjgl3.host;

import com.github.ykiselev.lwjgl3.events.layers.ShowMenuEvent;
import com.github.ykiselev.lwjgl3.layers.UiLayer;
import com.github.ykiselev.lwjgl3.layers.UiLayers;
import com.github.ykiselev.lwjgl3.layers.menu.Menu;
import com.github.ykiselev.lwjgl3.services.Services;

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
