package com.github.ykiselev.opengl.matrices;

import java.nio.FloatBuffer;

/**
 * Column-oriented 4x4 matrix.
 * <pre>
 *   A B C D
 *   E F G H
 *   I J K L
 *   M N O P
 *
 * or as indices:
 *
 *   0 4  8 12
 *   1 5  9 13
 *   2 6 10 14
 *   3 7 11 15
 * </pre>
 * So A have index 0, E - 1, I - 2, M - 3, etc.
 *
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class Matrix {

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

    /**
     * Copies matrix {@code a} to {@code result}.
     *
     * @param a      the original matrix
     * @param result the buffer to store copy
     */
    public static void copy(FloatBuffer a, FloatBuffer result) {
        if (result != a) {
            result.clear()
                    .put(a)
                    .flip();
        }
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
        m.clear()
                .put(2 / (right - left)).put(0).put(0).put(0)
                .put(0).put(2 / (top - bottom)).put(0).put(0)
                .put(0).put(0).put(-2 / (far - near)).put(0)
                .put(-(right + left) / (right - left)).put(-(top + bottom) / (top - bottom)).put(-(far + near) / (far - near)).put(1)
                .flip();
    }

    /**
     * Calculates perspective projection matrix.
     *
     * @param left   the left screen coordinate (usually 0)
     * @param right  the right screen coordinate (usually width)
     * @param top    the top screen coordinate (usually height)
     * @param bottom the bottom screen coordinate (usually 0)
     * @param near   the near z value (for example -1)
     * @param far    the far z coordinate (for example 1)
     * @param m      the buffer to store rersulting matrix in.
     */
    public static void perspective(float left, float right, float top, float bottom, float near, float far, FloatBuffer m) {
        m.clear();
        m.put(2 * near / (right - left)).put(0).put(0).put(0);
        m.put(0).put(2 * near / (top - bottom)).put(0).put(0);
        m.put((right + left) / (right - left)).put((top + bottom) / (top - bottom)).put(-(far + near) / (far - near)).put(-1);
        m.put(0).put(0).put(-2 * far * near / (far - near)).put(0);
        m.flip();
    }

    public static void add(FloatBuffer a, FloatBuffer b, FloatBuffer result) {
        float m0 = a.get(0) + b.get(0);
        float m1 = a.get(1) + b.get(1);
        float m2 = a.get(2) + b.get(2);
        float m3 = a.get(3) + b.get(3);

        float m4 = a.get(4) + b.get(4);
        float m5 = a.get(5) + b.get(5);
        float m6 = a.get(6) + b.get(6);
        float m7 = a.get(7) + b.get(7);

        float m8 = a.get(8) + b.get(8);
        float m9 = a.get(9) + b.get(9);
        float m10 = a.get(10) + b.get(10);
        float m11 = a.get(11) + b.get(11);

        float m12 = a.get(12) + b.get(12);
        float m13 = a.get(13) + b.get(13);
        float m14 = a.get(14) + b.get(14);
        float m15 = a.get(15) + b.get(15);

        result.clear()
                .put(m0).put(m1).put(m2).put(m3)
                .put(m4).put(m5).put(m6).put(m7)
                .put(m8).put(m9).put(m10).put(m11)
                .put(m12).put(m13).put(m14).put(m15)
                .flip();
    }

    /**
     * Maltiplies matrix by scalar value.
     *
     * @param a      the source matrix
     * @param s      the scalsr to multiply by
     * @param result the buffer to store result in
     */
    public static void multiply(FloatBuffer a, float s, FloatBuffer result) {
        final float m0 = s * a.get(0);
        final float m1 = s * a.get(1);
        final float m2 = s * a.get(2);
        final float m3 = s * a.get(3);

        final float m4 = s * a.get(4);
        final float m5 = s * a.get(5);
        final float m6 = s * a.get(6);
        final float m7 = s * a.get(7);

        final float m8 = s * a.get(8);
        final float m9 = s * a.get(9);
        final float m10 = s * a.get(10);
        final float m11 = s * a.get(11);

        final float m12 = s * a.get(12);
        final float m13 = s * a.get(13);
        final float m14 = s * a.get(14);
        final float m15 = s * a.get(15);

        result.clear()
                .put(m0).put(m1).put(m2).put(m3)
                .put(m4).put(m5).put(m6).put(m7)
                .put(m8).put(m9).put(m10).put(m11)
                .put(m12).put(m13).put(m14).put(m15)
                .flip();
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
     * Multiplies this matrix by {@code vector} and stores result in supplied 3-component {@code vector}.
     * Note: Vector's buffer position is advanced.
     *
     * @param vector the vector to multiply by.
     */
    public static void multiply(FloatBuffer a, FloatBuffer vector) {
        final int pos = vector.position();
        final float x = vector.get();
        final float y = vector.get();
        final float z = vector.get();

        final float x1 = a.get(0) * x + a.get(4) * y + a.get(8) * z + a.get(12);
        final float y1 = a.get(1) * x + a.get(5) * y + a.get(9) * z + a.get(13);
        final float z1 = a.get(2) * x + a.get(6) * y + a.get(10) * z + a.get(14);
        final float w1 = a.get(3) * x + a.get(7) * y + a.get(11) * z + a.get(15);
        //final float oow = 1 / w1;

        vector.put(pos, x1)
                .put(pos + 1, y1)
                .put(pos + 2, z1);
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
        copy(a, result);
        result.put(12, a.get(12) + dx)
                .put(13, a.get(13) + dy)
                .put(14, a.get(14) + dz);
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

    /**
     * Initializes {@code result} with rotation matrix from Euler's angles {@code ax, ay, az}.
     *
     * @param ax     the x-axis rotation angle (counter-clockwise)
     * @param ay     the y-axis rotation angle (counter-clockwise)
     * @param az     the z-axis rotation angle (counter-clockwise)
     * @param result the buffer to store result
     */
    public static void rotation(double ax, double ay, double az, FloatBuffer result) {
        final double a = Math.cos(ax);
        final double b = Math.sin(ax);
        final double c = Math.cos(ay);
        final double d = Math.sin(ay);
        final double e = Math.cos(az);
        final double f = Math.sin(az);
        result.clear()
                .put((float) (c * e)).put((float) (b * d * e + a * f)).put((float) (-a * d * e + b * f)).put(0)
                .put((float) (-c * f)).put((float) (-b * d * f + a * e)).put((float) (a * d * f + b * e)).put(0)
                .put((float) d).put((float) (-b * c)).put((float) (a * c)).put(0)
                .put(0).put(0).put(0).put(1)
                .flip();
    }

    /**
     * For 2x2 matrix M determinant is A*D - B*C
     * <pre>
     *       | A B |
     *   M = |     |
     *       | C D |
     * </pre>
     * So for our 4x4 matrix determinant {@code Det = A * Asubd - B * Bsubd + C * Csubd - D*Dsubd} where
     * <pre>
     *     0 4  8 12
     *     1 5  9 13
     * M   2 6 10 14
     *     3 7 11 15
     *
     *       5  9 13
     * Asub  6 10 14
     *       7 11 15
     *
     *       1  9 13
     * Bsub  2 10 14
     *       3 11 15
     *
     *       1 5 13
     * Csub  2 6 14
     *       3 7 15
     *
     *       1 5 9
     * Dsub  2 6 10
     *       3 7 11
     *
     * Here each number is a matrix element index:
     * Asubd = 5 * (10*15 - 11*14) - 9 * (6*15 - 7*14) + 13 * (6*11 - 7*10)
     * Bsubd = 1 * (10*15 - 11*14) - 9 * (2*15 - 3*14) + 13 * (2*11 - 3*10)
     * Csubd = 1 * (6*15 - 7*14) - 5 * (2*15 - 3*14) + 13 * (2*7 - 3*6)
     * Dsubd = 1 * (6*11 - 7*10) - 5 * (2*11 - 3*10) + 9 * (2*7 - 3*6)
     * </pre>
     *
     * @param a the matrix
     * @return the determinant of a matrix
     */
    public static double determinant(FloatBuffer a) {
        final double d6_11 = a.get(6) * a.get(11) - a.get(7) * a.get(10);
        final double d6_15 = a.get(6) * a.get(15) - a.get(7) * a.get(14);
        final double d10_15 = a.get(10) * a.get(15) - a.get(11) * a.get(14);
        final double d2_11 = a.get(2) * a.get(11) - a.get(3) * a.get(10);
        final double d2_15 = a.get(2) * a.get(15) - a.get(3) * a.get(14);
        final double d2_7 = a.get(2) * a.get(7) - a.get(3) * a.get(6);

        final double Asubd = a.get(5) * d10_15 - a.get(9) * d6_15 + a.get(13) * d6_11;
        final double Bsubd = a.get(1) * d10_15 - a.get(9) * d2_15 + a.get(13) * d2_11;
        final double Csubd = a.get(1) * d6_15 - a.get(5) * d2_15 + a.get(13) * d2_7;
        final double Dsubd = a.get(1) * d6_11 - a.get(5) * d2_11 + a.get(9) * d2_7;

        return a.get(0) * Asubd - a.get(4) * Bsubd + a.get(8) * Csubd - a.get(12) * Dsubd;
    }

    /**
     * Calculate inverse matrix.
     * See {@link Matrix#determinant} for details.
     * <pre>
     *     0 4  8 12     A B C D
     *     1 5  9 13     E F G H
     * M   2 6 10 14     I J K L
     *     3 7 11 15     M N O P
     *
     * Asub 5  9 13   Bsub 1  9 13   Csub 1 5 13   Dsub 1 5  9
     *      6 10 14        2 10 14        2 6 14        2 6 10
     *      7 11 15        3 11 15        3 7 15        3 7 11
     *
     * Esub 4  8 12   Fsub 0  8 12   Gsub 0 4 12   Hsub 0 4  8
     *      6 10 14        2 10 14        2 6 14        2 6 10
     *      7 11 15        3 11 15        3 7 15        3 7 11
     *
     * Isub 4  8 12   Jsub 0  8 12   Ksub 0 4 12   Lsub 0 4  8
     *      5  9 13        1  9 13        1 5 13        1 5  9
     *      7 11 15        3 11 15        3 7 15        3 7 11
     *
     * Msub 4  8 12   Nsub 0  8 12   Osub 0 4 12   Psub 0 4  8
     *      5  9 13        1  9 13        1 5 13        1 5  9
     *      6 10 14        2 10 14        2 6 14        2 6 10
     * </pre>
     *
     * @param a      the original matrix
     * @param result the inverse matrix
     */
    public static void inverse(FloatBuffer a, FloatBuffer result) {
        final double d6_11 = a.get(6) * a.get(11) - a.get(7) * a.get(10);
        final double d6_15 = a.get(6) * a.get(15) - a.get(7) * a.get(14);
        final double d10_15 = a.get(10) * a.get(15) - a.get(11) * a.get(14);
        final double d2_11 = a.get(2) * a.get(11) - a.get(3) * a.get(10);
        final double d2_15 = a.get(2) * a.get(15) - a.get(3) * a.get(14);
        final double d2_7 = a.get(2) * a.get(7) - a.get(3) * a.get(6);
        final double d9_14 = a.get(9) * a.get(14) - a.get(10) * a.get(13);
        final double d9_15 = a.get(9) * a.get(15) - a.get(11) * a.get(13);
        final double d5_14 = a.get(5) * a.get(14) - a.get(6) * a.get(13);
        final double d5_15 = a.get(5) * a.get(15) - a.get(7) * a.get(13);
        final double d1_14 = a.get(1) * a.get(14) - a.get(2) * a.get(13);
        final double d1_15 = a.get(1) * a.get(15) - a.get(3) * a.get(13);
        final double d5_10 = a.get(5) * a.get(10) - a.get(6) * a.get(9);
        final double d5_11 = a.get(5) * a.get(11) - a.get(7) * a.get(9);
        final double d1_11 = a.get(1) * a.get(11) - a.get(3) * a.get(9);
        final double d1_6 = a.get(1) * a.get(6) - a.get(2) * a.get(5);
        final double d1_7 = a.get(1) * a.get(7) - a.get(3) * a.get(5);
        final double d1_10 = a.get(1) * a.get(10) - a.get(2) * a.get(9);

        // row 0
        final double A = a.get(5) * d10_15 - a.get(9) * d6_15 + a.get(13) * d6_11;
        final double B = a.get(1) * d10_15 - a.get(9) * d2_15 + a.get(13) * d2_11;
        final double C = a.get(1) * d6_15 - a.get(5) * d2_15 + a.get(13) * d2_7;
        final double D = a.get(1) * d6_11 - a.get(5) * d2_11 + a.get(9) * d2_7;

        // row 1
        final double E = a.get(4) * d10_15 - a.get(8) * d6_15 + a.get(12) * d6_11;
        final double F = a.get(0) * d10_15 - a.get(8) * d2_15 + a.get(12) * d2_11;
        final double G = a.get(0) * d6_15 - a.get(4) * d2_15 + a.get(12) * d2_7;
        final double H = a.get(0) * d6_11 - a.get(4) * d2_11 + a.get(8) * d2_7;

        // row 2
        final double I = a.get(4) * d9_15 - a.get(8) * d5_15 + a.get(12) * d5_11;
        final double J = a.get(0) * d9_15 - a.get(8) * d1_15 + a.get(12) * d1_11;
        final double K = a.get(0) * d5_15 - a.get(4) * d1_15 + a.get(12) * d1_7;
        final double L = a.get(0) * d5_11 - a.get(4) * d1_11 + a.get(8) * d1_7;

        // row 3
        final double M = a.get(4) * d9_14 - a.get(8) * d5_14 + a.get(12) * d5_10;
        final double N = a.get(0) * d9_14 - a.get(8) * d1_14 + a.get(12) * d1_10;
        final double O = a.get(0) * d5_14 - a.get(4) * d1_14 + a.get(12) * d1_6;
        final double P = a.get(0) * d5_10 - a.get(4) * d1_10 + a.get(8) * d1_6;

        final double det = a.get(0) * A - a.get(4) * B + a.get(8) * C - a.get(12) * D;
        if (det == 0) {
            throw new IllegalArgumentException("Matrix can not be inverted!");
        }

        result.clear()
                .put((float) A).put((float) -E).put((float) I).put((float) -M)
                .put((float) -B).put((float) F).put((float) -J).put((float) N)
                .put((float) C).put((float) -G).put((float) K).put((float) -O)
                .put((float) -D).put((float) H).put((float) -L).put((float) P)
                .flip();

        transpose(result, result);

        final double ood = 1.0 / det;
        for (int i = 0; i < 16; i++) {
            a.put(i, (float) (a.get(i) * ood));
        }
    }
}
