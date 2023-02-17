package com.github.ykiselev.spi.camera;

import com.github.ykiselev.opengl.matrices.Matrix;
import com.github.ykiselev.spi.world.NormalNode;
import org.junit.jupiter.api.Test;

import java.nio.FloatBuffer;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FrustumClippingPredicateTest {

    private final Frustum frustum = new Frustum();

    private final FrustumClippingPredicate predicate = new FrustumClippingPredicate();

    @Test
    void shouldClip() {
        FloatBuffer m = FloatBuffer.allocate(16);
        Matrix.orthographic(-1f, 1f, 1f, -1f, 1f, 10, m);
        frustum.setFromMatrix(m);

        predicate.frustum(frustum);
        predicate.blockSize(1f);

        assertTrue(predicate.test(new NormalNode(0, 0, -1, 1, 2)));
        assertFalse(predicate.test(new NormalNode(0, 0, 1, 1, 2)));
        assertTrue(predicate.test(new NormalNode(0, 0, -15, 1, 2)));
        assertFalse(predicate.test(new NormalNode(0, 0, -16, 1, 2)));
    }
}