package com.github.ykiselev.tree;

import java.util.Optional;
import java.util.regex.Pattern;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class PathTree<V> {

    private final Pattern pattern;

    private final TreeLeaf<String, V> root;

    PathTree(Pattern pattern, TreeLeaf<String, V> root) {
        this.pattern = requireNonNull(pattern);
        this.root = requireNonNull(root);
    }

    public Optional<V> find(String path) {
        return find(pattern.split(path));
    }

    public Optional<V> find(String[] parts) {
        TreeLeaf<String, V> node = root;
        for (String part : parts) {
            node = node.find(part);
            if (node == null) {
                return Optional.empty();
            }
        }
        return Optional.ofNullable(node)
                .map(TreeLeaf::value);
    }
}
