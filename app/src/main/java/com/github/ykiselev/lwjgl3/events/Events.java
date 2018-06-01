package com.github.ykiselev.lwjgl3.events;

import java.util.function.Consumer;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface Events {

    /**
     * Subscribes to specified event class
     *
     * @param eventClass the event class to subscribe to
     * @param handler    the handler that will be called on event of specified class
     * @param <T>        the type parameter
     * @return the subscription object. To unsubscribe call it's {@link AutoCloseable#close()} method.
     */
    <T> AutoCloseable subscribe(Class<T> eventClass, Consumer<T> handler);

    /**
     * Passes supplied event to all subscribers synchronously (this method will return control to calling code only after
     * all subscribers handlers return control).
     *
     * @param event the event to raise
     */
    void fire(Object event);
}
