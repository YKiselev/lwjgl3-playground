package com.github.ykiselev.opengl.matrices

import java.nio.FloatBuffer

object EscapeAnalysisTest {

    data class V(var x: Float, var y: Float, var z: Float, var w: Float) {

        fun set(v: V) {
            x = v.x
            y = v.y
            z = v.z
            w = v.w
        }

        operator fun plus(other: V): V = V(x + other.x, y + other.y, z + other.z, w + other.w)

        operator fun minus(other: V): V = V(x - other.x, y - other.y, z - other.z, w - other.w)

        operator fun times(other: V): V = V(x * other.x, y * other.y, z * other.z, w * other.w)

        operator fun div(other: V): V = V(x / other.x, y / other.y, z / other.z, w / other.w)
    }

    @JvmInline
    value class M(val m: FloatBuffer = FloatBuffer.allocate(16)) {

        operator fun times(v: V): V =
            V(v.x * m[0] + v.y * m[1], v.x * m[2] + v.y * m[3], v.z * m[4], v.w * m[5])

        companion object {

            fun identity2(m: FloatBuffer): M = M(m.also {
                identity(it)
            })
        }
    }

    @JvmInline
    value class M2(val m: FloatArray = FloatArray(16)) {

        operator fun times(v: V): V =
            V(
                v.x * m[0] + v.y * m[1] + v.z * m[2] + m[3],
                v.x * m[4] + v.y * m[5] + v.z * m[6] + m[7],
                v.x * m[8] + v.y * m[9] + v.z * m[10] + m[11],
                v.w * m[12]
            )

        companion object {

            fun identity(): M2 = M2().also {
                it.m[0] = 1f
                it.m[1] = 0f
                it.m[2] = 0f
                it.m[3] = 0f

                it.m[4] = 0f
                it.m[5] = 1f
                it.m[6] = 0f
                it.m[7] = 0f

                it.m[8] = 0f
                it.m[9] = 0f
                it.m[10] = 1f
                it.m[11] = 0f

                it.m[12] = 0f
                it.m[13] = 0f
                it.m[14] = 0f
                it.m[15] = 1f
            }
        }
    }

    private var counter = 0L

    private fun sink(v: Any) {
        counter += v.hashCode()
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val a = Vector3f(1f, 2f, 3f)
        val t0 = System.currentTimeMillis()
        var i = 0L
        do {
            val b = a + a
            val c = b - a
            val d = c * a
            val e = d / a
            a.set(b)
            sink(a)
            a.set(c)
            sink(a)
            a.set(d)
            sink(a)
            a.set(e)
            sink(a)
            a.x = i.toFloat()
            a.y = c.y + e.y - b.y
            a.z = d.z - e.z

//            MemoryStack.stackPush().use {
//                val m = M.identity2(it.mallocFloat(16))
//                m.m.put(0, i.toFloat())
//                m.m.put(1, b.x)
//                m.m.put(2, c.x)
//                m.m.put(3, b.y)
//                m.m.put(4, d.y)
            val m = Matrix.identity()
            //m.m[0] = i.toFloat()
            //m.m[1] = b.x
            //m.m[2] = c.x
            //m.m[3] = b.y
            //m.m[4] = d.y

            val f = m * a
            a.set(f)
            sink(a)
//            }
            i++
        } while (System.currentTimeMillis() - t0 < 20 * 1000L)
        println("counter is $counter, a=$a")
    }
}