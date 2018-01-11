package com.github.ykiselev.tree;

import java.util.Arrays;
import java.util.Iterator;
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
        return find(
                Arrays.asList(pattern.split(path)).iterator()
        );
    }

    public Optional<V> find(Iterator<String> parts) {
        return Optional.ofNullable(root.find(parts))
                .map(PathTreeNode::value);
    }
}
