package com.github.ykiselev.lwjgl3.events;

import com.github.ykiselev.closeables.CompositeAutoCloseable;
import com.github.ykiselev.lwjgl3.events.layers.EventHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class SubscriptionsBuilder {

    private final Map<Class, EventHandler> handlers = new HashMap<>();

    public <T> SubscriptionsBuilder with(Class<T> eventType, EventHandler<T> handler) {
        handlers.putIfAbsent(eventType, handler);
        return this;
    }

    @SuppressWarnings("unchecked")
    public CompositeAutoCloseable build(Events events) {
        return new CompositeAutoCloseable(
                handlers.entrySet()
                        .stream()
                        .map(e -> events.subscribe(e.getKey(), e.getValue()))
                        .toArray(AutoCloseable[]::new)
        );
    }
}
