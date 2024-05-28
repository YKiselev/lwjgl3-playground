package com.github.ykiselev.opengl.matrices

interface MatrixOps : Vector3fOps, Vector4fOps {

    fun matrix(): Matrix

    fun identity(): Matrix {
        val result = matrix()
        identity(result.m)
        return result
    }

    fun orthographic(left: Float, right: Float, top: Float, bottom: Float, near: Float, far: Float): Matrix {
        val result = matrix()
        orthographic(left, right, top, bottom, near, far, result.m)
        return result
    }

    fun perspective(left: Float, right: Float, top: Float, bottom: Float, near: Float, far: Float): Matrix {
        val result = matrix()
        perspective(left, right, top, bottom, near, far, result.m)
        return result
    }

    fun perspective(fow: Float, ratio: Float, near: Float, far: Float): Matrix {
        val result = matrix()
        perspective(fow, ratio, near, far, result.m)
        return result
    }

    fun lookAt(target: Vector3f, eye: Vector3f, up: Vector3f): Matrix {
        val result = matrix()
        lookAt(target, eye, up, result.m)
        return result
    }


    fun rotation(ax: Double, ay: Double, az: Double): Matrix {
        val result = matrix()
        rotation(ax, ay, az, result.m)
        return result
    }

    /**
     * Adds this matrix to another.
     *
     * @param b      the second matrix
     * @return the resulting matrix
     */
    operator fun Matrix.plus(b: Matrix): Matrix {
        val result = matrix()
        add(m, b.m, result.m)
        return result
    }

    operator fun Matrix.times(value: Float): Matrix {
        val result = matrix()
        multiply(m, value, result.m)
        return result
    }

    operator fun Matrix.times(b: Matrix): Matrix {
        val result = matrix()
        multiply(m, b.m, result.m)
        return result
    }

    operator fun Matrix.times(v: Vector3f): Vector3f {
        val result = vec3f()
        multiply(m, v, result)
        return result
    }

    operator fun Matrix.times(v: Vector4f): Vector4f {
        val result = vec4f()
        multiply(m, v, result)
        return result
    }

    /**
     * Multiplies this matrix by translation matrix derived from `(dx,dy,dz)`.
     *
     * @param dx     x translation
     * @param dy     y translation
     * @param dz     z translation
     */
    fun Matrix.translate(dx: Float, dy: Float, dz: Float): Matrix {
        val result = matrix()
        translate(m, dx, dy, dz, result.m)
        return result
    }

    /**
     * Combines scaling `(sx,sy,sz)` with this matrix
     *
     * @param sx     x scaling factor
     * @param sy     y  scaling factor
     * @param sz     z  scaling factor
     */
    fun Matrix.scale(sx: Float, sy: Float, sz: Float): Matrix {
        val result = matrix()
        scale(m, sx, sy, sz, result.m)
        return result
    }

    fun Matrix.transpose(): Matrix {
        val result = matrix()
        transpose(m, result.m)
        return result
    }

    fun Matrix.determinant(): Double =
        determinant(m)

    fun Matrix.inverse(): Matrix {
        val result = matrix()
        inverse(m, result.m)
        return result
    }
}