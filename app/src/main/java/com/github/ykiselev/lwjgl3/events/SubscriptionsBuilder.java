package com.github.ykiselev.lwjgl3.events;

import com.github.ykiselev.closeables.CompositeAutoCloseable;
import com.github.ykiselev.lwjgl3.events.layers.EventHandler;

import java.util.function.UnaryOperator;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class SubscriptionsBuilder {

    private final Events events;

    private final CompositeAutoCloseable ac;

    public SubscriptionsBuilder(Events events, CompositeAutoCloseable ac) {
        this.events = requireNonNull(events);
        this.ac = requireNonNull(ac);
    }

    public SubscriptionsBuilder(Events events) {
        this(events, new CompositeAutoCloseable());
    }

    public <T> SubscriptionsBuilder with(Class<T> eventType, EventHandler<T> handler) {
        return and(
                events.subscribe(eventType, handler)
        );
    }

    public SubscriptionsBuilder with(UnaryOperator<SubscriptionsBuilder> subscriber) {
        return subscriber.apply(this);
    }

    public SubscriptionsBuilder and(AutoCloseable value) {
        return new SubscriptionsBuilder(events, ac.and(value));
    }

    public CompositeAutoCloseable build() {
        return ac;
    }
}
