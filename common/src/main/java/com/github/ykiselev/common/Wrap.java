package com.github.ykiselev.common;

/**
 * Abstract base of a class which knows what to do with it's value when it's not needed anymore.
 *
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public abstract class Wrap<T> implements AutoCloseable {

    private final T value;

    public Wrap(T value) {
        this.value = value;
    }

    public T value() {
        return value;
    }

    @Override
    public abstract void close();
}
