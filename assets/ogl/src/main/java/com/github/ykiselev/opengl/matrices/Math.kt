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
import kotlin.math.*


class MathArena : MatrixOps, Vector3fOps {

    private val vec3fs = mutableListOf<Vector3f>()

    private val vec4fs = mutableListOf<Vector4f>()

    private val matrices = mutableListOf<Matrix>()

    @PublishedApi
    internal var vec3Idx = 0

    @PublishedApi
    internal var vec4Idx = 0

    @PublishedApi
    internal var matIdx = 0

    override fun vec3f(): Vector3f {
        if (vec3Idx >= vec3fs.size) {
            vec3fs.add(Vector3f())
        }
        return vec3fs[vec3Idx++]
    }

    override fun matrix(): Matrix {
        if (matIdx >= matrices.size) {
            matrices.add(Matrix(MemoryUtil.memAllocFloat(16)))
        }
        return matrices[matIdx++]
    }

    override fun vec4f(): Vector4f {
        if (vec4Idx >= vec4fs.size) {
            vec4fs.add(Vector4f())
        }
        return vec4fs[vec4Idx++]
    }

    inline operator fun invoke(block: MathArena.() -> Unit) {
        val oldVec3Idx = vec3Idx
        val oldVec4Idx = vec4Idx
        val oldMatIdx = matIdx
        try {
            block()
        } finally {
            vec3Idx = oldVec3Idx
            vec4Idx = oldVec4Idx
            matIdx = oldMatIdx
        }
    }
}

object TlsMathArena {

    private val TLS: ThreadLocal<MathArena> = ThreadLocal.withInitial {
        MathArena()
    }

    fun get(): MathArena = TLS.get()
}


inline fun math(block: MathArena.() -> Unit) {
    TlsMathArena.get()(block)
}

/**
 * Initializes provided buffer with identity matrix.
 *
 * @param m the buffer to store resulting matrix in.
 */
fun identity(m: FloatBuffer) {
    m.put(0, 1f).put(1, 0f).put(2, 0f).put(3, 0f)
        .put(4, 0f).put(5, 1f).put(6, 0f).put(7, 0f)
        .put(8, 0f).put(9, 0f).put(10, 1f).put(11, 0f)
        .put(12, 0f).put(13, 0f).put(14, 0f).put(15, 1f)
}

/**
 * Copies matrix `a` to `result`.
 *
 * @param a      the original matrix
 * @param result the buffer to store copy
 */
