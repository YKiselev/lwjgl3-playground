package com.github.ykiselev.wrap;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public abstract class Wrap<T> implements AutoCloseable {

    private final T value;

    public Wrap(T value) {
        this.value = requireNonNull(value);
    }

    public final T value() {
        return value;
    }

    @Override
    public abstract void close();
}
