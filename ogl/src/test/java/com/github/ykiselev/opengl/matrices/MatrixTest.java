package com.github.ykiselev.opengl.matrices;

import org.junit.Assert;
import org.junit.Test;

import java.nio.FloatBuffer;

import static org.junit.Assert.assertEquals;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public class MatrixTest {

    private final FloatBuffer m = FloatBuffer.allocate(16);

    private void assertMatrixEquals(FloatBuffer actual, float... expected) {
        int i = 0;
        for (float v : expected) {
            final float v1 = actual.get(i);
            Assert.assertEquals("Difference at #" + i, v, v1, 0.0001f);
            i++;
        }
    }

    @Test
    public void shouldBeIdentity() {
        Matrix.identity(m);
        assertMatrixEquals(
                m,
                1, 0, 0, 0,
                0, 1, 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 1
        );
    }

    @Test
    public void shouldBeZero() {
        assertMatrixEquals(
                m,
                0, 0, 0, 0,
                0, 0, 0, 0,
                0, 0, 0, 0,
                0, 0, 0, 0
        );
    }

    @Test
    public void shouldTranslate() {
        Matrix.translate(m, 1, 2, 3, m);
        assertMatrixEquals(
                m,
                0, 0, 0, 0,
                0, 0, 0, 0,
                0, 0, 0, 0,
                1, 2, 3, 0
        );
    }

    @Test
    public void shouldScale() {
        Matrix.identity(m);
        Matrix.scale(m, 2, 4, 8, m);
        assertMatrixEquals(
                m,
                2, 0, 0, 0,
                0, 4, 0, 0,
                0, 0, 8, 0,
                0, 0, 0, 1
        );
    }

    @Test
    public void shouldTranspose() {
        m.clear()
                .put(1).put(2).put(3).put(4)
                .put(5).put(6).put(7).put(8)
                .put(9).put(10).put(11).put(12)
                .put(13).put(14).put(15).put(16)
                .flip();
        Matrix.transpose(m, m);
        assertMatrixEquals(
                m,
                1, 5, 9, 13,
                2, 6, 10, 14,
                3, 7, 11, 15,
                4, 8, 12, 16
        );
    }

    @Test
    public void shouldCopy() {
        m.clear()
                .put(1).put(2).put(3).put(4)
                .put(5).put(6).put(7).put(8)
                .put(9).put(10).put(11).put(12)
                .put(13).put(14).put(15).put(16)
                .flip();
        final FloatBuffer b = FloatBuffer.allocate(16);
        Matrix.copy(m, b);
        assertMatrixEquals(
                b,
                1, 2, 3, 4,
                5, 6, 7, 8,
                9, 10, 11, 12,
                13, 14, 15, 16
        );
    }

    @Test
    public void shouldAdd() {
        FloatBuffer a = FloatBuffer.allocate(16), b = FloatBuffer.allocate(16);
        for (int i = 1; i <= 16; i++) {
            a.put(i);
            b.put(17 - i);
        }
        a.flip();
        b.flip();
        Matrix.add(a, b, m);
        assertMatrixEquals(
                m,
                17, 17, 17, 17,
                17, 17, 17, 17,
                17, 17, 17, 17,
                17, 17, 17, 17
        );
    }

    @Test
    public void shouldMultiplyByScalar() {
        m.clear()
                .put(1).put(2).put(3).put(4)
                .put(5).put(6).put(7).put(8)
                .put(9).put(10).put(11).put(12)
                .put(13).put(14).put(15).put(16)
                .flip();
        Matrix.multiply(m, 2, m);
        assertMatrixEquals(
                m,
                2, 4, 6, 8,
                10, 12, 14, 16,
                18, 20, 22, 24,
                26, 28, 30, 32
        );
    }

    @Test
    public void shouldRotateAroundX() {
        final FloatBuffer vector = FloatBuffer.wrap(new float[]{0, 1, 0});
        Matrix.rotation(Math.toRadians(90), 0, 0, m);
        Matrix.multiply(m, vector);
        Assert.assertEquals(0, vector.get(0), 0.001f);
        Assert.assertEquals(0, vector.get(1), 0.001f);
        Assert.assertEquals(1, vector.get(2), 0.001f);
    }

    @Test
    public void shouldRotateAroundY() {
        final FloatBuffer vector = FloatBuffer.wrap(new float[]{1, 0, 0});
        Matrix.rotation(0, Math.toRadians(90), 0, m);
        Matrix.multiply(m, vector);
        Assert.assertEquals(0, vector.get(0), 0.001f);
        Assert.assertEquals(0, vector.get(1), 0.001f);
        Assert.assertEquals(-1, vector.get(2), 0.001f);
    }

    @Test
    public void shouldRotateAroundZ() {
        final FloatBuffer vector = FloatBuffer.wrap(new float[]{1, 0, 0});
        Matrix.rotation(0, 0, Math.toRadians(90), m);
        Matrix.multiply(m, vector);
        Assert.assertEquals(0, vector.get(0), 0.001f);
        Assert.assertEquals(1, vector.get(1), 0.001f);
        Assert.assertEquals(0, vector.get(2), 0.001f);
    }

    @Test
    public void shouldCalculateDeterminant() {
        m.clear()
                .put(1).put(3).put(4).put(10)
                .put(2).put(5).put(9).put(11)
                .put(6).put(8).put(12).put(15)
                .put(7).put(13).put(14).put(16)
                .flip();
        Assert.assertEquals(-594.0, Matrix.determinant(m), 0.0001d);
    }

    @Test
    public void determinantShouldBeOneForIdentity() {
        Matrix.identity(m);
        Assert.assertEquals(1.0, Matrix.determinant(m), 0.0001d);
    }

    @Test
    public void shouldInverse() {
        m.clear()
                .put(1).put(2).put(4).put(6)
                .put(3).put(1).put(7).put(10)
                .put(5).put(8).put(1).put(12)
                .put(9).put(11).put(13).put(1)
                .flip();
        Matrix.inverse(m, m);
        assertMatrixEquals(
                m,
                -1643f / 2369, 744f / 2369, 194f / 2369, 90f / 2369,
                816f / 2369, -593f / 2369, 81f / 2369, 62f / 2369,
                439f / 2369, -20f / 2369, -209f / 2369, 74f / 2369,
                104f / 2369, 87f / 2369, 80f / 2369, -85f / 2369
        );
    }

    private void assertVectorEquals(float x, float y, float z, FloatBuffer v) {
        assertEquals(x, v.get(0), 0.001f);
        assertEquals(y, v.get(1), 0.001f);
        assertEquals(z, v.get(2), 0.001f);
    }

    @Test
    public void shouldBeOrthographic() {
        Matrix.orthographic(0, 100, 200, 0, -1, 1, m);
        final FloatBuffer v = FloatBuffer.wrap(new float[]{0, 0, 0});
        Matrix.multiply(m, v);
        assertVectorEquals(-1, -1, 0, v);

        v.clear().put(50).put(100).put(0).flip();
        Matrix.multiply(m, v);
        assertVectorEquals(0, 0, 0, v);

        v.clear().put(100).put(200).put(0).flip();
        Matrix.multiply(m, v);
        assertVectorEquals(1, 1, 0, v);
    }

    @Test
    public void shouldBePerspective() {
        Matrix.perspective(-1, 1, 1, -1, 0.5f, 10, m);
        final FloatBuffer v = FloatBuffer.wrap(new float[]{0, 0, -5});
        Matrix.multiply(m, v);
        assertVectorEquals(0, 0, 4.4736f, v);

        v.clear().put(5).put(5).put(-5).flip();
        Matrix.multiply(m, v);
        assertVectorEquals(2.5f, 2.5f, 4.4736f, v);

        v.clear().put(-5).put(-5).put(-5).flip();
        Matrix.multiply(m, v);
        v.flip();
        System.out.println(Vector.toString(v));
        assertVectorEquals(-2.5f, -2.5f, 4.4736f, v);
    }

}