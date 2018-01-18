package com.github.ykiselev.caching;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface CachedReferences<V> {

    Cached<V> put(V value);

}
