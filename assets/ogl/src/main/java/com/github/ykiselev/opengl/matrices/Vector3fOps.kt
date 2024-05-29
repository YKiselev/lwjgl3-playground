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

@Suppress("NOTHING_TO_INLINE")
abstract class Vector3fOps {

    abstract fun vec3f(): Vector3f

    /**
     * Calculates cross-product vector c = (a × b) that is perpendicular to both a and b, with a direction given by the right-hand rule.
     *
     * @param b the second vector
     */
    infix fun Vector3f.cross(b: Vector3f): Vector3f =
        vec3f().set(
            y * b.z - z * b.y,
            z * b.x - x * b.z,
            x * b.y - y * b.x
        )

    /**
     * Scales the vector.
     */
    operator fun Vector3f.times(scale: Float): Vector3f =
        times(scale.toDouble())

    /**
     * Scales the vector.
     */
    operator fun Vector3f.times(scale: Double): Vector3f =
        vec3f().set(
            (x * scale).toFloat(),
            (y * scale).toFloat(),
            (z * scale).toFloat()
        )

    /**
     * Adds one vector to another.
     *
     * @param b the second vector
     */
    operator fun Vector3f.plus(b: Vector3f): Vector3f =
        apply(this, b, Ops.ADDITION)

    /**
     * Subtracts one vector from another.
     *
     * @param b the second vector
     */
    operator fun Vector3f.minus(b: Vector3f): Vector3f =
        apply(this, b, Ops.SUBTRACTION)


    /**
     * Multiplies one vector by another.
     *
     * @param b the second vector
     */
    operator fun Vector3f.times(b: Vector3f): Vector3f =
        apply(this, b, Ops.MULTIPLICATION)

    /**
     * Divides one vector by another.
     *
     * @param b the second vector
     */
    operator fun Vector3f.div(b: Vector3f): Vector3f =
        apply(this, b, Ops.DIVISION)

    fun Vector3f.normalize(): Vector3f {
        val length = length()
        if (length != 0.0) {
            return times(1.0 / length)
        }
        return vec3f().set(x, y, z)
    }

    private enum class Ops {
        ADDITION {
            override fun apply(a: Float, b: Float): Float {
                return a + b
            }
        },
        SUBTRACTION {
            override fun apply(a: Float, b: Float): Float {
                return a - b
            }
        },
        MULTIPLICATION {
            override fun apply(a: Float, b: Float): Float {
                return a * b
            }
        },
        DIVISION {
            override fun apply(a: Float, b: Float): Float {
                return a / b
            }
        };

        abstract fun apply(a: Float, b: Float): Float
    }

    /**
     * Applies specified operation to this and supplied vectors.
     *
     * @param b the second vector
     */
    private inline fun apply(a: Vector3f, b: Vector3f, op: Ops): Vector3f =
        vec3f().set(
            op.apply(a.x, b.x),
            op.apply(a.y, b.y),
            op.apply(a.z, b.z)
        )
}