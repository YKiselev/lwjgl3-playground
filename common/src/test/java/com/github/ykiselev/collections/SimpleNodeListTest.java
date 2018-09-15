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

package com.github.ykiselev.collections;

import com.github.ykiselev.collections.SimpleNodeList.ImmutableNode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
class SimpleNodeListTest {

    private final NodeList<ImmutableNode<Integer>> ints = new SimpleNodeList<>();

    private ImmutableNode<Integer> node(int value) {
        return new ImmutableNode<>(value);
    }

    private void assertListEquals(NodeList<ImmutableNode<Integer>> list, int... expected) {
        ImmutableNode<Integer> node = list.head();
        for (int v : expected) {
            assertNotNull(node);
            assertEquals(v, (int) node.value());
            node = node.next();
        }
        assertNull(node);
    }

    @Test
    void shouldAddFirst() {
        final ImmutableNode<Integer> n1 = ints.addFirst(node(1));
        ints.addFirst(node(2));
        final ImmutableNode<Integer> n3 = ints.addFirst(node(3));
        assertListEquals(ints, 3, 2, 1);
        assertSame(ints.head(), n3);
        assertSame(ints.tail(), n1);
    }

    @Test
    void shouldAddLast() {
        final ImmutableNode<Integer> n1 = ints.addLast(node(1));
        ints.addLast(node(2));
        final ImmutableNode<Integer> n3 = ints.addLast(node(3));
        assertListEquals(ints, 1, 2, 3);
        assertSame(ints.head(), n1);
        assertSame(ints.tail(), n3);
    }

    @Test
    void shouldRemove() {
        final ImmutableNode<Integer> n1 = ints.addLast(node(1));
        final ImmutableNode<Integer> n2 = ints.addLast(node(2));
        final ImmutableNode<Integer> n3 = ints.addLast(node(3));
        assertListEquals(ints, 1, 2, 3);
        ints.remove(n2);
        assertListEquals(ints, 1, 3);
        ints.remove(n1);
        assertListEquals(ints, 3);
        ints.remove(n3);
        assertNull(ints.head());
        assertNull(ints.tail());
    }

    @Test
    void shouldClear() {
        ints.addLast(node(1));
        ints.addLast(node(2));
        ints.addLast(node(3));
        assertListEquals(ints, 1, 2, 3);
        ints.clear();
        assertNull(ints.head());
        assertNull(ints.tail());
    }

}