package com.github.ykiselev.tree;

import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
        assertEquals(
                1,
                (int) tree.find("a.b.1").orElseThrow(NoSuchElementException::new)
        );
        assertEquals(
                2,
                (int) tree.find("a.b.2").orElseThrow(NoSuchElementException::new)
        );
        assertFalse(tree.find("a.b.3").isPresent());
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