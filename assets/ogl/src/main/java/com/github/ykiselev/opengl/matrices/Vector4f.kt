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

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
data class Vector4f(
    @JvmField
    var x: Float = 0f,
    @JvmField
    var y: Float = 0f,
    @JvmField
    var z: Float = 0f,
    @JvmField
    var w: Float = 0f
) {
    fun set(x: Float, y: Float, z: Float, w: Float) {
        this.x = x
        this.y = y
        this.z = z
        this.w = w
    }

    /**
     * Checks if two vectors are equal (absolute difference in each component is less than delta).
     *
     * @param b     the second vector
     * @param delta the maximum difference between two numbers for which they are still considered equal.
     * @return the true if vectors are equal or false otherwise.
     */
    fun equals(b: Vector4f, delta: Float): Boolean {
        return (equals(x, b.x, delta)
                && equals(y, b.y, delta)
                && equals(z, b.z, delta)
                && equals(w, b.w, delta))
    }

    private fun equals(a: Float, b: Float, delta: Float): Boolean {
        return abs((a - b).toDouble()) <= delta + delta * abs(b.toDouble())
    }
}
