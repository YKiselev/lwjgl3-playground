package com.github.ykiselev.opengl.matrices;

import org.junit.Test;

import java.nio.FloatBuffer;

import static org.junit.Assert.assertEquals;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public class VectorTest {

    @Test
    public void shouldCalculateSquaredLength() {
        final FloatBuffer v = FloatBuffer.wrap(new float[]{1, 2, 3});
        assertEquals(14f, Vector.squareLength(v), 0.001f);
        assertEquals(0, v.position());
    }

    @Test
    public void shouldCalculateLength() {
        final FloatBuffer v = FloatBuffer.wrap(new float[]{1, 2, 3});
        assertEquals(3.7416f, Vector.length(v), 0.001f);
        assertEquals(0, v.position());
    }

    @Test
    public void shouldNormalize() {
        final FloatBuffer v = FloatBuffer.wrap(new float[]{2, 4, 8});
        final FloatBuffer b = FloatBuffer.allocate(3);
        Vector.normalize(v, b);
        assertEquals(0, v.position());
        assertEquals(3, b.position());
        b.position(0);
        assertEquals(0.218f, b.get(0), 0.001f);
        assertEquals(0.436f, b.get(1), 0.001f);
        assertEquals(0.872f, b.get(2), 0.001f);
        assertEquals(1f, Vector.length(b), 0.001f);
    }

    @Test
    public void shouldCalculateDotProduct() {
        final FloatBuffer a = FloatBuffer.wrap(new float[]{1, 0, 0});
        final FloatBuffer b = FloatBuffer.wrap(new float[]{0.5f, 0.5f, 0});
        assertEquals(0.5f, Vector.dotProduct(a, b), 0.001f);
    }

    @Test
    public void dotProductShouldBeZero() {
        final FloatBuffer a = FloatBuffer.wrap(new float[]{1, 0, 0});
        final FloatBuffer b = FloatBuffer.wrap(new float[]{0, 1, 0});
        assertEquals(0, Vector.dotProduct(a, b), 0.001f);
    }

    @Test
    public void shouldCalculateCrossProduct() {
        final FloatBuffer a = FloatBuffer.wrap(new float[]{1, 0, 0});
        final FloatBuffer b = FloatBuffer.wrap(new float[]{0, 1, 0});
        Vector.crossProduct(a, b, a);
        assertEquals(3, a.position());
        assertEquals(0, b.position());
        assertEquals(0, a.get(0), 0.001f);
        assertEquals(0, a.get(1), 0.001f);
        assertEquals(1, a.get(2), 0.001f);
    }
}