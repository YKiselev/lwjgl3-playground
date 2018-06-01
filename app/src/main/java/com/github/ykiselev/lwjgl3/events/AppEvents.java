package com.github.ykiselev.lwjgl3.events;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import java.util.function.Consumer;

/**
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
        final Subscribers s = this.subscribers.get(event.getClass());
        if (s == null) {
            throw new IllegalStateException("No subscribers for " + event);
        }
        s.fire(event);
    }

    /**
     *
     */
    private static final class Subscribers {

        private static final AtomicReferenceFieldUpdater<Subscribers, Consumer[]> UPDATER = AtomicReferenceFieldUpdater.newUpdater(
                Subscribers.class,
                Consumer[].class,
                "handlers"
        );

        private volatile Consumer[] handlers = new Consumer[0];

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
                if (UPDATER.compareAndSet(this, prevArray, newArray)) {
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
                    if (UPDATER.compareAndSet(Subscribers.this, prevArray, newArray)) {
                        break;
                    }
                }
            };
        }

        @SuppressWarnings("unchecked")
        void fire(Object event) {
            if (handlers.length == 0) {
                throw new IllegalStateException("No subscribers for event " + event);
            }
            for (Consumer handler : handlers) {
                handler.accept(event);
            }
        }
    }
}
