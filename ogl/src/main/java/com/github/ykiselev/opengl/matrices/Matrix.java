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
     * @param m      the buffer to store resulting matrix in.
     */
    public static void perspective(float left, float right, float top, float bottom, float near, float far, FloatBuffer m) {
        m.clear();
        m.put(2 * near / (right - left)).put(0).put(0).put(0);
        m.put(0).put(2 * near / (top - bottom)).put(0).put(0);
        m.put((right + left) / (right - left)).put((top + bottom) / (top - bottom)).put(-(far + near) / (far - near)).put(-1);
        m.put(0).put(0).put(-2 * far * near / (far - near)).put(0);
        m.flip();
    }

    /**
     * Calculates perspective projection matrix.
     *
     * @param fow   the horizontal field of view (in radians)
     * @param ratio the aspect ratio between width and height of screen
     * @param far   the far z coordinate
     * @param m     the buffer to store resulting matrix in.
     */
    public static void perspective(float fow, float ratio, float far, FloatBuffer m) {
        final float h = 1 / ratio;
        final float near = (float) (1 / Math.tan(0.5 * fow));
        perspective(-1, 1, h, -h, near, far, m);
    }

    /**
     * Creates viewing matrix derived from the {@code eye} point, a reference point {@code center} indicating the center of the scene and vector {@code up}
     * Helpful tip: it's better to think of this as a coordinate system rotation.
     *
     * @param center the center of the scene
     * @param eye    the eye point
     * @param up     the upward vector, must not be parallel to the direction vector {@code dir = center - eye}
     * @param m      the buffer to store resulting matrix in.
     */
    public static void lookAt(Vector3f center, Vector3f eye, Vector3f up, FloatBuffer m) {
        final Vector3f f = new Vector3f(), up2 = new Vector3f(up);
        f.subtract(center, eye);
        f.normalize();
        up2.normalize();
        final Vector3f s = new Vector3f();
        s.crossProduct(up2, f);
        s.normalize();
        final Vector3f u = new Vector3f();
        u.crossProduct(f, s);
        // Now we should create fixed-axis rotation matrix and then transpose it to get matrix to rotate given point in old cs to new
        m.clear()
                .put(f.x).put(f.y).put(f.z).put(0)
                .put(s.x).put(s.y).put(s.z).put(0)
                .put(u.x).put(u.y).put(u.z).put(0)
                .put(0).put(0).put(0).put(1)
                .flip();
        transpose(m, m);
        translate(m, -eye.x, -eye.y, -eye.z, m);
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
     * Multiplies matrix by a scalar value.
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
     * Calclates index of cell in column-major matrix array from pair of row and column indices.
     *
     * @param row the matrix row (0-3)
     * @param col the matrix column (0-3)
     * @return the index in matrix linear buffer.
     */
    private static int idx(int row, int col) {
        return row + 4 * col;
    }

    /**
     * This method is kept for debugging {@link Matrix#multiply(java.nio.FloatBuffer, java.nio.FloatBuffer, java.nio.FloatBuffer)}.
     */
    static void multiplyUsingLoops(FloatBuffer a, FloatBuffer b, FloatBuffer result) {
        final float[] tmp = new float[16];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                tmp[idx(i, j)] = a.get(idx(i, 0)) * b.get(idx(0, j))+
                        a.get(idx(i, 1)) * b.get(idx(1, j))+
                        a.get(idx(i, 2)) * b.get(idx(2, j))+
                        a.get(idx(i, 3)) * b.get(idx(3, j));
            }
        }
        result.clear()
                .put(tmp)
                .flip();
    }
        /**
         * Each row of first matrix is multiplied by the column of second (component-wise) and sum of results is stored in {@code result}'s cell.
         *
         * @param a      the first matrix
         * @param b      the second matrix
         * @param result the matrix to store result in
         */
    public static void multiply(FloatBuffer a, FloatBuffer b, FloatBuffer result) {
        // r0
        final float m0 = a.get(0) * b.get(0) + a.get(4) * b.get(1) + a.get(8) * b.get(2) + a.get(12) * b.get(3);
        final float m4 = a.get(0) * b.get(4) + a.get(4) * b.get(5) + a.get(8) * b.get(6) + a.get(12) * b.get(7);
        final float m8 = a.get(0) * b.get(8) + a.get(4) * b.get(9) + a.get(8) * b.get(10) + a.get(12) * b.get(11);
        final float m12 = a.get(0) * b.get(12) + a.get(4) * b.get(13) + a.get(8) * b.get(14) + a.get(12) * b.get(15);
        // r1
        final float m1 = a.get(1) * b.get(0) + a.get(5) * b.get(1) + a.get(9) * b.get(2) + a.get(13) * b.get(3);
        final float m5 = a.get(1) * b.get(4) + a.get(5) * b.get(5) + a.get(9) * b.get(6) + a.get(13) * b.get(7);
        final float m9 = a.get(1) * b.get(8) + a.get(5) * b.get(9) + a.get(9) * b.get(10) + a.get(13) * b.get(11);
        final float m13 = a.get(1) * b.get(12) + a.get(5) * b.get(13) + a.get(9) * b.get(14) + a.get(13) * b.get(15);
        // r2
        final float m2 = a.get(2) * b.get(0) + a.get(6) * b.get(1) + a.get(10) * b.get(2) + a.get(14) * b.get(3);
        final float m6 = a.get(2) * b.get(4) + a.get(6) * b.get(5) + a.get(10) * b.get(6) + a.get(14) * b.get(7);
        final float m10 = a.get(2) * b.get(8) + a.get(6) * b.get(9) + a.get(10) * b.get(10) + a.get(14) * b.get(11);
        final float m14 = a.get(2) * b.get(12) + a.get(6) * b.get(13) + a.get(10) * b.get(14) + a.get(14) * b.get(15);
        // r3
        final float m3 = a.get(3) * b.get(0) + a.get(7) * b.get(1) + a.get(11) * b.get(2) + a.get(15) * b.get(3);
        final float m7 = a.get(3) * b.get(4) + a.get(7) * b.get(5) + a.get(11) * b.get(6) + a.get(15) * b.get(7);
        final float m11 = a.get(3) * b.get(8) + a.get(7) * b.get(9) + a.get(11) * b.get(10) + a.get(15) * b.get(11);
        final float m15 = a.get(3) * b.get(12) + a.get(7) * b.get(13) + a.get(11) * b.get(14) + a.get(15) * b.get(15);

        result.clear()
                .put(m0).put(m1).put(m2).put(m3)
                .put(m4).put(m5).put(m6).put(m7)
                .put(m8).put(m9).put(m10).put(m11)
                .put(m12).put(m13).put(m14).put(m15)
                .flip();
    }

    /**
     * Multiplies this matrix by vector {@code v} and stores result in vector {@code v}. This is a right multiplication
     *
     * @param a the matrix
     * @param v the vector
     */
    public static void multiply(FloatBuffer a, Vector3f v) {
        final float x1 = a.get(0) * v.x + a.get(4) * v.y + a.get(8) * v.z + a.get(12);
        final float y1 = a.get(1) * v.x + a.get(5) * v.y + a.get(9) * v.z + a.get(13);
        final float z1 = a.get(2) * v.x + a.get(6) * v.y + a.get(10) * v.z + a.get(14);
        //final float w1 = a.get(3) * x + a.get(7) * y + a.get(11) * z + a.get(15);
        v.x = x1;
        v.y = y1;
        v.z = z1;
    }

    /**
     * Multiplies matrix {@code a} by translation matrix derived from {@code (dx,dy,dz)} and stores result in {@code result}.
     *
     * @param a      the original matrix to add translation to
     * @param dx     x translation
     * @param dy     y translation
     * @param dz     z translation
     * @param result the buffer to store result.
     */
    public static void translate(FloatBuffer a, float dx, float dy, float dz, FloatBuffer result) {
        copy(a, result);

        final float m12 = a.get(0) * dx + a.get(4) * dy + a.get(8) * dz + a.get(12);
        final float m13 = a.get(1) * dx + a.get(5) * dy + a.get(9) * dz + a.get(13);
        final float m14 = a.get(2) * dx + a.get(6) * dy + a.get(10) * dz + a.get(14);
        final float m15 = a.get(3) * dx + a.get(7) * dy + a.get(11) * dz + a.get(15);

        result.put(12, m12)
                .put(13, m13)
                .put(14, m14)
                .put(15, m15);
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

    /**
     * Show matrix by rows
     */
    public static String toString(FloatBuffer m) {
        return "{r0(" + m.get(0) + " " + m.get(4) + " " + m.get(8) + " " + m.get(12) + "), r1(" +
                m.get(1) + " " + m.get(5) + " " + m.get(9) + " " + m.get(13) + "), r2(" +
                m.get(2) + " " + m.get(6) + " " + m.get(10) + " " + m.get(14) + "), r3(" +
                m.get(3) + " " + m.get(7) + " " + m.get(11) + " " + m.get(15) + ")}";
    }
}
