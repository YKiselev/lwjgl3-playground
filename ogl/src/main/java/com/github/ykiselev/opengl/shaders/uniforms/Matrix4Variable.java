package com.github.ykiselev.opengl.shaders.uniforms;

import java.nio.FloatBuffer;

/**
 * Created by Y.Kiselev on 12.05.2016.
 */
public final class Matrix4Variable {

    private final UniformVariable variable;

    public Matrix4Variable(UniformVariable variable) {
        this.variable = variable;
    }

    public void set(boolean transpose, FloatBuffer matrix) {
        this.variable.matrix4(transpose, matrix);
    }
}
