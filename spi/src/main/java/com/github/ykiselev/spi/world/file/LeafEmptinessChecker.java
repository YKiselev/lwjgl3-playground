package com.github.ykiselev.spi.world.file;

import java.util.function.Consumer;

class LeafEmptinessChecker implements Consumer<byte[]> {

    private int sum;

    boolean isEmpty() {
        return sum == 0;
    }

    @Override
    public void accept(byte[] bytes) {
        sum = 0;
        for (byte b : bytes) {
            sum |= b;
        }
    }
}
