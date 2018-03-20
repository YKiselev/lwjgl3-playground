package com.github.ykiselev.opengl.vertices;

import com.github.ykiselev.opengl.vbo.VertexBufferObject;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface VertexDefinition {

    void apply(VertexBufferObject target);
}
