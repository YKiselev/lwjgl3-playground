package com.github.ykiselev.tree;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class TreeNode<K, V> implements Node<TreeNode<K, V>> {

    private static final TreeNode[] EMPTY = new TreeNode[0];

    private final K key;

    private final V value;

    private final TreeNode<K, V>[] children;

    public K key() {
        return key;
    }

    public V value() {
        return value;
    }

    @Override
    public int size() {
        return children.length;
    }

    @Override
    public TreeNode<K, V> childAt(int index) {
        return children[index];
    }

    @SafeVarargs
    public TreeNode(K key, V value, TreeNode<K, V>... children) {
        this.key = key;
        this.value = value;
        this.children = children.length > 0 ? children.clone() : EMPTY;
    }

    private boolean match(K key) {
        return Objects.equals(this.key, key);
    }

    public TreeNode<K, V> find(K key) {
        for (TreeNode<K, V> child : children) {
            if (child.match(key)) {
                return child;
            }
        }
        return null;
    }

    public MutableTreeNode<K, V> toMutable() {
        if (children != null && children.length > 0) {
            return new MutableTreeNode<>(
                    key,
                    value,
                    Arrays.stream(children)
                            .map(TreeNode::toMutable)
                            .collect(Collectors.toList())
            );
        }
        return new MutableTreeNode<>(key, value);
    }
}
