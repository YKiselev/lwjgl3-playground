package com.github.ykiselev.opengl.vbo;

import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;

/**
 * Created by Y.Kiselev on 08.05.2016.
 */
public final class IndexBufferObject extends BufferObject {

    public IndexBufferObject() {
        super(GL_ELEMENT_ARRAY_BUFFER);
    }
}
