package com.github.ykiselev.spi.world;

public interface NodePredicate {

    NodePredicate DEFAULT = new NodePredicate() {
    };

    default boolean test(NormalNode node) {
        return true;
    }

    default boolean test(Leaf leaf) {
        return true;
    }

    default void beforeChild() {

    }

    default void afterChild() {

    }
}
