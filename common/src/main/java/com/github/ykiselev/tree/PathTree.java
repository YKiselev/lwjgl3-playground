package com.github.ykiselev.tree;

import java.util.Optional;
import java.util.regex.Pattern;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class PathTree<V> {

    private final Pattern pattern;

    private final PathTreeNode<V> root;

    PathTree(Pattern pattern, PathTreeNode<V> root) {
        this.pattern = requireNonNull(pattern);
        this.root = requireNonNull(root);
    }

    public Optional<V> find(String path) {
        return find(pattern.split(path));
    }

    public Optional<V> find(String[] parts) {
        PathTreeLeaf<V> node = root;
        for (String part : parts) {
            node = node.find(part);
            if (node == null) {
                return Optional.empty();
            }
        }
        return Optional.ofNullable(node)
                .map(PathTreeLeaf::value);
    }
}
