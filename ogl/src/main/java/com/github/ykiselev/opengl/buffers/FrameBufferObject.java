package com.github.ykiselev.opengl.buffers;

import com.github.ykiselev.opengl.Bindable;

import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;
import static org.lwjgl.opengl.GL30.glDeleteFramebuffers;
import static org.lwjgl.opengl.GL30.glGenFramebuffers;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class FrameBufferObject implements Bindable, AutoCloseable {

    private final int id;

    public FrameBufferObject(int id) {
        this.id = id;
    }

    public FrameBufferObject() {
        this(glGenFramebuffers());
    }

    @Override
    public int id() {
        return id;
    }

    @Override
    public void bind() {
        glBindFramebuffer(GL_FRAMEBUFFER, id);
    }

    @Override
    public void unbind() {
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    @Override
    public void close() {
        glDeleteFramebuffers(id);
    }
}
