package com.github.ykiselev.tree;

import java.util.Objects;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
final class TreeNode<K, V> {

    private final K key;

    private final V value;

    private final TreeNode<K, V>[] children;

    public V value() {
        return value;
    }

    @SafeVarargs
    TreeNode(K key, V value, TreeNode<K, V>... children) {
        this.key = key;
        this.value = value;
        this.children = children.clone();
    }

    private boolean match(K key) {
        return Objects.equals(this.key, key);
    }

    TreeNode<K, V> find(K key) {
        for (TreeNode<K, V> child : children) {
            if (child.match(key)) {
                return child;
            }
        }
        return null;
    }
}
