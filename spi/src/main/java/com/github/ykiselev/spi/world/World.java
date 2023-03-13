package com.github.ykiselev.spi.world;

import java.util.Objects;

/**
 *
 */
public final class World {

    private final Node root;

    private final NodeFactory factory;

    private final byte rangeShift;

    private final byte leafRangeShift;

    public byte rangeShift() {
        return rangeShift;
    }

    public byte leafRangeShift() {
        return leafRangeShift;
    }

    public World(NodeFactory factory, byte globalRangeShift) {
        if (globalRangeShift <= Leaf.SIDE_SHIFT) {
            throw new IllegalArgumentException("Global range shift should be greater than leaf side shift!");
        }
        this.rangeShift = globalRangeShift;
        this.leafRangeShift = Leaf.SIDE_SHIFT;
        this.factory = Objects.requireNonNull(factory);
        this.root = factory.create(0, 0, 0, globalRangeShift);
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
