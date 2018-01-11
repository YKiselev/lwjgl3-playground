package com.github.ykiselev.tree;

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

    PathTreeNode<V> find(String path) {
        for (PathTreeNode<V> child : children) {
            if (child.match(path)) {
                return child;
            }
        }
        return null;
    }
}
