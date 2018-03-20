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
import org.lwjgl.opengl.GL15;

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
    }

    @Override
    public final int id() {
        return id;
    }

    @Override
    public final void bind() {
        glBindBuffer(target, id);
    }

    @Override
    public final void unbind() {
        glBindBuffer(target, 0);
    }

    @Override
    public final void close() {
        glDeleteBuffers(id);
    }

    /**
     * Uploads data to gl
     *
     * @param data  the data to upload
     * @param usage the usage constant, like {@link GL15#GL_STATIC_DRAW}
     */
    public final void bufferData(FloatBuffer data, int usage) {
        glBufferData(target, data, usage);
    }

    public final void bufferData(ByteBuffer data, int usage) {
        glBufferData(target, data, usage);
    }

    public final void bufferData(IntBuffer data, int usage) {
        glBufferData(target, data, usage);
    }
}
