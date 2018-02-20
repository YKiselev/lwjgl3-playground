package com.github.ykiselev.caching;

/**
 * Interface to be implemented by classes which weight can be represented in bytes.
 *
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface LongWeighted {

    /**
     * @return instance weight in bytes
     */
    long weight();
}
