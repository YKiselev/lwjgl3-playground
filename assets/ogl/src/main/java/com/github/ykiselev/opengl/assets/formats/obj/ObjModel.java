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

package com.github.ykiselev.opengl.assets.formats.obj;

import com.github.ykiselev.common.memory.MemAlloc;
import com.github.ykiselev.opengl.IndexedGeometrySource;
import org.lwjgl.opengl.GL11;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.stream.StreamSupport;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class ObjModel {

    private final float[] vertices;

    private final List<ObjName> objects;

    public ObjModel(float[] vertices, List<ObjName> objects) {
        this.vertices = requireNonNull(vertices);
        this.objects = requireNonNull(objects);
    }

    public IndexedGeometrySource toIndexedTriangles() {
        final int totalTriangles = objects.stream()
                .flatMap(n -> StreamSupport.stream(n.spliterator(), true))
                .mapToInt(f -> f.size() - 2)
                .sum();
        final MemAlloc wrappedIndices = new MemAlloc(Integer.BYTES * 3 * totalTriangles);
        final ByteBuffer buffer = wrappedIndices.value();
        for (ObjName object : objects) {
            for (ObjFace face : object) {
                final int idx0 = face.indexAt(0);
                for (int i = 1; i < face.size() - 1; i++) {
                    buffer.putInt(idx0);
                    buffer.putInt(face.indexAt(i));
                    buffer.putInt(face.indexAt(i + 1));
                }
            }
        }
        buffer.flip();
        final MemAlloc wrappedVertices = new MemAlloc(Float.BYTES * vertices.length);
        final ByteBuffer vbuff = wrappedVertices.value();
        for (float v : vertices) {
            vbuff.putFloat(v);
        }
        vbuff.flip();
        return new ObjModelIndexedGeometrySource(
                wrappedVertices,
                wrappedIndices
        );
    }

    private static class ObjModelIndexedGeometrySource implements IndexedGeometrySource {

        private final MemAlloc wrappedVertices;

        private final MemAlloc wrappedIndices;

        ObjModelIndexedGeometrySource(MemAlloc wrappedVertices, MemAlloc wrappedIndices) {
            this.wrappedVertices = wrappedVertices;
            this.wrappedIndices = wrappedIndices;
        }

        @Override
        public ByteBuffer vertices() {
            return wrappedVertices.value();
        }

        @Override
        public ByteBuffer indices() {
            return wrappedIndices.value();
        }

        @Override
        public int mode() {
            return GL11.GL_TRIANGLES;
        }

        @Override
        public void close() {
            wrappedIndices.close();
        }
    }
}
