package com.github.ykiselev.opengl.matrices;

import org.junit.After;
import org.junit.Test;

import java.nio.FloatBuffer;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.fail;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public class MatrixTest {

    private final Matrix m = new Matrix();

    @After
    public void tearDown() {
        m.close();
    }

    private void assertEquals(Matrix actual, float... expected) {
        assertEquals(actual.buffer(), expected);
    }

    private void assertEquals(FloatBuffer actual, float... expected) {
        int i = 0;
        for (float v : expected) {
            final float v1 = actual.get(i);
            if (v != v1) {
                fail("Expected " + v + " got " + v1 + " at " + i);
            }
            i++;
        }
    }

    @Test
    public void shouldBeIdentity() {
        m.identity();
        assertEquals(
                m,
                1, 0, 0, 0,
                0, 1, 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 1
        );
    }

    @Test
    public void shouldBeZero() {
        assertEquals(
                m,
                0, 0, 0, 0,
                0, 0, 0, 0,
                0, 0, 0, 0,
                0, 0, 0, 0
        );
    }

    @Test
    public void shouldTranslateResult() {
        final FloatBuffer result = FloatBuffer.allocate(16);
        Matrix.translate(m.buffer(), 1, 2, 3, result);
        assertEquals(
                result,
                0, 0, 0, 0,
                0, 0, 0, 0,
                0, 0, 0, 0,
                1, 2, 3, 0
        );
    }

    @Test
    public void shouldTranslate() {
        m.translate(1, 2, 3);
        assertEquals(
                m,
                0, 0, 0, 0,
                0, 0, 0, 0,
                0, 0, 0, 0,
                1, 2, 3, 0
        );
    }

    @Test
    public void shouldScaleResult() {
        final FloatBuffer result = FloatBuffer.allocate(16);
        m.identity();
        Matrix.scale(m.buffer(), 2, 4, 8, result);
        assertEquals(
                result,
                2, 0, 0, 0,
                0, 4, 0, 0,
                0, 0, 8, 0,
                0, 0, 0, 1
        );
    }

    @Test
    public void shouldScale() {
        m.identity();
        m.scale(2, 4, 8);
        assertEquals(
                m,
                2, 0, 0, 0,
                0, 4, 0, 0,
                0, 0, 8, 0,
                0, 0, 0, 1
        );
    }

    @Test
    public void shouldTransposeResult() {
        final FloatBuffer result = FloatBuffer.allocate(16);
        m.buffer()
                .clear()
                .put(1).put(2).put(3).put(4)
                .put(5).put(6).put(7).put(8)
                .put(9).put(10).put(11).put(12)
                .put(13).put(14).put(15).put(16)
                .flip();
        Matrix.transpose(m.buffer(), result);
        assertEquals(
                result,
                1, 5, 9, 13,
                2, 6, 10, 14,
                3, 7, 11, 15,
                4, 8, 12, 16
        );
    }

    @Test
    public void shouldTranspose() {
        m.buffer()
                .clear()
                .put(1).put(2).put(3).put(4)
                .put(5).put(6).put(7).put(8)
                .put(9).put(10).put(11).put(12)
                .put(13).put(14).put(15).put(16)
                .flip();
        m.transpose();
        assertEquals(
                m,
                1, 5, 9, 13,
                2, 6, 10, 14,
                3, 7, 11, 15,
                4, 8, 12, 16
        );
    }

    @Test
    public void shouldRotateAroundX() {
        final float[] vector = new float[]{0, 1, 0, 1};
        Matrix.rotation(Math.toRadians(90), 0, 0, m.buffer());
        m.multiply(vector);
        assertArrayEquals(
                new float[]{0, 0, 1, 1},
                vector,
                0.001f
        );
    }

    @Test
    public void shouldRotateAroundY() {
        final float[] vector = new float[]{1, 0, 0, 1};
        Matrix.rotation(0, Math.toRadians(90), 0, m.buffer());
        m.multiply(vector);
        assertArrayEquals(
                new float[]{0, 0, -1, 1},
                vector,
                0.001f
        );
    }

    @Test
    public void shouldRotateAroundZ() {
        final float[] vector = new float[]{1, 0, 0, 1};
        Matrix.rotation(0, 0, Math.toRadians(90), m.buffer());
        m.multiply(vector);
        assertArrayEquals(
                new float[]{0, 1, 0, 1},
                vector,
                0.001f
        );
    }

}