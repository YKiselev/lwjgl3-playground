package com.github.ykiselev.tree;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.IntFunction;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class MutableTreeNode<K, V> implements Iterable<MutableTreeNode<K, V>> {

    private final K key;

    private V value;

    private List<MutableTreeNode<K, V>> children;

    public K key() {
        return key;
    }

    public V value() {
        return value;
    }

    public void value(V value) {
        this.value = value;
    }

    public MutableTreeNode(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public MutableTreeNode() {
        this(null, null);
    }

    boolean match(K part) {
        return Objects.equals(this.key, part);
    }

    /**
     * Adds new node with specified key and value as immediate child of this node. If node with such key already exists
     * sets its value.
     *
     * @param key   the key.
     * @param value the value.
     * @return the new or existing node for specified key.
     */
    MutableTreeNode<K, V> add(K key, V value) {
        final MutableTreeNode<K, V> existing = find(key);
        if (existing != null) {
            existing.value(value);
            return existing;
        }
        final MutableTreeNode<K, V> result = new MutableTreeNode<>(key, value);
        if (children == null) {
            children = new ArrayList<>();
        }
        children.add(result);
        return result;
    }

    /**
     * Searches for immediate children of this node.
     *
     * @param key the path part to search node for.
     * @return the node or {@code null} if not found.
     */
    MutableTreeNode<K, V> find(K key) {
        if (children != null) {
            for (MutableTreeNode<K, V> child : children) {
                if (child.match(key)) {
                    return child;
                }
            }
        }
        return null;
    }

    /**
     * Removes for immediate children of this node.
     *
     * @param key the path part to search node for.
     * @return the removed child node or {@code null} if no such node exists.
     */
    MutableTreeNode<K, V> remove(K key) {
        if (children != null) {
            final Iterator<MutableTreeNode<K, V>> it = children.iterator();
            while (it.hasNext()) {
                final MutableTreeNode<K, V> child = it.next();
                if (child.match(key)) {
                    it.remove();
                    return child;
                }
            }
        }
        return null;
    }

    TreeNode<K, V> emit() {
        if (children != null && !children.isEmpty()) {
            final Function<MutableTreeNode<K, V>, TreeNode<K, V>> transformation = MutableTreeNode::emit;
            final IntFunction<TreeNode<K, V>[]> generator = TreeNode[]::new;
            return new TreeNode<>(
                    key,
                    value,
                    children.stream()
                            .map(transformation)
                            .toArray(generator)
            );
        }
        return new TreeNode<>(key, value);
    }

    @Override
    public Iterator<MutableTreeNode<K, V>> iterator() {
        return new Iterator<>() {

            private final Iterator<MutableTreeNode<K, V>> it = children.iterator();

            @Override
            public boolean hasNext() {
                return it.hasNext();
            }

            @Override
            public MutableTreeNode<K, V> next() {
                return it.next();
            }
        };
    }
}
