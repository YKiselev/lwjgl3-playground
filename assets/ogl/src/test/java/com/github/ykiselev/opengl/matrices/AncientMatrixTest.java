/*
 * Copyright 2017 Yuriy Kiselev (uze@yandex.ru)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.ykiselev.opengl.matrices;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.nio.FloatBuffer;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AncientMatrixTest {

    private final FloatBuffer m = FloatBuffer.allocate(16);

    public static Stream<Arguments> translateArgs() {
        return Stream.of(
                Arguments.of(v(1, 2, 3), v(0, 0, 0), v(1, 2, 3)),
                Arguments.of(v(1, 2, 3), v(1, 0, 0), v(2, 2, 3)),
                Arguments.of(v(1, 2, 3), v(0, 1, 0), v(1, 3, 3)),
                Arguments.of(v(1, 2, 3), v(0, 0, 1), v(1, 2, 4)),
                Arguments.of(v(1, 2, 3), v(-1, 0, 0), v(0, 2, 3)),
                Arguments.of(v(1, 2, 3), v(0, -1, 0), v(1, 1, 3)),
                Arguments.of(v(1, 2, 3), v(0, 0, -1), v(1, 2, 2)),
                Arguments.of(v(1, 2, 3), v(1, 1, 1), v(2, 3, 4))
        );
    }

    /**
     * Note: angles are in degrees!
     */
    public static Stream<Arguments> rotationArgs() {
        return Stream.of(
                Arguments.of(90d, 0d, 0d, new Vector3f(0, 1, 0), new Vector3f(0, 0, 1)),
                Arguments.of(0d, 90d, 0d, new Vector3f(1, 0, 0), new Vector3f(0, 0, -1)),
                Arguments.of(0d, 0d, 90d, new Vector3f(1, 0, 0), new Vector3f(0, 1, 0)),
                Arguments.of(45d, 0d, 0d, new Vector3f(0, 1, 0), new Vector3f(0, 0.707f, 0.707f)),
                Arguments.of(0d, 45d, 0d, new Vector3f(1, 0, 0), new Vector3f(0.707f, 0, -0.707f)),
                Arguments.of(0d, 0d, 45d, new Vector3f(1, 0, 0), new Vector3f(0.707f, 0.707f, 0))
        );
    }

    private static void assertEquals(Vector3f expected, Vector3f actual) {
        Assertions.assertEquals(expected.x, actual.x, 0.001f, expected + " != " + actual);
        Assertions.assertEquals(expected.y, actual.y, 0.001f, expected + " != " + actual);
        Assertions.assertEquals(expected.z, actual.z, 0.001f, expected + " != " + actual);
    }

    public static Stream<Arguments> perspectiveArgs() {
        return Stream.of(
                Arguments.of(v(0, 0, 0, 1), v(0, 0, -2.222f, 0)),
                Arguments.of(v(0, 0, 1, 1), v(0, 0, -3.444f, -1)),
                Arguments.of(v(0, 0, 5, 1), v(0, 0, -8.333f, -5)),
                Arguments.of(v(0, 0, 10, 1), v(0, 0, -14.444f, -10)),
                Arguments.of(v(0, 0, 15, 1), v(0, 0, -20.555f, -15)),
                Arguments.of(v(1, 1, 1, 1), v(1, 1, -3.444f, -1)),
                Arguments.of(v(1, 1, 2, 1), v(1, 1, -4.666f, -2))
        );
    }

    private static Vector3f v(float x, float y, float z) {
        return new Vector3f(x, y, z);
    }

    private static Vector4f v(float x, float y, float z, float w) {
        return new Vector4f(x, y, z, w);
    }

    public static Stream<Arguments> lookAtArgs() {
        return Stream.of(
                Arguments.of(v(1, 0, 0), v(0, 0, 0), v(0, 0, 1), v(0, 0, -1), v(-1, 0, 0), v(0, 1, 0)),
                Arguments.of(v(1, 1, 0), v(0, 0, 0), v(0, 0, 1), v(0.707f, 0, -0.707f), v(-0.707f, 0, -0.707f), v(0, 1, 0)),
                Arguments.of(v(0, 1, 0), v(0, 0, 0), v(0, 0, 1), v(1, 0, 0), v(0, 0, -1), v(0, 1, 0)),
                Arguments.of(v(0, 0, 1), v(0, 0, 0), v(-1, 0, 0), v(0, -1, 0), v(-1, 0, 0), v(0, 0, -1)),
                Arguments.of(v(0, 0, 0), v(-1, 0, 0), v(0, 0, 1), v(0, 0, -2), v(-1, 0, -1), v(0, 1, -1)),
                Arguments.of(v(0, 0, 0), v(-1, -1, 0), v(0, 0, 1), v(0.707f, 0, -2.121f), v(-0.707f, 0, -2.121f), v(0, 1, -1.414f)),
                Arguments.of(v(0, 0, 0), v(0, -1, 0), v(0, 0, 1), v(1, 0, -1), v(0, 0, -2), v(0, 1, -1))
        );
    }

    private void assertMatrixEquals(FloatBuffer actual, float... expected) {
        int i = 0;
        for (float v : expected) {
            final float v1 = actual.get(i);
            Assertions.assertEquals(v, v1, 0.0001f, "Difference at #" + i);
            i++;
        }
    }

    @BeforeEach
    public void setUp() {
        Matrix.identity(m);
    }

    @Test
    public void shouldBeIdentity() {
        assertMatrixEquals(
                m,
                1, 0, 0, 0,
                0, 1, 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 1
        );
    }

    @ParameterizedTest
    @MethodSource("translateArgs")
    public void shouldTranslate(Vector3f v, Vector3f trans, Vector3f expected) {
        Matrix.translate(m, trans.x, trans.y, trans.z, m);
        Matrix.multiply(m, v);
        assertTrue(
                expected.equals(v, 0.0001f),
                "expected " + expected + " but was " + v
        );
    }

    @Test
    public void addingTranslationShouldBeEqualToMultiplication() {
        final FloatBuffer rm = FloatBuffer.allocate(16);
        Matrix.rotation(0, 0, Math.toRadians(45), rm);
        final FloatBuffer tm = FloatBuffer.allocate(16);
        Matrix.identity(tm);
        Matrix.translate(tm, 1, 2, 3, tm);

        // multiply rm * tm
        Matrix.multiply(rm, tm, m);
        final float[] r1 = Matrix.toArray(m);

        // add translation and store in separate matrix
        Matrix.translate(rm, 1, 2, 3, m);
        final float[] r2 = Matrix.toArray(m);

        // add translation in-place
        Matrix.translate(rm, 1, 2, 3, rm);
        final float[] r3 = Matrix.toArray(rm);

        assertArrayEquals(r1, r2, 0.001f);
        assertArrayEquals(r2, r3, 0.001f);
    }

    @Test
    public void shouldAddTranslationToExistingMatrix() {
        Matrix.rotation(0, 0, Math.toRadians(45), m);
        Matrix.translate(m, 1, 2, 3, m);
        final Vector3f v = v(4, 5, 6);
        Matrix.multiply(m, v);
        assertVectorEquals(-1.414f, 8.485f, 9, v);
    }

    @Test
    public void shouldScale() {
        m.clear()
                .put(1).put(2).put(3).put(4)
                .put(5).put(6).put(7).put(8)
                .put(9).put(10).put(11).put(12)
                .put(13).put(14).put(15).put(16)
                .flip();
        Matrix.scale(m, 2, 4, 8, m);
        assertMatrixEquals(
                m,
                2, 4, 6, 8,
                20, 24, 28, 32,
                72, 80, 88, 96,
                13, 14, 15, 16
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
    public void shouldMultiplyByVector() {
        m.clear()
                .put(1).put(4).put(7).put(0)
                .put(2).put(5).put(8).put(0)
                .put(3).put(6).put(9).put(0)
                .put(0).put(0).put(0).put(1)
                .flip();
        final Vector3f v = new Vector3f(1, 2, 3);
        Matrix.multiply(m, v);
        assertVectorEquals(14, 32, 50, v);
    }

    @Test
    public void shouldMultiply() {
        m.clear()
                .put(1).put(5).put(9).put(0)
                .put(2).put(6).put(10).put(0)
                .put(3).put(7).put(11).put(0)
                .put(0).put(0).put(0).put(1)
                .flip();
        Matrix.multiply(
                m,
                FloatBuffer.wrap(
                        new float[]{
                                1, 0, 0, 1,
                                0, 1, 0, 2,
                                0, 0, 1, 3,
                                0, 0, 0, 1
                        }
                ),
                m
        );
        assertMatrixEquals(
                m,
                1, 5, 9, 1,
                2, 6, 10, 2,
                3, 7, 11, 3,
                0, 0, 0, 1
        );
    }

    @ParameterizedTest
    @MethodSource("rotationArgs")
    public void shouldRotate(double ax, double ay, double az, Vector3f v, Vector3f expected) {
        Matrix.rotation(
                Math.toRadians(ax),
                Math.toRadians(ay),
                Math.toRadians(az),
                m
        );
        Matrix.multiply(m, v);
        assertEquals(expected, v);
//        assertTrue(
//                expected.equals(v, 0.0001f)
//        );
    }

    @Test
    public void shouldCalculateDeterminant() {
        m.clear()
                .put(1).put(3).put(4).put(10)
                .put(2).put(5).put(9).put(11)
                .put(6).put(8).put(12).put(15)
                .put(7).put(13).put(14).put(16)
                .flip();
        Assertions.assertEquals(-594.0, Matrix.determinant(m), 0.0001d);
    }

    @Test
    public void determinantShouldBeOneForIdentity() {
        Assertions.assertEquals(1.0, Matrix.determinant(m), 0.0001d);
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

    private void assertVectorEquals(float x, float y, float z, Vector3f v) {
        Assertions.assertEquals(x, v.x, 0.001f);
        Assertions.assertEquals(y, v.y, 0.001f);
        Assertions.assertEquals(z, v.z, 0.001f);
    }

    @Test
    public void shouldBeOrthographic() {
        Matrix.orthographic(0, 100, 200, 0, -1, 1, m);
        final Vector3f v = new Vector3f(0, 0, 0);
        Matrix.multiply(m, v);
        assertVectorEquals(-1, -1, 0, v);

        v.set(50, 100, 0);
        Matrix.multiply(m, v);
        assertVectorEquals(0, 0, 0, v);

        v.set(100, 200, 0);
        Matrix.multiply(m, v);
        assertVectorEquals(1, 1, 0, v);
    }

    @ParameterizedTest
    @MethodSource("perspectiveArgs")
    public void shouldBePerspective(Vector4f v, Vector4f expected) {
        Matrix.perspective(-1, 1, 1, -1, 1, 10, m);
        Matrix.multiply(m, v);
        assertTrue(expected.equals(v, 0.001f), "expected " + expected + " but was " + v);
    }

    @ParameterizedTest
    @MethodSource("lookAtArgs")
    public void shouldLookAt(Vector3f origin, Vector3f eye, Vector3f up, Vector3f ax, Vector3f ay, Vector3f az) {
        Matrix.lookAt(origin, eye, up, m);
        final Vector3f v1 = v(1, 0, 0), v2 = v(0, 1, 0), v3 = v(0, 0, 1);
        Matrix.multiply(m, v1);
        Matrix.multiply(m, v2);
        Matrix.multiply(m, v3);
        assertTrue(ax.equals(v1, 0.001f), "expected " + ax + " but was " + v1);
        assertTrue(ay.equals(v2, 0.001f), "expected " + ay + " but was " + v2);
        assertTrue(az.equals(v3, 0.001f), "expected " + az + " but was " + v3);
    }

    static final class Vector4f {

        public float x, y, z, w;

        public Vector4f() {
        }

        public Vector4f(float x, float y, float z, float w) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.w = w;
        }

        public void set(float x, float y, float z, float w) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.w = w;
        }

        /**
         * Checks if two vectors are equal (absolute difference in each components is less than delta).
         *
         * @param b     the second vector
         * @param delta the maximum difference between two numbers for which they are still considered equal.
         * @return the true if vectors are equal or false otherwise.
         */
        public boolean equals(Vector4f b, float delta) {
            return equals(x, b.x, delta)
                    && equals(y, b.y, delta)
                    && equals(z, b.z, delta)
                    && equals(w, b.w, delta);
        }

        private boolean equals(float a, float b, float delta) {
            return Math.abs(a - b) <= delta + delta * Math.abs(b);
        }

        @Override
        public String toString() {
            return "Vector4f{" +
                    "x=" + x +
                    ", y=" + y +
                    ", z=" + z +
                    ", w=" + w +
                    '}';
        }
    }

    static final class Vector3f {

        public float x, y, z;

        public Vector3f() {
        }

        public Vector3f(Vector3f src) {
            this(src.x, src.y, src.z);
        }

        public Vector3f(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public void set(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public void set(Vector3f src) {
            this.x = src.x;
            this.y = src.y;
            this.z = src.z;
        }

        @Override
        public String toString() {
            return "Vector3f{" +
                    "x=" + x +
                    ", y=" + y +
                    ", z=" + z +
                    '}';
        }

        /**
         * Normalizes vector.
         *
         * @return the normalizing coefficient (1/length) used
         */
        public double normalize() {
            final double length = length();
            if (length != 0) {
                double ool = 1.0 / length;
                x *= ool;
                y *= ool;
                z *= ool;
                return ool;
            }
            return 1;
        }

        /**
         * Calculates squared vector length.
         *
         * @return squared length of vector
         */
        public double squareLength() {
            return x * x + y * y + z * z;
        }

        /**
         * Calculates vector length.
         *
         * @return vector length
         */
        public double length() {
            return Math.sqrt(squareLength());
        }

        /**
         * Calculates Dot product between this and supplied vector.
         *
         * @param b the second vector
         */
        public double dotProduct(Vector3f b) {
            return x * b.x + y * b.y + z * b.z;
        }

        /**
         * Calculates cross-product vector c = (a × b) that is perpendicular to both a and b, with a direction given by the right-hand rule.
         *
         * @param a the first vector
         * @param b the second vector
         */
        public void crossProduct(Vector3f a, Vector3f b) {
            set(
                    a.y * b.z - a.z * b.y,
                    a.z * b.x - a.x * b.z,
                    a.x * b.y - a.y * b.x
            );
        }

        /**
         * Calculates cross-product of this vector and another.
         *
         * @param b the second vector
         */
        public void crossProduct(Vector3f b) {
            crossProduct(this, b);
        }

        /**
         * Scales the vector.
         */
        public void scale(float scale) {
            x *= scale;
            y *= scale;
            z *= scale;
        }

        /**
         * Applies specified operation to supplied vectors.
         *
         * @param a the first vector
         * @param b the second vector
         */
        private void apply(Vector3f a, Vector3f b, Ops op) {
            x = op.apply(a.x, b.x);
            y = op.apply(a.y, b.y);
            z = op.apply(a.z, b.z);
        }

        /**
         * Adds one vector to another.
         *
         * @param a the first vector
         * @param b the second vector
         */
        public void add(Vector3f a, Vector3f b) {
            apply(a, b, Ops.ADDITION);
        }

        public void add(Vector3f b) {
            add(this, b);
        }

        /**
         * Subtracts one vector from another.
         *
         * @param a the first vector
         * @param b the second vector
         */
        public void subtract(Vector3f a, Vector3f b) {
            apply(a, b, Ops.SUBTRACTION);
        }

        public void subtract(Vector3f b) {
            subtract(this, b);
        }

        /**
         * Multiplies one vector by another.
         *
         * @param a the first vector
         * @param b the second vector
         */
        public void multiply(Vector3f a, Vector3f b) {
            apply(a, b, Ops.MULTIPLICATION);
        }

        public void multiply(Vector3f b) {
            multiply(this, b);
        }

        /**
         * Divides one vector by another.
         *
         * @param a the first vector
         * @param b the second vector
         */
        public void divide(Vector3f a, Vector3f b) {
            apply(a, b, Ops.DIVISION);
        }

        public void divide(Vector3f b) {
            divide(this, b);
        }

        /**
         * Checks if two vectors are equal (absolute difference in each components is less than delta).
         *
         * @param b     the second vector
         * @param delta the maximum difference between two numbers for which they are still considered equal.
         * @return the true if vectors are equal or false otherwise.
         */
        public boolean equals(Vector3f b, float delta) {
            return equals(x, b.x, delta)
                    && equals(y, b.y, delta)
                    && equals(z, b.z, delta);
        }

        public boolean isEmpty(float delta) {
            return equals(x, 0, delta)
                    && equals(y, 0, delta)
                    && equals(z, 0, delta);
        }

        private boolean equals(float a, float b, float delta) {
            return Math.abs(a - b) <= delta + delta * Math.abs(b);
        }

        private enum Ops {

            ADDITION {
                @Override
                float apply(float a, float b) {
                    return a + b;
                }
            },
            SUBTRACTION {
                @Override
                float apply(float a, float b) {
                    return a - b;
                }
            },
            MULTIPLICATION {
                @Override
                float apply(float a, float b) {
                    return a * b;
                }
            },
            DIVISION {
                @Override
                float apply(float a, float b) {
                    return a / b;
                }
            };

            abstract float apply(float a, float b);
        }
    }

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
    static final class Matrix {

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
                if (a.remaining() != 16) {
                    throw new IllegalArgumentException("Expected exactly 16 elements!");
                }
                result.clear()
                        .put(a)
                        .flip();
                a.flip();
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
         * @param m      the buffer to store resulting matrix in.
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
            m.clear()
                    .put((float) (2.0 * near / (right - left))).put(0).put(0).put(0)
                    .put(0).put((float) (2.0 * near / (top - bottom))).put(0).put(0)
                    .put((right + left) / (right - left)).put((top + bottom) / (top - bottom)).put(-(far + near) / (far - near)).put(-1)
                    .put(0).put(0).put((float) (-2.0 * far * near / (far - near))).put(0)
                    .flip();
        }

        /**
         * Calculates perspective projection matrix.
         *
         * @param fow   the horizontal field of view (in radians)
         * @param ratio the aspect ratio between width and height of screen
         * @param near  the near z coordinate (should be > 0)
         * @param far   the far z coordinate
         * @param m     the buffer to store resulting matrix in.
         */
        public static void perspective(float fow, float ratio, float near, float far, FloatBuffer m) {
            final float w = (float) (near * Math.tan(0.5 * fow));
            final float h = w / ratio;
            perspective(-w, w, h, -h, near, far, m);
        }

        /**
         * Creates viewing matrix derived from the {@code eye} point, a reference point {@code target} indicating the center of the scene and vector {@code up}
         * Helpful tip: it's better to think of this as a coordinate system rotation.
         *
         * @param target the target point in the scene
         * @param eye    the eye point
         * @param up     the upward vector, must not be parallel to the direction vector {@code dir = target - eye}
         * @param m      the buffer to store resulting matrix in.
         */
        public static void lookAt(Vector3f target, Vector3f eye, Vector3f up, FloatBuffer m) {
            final Vector3f zaxis = new Vector3f();
            zaxis.subtract(eye, target);
            zaxis.normalize();
            final Vector3f xaxis = new Vector3f();
            xaxis.crossProduct(up, zaxis);
            xaxis.normalize();
            final Vector3f yaxis = new Vector3f();
            yaxis.crossProduct(zaxis, xaxis);
            m.clear()
                    .put(xaxis.x).put(xaxis.y).put(xaxis.z).put(0)
                    .put(yaxis.x).put(yaxis.y).put(yaxis.z).put(0)
                    .put(zaxis.x).put(zaxis.y).put(zaxis.z).put(0)
                    .put(0).put(0).put(0).put(1)
                    .flip();
            transpose(m, m);
            translate(m, -eye.x, -eye.y, -eye.z, m);
        }

        /**
         * Adds one matrix to another.
         *
         * @param a      the first matrix
         * @param b      the second matrix
         * @param result the resulting matrix buffer
         */
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
         * @param s      the scalar to multiply by
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
                    tmp[idx(i, j)] = a.get(idx(i, 0)) * b.get(idx(0, j)) +
                            a.get(idx(i, 1)) * b.get(idx(1, j)) +
                            a.get(idx(i, 2)) * b.get(idx(2, j)) +
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
            v.set(
                    a.get(0) * v.x + a.get(4) * v.y + a.get(8) * v.z + a.get(12),
                    a.get(1) * v.x + a.get(5) * v.y + a.get(9) * v.z + a.get(13),
                    a.get(2) * v.x + a.get(6) * v.y + a.get(10) * v.z + a.get(14)
            );
        }

        /**
         * Multiplies this matrix by vector {@code v} and stores result in vector {@code v}. This is a right multiplication
         *
         * @param a the matrix
         * @param v the vector
         */
        public static void multiply(FloatBuffer a, Vector4f v) {
            v.set(
                    a.get(0) * v.x + a.get(4) * v.y + a.get(8) * v.z + a.get(12) * v.w,
                    a.get(1) * v.x + a.get(5) * v.y + a.get(9) * v.z + a.get(13) * v.w,
                    a.get(2) * v.x + a.get(6) * v.y + a.get(10) * v.z + a.get(14) * v.w,
                    a.get(3) * v.x + a.get(7) * v.y + a.get(11) * v.z + a.get(15) * v.w
            );
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
            // r0
            final float m0 = a.get(0) * sx;
            final float m4 = a.get(4) * sy;
            final float m8 = a.get(8) * sz;
            final float m12 = a.get(12);
            // r1
            final float m1 = a.get(1) * sx;
            final float m5 = a.get(5) * sy;
            final float m9 = a.get(9) * sz;
            final float m13 = a.get(13);
            // r2
            final float m2 = a.get(2) * sx;
            final float m6 = a.get(6) * sy;
            final float m10 = a.get(10) * sz;
            final float m14 = a.get(14);
            // r3
            final float m3 = a.get(3) * sx;
            final float m7 = a.get(7) * sy;
            final float m11 = a.get(11) * sz;
            final float m15 = a.get(15);

            result.clear()
                    .put(m0).put(m1).put(m2).put(m3)
                    .put(m4).put(m5).put(m6).put(m7)
                    .put(m8).put(m9).put(m10).put(m11)
                    .put(m12).put(m13).put(m14).put(m15)
                    .flip();
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

        /**
         * Convert to float array
         */
        public static float[] toArray(FloatBuffer m) {
            float[] result = new float[16];
            m.get(result).flip();
            return result;
        }

    }
}