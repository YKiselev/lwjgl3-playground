package com.github.ykiselev.tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.regex.Pattern;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class PathTreeBuilder<V> {

    private final Pattern pattern;

    private final MutableTreeNode<V> root = new MutableTreeNode<>();

    public PathTreeBuilder(Pattern pattern) {
        this.pattern = requireNonNull(pattern);
    }

    public PathTreeBuilder(String separator) {
        this(Pattern.compile(separator));
    }

    public PathTreeBuilder<V> add(String path, V value) {
        root.add(
                Arrays.asList(
                        pattern.split(path)
                ).iterator(),
                value
        );
        return this;
    }

    public PathTreeNode<V> build() {
        return root.emit();
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

        MutableTreeNode<V> add(Iterator<String> parts, V value) {
            if (!parts.hasNext()) {
                throw new IllegalArgumentException("Empty parts!");
            }
            final String first = parts.next();
            MutableTreeNode<V> result = null;
            if (children != null) {
                for (MutableTreeNode<V> child : children) {
                    if (child.match(first)) {
                        result = child;
                        if (parts.hasNext()) {
                            child.add(parts, value);
                        } else {
                            throw new IllegalArgumentException("Duplicated path!");
                        }
                        break;
                    }
                }
            }
            if (result == null) {
                if (!parts.hasNext()) {
                    result = new MutableTreeNode<>(first, value);
                } else {
                    result = new MutableTreeNode<>(first, null);
                    result.add(parts, value);
                }
                if (children == null) {
                    children = new ArrayList<>();
                }
                children.add(result);
            }
            return result;
        }

        PathTreeNode<V> emit() {
            final Function<MutableTreeNode<V>, PathTreeNode<V>> transformation = MutableTreeNode::emit;
            final IntFunction<PathTreeNode<V>[]> generator = PathTreeNode[]::new;
            return new PathTreeNode<V>(
                    part,
                    value,
                    Optional.ofNullable(children)
                            .orElse(Collections.emptyList())
                            .stream()
                            .map(transformation)
                            .toArray(generator)
            );
        }
    }
}
