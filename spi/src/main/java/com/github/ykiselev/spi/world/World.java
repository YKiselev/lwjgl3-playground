package com.github.ykiselev.spi.world;

import java.util.Objects;

/**
 *
 */
public final class World {

    private final Node root;

    private final NodeFactory factory;

    private final int indexRange;

    private final int leafIndexRange;

    public int indexRange() {
        return indexRange;
    }

    public int leafIndexRange() {
        return leafIndexRange;
    }

    public World(NodeFactory factory, int globalRangeShift) {
        if (globalRangeShift <= Leaf.SIDE_SHIFT) {
            throw new IllegalArgumentException("Global range shift should be greater than leaf side shift!");
        }
        int nodeSideShift = 1;
//        while (nodeSideShift + 1 < Leaf.SIDE_SHIFT) {
//            nodeSideShift++;
//        }
        this.indexRange = 1 << globalRangeShift;
        this.leafIndexRange = 1 << Leaf.SIDE_SHIFT;
        this.factory = Objects.requireNonNull(factory);
        this.root = factory.create(0, 0, 0, nodeSideShift, globalRangeShift);
    }

    public int get(int i, int j, int k) {
        return root.get(i, j, k);
    }

    public Leaf leafForIndices(int i, int j, int k, boolean forceCreation) {
        return root.leafForIndices(i, j, k, forceCreation ? factory : null);
    }

    public void put(int i, int j, int k, int value) {
        root.put(i, j, k, value, factory);
    }

    public void visit(NodePredicate predicate, Visitor visitor) {
        root.visit(predicate, visitor);
    }
}
