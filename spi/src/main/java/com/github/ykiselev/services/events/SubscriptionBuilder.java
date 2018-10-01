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

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class SubscriptionBuilder {

    private final Events events;

    private final CompositeAutoCloseable ac;

    public SubscriptionBuilder(Events events, CompositeAutoCloseable ac) {
        this.events = requireNonNull(events);
        this.ac = requireNonNull(ac);
    }

    public SubscriptionBuilder(Events events) {
        this(events, new CompositeAutoCloseable());
    }

    public <T> SubscriptionBuilder with(Class<T> eventClass, Runnable handler) {
        return new SubscriptionBuilder(
                events,
                ac.and(events.subscribe(eventClass, handler))
        );
    }

    public <T> SubscriptionBuilder with(Class<T> eventClass, Consumer<T> handler) {
        return new SubscriptionBuilder(
                events,
                ac.and(events.subscribe(eventClass, handler))
        );
    }

    public CompositeAutoCloseable build() {
        return ac;
    }
}
