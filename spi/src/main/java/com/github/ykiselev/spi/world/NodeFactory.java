package com.github.ykiselev.spi.world;

public interface NodeFactory {

    /**
     * @param iorg       node's origin index i
     * @param jorg       node's origin index j
     * @param korg       node's origin index k
     * @param sideShift  node's side shift (size in children in one dimension is 1 << sideShift)
     * @param rangeShift node's index range is 1 << rangeShift
     * @return created node
     */
    Node create(int iorg, int jorg, int korg, int sideShift, int rangeShift);
}
