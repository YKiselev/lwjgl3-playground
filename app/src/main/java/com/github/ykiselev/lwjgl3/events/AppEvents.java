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

package com.github.ykiselev.lwjgl3.events;

import com.github.ykiselev.services.events.EventFilter;
import com.github.ykiselev.services.events.Events;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * Note to subscribers: Only exact event type match is supported.
 *
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class AppEvents implements Events {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Map<Class, Subscribers> subscribers = new ConcurrentHashMap<>();

    @Override
    public <T> AutoCloseable subscribe(Class<T> eventClass, Consumer<T> handler) {
        return subscribers.computeIfAbsent(
                eventClass,
                k -> new Subscribers()
        ).subscribe(handler);
    }

    @Override
    public <T> AutoCloseable add(Class<T> eventClass, EventFilter<T> filter) {
        return subscribers.computeIfAbsent(
                eventClass,
                k -> new Subscribers()
        ).add(filter);
    }

    @Override
    public <T> T fire(T event) {
        final Subscribers s = subscribers.get(event.getClass());
        if (s == null) {
            logger.warn("No subscribers for {}", event);
            return event;
        }
        return s.fire(event);
    }

    /**
     * Collection of subscribers.
     */
    private final class Subscribers {

        private final Delegates<EventFilter> filters = new Delegates<>(new EventFilter[0]);

        private final Delegates<Consumer> handlers = new Delegates<>(new Consumer[0]);

        AutoCloseable subscribe(Consumer<?> handler) {
            return handlers.add(handler);
        }

        AutoCloseable add(EventFilter<?> filter) {
            return filters.add(filter);
        }

        <T> T fire(T event) {
            final T filtered = filter(event);
            if (filtered != null) {
                handle(event);
            }
            return filtered;
        }

        private <T> T filter(T event) {
            T result = event;
            for (@SuppressWarnings("unchecked") EventFilter<T> filter : filters.array()) {
                result = filter.handle(result);
            }
            return result;
        }

        @SuppressWarnings("unchecked")
        private void handle(Object event) {
            final Consumer[] array = handlers.array();
            if (array.length == 0) {
                logger.warn("No handlers for {}", event);
            }
            for (Consumer consumer : array) {
                consumer.accept(event);
            }
        }
    }
}
