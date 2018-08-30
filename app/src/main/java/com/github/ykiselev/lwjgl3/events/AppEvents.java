package com.github.ykiselev.lwjgl3.events;

import com.github.ykiselev.services.events.EventFilter;
import com.github.ykiselev.services.events.Events;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * Note to subscribers: Only exact event type match is supported.
 *
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class AppEvents implements Events {

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
            throw new IllegalStateException("No subscribers for " + event);
        }
        return s.fire(event);
    }

    private static <T> int indexOf(T[] array, T value) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == value) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Collection of subscribers.
     */
    private static final class Subscribers {

        private final Filters filters = new Filters();

        private final Consumers consumers = new Consumers();

        AutoCloseable subscribe(Consumer<?> handler) {
            return consumers.subscribe(handler);
        }

        AutoCloseable add(EventFilter<?> filter) {
            return filters.subscribe(filter);
        }

        <T> T fire(T event) {
            final T filtered = filters.fire(event);
            if (filtered != null) {
                consumers.fire(event);
            }
            return filtered;
        }
    }

    private static abstract class Handlers<T> {

        private final Class<T> clazz;

        private volatile T[] handlers;

        private static final VarHandle handle;

        static {
            try {
                handle = MethodHandles.lookup()
                        .findVarHandle(Handlers.class, "handlers", Object[].class);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new Error(e);
            }
        }

        Handlers(Class<T> clazz) {
            @SuppressWarnings("unchecked") final T[] array = (T[]) Array.newInstance(clazz, 0);
            this.clazz = clazz;
            this.handlers = array;
        }

        AutoCloseable subscribe(T handler) {
            for (; ; ) {
                final T[] prevArray = array();
                final int existing = indexOf(prevArray, handler);
                if (existing >= 0) {
                    return newSubscription(handler);
                }
                final T[] newArray = Arrays.copyOf(prevArray, prevArray.length + 1);
                newArray[prevArray.length] = handler;
                if (handle.compareAndSet(this, prevArray, newArray)) {
                    return newSubscription(handler);
                }
            }
        }

        private AutoCloseable newSubscription(T handler) {
            return () -> {
                for (; ; ) {
                    final T[] prevArray = array();
                    final int idx = indexOf(prevArray, handler);
                    if (idx < 0) {
                        break;
                    }
                    @SuppressWarnings("unchecked") final T[] newArray = (T[]) Array.newInstance(clazz, prevArray.length - 1);
                    if (idx > 0) {
                        System.arraycopy(prevArray, 0, newArray, 0, idx);
                    }
                    if (idx < newArray.length) {
                        System.arraycopy(prevArray, idx + 1, newArray, idx, newArray.length - idx);
                    }
                    if (handle.compareAndSet(Handlers.this, prevArray, newArray)) {
                        break;
                    }
                }
            };
        }

        @SuppressWarnings("unchecked")
        T[] array() {
            return (T[]) handle.getVolatile(this);
        }
    }

    private static final class Consumers extends Handlers<Consumer> {

        Consumers() {
            super(Consumer.class);
        }

        void fire(Object event) {
            @SuppressWarnings("unchecked") final Consumer<Object>[] array = array();
            if (array.length == 0) {
                throw new IllegalStateException("No subscribers for event " + event);
            }
            for (Consumer<Object> consumer : array) {
                consumer.accept(event);
            }
        }
    }

    private static final class Filters extends Handlers<EventFilter> {

        Filters() {
            super(EventFilter.class);
        }

        <T> T fire(T event) {
            @SuppressWarnings("unchecked") final EventFilter<T>[] array = array();
            T result = event;
            for (EventFilter<T> filter : array) {
                result = filter.handle(result);
                if (result == null) {
                    return null;
                }
            }
            return result;
        }
    }
}
