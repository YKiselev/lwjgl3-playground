package com.github.ykiselev.spi.world;

import java.util.Arrays;
import java.util.function.Consumer;

public final class Leaf extends AbstractNode {

    public static final int SIDE_SHIFT = 4;

    private final byte[] blocks = new byte[1 << (3 * SIDE_SHIFT)];

    public Leaf init(int iorg, int jorg, int korg) {
        this.iorg = iorg;
        this.jorg = jorg;
        this.korg = korg;
        Arrays.fill(blocks, (byte) 0);
        return this;
    }

    @Override
    public int range() {
        return 1 << SIDE_SHIFT;
    }

    private int index(int i, int j, int k) {
        return (i - iorg) +
                ((j - jorg) << SIDE_SHIFT) +
                ((k - korg) << (2 * SIDE_SHIFT));
    }

    @Override
    public int get(int i, int j, int k) {
        return blocks[index(i, j, k)];
    }

    @Override
    public Leaf leafForIndices(int i, int j, int k, NodeFactory factory) {
        return this;
    }

    @Override
    public void put(int i, int j, int k, int value, NodeFactory factory) {
        blocks[index(i, j, k)] = (byte) value;
    }

    @Override
    public void visit(NodePredicate predicate, Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "Leaf{" +
                "iorg=" + iorg +
                ", jorg=" + jorg +
                ", korg=" + korg +
                '}';
    }

    public void visit(Consumer<byte[]> consumer) {
        consumer.accept(blocks);
    }
}
