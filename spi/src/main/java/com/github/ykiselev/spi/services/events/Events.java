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

package com.github.ykiselev.spi.services.events;

import java.util.function.Consumer;

/**
 * Event bus with exact event class matching. Each event type may have two kinds of subscribers - filters and handlers.
 * Any registered filter may mutate fired event (or event return {@code null} effectively aborting processing of event).
 * If after all filters there are non-null event it is passed to subscribed handlers.
 *
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface Events {

    /**
     * Subscribes to specified event class
     *
     * @param <T>        the type parameter
     * @param eventClass the event class to subscribe to
     * @param handler    the consumer that will be called on event of specified class
     * @return the subscription object. To unsubscribe call it's {@link AutoCloseable#close()} method.
     */
    <T> AutoCloseable subscribe(Class<T> eventClass, Consumer<T> handler);

    /**
     * Subscribes to specified event class. Convenient method for cases when event object isn't needed.
     *
     * @param <T>        the type parameter
     * @param eventClass the event class to subscribe to
     * @param handler    the event handler that will be called on event of specified class
     * @return the subscription object. To unsubscribe call it's {@link AutoCloseable#close()} method.
     */
    default <T> AutoCloseable subscribe(Class<T> eventClass, Runnable handler) {
        return subscribe(eventClass, evt -> handler.run());
    }

    /**
     * Registers filter for specified event class
     *
     * @param <T>        the type parameter
     * @param eventClass the event class to subscribe to
     * @param filter     the filter that will be called on event of specified class
     * @return the subscription object. To unsubscribe call it's {@link AutoCloseable#close()} method.
     */
    <T> AutoCloseable add(Class<T> eventClass, EventFilter<T> filter);

    /**
     * Passes supplied event to all subscribers synchronously (this method will return control to calling code only after
     * all filters and handlers return control).
     *
     * @param event the event to raise
     */
    <T> T fire(T event);
}
