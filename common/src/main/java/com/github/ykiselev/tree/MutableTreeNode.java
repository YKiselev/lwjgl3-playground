package com.github.ykiselev.tree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class MutableTreeNode<K, V> implements Node<MutableTreeNode<K, V>> {

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

    @Override
    public int size() {
        return children.size();
    }

    @Override
    public MutableTreeNode<K, V> childAt(int index) {
        return children.get(index);
    }

    public MutableTreeNode(K key, V value, Collection<MutableTreeNode<K, V>> children) {
        this.key = key;
        this.value = value;
        if (children != null && !children.isEmpty()) {
            this.children = new ArrayList<>(children);
        }
    }

    public MutableTreeNode(K key, V value, Stream<MutableTreeNode<K, V>> children) {
        this.key = key;
        this.value = value;
        this.children = children.collect(Collectors.toList());
    }

    public MutableTreeNode(K key, V value) {
        this(key, value, Collections.emptyList());
    }

    public MutableTreeNode() {
        this(null, null);
    }

    public Stream<MutableTreeNode<K, V>> stream() {
        return children != null ? children.stream() : Stream.empty();
    }

    private boolean match(K part) {
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
        return addChild(new MutableTreeNode<>(key, value));
    }

    /**
     * Adds new node as immediate child of this node. If node with such key already exists it will be replaced by passed node.
     *
     * @param child the child node to add
     * @return the new node.
     */
    MutableTreeNode<K, V> add(MutableTreeNode<K, V> child) {
        remove(child.key());
        return addChild(child);
    }

    private MutableTreeNode<K, V> addChild(MutableTreeNode<K, V> child) {
        if (children == null) {
            children = new ArrayList<>();
        }
        children.add(child);
        return child;
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

    MutableTreeNode<K, V> merge(MutableTreeNode<K, V> other) {
        if (other == null) {
            return this;
        }
        if (!Objects.equals(key, other.key)) {
            throw new IllegalStateException("Can not merge nodes with different keys: " + key + " != " + other.key);
        }
        // todo
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
}
