package com.github.ykiselev.spi.camera

import com.github.ykiselev.opengl.matrices.Vector3f

class Plane {
    enum class Classification {
        INSIDE, OUTSIDE, ON_PLANE
    }

    val normal = Vector3f()

    var d: Float = 0f
        private set

    fun set(a: Float, b: Float, c: Float, d: Float) {
        val n = Vector3f(a, b, c)
        val len = n.length()
        check(len != 0.0) {
            "Invalid plane: ($n, $d)"
        }
        val ool = 1.0 / len
        normal.set((n.x * ool).toFloat(), (n.y * ool).toFloat(), (n.z * ool).toFloat())
        this.d = (d * ool).toFloat()
    }

    fun classify(p: Vector3f, radius: Float = 0f): Classification {
        val r = normal.dot(p) + d + radius
        if (r < 0) {
            return Classification.OUTSIDE
        } else if (r == 0.0) {
            return Classification.ON_PLANE
        }
        return Classification.INSIDE
    }
}
