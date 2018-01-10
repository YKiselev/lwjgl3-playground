package com.github.ykiselev.tree;

import java.util.Iterator;
import java.util.Objects;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class PathTreeNode<V> {

    private final String part;

    private final V value;

    private final PathTreeNode<V>[] children;

    public V value() {
        return value;
    }

    @SafeVarargs
    public PathTreeNode(String part, V value, PathTreeNode<V>... children) {
        this.part = part;
        this.value = value;
        this.children = children.clone();
    }

    public PathTreeNode<V> find(Iterator<String> path) {
        if (path == null || !path.hasNext()) {
            return null;
        }
        final String first = path.next();
        for (PathTreeNode<V> child : children) {
            if (Objects.equals(child.part, first)) {
                if (path.hasNext()) {
                    return child.find(path);
                } else {
                    return child;
                }
            }
        }
        return null;
    }
}
