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

import org.lwjgl.system.MemoryUtil
import java.nio.FloatBuffer

/**
 * Column-oriented 4x4 matrix.
 * <pre>
 * A B C D
 * E F G H
 * I J K L
 * M N O P
 *
 * or as indices:
 *
 * 0 4  8 12
 * 1 5  9 13
 * 2 6 10 14
 * 3 7 11 15
</pre> *
 * So A have index 0, E - 1, I - 2, M - 3, etc.
 *
 * @author Yuriy Kiselev (uze@yandex.ru).
 */

@JvmInline
value class Matrix(val m: FloatBuffer = MemoryUtil.memAllocFloat(16)) {

    operator fun get(index: Int): Float = m[index]

    operator fun set(index: Int, value: Float) {
        m.put(index, value)
    }

    fun set(src: Matrix) {
        MatrixOps.copy(src.m, m)
    }

    /**
     * Show matrix by rows
     */
    override fun toString(): String {
        return "{r0(" + m[0] + " " + m[4] + " " + m[8] + " " + m[12] + "), r1(" +
                m[1] + " " + m[5] + " " + m[9] + " " + m[13] + "), r2(" +
                m[2] + " " + m[6] + " " + m[10] + " " + m[14] + "), r3(" +
                m[3] + " " + m[7] + " " + m[11] + " " + m[15] + ")}"
    }

    fun toArray(): FloatArray = MatrixOps.toArray(m)
}