package com.github.ykiselev.opengl.shaders.uniforms;

import org.lwjgl.opengl.GL20;

import java.nio.FloatBuffer;

/**
 * Created by Y.Kiselev on 08.05.2016.
 */
public final class UniformVariable {

    private final int location;

    private final String name;

    public int location() {
        return location;
    }

    public String name() {
        return name;
    }

    public UniformVariable(int location, String name) {
        this.location = location;
        this.name = name;
    }

    public void matrix4(boolean transpose, FloatBuffer matrix) {
        GL20.glUniformMatrix4fv(this.location, transpose, matrix);
        //Util.checkGLError();
    }
}
