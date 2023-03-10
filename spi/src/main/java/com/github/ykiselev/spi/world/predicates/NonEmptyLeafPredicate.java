package com.github.ykiselev.spi.world.predicates;

import com.github.ykiselev.spi.world.Leaf;
import com.github.ykiselev.spi.world.NodePredicate;

@Deprecated
public final class NonEmptyLeafPredicate implements NodePredicate {
    @Override
    public boolean test(Leaf leaf) {
        return NodePredicate.super.test(leaf);
    }
}
