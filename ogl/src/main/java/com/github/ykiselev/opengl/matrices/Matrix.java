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
    public void multiply(Matrix other) {
        multiply(m, other.m, m);
    }

    /**
     * Each column of this matrix is multiplied by row of other and then result is summed.
     *
     * @param a      the first matrix
     * @param b      the second matrix
     * @param result the matrix to store result in
     */
    public static void multiply(FloatBuffer a, FloatBuffer b, FloatBuffer result) {
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
     * Multiplies this matrix by {@code vector} and stores result in supplied {@code vector}.
     *
     * @param vector the vector to multiply by.
     */
    public void multiply(float[] vector) {
        final float x = vector[0];
        final float y = vector[1];
        final float z = vector[2];
        final float w = vector[3];

        vector[0] = m.get(0) * x + m.get(4) * y + m.get(8) * z + m.get(12) * w;
        vector[1] = m.get(1) * x + m.get(5) * y + m.get(9) * z + m.get(13) * w;
        vector[2] = m.get(2) * x + m.get(6) * y + m.get(10) * z + m.get(14) * w;
        vector[3] = m.get(3) * x + m.get(7) * y + m.get(11) * z + m.get(15) * w;
    }

    /**
     * Combines translation {@code (dx,dy,dz)} with matrix {@code a} and stores resulting matrix in {@code result}.
     *
     * @param a      the original matrix to add translation to
     * @param dx     x translation
     * @param dy     y translation
     * @param dz     z translation
     * @param result the buffer to store result.
     */
    public static void translate(FloatBuffer a, float dx, float dy, float dz, FloatBuffer result) {
        if (a != result) {
            result.clear()
                    .put(a)
                    .flip();
        }
        result.put(12, a.get(12) + dx)
                .put(13, a.get(13) + dy)
                .put(14, a.get(14) + dz);
    }

    public void translate(float dx, float dy, float dz) {
        translate(m, dx, dy, dz, m);
    }

    /**
     * Combines scaling {@code (sx,sy,sz)} with matrix {@code a} and stores resulting matric in {@code result}
     *
     * @param a      the original matrix
     * @param sx     x scaling factor
     * @param sy     y  scaling factor
     * @param sz     z  scaling factor
     * @param result the buffer to store result
     */
    public static void scale(FloatBuffer a, float sx, float sy, float sz, FloatBuffer result) {
        if (a != result) {
            result.clear()
                    .put(a)
                    .flip();
        }
        result.put(0, a.get(0) * sx)
                .put(5, a.get(5) * sy)
                .put(10, a.get(10) * sz);
    }

    /**
     * Scales this matrix.
     *
     * @param sx x scaling factor
     * @param sy y  scaling factor
     * @param sz z  scaling factor
     */
    public void scale(float sx, float sy, float sz) {
        scale(m, sx, sy, sz, m);
    }

    /**
     * Transposes the matrix {@code a} and stores resulting matrix in {@code result}
     *
     * @param a      the matrix to transpose
     * @param result the buffer to store result
     */
    public static void transpose(FloatBuffer a, FloatBuffer result) {
        float m0 = a.get(0);
        float m1 = a.get(1);
        float m2 = a.get(2);
        float m3 = a.get(3);

        float m4 = a.get(4);
        float m5 = a.get(5);
        float m6 = a.get(6);
        float m7 = a.get(7);

        float m8 = a.get(8);
        float m9 = a.get(9);
        float m10 = a.get(10);
        float m11 = a.get(11);

        float m12 = a.get(12);
        float m13 = a.get(13);
        float m14 = a.get(14);
        float m15 = a.get(15);

        result.clear()
                .put(m0).put(m4).put(m8).put(m12)
                .put(m1).put(m5).put(m9).put(m13)
                .put(m2).put(m6).put(m10).put(m14)
                .put(m3).put(m7).put(m11).put(m15)
                .flip();
    }

    public void transpose() {
        transpose(m, m);
    }

    /**
     * Initializes {@code result} with rotation matrix from Euler's angles {@code ax, ay, rz}.
     *
     * @param ax  the x-axis rotation angle (counter-clockwise)
     * @param ay   the y-axis rotation angle (counter-clockwise)
     * @param rz  the z-asix rotation angle (counter-clockwise)
     * @param result the buffer to store result
     */
    public static void rotation(double ax, double ay, double rz, FloatBuffer result) {
        final double a = Math.cos(ax);
        final double b = Math.sin(ax);
        final double c = Math.cos(ay);
        final double d = Math.sin(ay);
        final double e = Math.cos(rz);
        final double f = Math.sin(rz);
        result.clear()
                .put((float) (c * e)).put((float) (b * d * e + a * f)).put((float) (-a * d * e + b * f)).put(0)
                .put((float) (-c * f)).put((float) (-b * d * f + a * e)).put((float) (a * d * f + b * e)).put(0)
                .put((float) d).put((float) (-b * c)).put((float) (a * c)).put(0)
                .put(0).put(0).put(0).put(1)
                .flip();
    }
}
