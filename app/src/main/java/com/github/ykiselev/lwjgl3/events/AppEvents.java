package com.github.ykiselev.lwjgl3.events;

import com.github.ykiselev.services.events.EventFilter;
import com.github.ykiselev.services.events.Events;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
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

    /**
     * Array of handlers. Expected to be small thus each time new handler is added or removed array is expanded/trimmed by one element.
     * This class is thread-safe and lock-free.
     *
     * @param <T> type parameter
     */
    private static final class Delegates<T> {

        private volatile T[] items;

        private static final VarHandle handle;

        static {
            try {
                handle = MethodHandles.lookup()
                        .findVarHandle(Delegates.class, "items", Object[].class);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new Error(e);
            }
        }

        Delegates(T[] items) {
            this.items = items.clone();
        }

        /**
         * Adds new element to array if there is no such element (elements are compared by reference).
         *
         * @param item the item to add to the end of array
         * @return the handle which may be used to remove item later
         */
        AutoCloseable add(T item) {
            for (; ; ) {
                final T[] prevArray = array();
                final int existing = indexOf(prevArray, item);
                if (existing >= 0) {
                    return handle(item);
                }
                final T[] newArray = Arrays.copyOf(prevArray, prevArray.length + 1);
                newArray[prevArray.length] = item;
                if (handle.compareAndSet(this, prevArray, newArray)) {
                    return handle(item);
                }
            }
        }

        private AutoCloseable handle(T handler) {
            return () -> remove(handler);
        }

        private void remove(T handler) {
            for (; ; ) {
                final T[] prevArray = array();
                final int idx = indexOf(prevArray, handler);
                if (idx < 0) {
                    break;
                }
                final T[] newArray = Arrays.copyOf(prevArray, prevArray.length - 1);
                if (idx < newArray.length) {
                    System.arraycopy(prevArray, idx + 1, newArray, idx, newArray.length - idx);
                }
                if (handle.compareAndSet(Delegates.this, prevArray, newArray)) {
                    break;
                }
            }
        }

        private static <T> int indexOf(T[] array, T value) {
            for (int i = 0; i < array.length; i++) {
                if (array[i] == value) {
                    return i;
                }
            }
            return -1;
        }

        T[] array() {
            return items;
        }
    }
}
