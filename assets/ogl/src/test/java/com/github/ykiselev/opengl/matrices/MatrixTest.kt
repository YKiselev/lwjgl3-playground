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

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
class MatrixTest {

    private fun assertMatrixEquals(actual: Matrix, vararg expected: Float) {
        for ((i, v) in expected.withIndex()) {
            val v1 = actual[i]
            assertEquals(v, v1, 0.0001f, "Difference at #$i")
        }
    }

    @Test
    fun shouldBeIdentity() {
        assertMatrixEquals(
            Matrix.identity(),
            1f, 0f, 0f, 0f,
            0f, 1f, 0f, 0f,
            0f, 0f, 1f, 0f,
            0f, 0f, 0f, 1f
        )
    }

    @ParameterizedTest
    @MethodSource("translateArgs")
    fun shouldTranslate(v: Vector3f, trans: Vector3f, expected: Vector3f) {
        val r = Matrix.identity().translate(trans.x, trans.y, trans.z) * v
        assertTrue(
            expected.equals(r, 0.0001f),
            "expected $expected but was $v"
        )
    }

    @Test
    fun addingTranslationShouldBeEqualToMultiplication() {
        val rm = Matrix.rotation(0.0, 0.0, Math.toRadians(45.0))
        val tm = Matrix.identity().translate(1f, 2f, 3f)
        val m = rm * tm

        // add translation and store in separate matrix
        val m2 = rm.translate(1f, 2f, 3f)

        assertArrayEquals(m.toArray(), m2.toArray(), 0.001f)
    }

    @Test
    fun shouldAddTranslationToExistingMatrix() {
        val r = Matrix.rotation(0.0, 0.0, Math.toRadians(45.0)).translate(1f, 2f, 3f)
        val v = r * v(4f, 5f, 6f)
        assertVectorEquals(-1.414f, 8.485f, 9f, v)
    }

    @Test
    fun shouldScale() {
        val m = Matrix(FloatArray(16) {
            (it + 1).toFloat()
        })
        val r = m.scale(2f, 4f, 8f)
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
        val m = Matrix(FloatArray(16) {
            (it + 1).toFloat()
        })
        val r = m.transpose()
        assertMatrixEquals(
            r,
            1f, 5f, 9f, 13f,
            2f, 6f, 10f, 14f,
            3f, 7f, 11f, 15f,
            4f, 8f, 12f, 16f
        )
    }

    @Test
    fun shouldAdd() {
        val a = FloatArray(16)
        val b = FloatArray(16)
        for (i in 1..16) {
            a[i - 1] = i.toFloat()
            b[i - 1] = (17 - i).toFloat()
        }
        val ma = Matrix(a)
        val mb = Matrix(b)
        val r = ma + mb
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
        val m = Matrix(FloatArray(16) {
            (it + 1).toFloat()
        })
        val r = m * 2f
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
        val m = Matrix(
            floatArrayOf(
                1f, 4f, 7f, 0f,
                2f, 5f, 8f, 0f,
                3f, 6f, 9f, 0f,
                0f, 0f, 0f, 1f
            )
        )
        val v = Vector3f(1f, 2f, 3f)
        val r = m * v
        assertVectorEquals(14f, 32f, 50f, r)
    }

    @Test
    fun shouldMultiply() {
        val m = Matrix(
            floatArrayOf(
                1f, 5f, 9f, 0f,
                2f, 6f, 10f, 0f,
                3f, 7f, 11f, 0f,
                0f, 0f, 0f, 1f
            )
        )
        val r = m * Matrix(
            floatArrayOf(
                1f, 0f, 0f, 1f,
                0f, 1f, 0f, 2f,
                0f, 0f, 1f, 3f,
                0f, 0f, 0f, 1f
            )
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
        val m = Matrix.rotation(
            Math.toRadians(ax),
            Math.toRadians(ay),
            Math.toRadians(az)
        )
        assertEquals(expected, m * v)
    }

    @Test
    fun shouldCalculateDeterminant() {
        val m = Matrix(
            floatArrayOf(
                1f, 3f, 4f, 10f,
                2f, 5f, 9f, 11f,
                6f, 8f, 12f, 15f,
                7f, 13f, 14f, 16f
            )
        )
        assertEquals(-594.0, m.determinant(), 0.0001)
    }

    @Test
    fun determinantShouldBeOneForIdentity() {
        assertEquals(1.0, Matrix.identity().determinant(), 0.0001)
    }

    @Test
    fun shouldInverse() {
        val m = Matrix(
            floatArrayOf(
                1f, 2f, 4f, 6f,
                3f, 1f, 7f, 10f,
                5f, 8f, 1f, 12f,
                9f, 11f, 13f, 1f
            )
        )
        val r = m.inverse()
        assertMatrixEquals(
            r,
            -1643f / 2369, 744f / 2369, 194f / 2369, 90f / 2369,
            816f / 2369, -593f / 2369, 81f / 2369, 62f / 2369,
            439f / 2369, -20f / 2369, -209f / 2369, 74f / 2369,
            104f / 2369, 87f / 2369, 80f / 2369, -85f / 2369
        )
    }

    private fun assertVectorEquals(x: Float, y: Float, z: Float, v: Vector3f) {
        assertEquals(x, v.x, 0.001f)
        assertEquals(y, v.y, 0.001f)
        assertEquals(z, v.z, 0.001f)
    }

    @Test
    fun shouldBeOrthographic() {
        val m = Matrix.orthographic(0f, 100f, 200f, 0f, -1f, 1f)
        val v = Vector3f(0f, 0f, 0f)
        assertVectorEquals(-1f, -1f, 0f, m * v)

        v.set(50f, 100f, 0f)
        assertVectorEquals(0f, 0f, 0f, m * v)

        v.set(100f, 200f, 0f)
        assertVectorEquals(1f, 1f, 0f, m * v)
    }

    @ParameterizedTest
    @MethodSource("perspectiveArgs")
    fun shouldBePerspective(v: Vector4f, expected: Vector4f) {
        val m = Matrix.perspective(-1f, 1f, 1f, -1f, 1f, 10f)
        assertTrue(expected.equals(m * v, 0.001f), "expected $expected but was $v")
    }

    @ParameterizedTest
    @MethodSource("lookAtArgs")
    fun shouldLookAt(origin: Vector3f, eye: Vector3f, up: Vector3f, ax: Vector3f, ay: Vector3f, az: Vector3f) {
        val m = Matrix.lookAt(origin, eye, up)
        val v1 = v(1f, 0f, 0f)
        val v2 = v(0f, 1f, 0f)
        val v3 = v(0f, 0f, 1f)
        assertTrue(ax.equals(m * v1, 0.001f), "expected $ax but was $v1")
        assertTrue(ay.equals(m * v2, 0.001f), "expected $ay but was $v2")
        assertTrue(az.equals(m * v3, 0.001f), "expected $az but was $v3")
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
            assertEquals(expected.x, actual.x, 0.001f, "$expected != $actual")
            assertEquals(expected.y, actual.y, 0.001f, "$expected != $actual")
            assertEquals(expected.z, actual.z, 0.001f, "$expected != $actual")
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