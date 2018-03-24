package cob.github.ykiselev.lwjgl3.events;

import com.github.ykiselev.closeables.CompositeAutoCloseable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class SubscriptionsBuilder {

    private final Map<Class, Consumer> handlers = new HashMap<>();

    public <T> SubscriptionsBuilder with(Class<T> eventType, Consumer<T> handler) {
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
