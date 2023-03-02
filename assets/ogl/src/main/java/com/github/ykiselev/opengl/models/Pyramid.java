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

import com.github.ykiselev.common.closeables.Closeables;
import com.github.ykiselev.common.memory.MemAlloc;
import com.github.ykiselev.opengl.IndexedGeometrySource;
import org.lwjgl.opengl.GL11;

import java.nio.ByteBuffer;

/**
 * Simple triangular pyramid.
 * 4 vertices total: 0,1,2 - bottom, 3 - top.
 * CCW faces: Bottom (2 1 0) and (0 1 3),(1 2 3), (2 0 3)
 *
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class Pyramid implements IndexedGeometrySource, AutoCloseable {

    private final ByteBuffer vertices;

    private final ByteBuffer indices;

    private final AutoCloseable ac;

    public Pyramid() {
        try (var guard = Closeables.newGuard()) {
            vertices = guard.add(new MemAlloc(4 * Float.BYTES * (3 + 3)));
            indices = guard.add(new MemAlloc(12 * Integer.BYTES));
            ac = guard.detach();
        }
        vertices.clear()
                // x, y, z, r, g, b (red)
                .putFloat(0.5f).putFloat(-0.5f).putFloat(0).putFloat(1f).putFloat(0).putFloat(0)
                // green
                .putFloat(0.5f).putFloat(0.5f).putFloat(0).putFloat(0).putFloat(1f).putFloat(0)
                // blue
                .putFloat(-0.5f).putFloat(0).putFloat(0).putFloat(0).putFloat(0).putFloat(1f)
                // white
                .putFloat(0).putFloat(0).putFloat(1).putFloat(1).putFloat(1).putFloat(1)
                .flip();
        indices.clear()
                .putInt(0).putInt(1).putInt(3)
                .putInt(1).putInt(2).putInt(3)
                .putInt(2).putInt(0).putInt(3)
                .putInt(2).putInt(1).putInt(0)
                .flip();
    }

    @Override
    public ByteBuffer vertices() {
        return vertices;
    }

    @Override
    public ByteBuffer indices() {
        return indices;
    }

    @Override
    public int mode() {
        return GL11.GL_TRIANGLES;
    }

    @Override
    public void close() {
        Closeables.close(ac);
    }
}
