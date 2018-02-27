package com.github.ykiselev.common;

/**
 * Interface to be implemented by class which knows what to do with it's value when it's not needed anymore.
 *
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface Wrap<T> extends AutoCloseable {

    T value();

    @Override
    void close();
}
