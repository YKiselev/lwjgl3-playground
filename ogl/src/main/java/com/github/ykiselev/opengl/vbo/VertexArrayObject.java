package com.github.ykiselev.opengl.vbo;

import com.github.ykiselev.opengl.Bindable;

import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

/**
 * Created by Y.Kiselev on 08.05.2016.
 */
public final class VertexArrayObject implements Bindable, AutoCloseable {

    private final int id;

    private final BufferObject[] buffers;

    public VertexArrayObject(BufferObject... buffers) {
        this.buffers = buffers;
        this.id = glGenVertexArrays();
        //todo Util.checkGLError();
    }

    @Override
    public void close() throws Exception {
        for (BufferObject buffer : buffers) {
            buffer.close();
            //todo Util.checkGLError();
        }
        glDeleteVertexArrays(this.id);
        //todo Util.checkGLError();
    }

    @Override
    public void bind() {
        glBindVertexArray(id);
        //todo Util.checkGLError();
    }

    @Override
    public void unbind() {
        glBindVertexArray(0);
    }

    @Override
    public int id() {
        return id;
    }
}
