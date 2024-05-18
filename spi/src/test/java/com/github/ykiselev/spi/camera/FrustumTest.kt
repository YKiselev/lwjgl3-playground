package com.github.ykiselev.spi.camera;

import com.github.ykiselev.opengl.matrices.Matrix;
import com.github.ykiselev.opengl.matrices.Vector3f;
import org.junit.jupiter.api.Test;

import java.nio.FloatBuffer;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FrustumTest {

    private final Frustum frustum = new Frustum();

    @Test
    void shouldClassify() {
        FloatBuffer m = FloatBuffer.allocate(16);
        Matrix.perspective(-0.1f, 0.1f, 0.1f, -0.1f, 0.1f, 100, m);
        frustum.setFromMatrix(m);

        // check near plane
        assertTrue(frustum.isInside(new Vector3f(0, 0, -0.1f)));
        assertFalse(frustum.isInside(new Vector3f(0, 0, -0.05f)));
        assertTrue(frustum.intersects(new Vector3f(0, 0, -0.05f), 0.11f));

        // check far plane
        assertTrue(frustum.isInside(new Vector3f(0, 0, -100)));
        assertFalse(frustum.isInside(new Vector3f(0, 0, -100.5f)));

        // check left plane
        assertTrue(frustum.isInside(new Vector3f(-0.1f, 0, -0.1f)));
        assertFalse(frustum.isInside(new Vector3f(-0.11f, 0, -0.1f)));

        // check right plane
        assertTrue(frustum.isInside(new Vector3f(0.1f, 0, -0.1f)));
        assertFalse(frustum.isInside(new Vector3f(0.11f, 0, -0.1f)));

        // check top plane
        assertTrue(frustum.isInside(new Vector3f(0, 0.1f, -0.1f)));
        assertFalse(frustum.isInside(new Vector3f(0, 0.11f, -0.1f)));

        // check bottom plane
        assertTrue(frustum.isInside(new Vector3f(0, -0.1f, -0.1f)));
        assertFalse(frustum.isInside(new Vector3f(0, -0.11f, -0.1f)));

        // check point inside
        assertTrue(frustum.isInside(new Vector3f(0, 0, -1)));
    }
}