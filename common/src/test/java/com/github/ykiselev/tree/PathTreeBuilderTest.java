package com.github.ykiselev.tree;

import org.junit.jupiter.api.Test;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
class PathTreeBuilderTest {

    @Test
    void shouldBuild() {
        final PathTreeNode<Integer> treeNode = new PathTreeBuilder<Integer>("\\.")
                .add("a.1", 1)
                .add("a.2", 2)
                .build();
        assertEquals(
                (Integer) 1,
                treeNode.find(
                        asList("a", "1").iterator()
                ).value()
        );
        assertEquals(
                (Integer) 2,
                treeNode.find(
                        asList("a", "2").iterator()
                ).value()
        );
    }
}