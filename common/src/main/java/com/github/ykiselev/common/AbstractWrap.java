package com.github.ykiselev.common;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public abstract class AbstractWrap<T> implements Wrap<T> {

    private final T value;

    public AbstractWrap(T value) {
        this.value = requireNonNull(value);
    }

    @Override
    public final T value() {
        return value;
    }
}
