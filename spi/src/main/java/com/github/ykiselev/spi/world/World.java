package com.github.ykiselev.spi.world;

/**
 *
 */
public class World {

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

    public World(int globalRangeShift, int leafSideShift) {
        if (globalRangeShift <= leafSideShift) {
            throw new IllegalArgumentException("Global range shift should be greater than leaf side shift!");
        }
        int nodeSideShift = globalRangeShift - leafSideShift;
        while (nodeSideShift >= leafSideShift) {
            nodeSideShift >>= 1;
        }
        if (nodeSideShift < 1) {
            throw new IllegalArgumentException("Invalid shifts supplied! Bad node side shift: " + nodeSideShift);
        }
        this.indexRange = 1 << globalRangeShift;
        this.leafIndexRange = 1 << leafSideShift;
        this.factory = (iorg, jorg, korg, sideShift, rangeShift) -> {
            if (rangeShift <= leafSideShift) {
                return new Leaf(iorg, jorg, korg, rangeShift);
            }
            return new NormalNode(iorg, jorg, korg, sideShift, rangeShift);
        };
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
