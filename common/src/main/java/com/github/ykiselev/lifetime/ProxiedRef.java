package com.github.ykiselev.lifetime;

import com.github.ykiselev.proxy.AutoCloseableProxy;

import java.util.function.Consumer;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 * @deprecated not used
 */
@Deprecated
public final class ProxiedRef<T> implements Ref<T> {

    private final Class<T> clazz;

    private final Consumer<T> disposer;

    private volatile T reference;

    private volatile long counter = 0;

    public ProxiedRef(T reference, Class<T> clazz, Consumer<T> disposer) {
        this.reference = requireNonNull(reference);
        this.clazz = requireNonNull(clazz);
        this.disposer = requireNonNull(disposer);
    }

    @Override
    public synchronized T newRef() {
        final T ref = this.reference;
        if (ref == null) {
            return null;
        }
        ++counter;
        return AutoCloseableProxy.create(ref, clazz, v -> release());
    }

    @Override
    public synchronized long release() {
        final long value = --counter;
        if (value <= 0 && reference != null) {
            try {
                disposer.accept(reference);
            } finally {
                reference = null;
            }
        }
        return value;
    }

    @Override
    public void close() {
        release();
        if (reference != null) {
            throw new IllegalStateException("Resource leakage detected: Non-null reference after closing: " + reference);
        }
    }

    @Override
    public String toString() {
        return "ProxiedRef{" +
                "reference=" + reference +
                ", counter=" + counter +
                '}';
    }
}
