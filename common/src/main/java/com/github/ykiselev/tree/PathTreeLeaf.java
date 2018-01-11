package com.github.ykiselev.tree;

import java.util.Objects;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
class PathTreeLeaf<V> {

    private final String part;

    private final V value;

    public V value() {
        return value;
    }

    PathTreeLeaf(String part, V value) {
        this.part = part;
        this.value = value;
    }

    final boolean match(String part) {
        return Objects.equals(this.part, part);
    }

    PathTreeLeaf<V> find(String path) {
        return null;
    }
}
