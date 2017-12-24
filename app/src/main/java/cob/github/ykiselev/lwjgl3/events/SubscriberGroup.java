package cob.github.ykiselev.lwjgl3.events;

import java.util.Map;
import java.util.function.Consumer;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class SubscriberGroup {

    private final Events events;

    private final Map.Entry<Class, Consumer>[] handlers;

    @SafeVarargs
    SubscriberGroup(Events events, Map.Entry<Class, Consumer>... handlers) {
        this.events = requireNonNull(events);
        this.handlers = handlers.clone();
    }

    @SuppressWarnings("unchecked")
    public void unsubscribe() {
        for (Map.Entry<Class, Consumer> pair : handlers) {
            events.unsubscribe(pair.getKey(), pair.getValue());
        }
    }
}
