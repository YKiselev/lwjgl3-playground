package com.github.ykiselev.spi.world;

/**
 *
 */
public class World {

    private static final int MAX_LEAF_SHIFT = 4;

    private final Node root;

    private final NodeFactory factory;

    public World(int globalRangeShift) {
        this.factory = (iorg, jorg, korg, sideShift, rangeShift) -> {
            if (rangeShift <= MAX_LEAF_SHIFT) {
                return new Leaf(iorg, jorg, korg, rangeShift);
            }
            return new NormalNode(iorg, jorg, korg, sideShift, rangeShift);
        };
        this.root = factory.create(0, 0, 0, 3, globalRangeShift);
    }

    public int get(int i, int j, int k) {
        return root.get(i, j, k);
    }

    public void put(int i, int j, int k, int value) {
        root.put(i, j, k, value, factory);
    }

    public void visit(NodePredicate predicate, Visitor visitor) {
        root.visit(predicate, visitor);
    }
}