fun copy(a: FloatBuffer, result: FloatBuffer) {
    if (result !== a) {
        for (i in 0..15) {
            result.put(i, a[i])
        }
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
fun orthographic(left: Float, right: Float, top: Float, bottom: Float, near: Float, far: Float, m: FloatBuffer) {
    m.clear()
        .put(2 / (right - left)).put(0f).put(0f).put(0f)
        .put(0f).put(2 / (top - bottom)).put(0f).put(0f)
        .put(0f).put(0f).put(-2 / (far - near)).put(0f)
        .put(-(right + left) / (right - left)).put(-(top + bottom) / (top - bottom))
        .put(-(far + near) / (far - near)).put(1f)
        .flip()
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
fun perspective(left: Float, right: Float, top: Float, bottom: Float, near: Float, far: Float, m: FloatBuffer) {
    m.clear()
        .put((2.0 * near / (right - left)).toFloat()).put(0f).put(0f).put(0f)
        .put(0f).put((2.0 * near / (top - bottom)).toFloat()).put(0f).put(0f)
        .put((right + left) / (right - left)).put((top + bottom) / (top - bottom)).put(-(far + near) / (far - near))
        .put(-1f)
        .put(0f).put(0f).put((-2.0 * far * near / (far - near)).toFloat()).put(0f)
        .flip()
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
fun perspective(fow: Float, ratio: Float, near: Float, far: Float, m: FloatBuffer) {
    val w = (near * tan(0.5 * fow)).toFloat()
    val h = w / ratio
    perspective(-w, w, h, -h, near, far, m)
}


/**
 * Adds one matrix to another.
 *
 * @param a      the first matrix
 * @param b      the second matrix
 * @param result the resulting matrix buffer
 */
fun add(a: FloatBuffer, b: FloatBuffer, result: FloatBuffer) {
    val m0 = a[0] + b[0]
    val m1 = a[1] + b[1]
    val m2 = a[2] + b[2]
    val m3 = a[3] + b[3]

    val m4 = a[4] + b[4]
    val m5 = a[5] + b[5]
    val m6 = a[6] + b[6]
    val m7 = a[7] + b[7]

    val m8 = a[8] + b[8]
    val m9 = a[9] + b[9]
    val m10 = a[10] + b[10]
    val m11 = a[11] + b[11]

    val m12 = a[12] + b[12]
    val m13 = a[13] + b[13]
    val m14 = a[14] + b[14]
    val m15 = a[15] + b[15]

    result.clear()
        .put(m0).put(m1).put(m2).put(m3)
        .put(m4).put(m5).put(m6).put(m7)
        .put(m8).put(m9).put(m10).put(m11)
        .put(m12).put(m13).put(m14).put(m15)
        .flip()
}


/**
 * Multiplies matrix by a scalar value.
 *
 * @param a      the source matrix
 * @param s      the scalar to multiply by
 * @param result the buffer to store result in
 */
fun multiply(a: FloatBuffer, s: Float, result: FloatBuffer) {
    val m0 = s * a[0]
    val m1 = s * a[1]
    val m2 = s * a[2]
    val m3 = s * a[3]

    val m4 = s * a[4]
    val m5 = s * a[5]
    val m6 = s * a[6]
    val m7 = s * a[7]

    val m8 = s * a[8]
    val m9 = s * a[9]
    val m10 = s * a[10]
    val m11 = s * a[11]

    val m12 = s * a[12]
    val m13 = s * a[13]
    val m14 = s * a[14]
    val m15 = s * a[15]

    result.clear()
        .put(m0).put(m1).put(m2).put(m3)
        .put(m4).put(m5).put(m6).put(m7)
        .put(m8).put(m9).put(m10).put(m11)
        .put(m12).put(m13).put(m14).put(m15)
        .flip()
}

/**
 * Calculates index of cell in column-major matrix array from a pair of row and column indices.
 *
 * @param row the matrix row (0-3)
 * @param col the matrix column (0-3)
 * @return the index in matrix linear buffer.
 */
private fun idx(row: Int, col: Int): Int {
    return row + 4 * col
}

/**
 * This method is kept for debugging.
 */
fun multiplyUsingLoops(a: FloatBuffer, b: FloatBuffer, result: FloatBuffer) {
    val tmp = FloatArray(16)
    for (i in 0..3) {
        for (j in 0..3) {
            tmp[idx(i, j)] = a[idx(i, 0)] * b[idx(
                0,
                j
            )] + a[idx(i, 1)] * b[idx(
                1,
                j
            )] + a[idx(i, 2)] * b[idx(2, j)] + a[idx(i, 3)] * b[idx(3, j)]
        }
    }
    result.clear()
        .put(tmp)
        .flip()
}

/**
 * Each row of first matrix is multiplied by the column of second (component-wise) and sum of results is stored in `result`'s cell.
 *
 * @param a      the first matrix
 * @param b      the second matrix
 * @param result the matrix to store result in
 */
fun multiply(a: FloatBuffer, b: FloatBuffer, result: FloatBuffer) {
    // r0
    val m0 = a[0] * b[0] + a[4] * b[1] + a[8] * b[2] + a[12] * b[3]
    val m4 = a[0] * b[4] + a[4] * b[5] + a[8] * b[6] + a[12] * b[7]
    val m8 = a[0] * b[8] + a[4] * b[9] + a[8] * b[10] + a[12] * b[11]
    val m12 = a[0] * b[12] + a[4] * b[13] + a[8] * b[14] + a[12] * b[15]
    // r1
    val m1 = a[1] * b[0] + a[5] * b[1] + a[9] * b[2] + a[13] * b[3]
    val m5 = a[1] * b[4] + a[5] * b[5] + a[9] * b[6] + a[13] * b[7]
    val m9 = a[1] * b[8] + a[5] * b[9] + a[9] * b[10] + a[13] * b[11]
    val m13 = a[1] * b[12] + a[5] * b[13] + a[9] * b[14] + a[13] * b[15]
    // r2
    val m2 = a[2] * b[0] + a[6] * b[1] + a[10] * b[2] + a[14] * b[3]
    val m6 = a[2] * b[4] + a[6] * b[5] + a[10] * b[6] + a[14] * b[7]
    val m10 = a[2] * b[8] + a[6] * b[9] + a[10] * b[10] + a[14] * b[11]
    val m14 = a[2] * b[12] + a[6] * b[13] + a[10] * b[14] + a[14] * b[15]
    // r3
    val m3 = a[3] * b[0] + a[7] * b[1] + a[11] * b[2] + a[15] * b[3]
    val m7 = a[3] * b[4] + a[7] * b[5] + a[11] * b[6] + a[15] * b[7]
    val m11 = a[3] * b[8] + a[7] * b[9] + a[11] * b[10] + a[15] * b[11]
    val m15 = a[3] * b[12] + a[7] * b[13] + a[11] * b[14] + a[15] * b[15]

    result.clear()
        .put(m0).put(m1).put(m2).put(m3)
        .put(m4).put(m5).put(m6).put(m7)
        .put(m8).put(m9).put(m10).put(m11)
        .put(m12).put(m13).put(m14).put(m15)
        .flip()
}

/**
 * Multiplies this matrix by vector `v` and stores result in vector `r`. This is a right multiplication
 *
 * @param a the matrix
 * @param v the vector
 * @param r the result
 */
fun multiply(a: FloatBuffer, v: Vector3f, r: Vector3f = v) {
    r.set(
        a[0] * v.x + a[4] * v.y + a[8] * v.z + a[12],
        a[1] * v.x + a[5] * v.y + a[9] * v.z + a[13],
        a[2] * v.x + a[6] * v.y + a[10] * v.z + a[14]
    )
}

/**
 * Multiplies this matrix by vector `v` and stores result in vector `r`. This is a right multiplication
 *
 * @param a the matrix
 * @param v the vector
 * @param r the result
 */
fun multiply(a: FloatBuffer, v: Vector4f, r: Vector4f = v) {
    v.set(
        a[0] * v.x + a[4] * v.y + a[8] * v.z + a[12] * v.w,
        a[1] * v.x + a[5] * v.y + a[9] * v.z + a[13] * v.w,
        a[2] * v.x + a[6] * v.y + a[10] * v.z + a[14] * v.w,
        a[3] * v.x + a[7] * v.y + a[11] * v.z + a[15] * v.w
    )
}

/**
 * Multiplies matrix `a` by translation matrix derived from `(dx,dy,dz)` and stores result in `result`.
 *
 * @param a      the original matrix to add translation to
 * @param dx     x translation
 * @param dy     y translation
 * @param dz     z translation
 * @param result the buffer to store result.
 */
fun translate(a: FloatBuffer, dx: Float, dy: Float, dz: Float, result: FloatBuffer) {
    copy(a, result)

    val m12 = a[0] * dx + a[4] * dy + a[8] * dz + a[12]
    val m13 = a[1] * dx + a[5] * dy + a[9] * dz + a[13]
    val m14 = a[2] * dx + a[6] * dy + a[10] * dz + a[14]
    val m15 = a[3] * dx + a[7] * dy + a[11] * dz + a[15]

    result.put(12, m12)
        .put(13, m13)
        .put(14, m14)
        .put(15, m15)
}

/**
 * Combines scaling `(sx,sy,sz)` with matrix `a` and stores resulting matrix in `result`
 *
 * @param a      the original matrix
 * @param sx     x scaling factor
 * @param sy     y  scaling factor
 * @param sz     z  scaling factor
 * @param result the buffer to store result
 */
fun scale(a: FloatBuffer, sx: Float, sy: Float, sz: Float, result: FloatBuffer) {
    // r0
    val m0 = a[0] * sx
    val m4 = a[4] * sy
    val m8 = a[8] * sz
    val m12 = a[12]
    // r1
    val m1 = a[1] * sx
    val m5 = a[5] * sy
    val m9 = a[9] * sz
    val m13 = a[13]
    // r2
    val m2 = a[2] * sx
    val m6 = a[6] * sy
    val m10 = a[10] * sz
    val m14 = a[14]
    // r3
    val m3 = a[3] * sx
    val m7 = a[7] * sy
    val m11 = a[11] * sz
    val m15 = a[15]

    result.clear()
        .put(m0).put(m1).put(m2).put(m3)
        .put(m4).put(m5).put(m6).put(m7)
        .put(m8).put(m9).put(m10).put(m11)
        .put(m12).put(m13).put(m14).put(m15)
        .flip()
}

/**
 * Transposes the matrix `a` and stores resulting matrix in `result`
 *
 * @param a      the matrix to transpose
 * @param result the buffer to store result
 */
fun transpose(a: FloatBuffer, result: FloatBuffer) {
    val m0 = a[0]
    val m1 = a[1]
    val m2 = a[2]
    val m3 = a[3]

    val m4 = a[4]
    val m5 = a[5]
    val m6 = a[6]
    val m7 = a[7]

    val m8 = a[8]
    val m9 = a[9]
    val m10 = a[10]
    val m11 = a[11]

    val m12 = a[12]
    val m13 = a[13]
    val m14 = a[14]
    val m15 = a[15]

    result.clear()
        .put(m0).put(m4).put(m8).put(m12)
        .put(m1).put(m5).put(m9).put(m13)
        .put(m2).put(m6).put(m10).put(m14)
        .put(m3).put(m7).put(m11).put(m15)
        .flip()
}

/**
 * Initializes `result` with rotation matrix from Euler's angles `ax, ay, az`.
 *
 * @param ax     the x-axis rotation angle (counter-clockwise)
 * @param ay     the y-axis rotation angle (counter-clockwise)
 * @param az     the z-axis rotation angle (counter-clockwise)
 * @param result the buffer to store result
 */
fun rotation(ax: Double, ay: Double, az: Double, result: FloatBuffer) {
    val a = cos(ax)
    val b = sin(ax)
    val c = cos(ay)
    val d = sin(ay)
    val e = cos(az)
    val f = sin(az)
    result.clear()
        .put((c * e).toFloat()).put((b * d * e + a * f).toFloat()).put((-a * d * e + b * f).toFloat()).put(0f)
        .put((-c * f).toFloat()).put((-b * d * f + a * e).toFloat()).put((a * d * f + b * e).toFloat()).put(0f)
        .put(d.toFloat()).put((-b * c).toFloat()).put((a * c).toFloat()).put(0f)
        .put(0f).put(0f).put(0f).put(1f)
        .flip()
}

/**
 * For 2x2 matrix M determinant is A*D - B*C
 * <pre>
 *     | A B |
 * M = |     |
 *     | C D |
 * </pre>
 *
 * So for our 4x4 matrix determinant `Det = A * Asubd - B * Bsubd + C * Csubd - D*Dsubd` where
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
</pre> *
 *
 * @param a the matrix
 * @return the determinant of a matrix
 */
fun determinant(a: FloatBuffer): Double {
    val d6_11 = (a[6] * a[11] - a[7] * a[10]).toDouble()
    val d6_15 = (a[6] * a[15] - a[7] * a[14]).toDouble()
    val d10_15 = (a[10] * a[15] - a[11] * a[14]).toDouble()
    val d2_11 = (a[2] * a[11] - a[3] * a[10]).toDouble()
    val d2_15 = (a[2] * a[15] - a[3] * a[14]).toDouble()
    val d2_7 = (a[2] * a[7] - a[3] * a[6]).toDouble()

    val Asubd = a[5] * d10_15 - a[9] * d6_15 + a[13] * d6_11
    val Bsubd = a[1] * d10_15 - a[9] * d2_15 + a[13] * d2_11
    val Csubd = a[1] * d6_15 - a[5] * d2_15 + a[13] * d2_7
    val Dsubd = a[1] * d6_11 - a[5] * d2_11 + a[9] * d2_7

    return a[0] * Asubd - a[4] * Bsubd + a[8] * Csubd - a[12] * Dsubd
}

/**
 * Calculate inverse matrix.
 * See [Math.determinant] for details.
 * <pre>
 * 0 4  8 12     A B C D
 * 1 5  9 13     E F G H
 * M   2 6 10 14     I J K L
 * 3 7 11 15     M N O P
 *
 * Asub 5  9 13   Bsub 1  9 13   Csub 1 5 13   Dsub 1 5  9
 * 6 10 14        2 10 14        2 6 14        2 6 10
 * 7 11 15        3 11 15        3 7 15        3 7 11
 *
 * Esub 4  8 12   Fsub 0  8 12   Gsub 0 4 12   Hsub 0 4  8
 * 6 10 14        2 10 14        2 6 14        2 6 10
 * 7 11 15        3 11 15        3 7 15        3 7 11
 *
 * Isub 4  8 12   Jsub 0  8 12   Ksub 0 4 12   Lsub 0 4  8
 * 5  9 13        1  9 13        1 5 13        1 5  9
 * 7 11 15        3 11 15        3 7 15        3 7 11
 *
 * Msub 4  8 12   Nsub 0  8 12   Osub 0 4 12   Psub 0 4  8
 * 5  9 13        1  9 13        1 5 13        1 5  9
 * 6 10 14        2 10 14        2 6 14        2 6 10
</pre> *
 *
 * @param a      the original matrix
 * @param result the inverse matrix
 */
fun inverse(a: FloatBuffer, result: FloatBuffer) {
    val d6_11 = (a[6] * a[11] - a[7] * a[10]).toDouble()
    val d6_15 = (a[6] * a[15] - a[7] * a[14]).toDouble()
    val d10_15 = (a[10] * a[15] - a[11] * a[14]).toDouble()
    val d2_11 = (a[2] * a[11] - a[3] * a[10]).toDouble()
    val d2_15 = (a[2] * a[15] - a[3] * a[14]).toDouble()
    val d2_7 = (a[2] * a[7] - a[3] * a[6]).toDouble()
    val d9_14 = (a[9] * a[14] - a[10] * a[13]).toDouble()
    val d9_15 = (a[9] * a[15] - a[11] * a[13]).toDouble()
    val d5_14 = (a[5] * a[14] - a[6] * a[13]).toDouble()
    val d5_15 = (a[5] * a[15] - a[7] * a[13]).toDouble()
    val d1_14 = (a[1] * a[14] - a[2] * a[13]).toDouble()
    val d1_15 = (a[1] * a[15] - a[3] * a[13]).toDouble()
    val d5_10 = (a[5] * a[10] - a[6] * a[9]).toDouble()
    val d5_11 = (a[5] * a[11] - a[7] * a[9]).toDouble()
    val d1_11 = (a[1] * a[11] - a[3] * a[9]).toDouble()
    val d1_6 = (a[1] * a[6] - a[2] * a[5]).toDouble()
    val d1_7 = (a[1] * a[7] - a[3] * a[5]).toDouble()
    val d1_10 = (a[1] * a[10] - a[2] * a[9]).toDouble()

    // row 0
    val A = a[5] * d10_15 - a[9] * d6_15 + a[13] * d6_11
    val B = a[1] * d10_15 - a[9] * d2_15 + a[13] * d2_11
    val C = a[1] * d6_15 - a[5] * d2_15 + a[13] * d2_7
    val D = a[1] * d6_11 - a[5] * d2_11 + a[9] * d2_7

    // row 1
    val E = a[4] * d10_15 - a[8] * d6_15 + a[12] * d6_11
    val F = a[0] * d10_15 - a[8] * d2_15 + a[12] * d2_11
    val G = a[0] * d6_15 - a[4] * d2_15 + a[12] * d2_7
    val H = a[0] * d6_11 - a[4] * d2_11 + a[8] * d2_7

    // row 2
    val I = a[4] * d9_15 - a[8] * d5_15 + a[12] * d5_11
    val J = a[0] * d9_15 - a[8] * d1_15 + a[12] * d1_11
    val K = a[0] * d5_15 - a[4] * d1_15 + a[12] * d1_7
    val L = a[0] * d5_11 - a[4] * d1_11 + a[8] * d1_7

    // row 3
    val M = a[4] * d9_14 - a[8] * d5_14 + a[12] * d5_10
    val N = a[0] * d9_14 - a[8] * d1_14 + a[12] * d1_10
    val O = a[0] * d5_14 - a[4] * d1_14 + a[12] * d1_6
    val P = a[0] * d5_10 - a[4] * d1_10 + a[8] * d1_6

    val det = a[0] * A - a[4] * B + a[8] * C - a[12] * D
    require(det != 0.0) { "Matrix can not be inverted!" }

    result.clear()
        .put(A.toFloat()).put(-E.toFloat()).put(I.toFloat()).put(-M.toFloat())
        .put(-B.toFloat()).put(F.toFloat()).put(-J.toFloat()).put(N.toFloat())
        .put(C.toFloat()).put(-G.toFloat()).put(K.toFloat()).put(-O.toFloat())
        .put(-D.toFloat()).put(H.toFloat()).put(-L.toFloat()).put(P.toFloat())
        .flip()

    transpose(result, result)

    val ood = 1.0 / det
    for (i in 0..15) {
        result.put(i, (result[i] * ood).toFloat())
    }
}

/**
 * Creates viewing matrix derived from the `eye` point, a reference point `target` indicating the center of the scene and vector `up`
 * Helpful tip: it's better to think of this as a coordinate system rotation.
 *
 * @param target the target point in the scene
 * @param eye    the eye point
 * @param up     the upward vector, must not be parallel to the direction vector `dir = target - eye`
 * @param m      the buffer to store resulting matrix in.
 */
fun MatrixOps.lookAt(target: Vector3f, eye: Vector3f, up: Vector3f, m: FloatBuffer) {
    val zaxis = (eye - target).normalize()
    val xaxis = (up.cross(zaxis)).normalize()
    val yaxis = zaxis.cross(xaxis)
    m.clear()
        .put(xaxis.x).put(xaxis.y).put(xaxis.z).put(0f)
        .put(yaxis.x).put(yaxis.y).put(yaxis.z).put(0f)
        .put(zaxis.x).put(zaxis.y).put(zaxis.z).put(0f)
        .put(0f).put(0f).put(0f).put(1f)
        .flip()
    transpose(m, m)
    translate(m, -eye.x, -eye.y, -eye.z, m)
}


/**
 * Show matrix by rows
 */
fun toString(m: FloatBuffer): String {
    return "{r0(" + m[0] + " " + m[4] + " " + m[8] + " " + m[12] + "), r1(" +
            m[1] + " " + m[5] + " " + m[9] + " " + m[13] + "), r2(" +
            m[2] + " " + m[6] + " " + m[10] + " " + m[14] + "), r3(" +
            m[3] + " " + m[7] + " " + m[11] + " " + m[15] + ")}"
}

/**
 * Convert to float array
 */
fun toArray(m: FloatBuffer): FloatArray {
    val result = FloatArray(16)
    m.get(result).flip()
    return result
}