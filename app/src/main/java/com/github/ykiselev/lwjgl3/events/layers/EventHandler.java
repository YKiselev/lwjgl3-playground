package com.github.ykiselev.lwjgl3.events.layers;

/**
 * Instances of this interface should be passed to {@link com.github.ykiselev.lwjgl3.events.Events#subscribe(Class, EventHandler)} method.
 * Each event type may have many subscribers. When event is fired by a call to {@link com.github.ykiselev.lwjgl3.events.Events#fire(Object)}
 * it is passed to each handler subscribed to that event type, serially.  Handler may:
 * <ul>
 * <li>Return passed event instance as a result from {@link EventHandler#handle(T)} method in which case returned value will be passed to next handler in chain.</li>
 * <li>Return new event instance as a result from {@link EventHandler#handle(T)} method in which case returned value will be passed to next handler in chain.</li>
 * <li>Return {@code null} to abort processing of event. In that case any handlers left in chain will not be called.</li>
 * </ul>
 *
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
@FunctionalInterface
public interface EventHandler<T> {

    /**
     * This method is called by {@link com.github.ykiselev.lwjgl3.events.Events#fire(Object)} method implementation.
     *
     * @param event the event
     * @return the original (or modified) event if processing should continue or {@code null} if processing should be aborted.
     */
    T handle(T event);

}
