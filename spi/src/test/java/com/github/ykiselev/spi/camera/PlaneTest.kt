package com.github.ykiselev.spi.camera

import com.github.ykiselev.opengl.matrices.Vector3f
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class PlaneTest {
    @Test
    fun shouldClassify() {
        val p = Plane()
        // yz plane translated by 0.5 in x direction
        p.set(1f, 0f, 0f, -0.5f)

        assertEquals(Plane.Classification.INSIDE, p.classify(Vector3f(1f, 0.5f, 0.5f)))
        assertEquals(Plane.Classification.ON_PLANE, p.classify(Vector3f(0.5f, 0.5f, 0.5f)))
        assertEquals(Plane.Classification.OUTSIDE, p.classify(Vector3f(0.3f, 0.5f, 0.5f)))
    }
}