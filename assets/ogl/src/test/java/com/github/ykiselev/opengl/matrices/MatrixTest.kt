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
package com.github.ykiselev.opengl.matrices

import com.github.ykiselev.opengl.pools.math
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.nio.FloatBuffer
import java.util.stream.Stream

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
class MatrixTest {

    private val m: FloatBuffer = FloatBuffer.allocate(16)

    private val r: FloatBuffer = FloatBuffer.allocate(16)

    private fun assertMatrixEquals(actual: FloatBuffer, vararg expected: Float) {
        for ((i, v) in expected.withIndex()) {
            val v1 = actual[i]
            Assertions.assertEquals(v, v1, 0.0001f, "Difference at #$i")
        }
    }

    @BeforeEach
    fun setUp() {
        identity(m)
    }

    @Test
    fun shouldBeIdentity() {
        assertMatrixEquals(
            m,
            1f, 0f, 0f, 0f,
            0f, 1f, 0f, 0f,
            0f, 0f, 1f, 0f,
            0f, 0f, 0f, 1f
        )
    }

    @ParameterizedTest
    @MethodSource("translateArgs")
    fun shouldTranslate(v: Vector3f, trans: Vector3f, expected: Vector3f) {
        translate(m, trans.x, trans.y, trans.z, r)
        multiply(r, v, v)
        Assertions.assertTrue(
            expected.equals(v, 0.0001f),
            "expected $expected but was $v"
        )
    }

    @Test
    fun addingTranslationShouldBeEqualToMultiplication() {
        val rm = FloatBuffer.allocate(16)
        rotation(0.0, 0.0, Math.toRadians(45.0), rm)
        val tm = FloatBuffer.allocate(16)
        identity(tm)
        translate(tm, 1f, 2f, 3f, tm)

        // multiply rm * tm
        multiply(rm, tm, m)
        val r1 = toArray(m)

        // add translation and store in separate matrix
        translate(rm, 1f, 2f, 3f, m)
        val r2 = toArray(m)

        // add translation in-place
        translate(rm, 1f, 2f, 3f, rm)
        val r3 = toArray(rm)

        Assertions.assertArrayEquals(r1, r2, 0.001f)
        Assertions.assertArrayEquals(r2, r3, 0.001f)
    }

    @Test
    fun shouldAddTranslationToExistingMatrix() {
        rotation(0.0, 0.0, Math.toRadians(45.0), m)
        translate(m, 1f, 2f, 3f, r)
        val v = v(4f, 5f, 6f)
        multiply(r, v, v)
        assertVectorEquals(-1.414f, 8.485f, 9f, v)
    }

    @Test
    fun shouldScale() {
        m.clear()
            .put(1f).put(2f).put(3f).put(4f)
            .put(5f).put(6f).put(7f).put(8f)
            .put(9f).put(10f).put(11f).put(12f)
            .put(13f).put(14f).put(15f).put(16f)
            .flip()
        scale(m, 2f, 4f, 8f, r)
        assertMatrixEquals(
            r,
            2f, 4f, 6f, 8f,
            20f, 24f, 28f, 32f,
            72f, 80f, 88f, 96f,
            13f, 14f, 15f, 16f
        )
    }

    @Test
    fun shouldTranspose() {
        m.clear()
            .put(1f).put(2f).put(3f).put(4f)
            .put(5f).put(6f).put(7f).put(8f)
            .put(9f).put(10f).put(11f).put(12f)
            .put(13f).put(14f).put(15f).put(16f)
            .flip()
        transpose(m, r)
        assertMatrixEquals(
            r,
            1f, 5f, 9f, 13f,
            2f, 6f, 10f, 14f,
            3f, 7f, 11f, 15f,
            4f, 8f, 12f, 16f
        )
    }

