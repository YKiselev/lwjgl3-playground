package com.github.ykiselev.spi.world;

import java.util.Arrays;

public final class NormalNode extends AbstractNode {

    private static final int SIDE_SHIFT = 1;

    /**
     * Child node index range is 1 << childShift
     */
    private int childRangeShift;

    private final Node[] children = new Node[1 << (3 * SIDE_SHIFT)];

    /**
     * @param iorg       i origin index
     * @param jorg       j origin index
     * @param korg       k origin index
     * @param rangeShift index range for this node is 1 << rangeShift
     */
    public NormalNode init(int iorg, int jorg, int korg, int rangeShift) {
        this.iorg = iorg;
        this.jorg = jorg;
        this.korg = korg;
        this.childRangeShift = Integer.numberOfTrailingZeros(1 << (rangeShift - SIDE_SHIFT));
        Arrays.fill(children, null);
        return this;
    }

    @Override
    public int range() {
        return (1 << childRangeShift) * (1 << SIDE_SHIFT);
    }

    private int index(int i, int j, int k) {
        return ((i - iorg) >> childRangeShift) +
                (((j - jorg) >> childRangeShift) << SIDE_SHIFT) +
                (((k - korg) >> childRangeShift) << (2 * SIDE_SHIFT));
    }

    @Override
    public int get(int i, int j, int k) {
        final int index = index(i, j, k);
        final Node child = children[index];
        return child != null
                ? child.get(i, j, k)
                : 0;
    }

    @Override
    public Leaf leafForIndices(int i, int j, int k, NodeFactory factory) {
        final Node child = getChild(i, j, k, factory);
        return child != null
                ? child.leafForIndices(i, j, k, factory)
                : null;
    }

    private Node getChild(int i, int j, int k, NodeFactory factory) {
        final int index = index(i, j, k);
        final Node child;
        if (children[index] == null && factory != null) {
            final int mask = -(1 << childRangeShift);
            child = factory.create(i & mask, j & mask, k & mask, childRangeShift);
            children[index] = child;
        } else {
            child = children[index];
        }
        return child;
    }

    @Override
    public void put(int i, int j, int k, int value, NodeFactory factory) {
        getChild(i, j, k, factory)
                .put(i, j, k, value, factory);
    }

    @Override
    public void visit(NodePredicate predicate, Visitor visitor) {
        if (!predicate.test(this)) {
            return;
        }

        visitor.visit(this);

        for (Node child : children) {
            if (child != null) {
                child.visit(predicate, visitor);
            }
        }
    }

    @Override
    public String toString() {
        return "NormalNode{" +
                "iorg=" + iorg +
                ", jorg=" + jorg +
                ", korg=" + korg +
                ", childRangeShift=" + childRangeShift +
                '}';
    }
}
