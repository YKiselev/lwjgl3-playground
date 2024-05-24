package com.github.ykiselev.opengl.matrices

import java.nio.FloatBuffer
import java.util.*
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tan

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
value class Matrix(val m: FloatBuffer) {

    operator fun get(index: Int): Float  = m[index]
/*
    /**
     * Adds this matrix to another.
     *
     * @param b      the second matrix
     * @return the resulting matrix
     */
    operator fun plus(b: Matrix): Matrix {
        val result = FloatArray(16)
        add(m, b.m, result)
        return Matrix(result)
    }

    operator fun times(value: Double): Matrix {
        val result = FloatArray(16)
        multiply(m, value, result)
        return Matrix(result)
    }

    operator fun times(value: Float): Matrix =
        times(value.toDouble())

    operator fun times(b: Matrix): Matrix {
        val result = FloatArray(16)
        multiply(m, b.m, result)
        return Matrix(result)
    }

    /**
     * Multiplies this matrix by vector `v`. This is a right multiplication
     *
     * @param a the matrix
     * @param v the vector
     */
    operator fun times(v: Vector3f): Vector3f =
        Vector3f(
            m[0] * v.x + m[4] * v.y + m[8] * v.z + m[12],
            m[1] * v.x + m[5] * v.y + m[9] * v.z + m[13],
            m[2] * v.x + m[6] * v.y + m[10] * v.z + m[14]
        )

    operator fun times(v: Vector4f): Vector4f =
        Vector4f(
            m[0] * v.x + m[4] * v.y + m[8] * v.z + m[12] * v.w,
            m[1] * v.x + m[5] * v.y + m[9] * v.z + m[13] * v.w,
            m[2] * v.x + m[6] * v.y + m[10] * v.z + m[14] * v.w,
            m[3] * v.x + m[7] * v.y + m[11] * v.z + m[15] * v.w
        )


    /**
     * Multiplies this matrix by translation matrix derived from `(dx,dy,dz)`.
     *
     * @param dx     x translation
     * @param dy     y translation
     * @param dz     z translation
     */
    fun translate(dx: Float, dy: Float, dz: Float): Matrix {
        val result = FloatArray(16)
        translate(m, dx, dy, dz, result)
        return Matrix(result)
    }

    /**
     * Combines scaling `(sx,sy,sz)` with this matrix
     *
     * @param sx     x scaling factor
     * @param sy     y  scaling factor
     * @param sz     z  scaling factor
     */
    fun scale(sx: Float, sy: Float, sz: Float): Matrix {
        val result = FloatArray(16)
        scale(m, sx, sy, sz, result)
        return Matrix(result)
    }

    fun transpose(): Matrix {
        val result = FloatArray(16)
        transpose(m, result)
        return Matrix(result)
    }

    fun determinant(): Double =
        determinant(m)

    fun inverse(): Matrix {
        val result = FloatArray(16)
        inverse(m, result)
        return Matrix(result)
    }

    fun toArray(): FloatArray =
        m.copyOf()
*/
    /**
     * Show matrix by rows
     */
    override fun toString(): String {
        return "{r0(" + m[0] + " " + m[4] + " " + m[8] + " " + m[12] + "), r1(" +
                m[1] + " " + m[5] + " " + m[9] + " " + m[13] + "), r2(" +
                m[2] + " " + m[6] + " " + m[10] + " " + m[14] + "), r3(" +
                m[3] + " " + m[7] + " " + m[11] + " " + m[15] + ")}"
    }
/*
    companion object {

        private fun identity(m: FloatArray) {
            Arrays.fill(m, 0f)
            m[0] = 1f
            m[5] = 1f
            m[10] = 1f
            m[15] = 1f
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
        private fun orthographic(
            left: Float,
            right: Float,
            top: Float,
            bottom: Float,
            near: Float,
            far: Float,
            m: FloatArray
        ) {
            Arrays.fill(m, 0f)
            m[0] = 2 / (right - left)
            m[5] = 2 / (top - bottom)
            m[10] = -2 / (far - near)
            m[12] = -(right + left) / (right - left)
            m[13] = -(top + bottom) / (top - bottom)
            m[14] = -(far + near) / (far - near)
            m[15] = 1f
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
        private fun perspective(
            left: Float,
            right: Float,
            top: Float,
            bottom: Float,
            near: Float,
            far: Float,
            m: FloatArray
        ) {
            Arrays.fill(m, 0f)
            m[0] = (2.0 * near / (right - left)).toFloat()
            m[5] = (2.0 * near / (top - bottom)).toFloat()
            m[8] = (right + left) / (right - left)
            m[9] = (top + bottom) / (top - bottom)
            m[10] = -(far + near) / (far - near)
            m[11] = -1f
            m[14] = (-2.0 * far * near / (far - near)).toFloat()
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
        private fun perspective(fow: Float, ratio: Float, near: Float, far: Float, m: FloatArray) {
            val w = (near * tan(0.5 * fow)).toFloat()
            val h = w / ratio
            perspective(-w, w, h, -h, near, far, m)
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
        private fun lookAt(target: Vector3f, eye: Vector3f, up: Vector3f, m: FloatArray) {
            val zaxis = (eye - target).normalize()
            val xaxis = (up.cross(zaxis)).normalize()
            val yaxis = zaxis.cross(xaxis)
            m[0] = xaxis.x
            m[1] = xaxis.y
            m[2] = xaxis.z
            m[3] = 0f

            m[4] = yaxis.x
            m[5] = yaxis.y
            m[6] = yaxis.z
            m[7] = 0f

            m[8] = zaxis.x
            m[9] = zaxis.y
            m[10] = zaxis.z
            m[11] = 0f

            m[12] = 0f
            m[13] = 0f
            m[14] = 0f
            m[15] = 1f

            transpose(m, m)
            translate(m, -eye.x, -eye.y, -eye.z, m)
        }

        /**
         * Transposes the matrix `a` and stores resulting matrix in `result`
         *
         * @param a      the matrix to transpose
         * @param result the buffer to store result
         */
        private fun transpose(a: FloatArray, result: FloatArray) {
            val m0 = a[0]
            val m1 = a[1]
            val m2 = a[2]
            val m3 = a[3]

            result[0] = m0
            result[1] = a[4]
            result[2] = a[8]
            result[3] = a[12]

            val m5 = a[5]
            val m6 = a[6]
            val m7 = a[7]

            result[4] = m1
            result[5] = m5
            result[6] = a[9]
            result[7] = a[13]

            val m10 = a[10]
            val m11 = a[11]

            result[8] = m2
            result[9] = m6
            result[10] = m10
            result[11] = a[14]

            result[12] = m3
            result[13] = m7
            result[14] = m11
            result[15] = a[15]
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
        private fun translate(a: FloatArray, dx: Float, dy: Float, dz: Float, result: FloatArray) {
            // We can't use a.copyInto(result) because it's not EA-friendly
            for (i in 0..11) {
                result[i]  = a[i]
            }

            val m12 = a[0] * dx + a[4] * dy + a[8] * dz + a[12]
            val m13 = a[1] * dx + a[5] * dy + a[9] * dz + a[13]
            val m14 = a[2] * dx + a[6] * dy + a[10] * dz + a[14]
            val m15 = a[3] * dx + a[7] * dy + a[11] * dz + a[15]

            result[12] = m12
            result[13] = m13
            result[14] = m14
            result[15] = m15
        }

        /**
         * Initializes `result` with rotation matrix from Euler's angles `ax, ay, az`.
         *
         * @param ax     the x-axis rotation angle (counter-clockwise)
         * @param ay     the y-axis rotation angle (counter-clockwise)
         * @param az     the z-axis rotation angle (counter-clockwise)
         * @param result the buffer to store result
         */
        private fun rotation(ax: Double, ay: Double, az: Double, result: FloatArray) {
            val a = cos(ax)
            val b = sin(ax)
            val c = cos(ay)
            val d = sin(ay)
            val e = cos(az)
            val f = sin(az)
            result[0] = (c * e).toFloat()
            result[1] = (b * d * e + a * f).toFloat()
            result[2] = (-a * d * e + b * f).toFloat()
            result[3] = 0f

            result[4] = (-c * f).toFloat()
            result[5] = (-b * d * f + a * e).toFloat()
            result[6] = (a * d * f + b * e).toFloat()
            result[7] = 0f

            result[8] = d.toFloat()
            result[9] = (-b * c).toFloat()
            result[10] = (a * c).toFloat()
            result[11] = 0f

            result[12] = 0f
            result[13] = 0f
            result[14] = 0f
            result[15] = 1f
        }

        /**
         * Adds one matrix to another.
         *
         * @param a      the first matrix
         * @param b      the second matrix
         * @param result the resulting matrix buffer
         */
        private fun add(a: FloatArray, b: FloatArray, result: FloatArray) {
            result[0] = a[0] + b[0]
            result[1] = a[1] + b[1]
            result[2] = a[2] + b[2]
            result[3] = a[3] + b[3]

            result[4] = a[4] + b[4]
            result[5] = a[5] + b[5]
            result[6] = a[6] + b[6]
            result[7] = a[7] + b[7]

            result[8] = a[8] + b[8]
            result[9] = a[9] + b[9]
            result[10] = a[10] + b[10]
            result[11] = a[11] + b[11]

            result[12] = a[12] + b[12]
            result[13] = a[13] + b[13]
            result[14] = a[14] + b[14]
            result[15] = a[15] + b[15]
        }

        /**
         * Each row of first matrix is multiplied by the column of second (component-wise) and sum of results is stored in `result`'s cell.
         *
         * @param a      the first matrix
         * @param b      the second matrix
         * @param result the matrix to store result in
         */
        private fun multiply(a: FloatArray, b: FloatArray, result: FloatArray) {
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
//            val m2 = a[2] * b[0] + a[6] * b[1] + a[10] * b[2] + a[14] * b[3]
//            val m6 = a[2] * b[4] + a[6] * b[5] + a[10] * b[6] + a[14] * b[7]
//            val m10 = a[2] * b[8] + a[6] * b[9] + a[10] * b[10] + a[14] * b[11]
//            val m14 = a[2] * b[12] + a[6] * b[13] + a[10] * b[14] + a[14] * b[15]
            // r3
//            val m3 = a[3] * b[0] + a[7] * b[1] + a[11] * b[2] + a[15] * b[3]
//            val m7 = a[3] * b[4] + a[7] * b[5] + a[11] * b[6] + a[15] * b[7]
//            val m11 = a[3] * b[8] + a[7] * b[9] + a[11] * b[10] + a[15] * b[11]
//            val m15 = a[3] * b[12] + a[7] * b[13] + a[11] * b[14] + a[15] * b[15]

            result[0] = m0
            result[1] = m1
            //result[2] = m2
            //result[3] = m3

            result[4] = m4
            result[5] = m5
            //result[6] = m6
            //result[7] = m7

            result[8] = m8
            result[9] = m9
            //result[10] = m10
            //result[11] = m11

            result[12] = m12
            result[13] = m13
            //result[14] = m14
            //result[15] = m15
        }

        /**
         * Multiplies matrix by a scalar value.
         *
         * @param a      the source matrix
         * @param s      the scalar to multiply by
         * @param result the buffer to store result in
         */
        private fun multiply(a: FloatArray, s: Double, result: FloatArray) {
            result[0] = (s * a[0]).toFloat()
            result[1] = (s * a[1]).toFloat()
            result[2] = (s * a[2]).toFloat()
            result[3] = (s * a[3]).toFloat()

            result[4] = (s * a[4]).toFloat()
            result[5] = (s * a[5]).toFloat()
            result[6] = (s * a[6]).toFloat()
            result[7] = (s * a[7]).toFloat()

            result[8] = (s * a[8]).toFloat()
            result[9] = (s * a[9]).toFloat()
            result[10] = (s * a[10]).toFloat()
            result[11] = (s * a[11]).toFloat()

            result[12] = (s * a[12]).toFloat()
            result[13] = (s * a[13]).toFloat()
            result[14] = (s * a[14]).toFloat()
            result[15] = (s * a[15]).toFloat()
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
        private fun scale(a: FloatArray, sx: Float, sy: Float, sz: Float, result: FloatArray) {
            result[0] = a[0] * sx
            result[1] = a[1] * sx
            result[2] = a[2] * sx
            result[3] = a[3] * sx

            result[4] = a[4] * sy
            result[5] = a[5] * sy
            result[6] = a[6] * sy
            result[7] = a[7] * sy

            result[8] = a[8] * sz
            result[9] = a[9] * sz
            result[10] = a[10] * sz
            result[11] = a[11] * sz

            result[12] = a[12]
            result[13] = a[13]
            result[14] = a[14]
            result[15] = a[15]
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
        private fun determinant(a: FloatArray): Double {
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
        private fun inverse(a: FloatArray, result: FloatArray) {
            val d6_11 = a[6].toDouble() * a[11] - a[7] * a[10]
            val d6_15 = a[6].toDouble() * a[15] - a[7] * a[14]
            val d10_15 = a[10].toDouble() * a[15] - a[11] * a[14]
            val d2_11 = a[2].toDouble() * a[11] - a[3] * a[10]
            val d2_15 = a[2].toDouble() * a[15] - a[3] * a[14]
            val d2_7 = a[2].toDouble() * a[7] - a[3] * a[6]
            val d9_14 = a[9].toDouble() * a[14] - a[10] * a[13]
            val d9_15 = a[9].toDouble() * a[15] - a[11] * a[13]
            val d5_14 = a[5].toDouble() * a[14] - a[6] * a[13]
            val d5_15 = a[5].toDouble() * a[15] - a[7] * a[13]
            val d1_14 = a[1].toDouble() * a[14] - a[2] * a[13]
            val d1_15 = a[1].toDouble() * a[15] - a[3] * a[13]
            val d5_10 = a[5].toDouble() * a[10] - a[6] * a[9]
            val d5_11 = a[5].toDouble() * a[11] - a[7] * a[9]
            val d1_11 = a[1].toDouble() * a[11] - a[3] * a[9]
            val d1_6 = a[1].toDouble() * a[6] - a[2] * a[5]
            val d1_7 = a[1].toDouble() * a[7] - a[3] * a[5]
            val d1_10 = a[1].toDouble() * a[10] - a[2] * a[9]

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

            result[0] = A.toFloat()
            result[1] = -E.toFloat()
            result[2] = I.toFloat()
            result[3] = -M.toFloat()

            result[4] = -B.toFloat()
            result[5] = F.toFloat()
            result[6] = -J.toFloat()
            result[7] = N.toFloat()

            result[8] = C.toFloat()
            result[9] = -G.toFloat()
            result[10] = K.toFloat()
            result[11] = -O.toFloat()

            result[12] = -D.toFloat()
            result[13] = H.toFloat()
            result[14] = -L.toFloat()
            result[15] = P.toFloat()

            transpose(result, result)

            val ood = 1.0 / det
            multiply(result, ood, result)
        }

        fun identity(): Matrix {
            val m = FloatArray(16)
            identity(m)
            return Matrix(m)
        }

        fun orthographic(left: Float, right: Float, top: Float, bottom: Float, near: Float, far: Float): Matrix {
            val m = FloatArray(16)
            orthographic(left, right, top, bottom, near, far, m)
            return Matrix(m)
        }

        fun perspective(left: Float, right: Float, top: Float, bottom: Float, near: Float, far: Float): Matrix {
            val m = FloatArray(16)
            perspective(left, right, top, bottom, near, far, m)
            return Matrix(m)
        }

        fun perspective(fow: Float, ratio: Float, near: Float, far: Float): Matrix {
            val m = FloatArray(16)
            perspective(fow, ratio, near, far, m)
            return Matrix(m)
        }

        fun lookAt(target: Vector3f, eye: Vector3f, up: Vector3f): Matrix {
            val m = FloatArray(16)
            lookAt(target, eye, up, m)
            return Matrix(m)
        }

        fun rotation(ax: Double, ay: Double, az: Double): Matrix {
            val m = FloatArray(16)
            rotation(ax, ay, az, m)
            return Matrix(m)
        }
    }*/
}