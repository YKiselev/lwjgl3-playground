package com.github.ykiselev.opengl.vbo;

import com.github.ykiselev.opengl.Bindable;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.Util;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;

/**
 * Created by Y.Kiselev on 04.06.2016.
 */
public abstract class BufferObject implements Bindable, AutoCloseable {

    private final int id;

    private final int target;

    protected BufferObject(int target) {
        this.target = target;
        this.id = GL15.glGenBuffers();
        Util.checkGLError();
    }

    @Override
    public final int id() {
        return id;
    }

    @Override
    public final void bind() {
        glBindBuffer(this.target, this.id);
    }

    @Override
    public final void unbind() {
        glBindBuffer(this.target, 0);
    }

    @Override
    public final void close() throws Exception {
        glDeleteBuffers(this.id);
    }

    /**
     * Uploads data to gl
     *
     * @param data  the data to upload
     * @param usage the usage constant, like {@link GL15#GL_STATIC_DRAW}
     */
    public final void bufferData(FloatBuffer data, int usage) {
        glBufferData(this.target, data, usage);
        Util.checkGLError();
    }

    public final void bufferData(ByteBuffer data, int usage) {
        glBufferData(this.target, data, usage);
        Util.checkGLError();
    }

    public final void bufferData(IntBuffer data, int usage) {
        glBufferData(this.target, data, usage);
        Util.checkGLError();
    }
}
