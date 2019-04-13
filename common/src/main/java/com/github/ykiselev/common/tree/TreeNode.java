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

package com.github.ykiselev.common.tree;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class TreeNode<K, V> implements Node<TreeNode<K, V>> {

    private static final TreeNode[] EMPTY = new TreeNode[0];

    private final K key;

    private final V value;

    private final TreeNode<K, V>[] children;

    public K key() {
        return key;
    }

    public V value() {
        return value;
    }

    @Override
    public int size() {
        return children.length;
    }

    @Override
    public TreeNode<K, V> childAt(int index) {
        return children[index];
    }

    @SafeVarargs
    public TreeNode(K key, V value, TreeNode<K, V>... children) {
        this.key = key;
        this.value = value;
        this.children = children.length > 0 ? children.clone() : EMPTY;
    }

    private boolean match(K key) {
        return Objects.equals(this.key, key);
    }

    public TreeNode<K, V> find(K key) {
        for (TreeNode<K, V> child : children) {
            if (child.match(key)) {
                return child;
            }
        }
        return null;
    }

    public MutableTreeNode<K, V> toMutable() {
        if (children != null && children.length > 0) {
            return new MutableTreeNode<>(
                    key,
                    value,
                    Arrays.stream(children)
                            .map(TreeNode::toMutable)
                            .collect(Collectors.toList())
            );
        }
        return new MutableTreeNode<>(key, value);
    }
}
