package com.github.ykiselev.spi.world.predicates;

import com.github.ykiselev.spi.world.Leaf;
import com.github.ykiselev.spi.world.NodePredicate;
import com.github.ykiselev.spi.world.NormalNode;

import java.util.Objects;

public final class CountingPredicate implements NodePredicate {

    private final NodePredicate delegate;
    private int nodesPassed, nodesRejected, leafsPassed, leafsRejected;

    public CountingPredicate(NodePredicate delegate) {
        this.delegate = Objects.requireNonNull(delegate);
    }

    public int nodesPassed() {
        return nodesPassed;
    }

    public int nodesRejected() {
        return nodesRejected;
    }

    public int leafsPassed() {
        return leafsPassed;
    }

    public int leafsRejected() {
        return leafsRejected;
    }

    public void reset() {
        nodesPassed = nodesRejected = leafsPassed = leafsRejected = 0;
    }

    @Override
    public boolean test(NormalNode node) {
        var r = delegate.test(node);
        if (r) {
            nodesPassed++;
        } else {
            nodesRejected++;
        }
        return r;
    }

    @Override
    public boolean test(Leaf leaf) {
        var r = delegate.test(leaf);
        if (r) {
            leafsPassed++;
        } else {
            leafsRejected++;
        }
        return r;
    }
}
