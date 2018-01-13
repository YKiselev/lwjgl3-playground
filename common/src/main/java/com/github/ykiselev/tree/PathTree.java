package com.github.ykiselev.tree;

import java.util.Optional;
import java.util.regex.Pattern;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class PathTree<V> {

    private final Pattern pattern;

    private final TreeNode<String, V> root;

    PathTree(Pattern pattern, TreeNode<String, V> root) {
        this.pattern = requireNonNull(pattern);
        this.root = requireNonNull(root);
    }

    public Optional<V> find(String path) {
        return find(pattern.split(path));
    }

    public Optional<V> find(String[] parts) {
        TreeNode<String, V> node = root;
        for (String part : parts) {
            node = node.find(part);
            if (node == null) {
                return Optional.empty();
            }
        }
        return Optional.ofNullable(node)
                .map(TreeNode::value);
    }
}
