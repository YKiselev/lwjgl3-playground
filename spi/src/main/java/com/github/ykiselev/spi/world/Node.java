package com.github.ykiselev.spi.world;

public interface Node {

    int iorg();

    int jorg();

    int korg();

    /**
     *
     * @return node's index range for single dimension
     */
    int range();

    int get(int i, int j, int k);

    Leaf leafForIndices(int i, int j, int k, NodeFactory factory);

    void put(int i, int j, int k, int value, NodeFactory factory);

    void visit(NodePredicate predicate, Visitor visitor);

}
