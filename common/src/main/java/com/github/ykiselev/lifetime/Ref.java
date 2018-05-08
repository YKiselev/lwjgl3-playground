package com.github.ykiselev.lifetime;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface Ref<T> extends AutoCloseable {

    T newRef();

    long release();

    @Override
    void close() throws IllegalStateException;
}
