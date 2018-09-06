package com.github.ykiselev.recursion;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
@Disabled
class StageTest {

    private int call(int value) {
        if (value == 0) {
            return 0;
        }
        return call(value - 1);
    }

    private Stage<Integer> callStage(int value) {
        if (value == 0) {
            return new Stage<>(0, null);
        }
        return new Stage<>(value - 1, () -> callStage(value - 1));
    }

    @Test
    void shouldOverflowStack() {
        assertThrows(StackOverflowError.class, () ->
                assertEquals(0, call(100_000)));
    }

    @Test
    void shouldWorkWithoutStackOverflows() {
        for (Stage<Integer> stage = callStage(100_000); ; ) {
            final Stage<Integer> nextStage = stage.call();
            if (nextStage != null) {
                stage = nextStage;
            } else {
                System.out.println("Answer is " + stage.value());
                break;
            }
        }
    }

    static class Node {

        private final String key;

        private final Node[] children;

        public Node(String key, Node... children) {
            this.key = key;
            this.children = children;
        }

        @Override
        public String toString() {
            return "Node{" +
                    "key='" + key + '\'' +
                    '}';
        }
    }

    private TreeStage<Node, Node> of(Node node) {
        return new TreeStage<>(
                () -> Arrays.stream(node.children)
                        .map(this::of)
                        .collect(Collectors.toList()),
                stages -> new Node(
                        node.key,
                        stages.stream()
                                .filter(ns -> ns.result() != null)
                                .map(TreeStage::result)
                                .toArray(Node[]::new)
                )
        );
    }

    @Test
    void shouldCreateTree() throws Exception {
        final Node root = new Node(
                "a",
                new Node(
                        "b",
                        new Node("d")
                ),
                new Node("c",
                        new Node("e"),
                        new Node("f")
                )
        );
        List<TreeStage<Node, Node>> nodes = new ArrayList<>();
        nodes.add(of(root));
        int from = 0;
        while (from < nodes.size()) {
            final TreeStage<Node, Node> nodeStage = nodes.get(from);
            from++;
            if (nodeStage == null) {
                continue;
            }
            nodes.addAll(nodeStage.call());
        }
        ListIterator<TreeStage<Node, Node>> it = nodes.listIterator(nodes.size());
        while (it.hasPrevious()) {
            it.previous().collectResult();
        }
        Node result = nodes.get(0).result();
        System.out.println(result);
    }
}