package com.github.ykiselev.opengl.pools

import org.junit.jupiter.api.Test

class MathPoolsTest {

    @Test
    fun shouldPush() {
        math {
            val m = identity()
            val v = m * vec3f(1f, 2f, 3f)
        }
        printMathPoolInfo{
            println(it)
        }
    }
}