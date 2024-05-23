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

data class Matrix(val m: FloatBuffer = MemoryUtil.memAllocFloat(16)) {

    /**
     * Show matrix by rows
     */
    override fun toString(): String {
        return "{r0(" + m[0] + " " + m[4] + " " + m[8] + " " + m[12] + "), r1(" +
                m[1] + " " + m[5] + " " + m[9] + " " + m[13] + "), r2(" +
                m[2] + " " + m[6] + " " + m[10] + " " + m[14] + "), r3(" +
                m[3] + " " + m[7] + " " + m[11] + " " + m[15] + ")}"
    }

    /**
     * Convert to float array
     */
    fun toArray(): FloatArray {
        val result = FloatArray(16)
        m.get(result).flip()
        return result
    }
}