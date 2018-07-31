package com.github.ykiselev.lwjgl3.host;

import com.github.ykiselev.closeables.Closeables;
import com.github.ykiselev.lwjgl3.events.layers.ShowMenuEvent;
import com.github.ykiselev.lwjgl3.layers.UiLayers;
import com.github.ykiselev.lwjgl3.layers.menu.Menu;
import com.github.ykiselev.lwjgl3.services.Services;
import com.github.ykiselev.lwjgl3.services.schedule.Schedule;

import java.util.concurrent.TimeUnit;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class MenuEvents implements AutoCloseable {

    private final Services services;

    private volatile Menu menu;

    public MenuEvents(Services services) {
        this.services = requireNonNull(services);
    }

    public synchronized ShowMenuEvent onShowMenuEvent(ShowMenuEvent event) {
        if (menu == null) {
            menu = new Menu(services);
            services.resolve(Schedule.class)
                    .schedule(10, TimeUnit.SECONDS, this::recycle);
        }
        services.resolve(UiLayers.class)
                .push(menu);
        return null;
    }

    private synchronized boolean recycle() {
        final Menu m = menu;
        if (m != null && m.canBeRemoved()) {
            Closeables.close(m);
            menu = null;
            return false;
        }
        return true;
    }

    @Override
    public synchronized void close() {
        Closeables.close(menu);
        menu = null;
    }
}
