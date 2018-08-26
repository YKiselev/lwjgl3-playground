package com.github.ykiselev.tree;

import java.util.Optional;
import java.util.regex.Pattern;

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

    PrefixTree(Pattern pattern, TreeNode<String, V> root) {
        this.pattern = requireNonNull(pattern);
        this.root = requireNonNull(root);
    }

    /**
     * Searches for node by supplied path. Supplied path is split to elements (refixes) by {@code pattern} supplied to constructor of this class.
     *
     * @param path the path to search node for.
     * @return the found node or {@code null}
     */
    public Optional<V> find(String path) {
        return find(pattern.split(path));
    }

    /**
     * Searches for node by supplied array of path partprefixes.
     * Supplied path is split by {@code pattern} supplied to constructor of this class.
     *
     * @param parts the node path splitted to elements
     * @return the node or {@code null}
     */
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
