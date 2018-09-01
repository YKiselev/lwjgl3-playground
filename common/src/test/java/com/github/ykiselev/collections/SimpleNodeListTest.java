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