package com.github.ykiselev.opengl.shaders.uniforms;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Matrix4f;

import java.nio.FloatBuffer;

/**
 * Created by Y.Kiselev on 12.05.2016.
 */
public final class UniformVariableAdapter {

    private final FloatBuffer floats = BufferUtils.createFloatBuffer(16);

    public void put(Matrix4f m) {
        this.floats.clear();
        m.store(this.floats);
        this.floats.flip();
    }

    public void get(boolean transpose, Matrix4Variable target) {
        target.set(transpose, this.floats);
    }

    public void copy(Matrix4f src, boolean transpose, Matrix4Variable dest) {
        put(src);
        get(transpose, dest);
    }

}
