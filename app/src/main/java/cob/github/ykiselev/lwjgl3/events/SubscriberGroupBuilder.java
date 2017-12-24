package cob.github.ykiselev.lwjgl3.events;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class SubscriberGroupBuilder {

    @SuppressWarnings("unchecked")
    private final Map.Entry<Class, Consumer>[] EMPTY = new Map.Entry[0];

    private final Map<Class, Consumer> handlers = new HashMap<>();

    public <T> SubscriberGroupBuilder add(Class<T> eventType, Consumer<T> handler) {
        handlers.put(eventType, handler);
        return this;
    }

    public SubscriberGroup build(Events events) {
        return new SubscriberGroup(
                events,
                handlers.entrySet().toArray(EMPTY)
        );
    }
}
