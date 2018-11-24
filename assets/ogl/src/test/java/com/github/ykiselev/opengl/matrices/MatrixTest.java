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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.nio.FloatBuffer;
import java.util.stream.Stream;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public class MatrixTest {

    private final FloatBuffer m = FloatBuffer.allocate(16);

    private void assertMatrixEquals(FloatBuffer actual, float... expected) {
        int i = 0;
        for (float v : expected) {
            final float v1 = actual.get(i);
            assertEquals("Difference at #" + i, v, v1, 0.0001f);
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

    @ParameterizedTest
    @MethodSource("translateArgs")
    public void shouldTranslate(Vector3f v, Vector3f trans, Vector3f expected) {
        Matrix.translate(m, trans.x, trans.y, trans.z, m);
        Matrix.multiply(m, v);
        assertTrue(
                "expected " + expected + " but was " + v,
                expected.equals(v, 0.0001f)
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

        assertArrayEquals("r1 == r2 ?", r1, r2, 0.001f);
        assertArrayEquals("r2 == r3 ?", r2, r3, 0.001f);
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

    /**
     * Note: angles are in degrees!
     */
    public static Stream<Arguments> rotationArgs() {
        return Stream.of(
                Arguments.of(90d, 0d, 0d, new Vector3f(0, 1, 0), new Vector3f(0, 0, 1)),
                Arguments.of(0d, 90d, 0d, new Vector3f(1, 0, 0), new Vector3f(0, 0, -1)),
                Arguments.of(0d, 0d, 90d, new Vector3f(1, 0, 0), new Vector3f(0, 1, 0))
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
        assertTrue(
                expected.equals(v, 0.0001f)
        );
    }

    @Test
    public void shouldCalculateDeterminant() {
        m.clear()
                .put(1).put(3).put(4).put(10)
                .put(2).put(5).put(9).put(11)
                .put(6).put(8).put(12).put(15)
                .put(7).put(13).put(14).put(16)
                .flip();
        assertEquals(-594.0, Matrix.determinant(m), 0.0001d);
    }

    @Test
    public void determinantShouldBeOneForIdentity() {
        assertEquals(1.0, Matrix.determinant(m), 0.0001d);
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
        assertEquals("x", x, v.x, 0.001f);
        assertEquals("y", y, v.y, 0.001f);
        assertEquals("z", z, v.z, 0.001f);
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

    @ParameterizedTest
    @MethodSource("perspectiveArgs")
    public void shouldBePerspective(Vector4f v, Vector4f expected) {
        Matrix.perspective(-1, 1, 1, -1, 1, 10, m);
        Matrix.multiply(m, v);
        assertTrue("expected " + expected + " but was " + v, expected.equals(v, 0.001f));
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

    @ParameterizedTest
    @MethodSource("lookAtArgs")
    public void shouldLookAt(Vector3f origin, Vector3f eye, Vector3f up, Vector3f ax, Vector3f ay, Vector3f az) {
        Matrix.lookAt(origin, eye, up, m);
        final Vector3f v1 = v(1, 0, 0), v2 = v(0, 1, 0), v3 = v(0, 0, 1);
        Matrix.multiply(m, v1);
        Matrix.multiply(m, v2);
        Matrix.multiply(m, v3);
        assertTrue("expected " + ax + " but was " + v1, ax.equals(v1, 0.001f));
        assertTrue("expected " + ay + " but was " + v2, ay.equals(v2, 0.001f));
        assertTrue("expected " + az + " but was " + v3, az.equals(v3, 0.001f));
    }
}