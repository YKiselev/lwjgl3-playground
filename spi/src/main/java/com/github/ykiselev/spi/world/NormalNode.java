package com.github.ykiselev.spi.world;

import java.util.Arrays;

public final class NormalNode extends AbstractNode {

    private static final Node[] EMPTY_ARRAY = new Node[0];

    /**
     * Number of child nodes in one dimension is 1 << sideShift
     */
    private int sideShift;
    /**
     * Child node index range is 1 << childShift
     */
    private int childRangeShift;

    private Node[] children = EMPTY_ARRAY;

    /**
     * @param iorg       i origin index
     * @param jorg       j origin index
     * @param korg       k origin index
     * @param sideShift  number of child nodes in one dimension is 1 << sideShift
     * @param rangeShift index range for this node is 1 << rangeShift
     */
    public NormalNode init(int iorg, int jorg, int korg, int sideShift, int rangeShift) {
        this.iorg = iorg;
        this.jorg = jorg;
        this.korg = korg;
        this.sideShift = sideShift;
        this.childRangeShift = Integer.numberOfTrailingZeros(1 << (rangeShift - sideShift));
        final int length = 1 << (3 * sideShift);
        if (length != children.length) {
            this.children = new Node[length];
        } else {
            Arrays.fill(children, null);
        }
        return this;
    }

    @Override
    public int range() {
        return (1 << childRangeShift) * (1 << sideShift);
    }

    private int index(int i, int j, int k) {
        return ((i - iorg) >> childRangeShift) +
                (((j - jorg) >> childRangeShift) << sideShift) +
                (((k - korg) >> childRangeShift) << (2 * sideShift));
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
            child = factory.create(i & mask, j & mask, k & mask, sideShift, childRangeShift);
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
                ", sideShift=" + sideShift +
                ", childRangeShift=" + childRangeShift +
                '}';
    }
}
