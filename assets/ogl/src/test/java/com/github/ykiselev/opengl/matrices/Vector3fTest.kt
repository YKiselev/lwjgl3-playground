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

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
class Vector3fTest : Vector3fOps() {

    private fun assertVectorEquals(x: Float, y: Float, z: Float, v: Vector3f) {
        Assertions.assertEquals(x, v.x, 0.001f, "x")
        Assertions.assertEquals(y, v.y, 0.001f, "y")
        Assertions.assertEquals(z, v.z, 0.001f, "z")
    }

    @Test
    fun set() {
        val v = Vector3f(1f, 2f, 3f)
        v.set(4f, 5f, 6f)
        assertVectorEquals(4f, 5f, 6f, v)
    }

    @Test
    fun normalize() {
        assertVectorEquals(0.577f, 0.577f, 0.577f, Vector3f(1f, 1f, 1f).normalize())
    }

    @Test
    fun squareLength() {
        Assertions.assertEquals(
            3.0,
            Vector3f(1f, 1f, 1f)
                .squareLength(),
            0.0001
        )
    }

    @Test
    fun length() {
        Assertions.assertEquals(
            1.732,
            Vector3f(1f, 1f, 1f)
                .length(),
            0.001
        )
    }

    @Test
    fun dotProduct() {
        Assertions.assertEquals(
            9.0,
            Vector3f(1f, 1f, 1f)
                .dot(Vector3f(2f, 3f, 4f)),
            0.001
        )
    }

    @Test
    fun crossProduct() {
        var v = Vector3f(1f, 0f, 0f) cross Vector3f(0f, 1f, 0f)
        assertVectorEquals(0f, 0f, 1f, v)

        v = Vector3f(-1f, 0f, 0f) cross Vector3f(0f, -1f, 0f)
        assertVectorEquals(0f, 0f, 1f, v)

        v = Vector3f(-1f, 0f, 0f) cross Vector3f(0f, 1f, 0f)
        assertVectorEquals(0f, 0f, -1f, v)

        v.set(1f, 0f, 0f)
        v = v cross Vector3f(0f, 1f, 0f)
        assertVectorEquals(0f, 0f, 1f, v)
    }

    @Test
    fun scale() {
        val v = Vector3f(1f, 2f, 3f) * 3f
        assertVectorEquals(3f, 6f, 9f, v)
    }

    @Test
    fun addTwoArgs() {
        val v = Vector3f(1f, 2f, 3f) + Vector3f(4f, 5f, 6f)
        assertVectorEquals(5f, 7f, 9f, v)
    }

    @Test
    fun add() {
        val v = Vector3f(1f, 2f, 3f) + Vector3f(4f, 5f, 6f)
        assertVectorEquals(5f, 7f, 9f, v)
    }

    @Test
    fun subtractTwoArgs() {
        val v = Vector3f(4f, 7f, 11f) - Vector3f(1f, 2f, 3f)
        assertVectorEquals(3f, 5f, 8f, v)
    }

    @Test
    fun subtract() {
        val v = Vector3f(4f, 7f, 11f) - Vector3f(1f, 2f, 3f)
        assertVectorEquals(3f, 5f, 8f, v)
    }

    @Test
    fun multiplyTwoArgs() {
        val v = Vector3f(1f, 2f, 3f) * Vector3f(4f, 5f, 6f)
        assertVectorEquals(4f, 10f, 18f, v)
    }

    @Test
    fun multiply() {
        val v = Vector3f(1f, 2f, 3f) * Vector3f(4f, 5f, 6f)
        assertVectorEquals(4f, 10f, 18f, v)
    }

    @Test
    fun divideTwoArgs() {
        val v = Vector3f(2f, 6f, 9f) / Vector3f(1f, 2f, 3f)
        assertVectorEquals(2f, 3f, 3f, v)
    }

    @Test
    fun divide() {
        val v = Vector3f(2f, 6f, 9f) / Vector3f(1f, 2f, 3f)
        assertVectorEquals(2f, 3f, 3f, v)
    }

    @Test
    fun equals() {
        val v = Vector3f(1.0001f, 2.0001f, 3.0001f)
        assertTrue(
            v.equals(
                Vector3f(1.0002f, 2.0002f, 3.0000f),
                0.0001f
            )
        )
        assertFalse(
            v.equals(
                Vector3f(1.0003f, 2.0002f, 3.0000f),
                0.0001f
            )
        )
    }

    @Test
    fun isEmpty() {
        assertTrue(
            Vector3f(0.0001f, 0.0001f, 0.0001f)
                .isEmpty(0.0001f)
        )
    }

    override fun vec3f(): Vector3f = Vector3f()
}