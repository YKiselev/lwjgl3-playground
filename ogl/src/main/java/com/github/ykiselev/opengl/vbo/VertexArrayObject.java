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

package com.github.ykiselev.opengl.vbo;

import com.github.ykiselev.opengl.Bindable;

import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

/**
 * Stores the format of the vertex data as well as the Buffer Objects providing the vertex data arrays.
 *
 * Created by Y.Kiselev on 08.05.2016.
 */
public final class VertexArrayObject implements Bindable, AutoCloseable {

    private final int id;

    public VertexArrayObject() {
        this.id = glGenVertexArrays();
    }

    @Override
    public void close() {
        glDeleteVertexArrays(id);
    }

    @Override
    public void bind() {
        glBindVertexArray(id);
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
