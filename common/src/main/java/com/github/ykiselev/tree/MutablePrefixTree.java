package com.github.ykiselev.tree;

import java.util.regex.Pattern;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class MutablePrefixTree<V> {

    private final Pattern pattern;

    private final MutableTreeNode<String, V> root = new MutableTreeNode<>();

    public MutableTreeNode<String, V> root() {
        return root;
    }

    public MutablePrefixTree(Pattern pattern) {
        this.pattern = requireNonNull(pattern);
    }

    public MutablePrefixTree(String separator) {
        this(Pattern.compile(separator));
    }

    public MutablePrefixTree<V> add(String path, V value) {
        MutableTreeNode<String, V> node = root;
        for (String part : pattern.split(path)) {
            node = node.add(part, value);
        }
        return this;
    }

    public MutableTreeNode<String, V> get(String path) {
        return find(pattern.split(path));
    }

    private MutableTreeNode<String, V> find(String[] parts) {
        return find(parts, parts.length);
    }

    private MutableTreeNode<String, V> find(String[] parts, int count) {
        MutableTreeNode<String, V> node = root;
        for (int i = 0; i < count; i++) {
            node = node.find(parts[i]);
            if (node == null) {
                return null;
            }
        }
        return node;
    }

    public MutableTreeNode<String, V> remove(String path) {
        final String[] parts = pattern.split(path);
        final MutableTreeNode<String, V> parent = find(parts, parts.length - 1);
        if (parent != null) {
            return parent.remove(parts[parts.length - 1]);
        }
        return null;
    }

    public PrefixTree<V> toPrefixTree() {
        return new PrefixTree<>(pattern, root.emit());
    }
}