package com.github.ykiselev.spi.world;

import java.nio.channels.ReadableByteChannel;

public final class Leaf extends AbstractNode {

    private final int shift;

    private final byte[] blocks;

    public Leaf(int iorg, int jorg, int korg, int shift) {
        super(iorg, jorg, korg);
        this.shift = shift;
        this.blocks = new byte[1 << (3 * shift)];
    }

    @Override
    public int range() {
        return 1 << shift;
    }

    private int index(int i, int j, int k) {
        return (i - iorg) +
                ((j - jorg) << shift) +
                ((k - korg) << (2 * shift));
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
                ", shift=" + shift +
                '}';
    }

    public void read(ReadableByteChannel channel) {

    }
}
