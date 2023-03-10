package com.github.ykiselev.spi.camera;

import com.github.ykiselev.spi.world.Leaf;
import com.github.ykiselev.spi.world.Node;
import com.github.ykiselev.spi.world.NodePredicate;
import com.github.ykiselev.spi.world.NormalNode;

import java.util.Objects;

public final class FrustumClippingPredicate implements NodePredicate {

    private final BoundingSphere sphere = new BoundingSphere();

    private final Frustum frustum;

    private float blockSize;

    public FrustumClippingPredicate(Frustum frustum) {
        this.frustum = Objects.requireNonNull(frustum);
    }

    public void blockSize(float blockSize) {
        this.blockSize = blockSize;
    }

    private boolean intersects(Node node) {
        int halfRange = node.range() >> 1;
        float cx = (node.iorg() + halfRange) * blockSize;
        float cy = (node.jorg() + halfRange) * blockSize;
        float cz = (node.korg() + halfRange) * blockSize;
        float dx = halfRange * blockSize;
        float radius = (float) Math.sqrt(3 * dx * dx);
        sphere.set(cx, cy, cz, radius);
        return sphere.intersects(frustum);
    }

    @Override
    public boolean test(NormalNode node) {
        return intersects(node);
    }

    @Override
    public boolean test(Leaf leaf) {
        return intersects(leaf);
    }
}
