package com.github.ykiselev.assets.formats.obj;

import com.github.ykiselev.memory.MemAlloc;

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

    public IndexedGeometry toIndexedTriangles() {
        //final MemAlloc vertices2 = new MemAlloc(Float.BYTES * vertices.length);
        final int totalTriangles = objects.stream()
                .flatMap(n -> StreamSupport.stream(n.spliterator(), true))
                .mapToInt(f -> f.size() - 2)
                .sum();
        final MemAlloc indices = new MemAlloc(Integer.BYTES * 3 * totalTriangles);
        final ByteBuffer buffer = indices.value();
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
        throw new UnsupportedOperationException();
    }
}
