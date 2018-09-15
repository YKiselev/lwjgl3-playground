/*
 * Copyright 2017 Yuriy Kiselev (uze@yandex.ru)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.ykiselev.tree;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

/**
 * Immutable prefix tree. Each node in that tree has name (referred to as prefix) unique among children or parent node.
 * For example, given a tree with three nodes with names A, B and C and separator (defined by supplied pattern) of ":"
 * <pre>
 *     A
 *     | \
 *     B  C
 * </pre>
 * it would have three possible paths: "A", "A:B", "A:C".
 * <p/>
 * Use {@link MutablePrefixTree} to create instances of this class.
 *
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class PrefixTree<V> {

    private final Pattern pattern;

    private final TreeNode<String, V> root;

    /**
     * @param pattern the pattern to use to split paths into parts
     * @param root    the root node
     */
    PrefixTree(Pattern pattern, TreeNode<String, V> root) {
        this.pattern = requireNonNull(pattern);
        this.root = requireNonNull(root);
    }

    /**
     * Searches for node by supplied path.
     *
     * @param path the path to search node for.
     * @return the found node or {@code null}
     */
    public V find(String path) {
        return find(pattern.split(path));
    }

    /**
     * Searches for node by supplied array of path parts (prefixes).
     *
     * @param parts the node path splitted to elements
     * @return the node or {@code null}
     */
    public V find(String[] parts) {
        TreeNode<String, V> node = root;
        for (String part : parts) {
            node = node.find(part);
            if (node == null) {
                return null;
            }
        }
        return node != null ? node.value() : null;
    }

    public PrefixTree<V> merge(PrefixTree<V> other) {
        return new PrefixTree<>(
                pattern,
                new MergeNode<>(root, other.root)
                        .emit()
        );
    }

    private static class MergeNode<V> {

        private final TreeNode<String, V> a;

        private final TreeNode<String, V> b;

        MergeNode(TreeNode<String, V> a, TreeNode<String, V> b) {
            this.a = a;
            this.b = b;
        }

        TreeNode<String, V> emit() {
            if (a != null) {
                if (b == null) {
                    return a;
                } else {
                    if (!Objects.equals(a.key(), b.key())) {
                        throw new IllegalArgumentException("Can not merge nodes with different keys: " + a.key() + " != " + b.key());
                    }
                    if (a.size() == 0) {
                        return b;
                    } else if (b.size() == 0) {
                        return a;
                    }
                    return merge();
                }
            } else {
                return b;
            }
        }

        private TreeNode<String, V> merge() {
            final Map<String, TreeNode<String, V>> keyToA = a.children()
                    .collect(Collectors.toMap(
                            TreeNode::key,
                            Function.identity()
                    ));
            final Map<String, TreeNode<String, V>> keyToB = b.children()
                    .collect(Collectors.toMap(
                            TreeNode::key,
                            Function.identity()
                    ));
            final IntFunction<TreeNode<String, V>[]> generator = TreeNode[]::new;
            return new TreeNode<>(a.key(), a.value(),
                    Stream.concat(
                            a.children()
                                    .map(n -> new MergeNode<>(n, keyToB.get(n.key())))
                                    .map(MergeNode::emit),
                            b.children()
                                    .filter(n -> !keyToA.containsKey(n.key()))
                                    .map(n -> new MergeNode<>(null, n))
                                    .map(MergeNode::emit)
                    ).toArray(generator)
            );
        }
    }
}
