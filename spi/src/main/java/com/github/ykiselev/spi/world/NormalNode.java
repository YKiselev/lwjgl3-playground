package com.github.ykiselev.spi.world;

public final class NormalNode extends AbstractNode {

    /**
     * Number of child nodes in one dimension is 1 << sideShift
     */
    private final int sideShift;
    /**
     * Child node index range is 1 << childShift
     */
    private final int childRangeShift;

    private final Node[] children;

    public NormalNode(int iorg, int jorg, int korg, int sideShift, int rangeShift) {
        super(iorg, jorg, korg);
        this.sideShift = sideShift;
        this.childRangeShift = Integer.numberOfTrailingZeros(1 << (rangeShift - sideShift));
        this.children = new Node[1 << (3 * sideShift)];
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
    public void put(int i, int j, int k, int value, NodeFactory factory) {
        final int index = index(i, j, k);
        final Node child;
        if (children[index] == null) {
            final int mask = -(1 << childRangeShift);
            child = factory.create(i & mask, j & mask, k & mask, sideShift, childRangeShift);
            children[index] = child;
        } else {
            child = children[index];
        }
        child.put(i, j, k, value, factory);
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
