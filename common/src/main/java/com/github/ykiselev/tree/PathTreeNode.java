package com.github.ykiselev.tree;

import java.util.Iterator;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
final class PathTreeNode<V> extends PathTreeLeaf<V> {

    private final PathTreeNode<V>[] children;

    @SafeVarargs
    PathTreeNode(String part, V value, PathTreeNode<V>... children) {
        super(part, value);
        this.children = children.clone();
    }

    PathTreeNode<V> find(Iterator<String> path) {
        if (path == null || !path.hasNext()) {
            return null;
        }
        final String first = path.next();
        for (PathTreeNode<V> child : children) {
            if (child.match(first)) {
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
