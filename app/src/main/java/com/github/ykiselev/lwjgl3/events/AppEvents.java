package com.github.ykiselev.lwjgl3.events;

import com.github.ykiselev.services.events.EventHandler;
import com.github.ykiselev.services.events.Events;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Note to subscribers: Only exact event type match is supported.
 *
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class AppEvents implements Events {

    private final Map<Class, Subscribers> subscribers = new ConcurrentHashMap<>();

    @Override
    public <T> AutoCloseable subscribe(Class<T> eventClass, EventHandler<T> handler) {
        return subscribers.computeIfAbsent(
                eventClass,
                k -> new Subscribers()
        ).subscribe(handler);
    }

    @Override
    public <T> T fire(T event) {
        final Subscribers s = subscribers.get(event.getClass());
        if (s == null) {
            throw new IllegalStateException("No subscribers for " + event);
        }
        return s.fire(event);
    }

    /**
     * Collection of subscribers.
     */
    private static final class Subscribers {

        private static final VarHandle VH;

        private volatile EventHandler[] handlers = new EventHandler[0];

        static {
            try {
                VH = MethodHandles.lookup()
                        .findVarHandle(Subscribers.class, "handlers", EventHandler[].class);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new Error(e);
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

        AutoCloseable subscribe(EventHandler<?> handler) {
            for (; ; ) {
                final EventHandler[] prevArray = this.handlers;
                final int existing = indexOf(prevArray, handler);
                if (existing >= 0) {
                    return newSubscription(handler);
                }
                final EventHandler[] newArray = Arrays.copyOf(prevArray, prevArray.length + 1);
                newArray[prevArray.length] = handler;
                if (VH.compareAndSet(this, prevArray, newArray)) {
                    return newSubscription(handler);
                }
            }
        }

        private AutoCloseable newSubscription(EventHandler<?> handler) {
            return () -> {
                for (; ; ) {
                    final EventHandler[] prevArray = Subscribers.this.handlers;
                    final int idx = indexOf(prevArray, handler);
                    if (idx < 0) {
                        break;
                    }
                    final EventHandler[] newArray = new EventHandler[prevArray.length - 1];
                    if (idx > 0) {
                        System.arraycopy(prevArray, 0, newArray, 0, idx);
                    }
                    if (idx < newArray.length) {
                        System.arraycopy(prevArray, idx + 1, newArray, idx, newArray.length - idx);
                    }
                    if (VH.compareAndSet(Subscribers.this, prevArray, newArray)) {
                        break;
                    }
                }
            };
        }

        @SuppressWarnings("unchecked")
        <T> T fire(T event) {
            final EventHandler<T>[] h = handlers;
            if (h.length == 0) {
                throw new IllegalStateException("No subscribers for event " + event);
            }
            for (EventHandler<T> handler : h) {
                if (event == null) {
                    break;
                }
                event = handler.handle(event);
            }
            return event;
        }
    }
}
