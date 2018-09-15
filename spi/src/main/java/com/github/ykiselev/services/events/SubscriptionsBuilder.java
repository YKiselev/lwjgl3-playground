/*
 * Copyright 2017 Yuriy Kiselev (uze@yandex.ru)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.ykiselev.services.events;

import com.github.ykiselev.closeables.CompositeAutoCloseable;

import java.util.function.Consumer;
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

    public <T> SubscriptionsBuilder with(Class<T> eventType, Consumer<T> handler) {
        return and(events.subscribe(eventType, handler));
    }

    public <T> SubscriptionsBuilder with(Class<T> eventType, EventFilter<T> handler) {
        return and(events.add(eventType, handler));
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