    @Test
    fun shouldCopy() {
        m.clear()
            .put(1f).put(2f).put(3f).put(4f)
            .put(5f).put(6f).put(7f).put(8f)
            .put(9f).put(10f).put(11f).put(12f)
            .put(13f).put(14f).put(15f).put(16f)
            .flip()
        val b = FloatBuffer.allocate(16)
        copy(m, b)
        assertMatrixEquals(
            b,
            1f, 2f, 3f, 4f,
            5f, 6f, 7f, 8f,
            9f, 10f, 11f, 12f,
            13f, 14f, 15f, 16f
        )
    }

    @Test
    fun shouldAdd() {
        val a = FloatBuffer.allocate(16)
        val b = FloatBuffer.allocate(16)
        for (i in 1..16) {
            a.put(i.toFloat())
            b.put((17 - i).toFloat())
        }
        a.flip()
        b.flip()
        add(a, b, r)
        assertMatrixEquals(
            r,
            17f, 17f, 17f, 17f,
            17f, 17f, 17f, 17f,
            17f, 17f, 17f, 17f,
            17f, 17f, 17f, 17f
        )
    }

    @Test
    fun shouldMultiplyByScalar() {
        m.clear()
            .put(1f).put(2f).put(3f).put(4f)
            .put(5f).put(6f).put(7f).put(8f)
            .put(9f).put(10f).put(11f).put(12f)
            .put(13f).put(14f).put(15f).put(16f)
            .flip()
        multiply(m, 2f, r)
        assertMatrixEquals(
            r,
            2f, 4f, 6f, 8f,
            10f, 12f, 14f, 16f,
            18f, 20f, 22f, 24f,
            26f, 28f, 30f, 32f
        )
    }

    @Test
    fun shouldMultiplyByVector() {
        m.clear()
            .put(1f).put(4f).put(7f).put(0f)
            .put(2f).put(5f).put(8f).put(0f)
            .put(3f).put(6f).put(9f).put(0f)
            .put(0f).put(0f).put(0f).put(1f)
            .flip()
        val v = Vector3f(1f, 2f, 3f)
        multiply(m, v, v)
        assertVectorEquals(14f, 32f, 50f, v)
    }

    @Test
    fun shouldMultiply() {
        m.clear()
            .put(1f).put(5f).put(9f).put(0f)
            .put(2f).put(6f).put(10f).put(0f)
            .put(3f).put(7f).put(11f).put(0f)
            .put(0f).put(0f).put(0f).put(1f)
            .flip()
        multiply(
            m,
            FloatBuffer.wrap(
                floatArrayOf(
                    1f, 0f, 0f, 1f,
                    0f, 1f, 0f, 2f,
                    0f, 0f, 1f, 3f,
                    0f, 0f, 0f, 1f
                )
            ),
            r
        )
        assertMatrixEquals(
            r,
            1f, 5f, 9f, 1f,
            2f, 6f, 10f, 2f,
            3f, 7f, 11f, 3f,
            0f, 0f, 0f, 1f
        )
    }

    @ParameterizedTest
    @MethodSource("rotationArgs")
    fun shouldRotate(ax: Double, ay: Double, az: Double, v: Vector3f, expected: Vector3f) {
        rotation(
            Math.toRadians(ax),
            Math.toRadians(ay),
            Math.toRadians(az),
            m
        )
        multiply(m, v, v)
        assertEquals(expected, v)
    }

    @Test
    fun shouldCalculateDeterminant() {
        m.clear()
            .put(1f).put(3f).put(4f).put(10f)
            .put(2f).put(5f).put(9f).put(11f)
            .put(6f).put(8f).put(12f).put(15f)
            .put(7f).put(13f).put(14f).put(16f)
            .flip()
        Assertions.assertEquals(-594.0, determinant(m), 0.0001)
    }

    @Test
    fun determinantShouldBeOneForIdentity() {
        Assertions.assertEquals(1.0, determinant(m), 0.0001)
    }

