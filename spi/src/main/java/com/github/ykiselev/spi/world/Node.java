package com.github.ykiselev.spi.world;

import java.util.function.Predicate;

public interface Node {

    int iorg();

    int jorg();

    int korg();

    int range();

    int get(int i, int j, int k);

    void put(int i, int j, int k, int value, NodeFactory factory);

    void visit(NodePredicate predicate, Visitor visitor);

}
