package com.github.ykiselev.opengl.matrices;

import org.junit.Test;

import java.nio.FloatBuffer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public class VectorTest {

    private void assertVectorEquals(float x, float y, float z, FloatBuffer v) {
        assertEquals(x, v.get(0), 0.001f);
        assertEquals(y, v.get(1), 0.001f);
        assertEquals(z, v.get(2), 0.001f);
    }

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
        assertVectorEquals(0.218f, 0.436f, 0.872f, b);
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
        assertVectorEquals(0, 0, 1, a);
    }

    @Test
    public void shouldScale() {
        final FloatBuffer a = FloatBuffer.wrap(new float[]{3, 5, 7});
        final FloatBuffer b = FloatBuffer.allocate(3);
        Vector.scale(a, 3.14f, b);
        assertEquals(0, a.position());
        assertEquals(3, b.position());
        assertVectorEquals(9.42f, 15.7f, 21.98f, b);
    }

    @Test
    public void shouldAdd() {
        final FloatBuffer a = FloatBuffer.wrap(new float[]{1, 2, 3});
        final FloatBuffer b = FloatBuffer.wrap(new float[]{4, 5, 6});
        Vector.add(a, b, a);
        assertEquals(3, a.position());
        assertEquals(0, b.position());
        assertVectorEquals(5, 7, 9, a);
    }

    @Test
    public void shouldSubtract() {
        final FloatBuffer a = FloatBuffer.wrap(new float[]{1, 2, 3});
        final FloatBuffer b = FloatBuffer.wrap(new float[]{3, 2, 1});
        Vector.subtract(a, b, b);
        assertEquals(0, a.position());
        assertEquals(3, b.position());
        assertVectorEquals(-2, 0, 2, b);
    }

    @Test
    public void shouldMultiply() {
        final FloatBuffer a = FloatBuffer.wrap(new float[]{2, 3, 4});
        final FloatBuffer b = FloatBuffer.wrap(new float[]{5, 6, 7});
        Vector.multiply(a, b, a);
        assertEquals(3, a.position());
        assertEquals(0, b.position());
        assertVectorEquals(10, 18, 28, a);
    }

    @Test
    public void shouldDivide() {
        final FloatBuffer a = FloatBuffer.wrap(new float[]{4, 9, 20});
        final FloatBuffer b = FloatBuffer.wrap(new float[]{2, 3, 5});
        Vector.divide(a, b, a);
        assertEquals(3, a.position());
        assertEquals(0, b.position());
        assertVectorEquals(2, 3, 4, a);
    }

    @Test
    public void shouldBeEqual() {
        final FloatBuffer a = FloatBuffer.wrap(new float[]{1.23456f, 2.3459f, 3.45678f});
        final FloatBuffer b = FloatBuffer.wrap(new float[]{1.23457f, 2.3458f, 3.45677f});
        assertTrue(Vector.equals(a, b, 0.0001f));
        assertEquals(0, a.position());
        assertEquals(0, b.position());
    }

    @Test
    public void shouldNotBeEqual() {
        final FloatBuffer a = FloatBuffer.wrap(new float[]{1.23456f, 2.3459f, 3.45678f});
        final FloatBuffer b = FloatBuffer.wrap(new float[]{1.23457f, 2.3458f, 3.45677f});
        assertFalse(Vector.equals(a, b, 0.00001f));
        assertEquals(0, a.position());
        assertEquals(0, b.position());
    }
}