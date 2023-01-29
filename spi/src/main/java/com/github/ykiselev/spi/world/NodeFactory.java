package com.github.ykiselev.spi.world;

public interface NodeFactory {

    Node create(int iorg, int jorg, int korg, int sideShift, int rangeShift);
}
