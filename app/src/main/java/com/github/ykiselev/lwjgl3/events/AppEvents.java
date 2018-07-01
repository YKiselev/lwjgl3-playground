package com.github.ykiselev.lwjgl3.events;

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

    private final Map<Class, Subscribers> subscribers = new ConcurrentHashMap<>();

    @Override
    public <T> AutoCloseable subscribe(Class<T> eventClass, Consumer<T> handler) {
        return subscribers.computeIfAbsent(
                eventClass,
                k -> new Subscribers()
        ).subscribe(handler);
    }

    @Override
    public void fire(Object event) {
        final Subscribers s = subscribers.get(event.getClass());
        if (s == null) {
            throw new IllegalStateException("No subscribers for " + event);
        }
        s.fire(event);
    }

    /**
     *
     */
    private static final class Subscribers {

        private static final VarHandle VH;

        private volatile Consumer[] handlers = new Consumer[0];

        static {
            try {
                VH = MethodHandles.lookup()
                        .findVarHandle(Subscribers.class, "handlers", Consumer[].class);
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

        AutoCloseable subscribe(Consumer<?> handler) {
            for (; ; ) {
                final Consumer[] prevArray = this.handlers;
                final int existing = indexOf(prevArray, handler);
                if (existing >= 0) {
                    return newSubscription(handler);
                }
                final Consumer[] newArray = Arrays.copyOf(prevArray, prevArray.length + 1);
                newArray[prevArray.length] = handler;
                if (VH.compareAndSet(this, prevArray, newArray)) {
                    return newSubscription(handler);
                }
            }
        }

        private AutoCloseable newSubscription(Consumer<?> handler) {
            return () -> {
                for (; ; ) {
                    final Consumer[] prevArray = Subscribers.this.handlers;
                    final int idx = indexOf(prevArray, handler);
                    if (idx < 0) {
                        break;
                    }
                    final Consumer[] newArray = new Consumer[prevArray.length - 1];
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
        void fire(Object event) {
            final Consumer[] h = handlers;
            if (h.length == 0) {
                throw new IllegalStateException("No subscribers for event " + event);
            }
            for (Consumer handler : h) {
                handler.accept(event);
            }
        }
    }
}
