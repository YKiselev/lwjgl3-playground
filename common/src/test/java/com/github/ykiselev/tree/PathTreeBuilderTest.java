package com.github.ykiselev.tree;

import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
class PathTreeBuilderTest {

    @Test
    void shouldBuild() {
        final PathTree<Integer> tree = new PathTreeBuilder<Integer>("\\.")
                .add("a.b.1", 1)
                .add("a.b.2", 2)
                .build();
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
}