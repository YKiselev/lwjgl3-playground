package com.github.ykiselev.tree;

import java.util.Objects;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
class TreeLeaf<K, V> {

    private final K key;

    private final V value;

    public V value() {
        return value;
    }

    TreeLeaf(K key, V value) {
        this.key = key;
        this.value = value;
    }

    final boolean match(K key) {
        return Objects.equals(this.key, key);
    }

    TreeLeaf<K, V> find(K key) {
        return null;
    }
}
