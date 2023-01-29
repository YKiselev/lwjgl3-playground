package com.github.ykiselev.spi.world;

abstract class AbstractNode implements Node {

    protected final int iorg, jorg, korg;

    public AbstractNode(int iorg, int jorg, int korg) {
        this.iorg = iorg;
        this.jorg = jorg;
        this.korg = korg;
    }

    @Override
    public int iorg() {
        return iorg;
    }

    @Override
    public int jorg() {
        return jorg;
    }

    @Override
    public int korg() {
        return korg;
    }
}
