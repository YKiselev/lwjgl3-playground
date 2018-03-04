package com.github.ykiselev.lifetime;

import java.util.function.Consumer;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class CountedRef<T> {

    private volatile T reference;

    private volatile long counter = 1;

    private final Consumer<T> disposer;

    public T reference() {
        return reference;
    }

    public CountedRef(T reference, Consumer<T> disposer) {
        this.reference = requireNonNull(reference);
        this.disposer = requireNonNull(disposer);
    }

    public synchronized T addRef() {
        if (reference == null) {
            throw new IllegalStateException("Null reference!");
        }
        ++counter;
        return reference;
    }

    public synchronized long release() {
        final long value = --counter;
        if (value == 0) {
            try {
                disposer.accept(reference);
            } finally {
                reference = null;
            }
        }
        return value;
    }
}
