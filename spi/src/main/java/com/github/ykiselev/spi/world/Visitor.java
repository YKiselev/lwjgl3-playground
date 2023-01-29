package com.github.ykiselev.spi.world;

public interface Visitor {

    default void visit(NormalNode node) {

    }

    default void visit(Leaf leaf) {

    }
}
