package com.github.ykiselev.services.events;

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
