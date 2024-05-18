package com.github.ykiselev.spi.camera

import com.github.ykiselev.opengl.matrices.Vector3f
import com.github.ykiselev.opengl.matrices.perspective
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.nio.FloatBuffer

internal class FrustumTest {

    private val frustum = Frustum()

    @Test
    fun shouldClassify() {
        val m = FloatBuffer.allocate(16)

        perspective(-0.1f, 0.1f, 0.1f, -0.1f, 0.1f, 100f, m)

        frustum.setFromMatrix(m)

        // check near plane
        Assertions.assertTrue(frustum.isInside(Vector3f(0f, 0f, -0.1f)))
        Assertions.assertFalse(frustum.isInside(Vector3f(0f, 0f, -0.05f)))
        Assertions.assertTrue(frustum.intersects(Vector3f(0f, 0f, -0.05f), 0.11f))

        // check far plane
        Assertions.assertTrue(frustum.isInside(Vector3f(0f, 0f, -100f)))
        Assertions.assertFalse(frustum.isInside(Vector3f(0f, 0f, -100.5f)))

        // check left plane
        Assertions.assertTrue(frustum.isInside(Vector3f(-0.1f, 0f, -0.1f)))
        Assertions.assertFalse(frustum.isInside(Vector3f(-0.11f, 0f, -0.1f)))

        // check right plane
        Assertions.assertTrue(frustum.isInside(Vector3f(0.1f, 0f, -0.1f)))
        Assertions.assertFalse(frustum.isInside(Vector3f(0.11f, 0f, -0.1f)))

        // check top plane
        Assertions.assertTrue(frustum.isInside(Vector3f(0f, 0.1f, -0.1f)))
        Assertions.assertFalse(frustum.isInside(Vector3f(0f, 0.11f, -0.1f)))

        // check bottom plane
        Assertions.assertTrue(frustum.isInside(Vector3f(0f, -0.1f, -0.1f)))
        Assertions.assertFalse(frustum.isInside(Vector3f(0f, -0.11f, -0.1f)))

        // check point inside
        Assertions.assertTrue(frustum.isInside(Vector3f(0f, 0f, -1f)))
    }
}