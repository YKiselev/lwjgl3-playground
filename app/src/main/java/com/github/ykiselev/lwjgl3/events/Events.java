package com.github.ykiselev.lwjgl3.events;

import com.github.ykiselev.lwjgl3.events.layers.EventHandler;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface Events {

    /**
     * Subscribes to specified event class
     *
     * @param <T>        the type parameter
     * @param eventClass the event class to subscribe to
     * @param handler    the handler that will be called on event of specified class
     * @return the subscription object. To unsubscribe call it's {@link AutoCloseable#close()} method.
     */
    <T> AutoCloseable subscribe(Class<T> eventClass, EventHandler<T> handler);

    /**
     * Passes supplied event to all subscribers synchronously (this method will return control to calling code only after
     * all subscribed handlers return control).
     *
     * @param event the event to raise
     */
    <T> T fire(T event);
}
