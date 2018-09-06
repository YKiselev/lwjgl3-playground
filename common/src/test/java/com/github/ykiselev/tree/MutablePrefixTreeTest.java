package com.github.ykiselev.tree;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
class MutablePrefixTreeTest {

    @Test
    void shouldBuildImmutableTree() {
        final PrefixTree<Integer> tree = new MutablePrefixTree<Integer>("\\.")
                .add("a.b.1", 1)
                .add("a.b.2", 2)
                .toPrefixTree();
        assertEquals(1, (int) tree.find("a.b.1"));
        assertEquals(2, (int) tree.find("a.b.2"));
        assertNull(tree.find("a.b.3"));
    }

    @Test
    void shouldBuildImmutableTreeNonRec() {
        final PrefixTree<Integer> tree = new MutablePrefixTree<Integer>("\\.")
                .add("a.b.c.d.e.f.g.1", 1)
                .add("a.b.k.l.m.n.2", 2)
                .toPrefixTreeNonRec();
        assertEquals(1, (int) tree.find("a.b.c.d.e.f.g.1"));
        assertEquals(2, (int) tree.find("a.b.k.l.m.n.2"));
        assertNull(tree.find("a.b.3"));
    }

    @Test
    void shouldMerge() {
        PrefixTree<Integer> a = new MutablePrefixTree<Integer>("\\.")
                .add("a.b.c.d.e", 1)
                .add("a.b.k.l.m.n", 2)
                .toPrefixTree();
        PrefixTree<Integer> b = new MutablePrefixTree<Integer>("\\.")
                .add("a.b.c.d.f", 3)
                .add("a.b.g", 4)
                .add("a.b.k.h", 5)
                .toPrefixTree();
        PrefixTree<Integer> tree = a.merge(b);
        assertEquals(1, (int) tree.find("a.b.c.d.e"));
        assertEquals(2, (int) tree.find("a.b.k.l.m.n"));
        assertEquals(3, (int) tree.find("a.b.c.d.f"));
        assertEquals(4, (int) tree.find("a.b.g"));
        assertEquals(5, (int) tree.find("a.b.k.h"));
    }

    @Test
    void shouldFind() {
        assertEquals(
                1,
                (int) new MutablePrefixTree<Integer>("\\.")
                        .add("a.b.1", 1)
                        .add("a.b.2", 2)
                        .get("a.b.1")
                        .value()
        );
    }

    @Test
    void shouldRemove() {
        MutablePrefixTree<Integer> tree = new MutablePrefixTree<Integer>("\\.")
                .add("a.b.c", 1)
                .add("a.b.d", 2);
        assertEquals("b", tree.remove("a.b").key());
        assertNull(tree.get("a.b.c"));
        assertNull(tree.get("a.b.d"));
        assertNull(tree.get("a.b"));
        assertEquals("a", tree.get("a").key());
    }
}