    @Test
    fun shouldInverse() {
        m.clear()
            .put(1f).put(2f).put(4f).put(6f)
            .put(3f).put(1f).put(7f).put(10f)
            .put(5f).put(8f).put(1f).put(12f)
            .put(9f).put(11f).put(13f).put(1f)
            .flip()
        inverse(m, r)
        assertMatrixEquals(
            r,
            -1643f / 2369, 744f / 2369, 194f / 2369, 90f / 2369,
            816f / 2369, -593f / 2369, 81f / 2369, 62f / 2369,
            439f / 2369, -20f / 2369, -209f / 2369, 74f / 2369,
            104f / 2369, 87f / 2369, 80f / 2369, -85f / 2369
        )
    }

    private fun assertVectorEquals(x: Float, y: Float, z: Float, v: Vector3f) {
        Assertions.assertEquals(x, v.x, 0.001f)
        Assertions.assertEquals(y, v.y, 0.001f)
        Assertions.assertEquals(z, v.z, 0.001f)
    }

    @Test
    fun shouldBeOrthographic() {
        orthographic(0f, 100f, 200f, 0f, -1f, 1f, m)
        val v = Vector3f(0f, 0f, 0f)
        multiply(m, v, v)
        assertVectorEquals(-1f, -1f, 0f, v)

        v.set(50f, 100f, 0f)
        multiply(m, v, v)
        assertVectorEquals(0f, 0f, 0f, v)

        v.set(100f, 200f, 0f)
        multiply(m, v, v)
        assertVectorEquals(1f, 1f, 0f, v)
    }

    @ParameterizedTest
    @MethodSource("perspectiveArgs")
    fun shouldBePerspective(v: Vector4f, expected: Vector4f) {
        perspective(-1f, 1f, 1f, -1f, 1f, 10f, m)
        multiply(m, v, v)
        Assertions.assertTrue(expected.equals(v, 0.001f), "expected $expected but was $v")
    }

    @ParameterizedTest
    @MethodSource("lookAtArgs")
    fun shouldLookAt(origin: Vector3f, eye: Vector3f, up: Vector3f, ax: Vector3f, ay: Vector3f, az: Vector3f) {
        math {
            lookAt(origin, eye, up, m)
        }
        val v1 = v(1f, 0f, 0f)
        val v2 = v(0f, 1f, 0f)
        val v3 = v(0f, 0f, 1f)
        multiply(m, v1, v1)
        multiply(m, v2, v2)
        multiply(m, v3, v3)
        Assertions.assertTrue(ax.equals(v1, 0.001f), "expected $ax but was $v1")
        Assertions.assertTrue(ay.equals(v2, 0.001f), "expected $ay but was $v2")
        Assertions.assertTrue(az.equals(v3, 0.001f), "expected $az but was $v3")
    }

    companion object {

        @JvmStatic
        fun translateArgs(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(v(1f, 2f, 3f), v(0f, 0f, 0f), v(1f, 2f, 3f)),
                Arguments.of(v(1f, 2f, 3f), v(1f, 0f, 0f), v(2f, 2f, 3f)),
                Arguments.of(v(1f, 2f, 3f), v(0f, 1f, 0f), v(1f, 3f, 3f)),
                Arguments.of(v(1f, 2f, 3f), v(0f, 0f, 1f), v(1f, 2f, 4f)),
                Arguments.of(v(1f, 2f, 3f), v(-1f, 0f, 0f), v(0f, 2f, 3f)),
                Arguments.of(v(1f, 2f, 3f), v(0f, -1f, 0f), v(1f, 1f, 3f)),
                Arguments.of(v(1f, 2f, 3f), v(0f, 0f, -1f), v(1f, 2f, 2f)),
                Arguments.of(v(1f, 2f, 3f), v(1f, 1f, 1f), v(2f, 3f, 4f))
            )
        }

