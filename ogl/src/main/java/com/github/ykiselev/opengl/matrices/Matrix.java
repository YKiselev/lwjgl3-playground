package com.github.ykiselev.opengl.matrices;

import com.github.ykiselev.opengl.shaders.uniforms.UniformVariable;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class Matrix implements AutoCloseable {

    private final FloatBuffer m = MemoryUtil.memAllocFloat(16);

    @Override
    public void close() {
        MemoryUtil.memFree(m);
    }

    public void orthographic(float left, float right, float top, float bottom, float near, float far) {
        m.clear();
        m.put(2 / (right - left)).put(0).put(0).put(0);
        m.put(0).put(2 / (top - bottom)).put(0).put(0);
        m.put(0).put(0).put(-2 / (far - near)).put(0);
        m.put(-(right + left) / (right - left)).put(-(top + bottom) / (top - bottom)).put(-(far + near) / (far - near)).put(1);
        m.flip();
    }

    public void assignTo(UniformVariable variable, boolean transpose) {
        variable.matrix4(transpose, m);
    }
}
