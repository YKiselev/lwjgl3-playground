package com.github.ykiselev.caching;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface Cache<K,V> {

    V get(K key);
}
