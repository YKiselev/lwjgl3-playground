package com.github.ykiselev.opengl.matrices

import org.lwjgl.system.MemoryStack
import java.nio.FloatBuffer

object EscapeAnalysisApp {

    private var counter = 0L

    private fun sink(v: Any) {
        counter += v.hashCode()
    }

    private fun MatrixOps.setup(): Matrix {
        val p = perspective(Math.toRadians(90.0).toFloat(), 1f, 0.001f, 1000f)
        val rot = rotation(Math.toRadians(34.0), 0.0, Math.toRadians(45.0))
        return p * rot * identity().translate(0f, 0f, 0f)
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val a = Vector3f(1f, 2f, 3f)
        val t0 = System.currentTimeMillis()
        var i = 0L
        val touch = { it:FloatBuffer->
            sink(it.hashCode())
        }
        do {
            math {
                a.set(1f + i, 0.001f * i, i.toFloat())

                val b = a + a
                val c = a - b * b
                val d = a * 0.5f - b
                val e = b - c * 2f
                val f = d * a - c / a

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


                val m = identity()

                val g = m * a
                a.set(g)
                sink(a)

                a.x = 34f
                a.y = 35f
                a.z = 36f
                sink(a)
                a.set(d.x, d.y, d.z)

                val m2 = setup()
                val g2 = m2 * a
                a.set(g2)
                sink(a)
            }

            i++
        } while (System.currentTimeMillis() - t0 < 20 * 1000L)
        println("counter is $counter, a=$a")
    }
}