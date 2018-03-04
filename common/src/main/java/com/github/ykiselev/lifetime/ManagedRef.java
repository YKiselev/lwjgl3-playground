package com.github.ykiselev.lifetime;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class ManagedRef<T extends Manageable<T> & AutoCloseable> implements AutoCloseable {

    private volatile T reference;

    private volatile long counter = 1;

    public ManagedRef(T reference) {
        this.reference = requireNonNull(reference);
    }

    public synchronized T newRef() {
        if (reference == null) {
            throw new IllegalStateException("Null reference!");
        }
        ++counter;
        return reference.manage(v -> this.release());
    }

    private synchronized long release() {
        final long value = --counter;
        if (value == 0) {
            try {
                reference.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                reference = null;
            }
        }
        return value;
    }

    @Override
    public void close() {
        release();
    }
}
