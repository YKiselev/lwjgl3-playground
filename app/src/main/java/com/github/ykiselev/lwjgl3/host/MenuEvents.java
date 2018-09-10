package com.github.ykiselev.lwjgl3.host;

import com.github.ykiselev.closeables.Closeables;
import com.github.ykiselev.services.events.SubscriptionsBuilder;
import com.github.ykiselev.lwjgl3.layers.menu.Menu;
import com.github.ykiselev.services.Services;
import com.github.ykiselev.services.events.menu.ShowMenuEvent;
import com.github.ykiselev.services.layers.UiLayers;
import com.github.ykiselev.services.schedule.Schedule;

import java.util.concurrent.TimeUnit;
import java.util.function.UnaryOperator;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class MenuEvents implements AutoCloseable, UnaryOperator<SubscriptionsBuilder> {

    private final Services services;

    private volatile Menu menu;

    private final Object lock = new Object();

    public MenuEvents(Services services) {
        this.services = requireNonNull(services);
    }

    private ShowMenuEvent onShowMenuEvent(ShowMenuEvent event) {
        synchronized (lock) {
            if (menu == null) {
                menu = new Menu(services);
                services.resolve(Schedule.class)
                        .schedule(10, TimeUnit.SECONDS, this::recycle);
            }
            services.resolve(UiLayers.class)
                    .push(menu);
        }
        return null;
    }

    private boolean recycle() {
        synchronized (lock) {
            final Menu m = menu;
            if (m != null && m.canBeRemoved()) {
                Closeables.close(m);
                menu = null;
                return false;
            }
        }
        return true;
    }

    @Override
    public void close() {
        synchronized (lock) {
            Closeables.close(menu);
            menu = null;
        }
    }

    @Override
    public SubscriptionsBuilder apply(SubscriptionsBuilder builder) {
        return builder.with(ShowMenuEvent.class, this::onShowMenuEvent)
                .and(this);
    }
}
