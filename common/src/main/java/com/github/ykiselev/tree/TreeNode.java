package com.github.ykiselev.tree;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
final class TreeNode<K, V> extends TreeLeaf<K, V> {

    private final TreeLeaf<K, V>[] children;

    @SafeVarargs
    TreeNode(K key, V value, TreeLeaf<K, V>... children) {
        super(key, value);
        this.children = children.clone();
    }

    TreeLeaf<K, V> find(K key) {
        for (TreeLeaf<K, V> child : children) {
            if (child.match(key)) {
                return child;
            }
        }
        return null;
    }
}