        /**
         * Note: angles are in degrees!
         */
        @JvmStatic
        fun rotationArgs(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(90.0, 0.0, 0.0, Vector3f(0f, 1f, 0f), Vector3f(0f, 0f, 1f)),
                Arguments.of(0.0, 90.0, 0.0, Vector3f(1f, 0f, 0f), Vector3f(0f, 0f, -1f)),
                Arguments.of(0.0, 0.0, 90.0, Vector3f(1f, 0f, 0f), Vector3f(0f, 1f, 0f)),
                Arguments.of(45.0, 0.0, 0.0, Vector3f(0f, 1f, 0f), Vector3f(0f, 0.707f, 0.707f)),
                Arguments.of(0.0, 45.0, 0.0, Vector3f(1f, 0f, 0f), Vector3f(0.707f, 0f, -0.707f)),
                Arguments.of(0.0, 0.0, 45.0, Vector3f(1f, 0f, 0f), Vector3f(0.707f, 0.707f, 0f))
            )
        }

        private fun assertEquals(expected: Vector3f, actual: Vector3f) {
            Assertions.assertEquals(expected.x, actual.x, 0.001f, "$expected != $actual")
            Assertions.assertEquals(expected.y, actual.y, 0.001f, "$expected != $actual")
            Assertions.assertEquals(expected.z, actual.z, 0.001f, "$expected != $actual")
        }

        @JvmStatic
        fun perspectiveArgs(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(v(0f, 0f, 0f, 1f), v(0f, 0f, -2.222f, 0f)),
                Arguments.of(v(0f, 0f, 1f, 1f), v(0f, 0f, -3.444f, -1f)),
                Arguments.of(v(0f, 0f, 5f, 1f), v(0f, 0f, -8.333f, -5f)),
                Arguments.of(v(0f, 0f, 10f, 1f), v(0f, 0f, -14.444f, -10f)),
                Arguments.of(v(0f, 0f, 15f, 1f), v(0f, 0f, -20.555f, -15f)),
                Arguments.of(v(1f, 1f, 1f, 1f), v(1f, 1f, -3.444f, -1f)),
                Arguments.of(v(1f, 1f, 2f, 1f), v(1f, 1f, -4.666f, -2f))
            )
        }

        private fun v(x: Float, y: Float, z: Float): Vector3f {
            return Vector3f(x, y, z)
        }

        private fun v(x: Float, y: Float, z: Float, w: Float): Vector4f {
            return Vector4f(x, y, z, w)
        }

        @JvmStatic
        fun lookAtArgs(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(
                    v(1f, 0f, 0f),
                    v(0f, 0f, 0f),
                    v(0f, 0f, 1f),
                    v(0f, 0f, -1f),
                    v(-1f, 0f, 0f),
                    v(0f, 1f, 0f)
                ),
                Arguments.of(
                    v(1f, 1f, 0f),
                    v(0f, 0f, 0f),
                    v(0f, 0f, 1f),
                    v(0.707f, 0f, -0.707f),
                    v(-0.707f, 0f, -0.707f),
                    v(0f, 1f, 0f)
                ),
                Arguments.of(v(0f, 1f, 0f), v(0f, 0f, 0f), v(0f, 0f, 1f), v(1f, 0f, 0f), v(0f, 0f, -1f), v(0f, 1f, 0f)),
                Arguments.of(
                    v(0f, 0f, 1f),
                    v(0f, 0f, 0f),
                    v(-1f, 0f, 0f),
                    v(0f, -1f, 0f),
                    v(-1f, 0f, 0f),
                    v(0f, 0f, -1f)
                ),
                Arguments.of(
                    v(0f, 0f, 0f),
                    v(-1f, 0f, 0f),
                    v(0f, 0f, 1f),
                    v(0f, 0f, -2f),
                    v(-1f, 0f, -1f),
                    v(0f, 1f, -1f)
                ),
                Arguments.of(
                    v(0f, 0f, 0f),
                    v(-1f, -1f, 0f),
                    v(0f, 0f, 1f),
                    v(0.707f, 0f, -2.121f),
                    v(-0.707f, 0f, -2.121f),
                    v(0f, 1f, -1.414f)
                ),
                Arguments.of(
                    v(0f, 0f, 0f),
                    v(0f, -1f, 0f),
                    v(0f, 0f, 1f),
                    v(1f, 0f, -1f),
                    v(0f, 0f, -2f),
                    v(0f, 1f, -1f)
                )
            )
        }
    }
}