package com.github.ykiselev.spi.world.file;

import com.github.ykiselev.spi.world.Leaf;
import com.github.ykiselev.spi.world.Visitor;

import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.function.Consumer;

final class SaveLeafVisitor implements Visitor {

    private final LeafEmptinessChecker checker = new LeafEmptinessChecker();

    private ByteBuffer buffer;

    private Consumer<ByteBuffer> dest;

    public void init(Consumer<ByteBuffer> dest, ByteBuffer buffer) {
        this.dest = Objects.requireNonNull(dest);
        this.buffer = Objects.requireNonNull(buffer);
    }

    @Override
    public void visit(Leaf leaf) {
        leaf.visit(checker);
        if (!checker.isEmpty()) {
            buffer.clear()
                    .put(WorldFile.LEAF_SIGNATURE)
                    .putInt(leaf.iorg())
                    .putInt(leaf.jorg())
                    .putInt(leaf.korg());
            leaf.visit(buffer::put);
            buffer.flip();
            dest.accept(buffer);
        }
    }
}
