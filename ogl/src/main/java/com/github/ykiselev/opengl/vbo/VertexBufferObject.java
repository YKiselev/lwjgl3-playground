package com.github.ykiselev.opengl.vbo;

import org.lwjgl.opengl.Util;

import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

/**
 * Created by Y.Kiselev on 08.05.2016.
 */
public final class VertexBufferObject extends BufferObject {

    public VertexBufferObject() {
        super(GL_ARRAY_BUFFER);
    }

    public void attribute(int index, int size, int type, boolean normalized, int stride, long bbo) {
        glVertexAttribPointer(index, size, type, normalized, stride, bbo);
        Util.checkGLError();
        glEnableVertexAttribArray(index);
        Util.checkGLError();
    }

}
