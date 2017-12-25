package cob.github.ykiselev.lwjgl3.host;

import cob.github.ykiselev.lwjgl3.events.AppEvents;
import cob.github.ykiselev.lwjgl3.events.Events;
import cob.github.ykiselev.lwjgl3.services.MapBasedServices;
import cob.github.ykiselev.lwjgl3.services.Services;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class AppHost implements Host, AutoCloseable {

    private final Events events = new AppEvents();

    private final Services services = new MapBasedServices();

    @Override
    public Events events() {
        return events;
    }

    @Override
    public Services services() {
        return services;
    }

    @Override
    public void close() throws Exception {
        services.close();
    }
}
