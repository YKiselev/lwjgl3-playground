package com.github.ykiselev.opengl.matrices;

import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;

/**
 * Column-oriented 4x4 matrix.
 * <pre>
 *     A B C D
 *     E F G H
 *     I J K L
 *     M N O P
 * </pre>
 * So A have index 0, E - 1, I - 2, M - 3, etc.
 *
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class Matrix implements AutoCloseable {

    private final FloatBuffer m = MemoryUtil.memCallocFloat(16);

    public FloatBuffer buffer() {
        return m;
    }

    @Override
    public void close() {
        MemoryUtil.memFree(m);
    }

    /**
     * Sets this matrix to identity.
     */
    public void identity() {
        identity(m);
    }

    /**
     * Initialized provided buffer with identity matrix.
     *
     * @param result the buffer to store resulting matrix in.
     */
    public static void identity(FloatBuffer result) {
        result.clear()
                .put(1).put(0).put(0).put(0)
                .put(0).put(1).put(0).put(0)
                .put(0).put(0).put(1).put(0)
                .put(0).put(0).put(0).put(1)
                .flip();
    }

    public void orthographic(float left, float right, float top, float bottom, float near, float far) {
        orthographic(left, right, top, bottom, near, far, m);
    }

    /**
     * Calculates orthographic projection matrix.
     *
     * @param left   the left screen coordinate (usually 0)
     * @param right  the right screen coordinate (usually width)
     * @param top    the top screen coordinate (usually height)
     * @param bottom the bottom screen coordinate (usually 0)
     * @param near   the near z value (for example -1)
     * @param far    the far z coordinate (for example 1)
     * @param m      the buffer to store rersulting matrix in.
     */
    public static void orthographic(float left, float right, float top, float bottom, float near, float far, FloatBuffer m) {
        m.clear();
        m.put(2 / (right - left)).put(0).put(0).put(0);
        m.put(0).put(2 / (top - bottom)).put(0).put(0);
        m.put(0).put(0).put(-2 / (far - near)).put(0);
        m.put(-(right + left) / (right - left)).put(-(top + bottom) / (top - bottom)).put(-(far + near) / (far - near)).put(1);
        m.flip();
    }

    /**
     * Multiplies this matrix by other.
     *
     * @param other the matrix to multiply by.
     */
    public void mul(Matrix other) {
        mul(m, other.m, m);
    }

    /**
     * Each column of this matrix is multiplied by row of other and then result is summed.
     *
     * @param a      the first matrix
     * @param b      the second matrix
     * @param result the matrix to store result in
     */
    public static void mul(FloatBuffer a, FloatBuffer b, FloatBuffer result) {
        final float m0 = a.get(0) * b.get(0) + a.get(1) * b.get(4) + a.get(2) * b.get(8) + a.get(3) * b.get(12);
        final float m1 = a.get(0) * b.get(1) + a.get(1) * b.get(5) + a.get(2) * b.get(9) + a.get(3) * b.get(13);
        final float m2 = a.get(0) * b.get(2) + a.get(1) * b.get(6) + a.get(2) * b.get(10) + a.get(3) * b.get(14);
        final float m3 = a.get(0) * b.get(3) + a.get(1) * b.get(7) + a.get(2) * b.get(11) + a.get(3) * b.get(15);

        final float m4 = a.get(4) * b.get(0) + a.get(5) * b.get(4) + a.get(6) * b.get(8) + a.get(7) * b.get(12);
        final float m5 = a.get(4) * b.get(1) + a.get(5) * b.get(5) + a.get(6) * b.get(9) + a.get(7) * b.get(13);
        final float m6 = a.get(4) * b.get(2) + a.get(5) * b.get(6) + a.get(6) * b.get(10) + a.get(7) * b.get(14);
        final float m7 = a.get(4) * b.get(3) + a.get(5) * b.get(7) + a.get(6) * b.get(11) + a.get(7) * b.get(15);

        final float m8 = a.get(8) * b.get(0) + a.get(9) * b.get(4) + a.get(10) * b.get(8) + a.get(11) * b.get(12);
        final float m9 = a.get(8) * b.get(1) + a.get(9) * b.get(5) + a.get(10) * b.get(9) + a.get(11) * b.get(13);
        final float m10 = a.get(8) * b.get(2) + a.get(9) * b.get(6) + a.get(10) * b.get(10) + a.get(11) * b.get(14);
        final float m11 = a.get(8) * b.get(3) + a.get(9) * b.get(7) + a.get(10) * b.get(11) + a.get(11) * b.get(15);

        final float m12 = a.get(12) * b.get(0) + a.get(13) * b.get(4) + a.get(14) * b.get(8) + a.get(15) * b.get(12);
        final float m13 = a.get(12) * b.get(1) + a.get(13) * b.get(5) + a.get(14) * b.get(9) + a.get(15) * b.get(13);
        final float m14 = a.get(12) * b.get(2) + a.get(13) * b.get(6) + a.get(14) * b.get(10) + a.get(15) * b.get(14);
        final float m15 = a.get(12) * b.get(3) + a.get(13) * b.get(7) + a.get(14) * b.get(11) + a.get(15) * b.get(15);
        result.clear()
                .put(m0).put(m1).put(m2).put(m3)
                .put(m4).put(m5).put(m6).put(m7)
                .put(m8).put(m9).put(m10).put(m11)
                .put(m12).put(m13).put(m14).put(m15)
                .flip();
    }

    /**
     * Adds translation to matrix {@code a}.
     *
     * @param a      the original matrix to add translation to
     * @param dx     x translation
     * @param dy     y translation
     * @param dz     z translation
     * @param result the buffer to store result.
     */
    public static void translate(FloatBuffer a, float dx, float dy, float dz, FloatBuffer result) {
        if (a == result) {
            result.put(12, a.get(12) + dx).put(13, a.get(13) + dy).put(14, a.get(14) + dz);
        } else {
            result.clear()
                    .put(a.get(0)).put(a.get(1)).put(a.get(2)).put(a.get(3))
                    .put(a.get(4)).put(a.get(5)).put(a.get(6)).put(a.get(7))
                    .put(a.get(8)).put(a.get(9)).put(a.get(10)).put(a.get(11))
                    .put(a.get(12) + dx).put(a.get(13) + dy).put(a.get(14) + dz).put(a.get(15))
                    .flip();
        }
    }
}
