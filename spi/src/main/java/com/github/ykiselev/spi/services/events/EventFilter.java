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

/**
 * Instances of this interface should be passed to {@link Events#add(Class, EventFilter)} method.
 * Each event type may have many filters. When event is fired by a call to {@link Events#fire(Object)}
 * it is first passed to each registerd filter (serially). Filter may:
 * <ul>
 * <li>Return passed event instance as a result from {@link EventFilter#handle(T)} method in which case returned value will be passed to next filter (or handler) in chain.</li>
 * <li>Return new event instance as a result from {@link EventFilter#handle(T)} method in which case returned value will be passed to next filter (or handler) in chain.</li>
 * <li>Return {@code null} to abort processing of event. In that case any filters (or handlers) left in chain will not be called.</li>
 * </ul>
 * If after filtering there are {@code non-null} instance of event it is passed to subscribed handlers.
 *
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
@FunctionalInterface
public interface EventFilter<T> {

    /**
     * This method is called by {@link Events#fire(Object)} method implementation before any subscribed handlers.
     *
     * @param event the event
     * @return the original (or modified) event if processing should continue or {@code null} if processing should be aborted.
     */
    T handle(T event);

}
