package com.github.ykiselev.spi.world;

public final class DefaultNodeFactory implements NodeFactory {

    @Override
    public Node create(int iorg, int jorg, int korg, int sideShift, int rangeShift) {
        if (rangeShift == Leaf.SIDE_SHIFT) {
            return new Leaf()
                    .init(iorg, jorg, korg);
        } else if (rangeShift < Leaf.SIDE_SHIFT) {
            throw new IllegalArgumentException("Bad range shift: " + rangeShift);
        }
        return new NormalNode()
                .init(iorg, jorg, korg, sideShift, rangeShift);
    }
}
