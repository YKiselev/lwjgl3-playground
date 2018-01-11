package com.github.ykiselev.tree;

import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
class PathTreeBuilderTest {

    @Test
    void shouldBuild() {
        final PathTree<Integer> tree = new PathTreeBuilder<Integer>("\\.")
                .add("a.1", 1)
                .add("a.2", 2)
                .build();
        assertEquals(
                1,
                (int) tree.find("a.1").orElseThrow(NoSuchElementException::new)
        );
        assertEquals(
                2,
                (int) tree.find("a.2").orElseThrow(NoSuchElementException::new)
        );
    }
}