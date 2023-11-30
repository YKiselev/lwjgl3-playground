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
package com.github.ykiselev.opengl.assets.formats.obj

import com.github.ykiselev.common.memory.MemAlloc
import com.github.ykiselev.opengl.IndexedGeometrySource
import org.lwjgl.opengl.GL11
import java.nio.ByteBuffer
import java.util.stream.StreamSupport

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
class ObjModel internal constructor(private val vertices: FloatArray, private val objects: List<NamedObject>) {

    fun toIndexedTriangles(): IndexedGeometrySource {
        val totalTriangles = objects.stream()
            .flatMap { n: NamedObject -> StreamSupport.stream(n.spliterator(), true) }
            .mapToInt { f: ObjFace -> f.size() - 2 }
            .sum()
        val wrappedIndices = MemAlloc(Integer.BYTES * 3 * totalTriangles)
        val buffer = wrappedIndices.value()
        for (`object` in objects) {
            for (face in `object`) {
                val idx0 = face.indexAt(0)
                for (i in 1 until face.size() - 1) {
                    buffer.putInt(idx0)
                    buffer.putInt(face.indexAt(i))
                    buffer.putInt(face.indexAt(i + 1))
                }
            }
        }
        buffer.flip()
        val wrappedVertices = MemAlloc(java.lang.Float.BYTES * vertices.size)
        val vbuff = wrappedVertices.value()
        for (v in vertices) {
            vbuff.putFloat(v)
        }
        vbuff.flip()
        return ObjModelIndexedGeometrySource(
            wrappedVertices,
            wrappedIndices
        )
    }

    private class ObjModelIndexedGeometrySource(
        private val wrappedVertices: MemAlloc,
        private val wrappedIndices: MemAlloc
    ) : IndexedGeometrySource {
        override fun vertices(): ByteBuffer {
            return wrappedVertices.value()
        }

        override fun indices(): ByteBuffer {
            return wrappedIndices.value()
        }

        override fun mode(): Int {
            return GL11.GL_TRIANGLES
        }

        override fun close() {
            wrappedVertices.close()
            wrappedIndices.close()
        }
    }
}
