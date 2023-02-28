/*
 * Copyright 2017 Yuriy Kiselev (uze@yandex.ru)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.ykiselev.opengl.models;

import com.github.ykiselev.opengl.IndexedGeometrySource;
import com.github.ykiselev.opengl.vbo.IndexBufferObject;
import com.github.ykiselev.opengl.vbo.VertexArrayObject;
import com.github.ykiselev.opengl.vbo.VertexBufferObject;
import com.github.ykiselev.opengl.vertices.VertexDefinition;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class GenericIndexedGeometry implements AutoCloseable {

    private final VertexArrayObject vao;

    private final VertexBufferObject vbo;

    private final IndexBufferObject ebo;

    private final int mode;

    private final int count;

    public GenericIndexedGeometry(VertexDefinition definition, IndexedGeometrySource source) {
        this(source.mode(), definition, source.vertices(),
                source.indices(), source.indices().remaining() / source.indexSizeInBytes());
    }

    public GenericIndexedGeometry(int mode, VertexDefinition definition, ByteBuffer vertices, ByteBuffer indices, int indexCount) {
        this.mode = mode;
        this.count = indexCount;

        vao = new VertexArrayObject();
        vao.bind();

        vbo = new VertexBufferObject();
        vbo.bind();
        definition.apply();
        vbo.bufferData(vertices, GL_STATIC_DRAW);

        ebo = new IndexBufferObject();
        ebo.bind();
        ebo.bufferData(indices, GL_STATIC_DRAW);

        vao.unbind();
        vbo.unbind();
        ebo.unbind();
    }

    @Override
    public void close() {
        vbo.close();
        ebo.close();
        vao.close();
    }

    public void begin() {
        vao.bind();
    }

    public void draw() {
        glDrawElements(mode, count, GL_UNSIGNED_INT, 0);
    }

    public void end() {
        vao.unbind();
    }
}
