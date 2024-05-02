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

import kotlin.math.abs
import kotlin.math.sqrt

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
data class Vector3f(
    @JvmField
    var x: Float = 0f,
    @JvmField
    var y: Float = 0f,
    @JvmField
    var z: Float = 0f
) {

    fun set(x: Float, y: Float, z: Float): Vector3f {
        this.x = x
        this.y = y
        this.z = z
        return this
    }

    fun set(src: Vector3f): Vector3f {
        this.x = src.x
        this.y = src.y
        this.z = src.z
        return this
    }

    /**
     * Normalizes vector.
     *
     * @return the normalizing coefficient (1/length) used
     */
    fun normalize(): Double {
        val length = length()
        if (length != 0.0) {
            val ool = 1.0 / length
            x *= ool.toFloat()
            y *= ool.toFloat()
            z *= ool.toFloat()
            return ool
        }
        return 1.0
    }

    fun normalized(): Vector3f {
        normalize()
        return this
    }

    /**
     * Calculates squared vector length.
     *
     * @return squared length of vector
     */
    fun squareLength(): Double {
        return (x * x + y * y + z * z).toDouble()
    }

    /**
     * Calculates vector length.
     *
     * @return vector length
     */
    fun length(): Double {
        return sqrt(squareLength())
    }

    /**
     * Calculates Dot product between this and supplied vector.
     *
     * @param b the second vector
     */
    infix fun dot(b: Vector3f): Double =
        x.toDouble() * b.x + y * b.y + z * b.z

    /**
     * Checks if two vectors are equal (absolute difference in each component is less than delta).
     *
     * @param b     the second vector
     * @param delta the maximum difference between two numbers for which they are still considered equal.
     * @return the true if vectors are equal or false otherwise.
     */
    fun equals(b: Vector3f, delta: Float): Boolean =
        (equals(x, b.x, delta)
                && equals(y, b.y, delta)
                && equals(z, b.z, delta))

    fun isEmpty(delta: Float): Boolean =
        (equals(x, 0f, delta)
                && equals(y, 0f, delta)
                && equals(z, 0f, delta))

    private fun equals(a: Float, b: Float, delta: Float): Boolean =
        abs((a - b).toDouble()) <= delta + delta * abs(b.toDouble())
}
