package com.github.ykiselev.tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.regex.Pattern;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class PrefixTreeBuilder<V> {

    private final Pattern pattern;

    private final MutableTreeNode<V> root = new MutableTreeNode<>();

    public PrefixTreeBuilder(Pattern pattern) {
        this.pattern = requireNonNull(pattern);
    }

    public PrefixTreeBuilder(String separator) {
        this(Pattern.compile(separator));
    }

    public PrefixTreeBuilder<V> add(String path, V value) {
        MutableTreeNode<V> node = root;
        for (String part : pattern.split(path)) {
            node = node.add(part, value);
        }
        return this;
    }

    public PrefixTree<V> build() {
        return new PrefixTree<>(pattern, root.emit());
    }

    private static class MutableTreeNode<V> {

        private final String part;

        private final V value;

        private List<MutableTreeNode<V>> children;

        MutableTreeNode(String part, V value) {
            this.part = part;
            this.value = value;
        }

        MutableTreeNode() {
            this(null, null);
        }

        boolean match(String part) {
            return Objects.equals(this.part, part);
        }

        MutableTreeNode<V> add(String part, V value) {
            if (children != null) {
                for (MutableTreeNode<V> child : children) {
                    if (child.match(part)) {
                        return child;
                    }
                }
            }
            final MutableTreeNode<V> result = new MutableTreeNode<>(part, value);
            if (children == null) {
                children = new ArrayList<>();
            }
            children.add(result);
            return result;
        }

        TreeNode<String, V> emit() {
            if (children != null && !children.isEmpty()) {
                final Function<MutableTreeNode<V>, TreeNode<String, V>> transformation = MutableTreeNode::emit;
                final IntFunction<TreeNode<String, V>[]> generator = TreeNode[]::new;
                return new TreeNode<>(
                        part,
                        value,
                        children.stream()
                                .map(transformation)
                                .toArray(generator)
                );
            }
            return new TreeNode<>(part, value);
        }
    }
}