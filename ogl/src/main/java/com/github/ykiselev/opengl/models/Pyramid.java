package com.github.ykiselev.opengl.models;

import com.github.ykiselev.common.Wrap;
import com.github.ykiselev.memory.MemAlloc;
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

    private Wrap<ByteBuffer> vertices = new MemAlloc(4 * Float.BYTES * (3 + 2 + 3));

    private Wrap<ByteBuffer> indices = new MemAlloc(12 * Integer.BYTES);

    public Pyramid() {
        vertices.value()
                .clear()
                // x, y, z, r, g, b
                .putFloat(0.5f).putFloat(-0.5f).putFloat(0).putFloat(1f).putFloat(0).putFloat(0)
                .putFloat(0.5f).putFloat(0.5f).putFloat(0).putFloat(0).putFloat(1f).putFloat(0)
                .putFloat(-0.5f).putFloat(0).putFloat(0).putFloat(0).putFloat(0).putFloat(1f)
                .putFloat(0).putFloat(0).putFloat(1).putFloat(1).putFloat(1).putFloat(1)
                .flip();
        indices.value()
                .clear()
                .putInt(0).putInt(1).putInt(3)
                .putInt(1).putInt(2).putInt(3)
                .putInt(2).putInt(0).putInt(3)
                .putInt(2).putInt(1).putInt(0)
                .flip();
    }

    @Override
    public ByteBuffer vertices() {
        return vertices.value();
    }

    @Override
    public ByteBuffer indices() {
        return indices.value();
    }

    @Override
    public int mode() {
        return GL11.GL_TRIANGLES;
    }

    @Override
    public void close() {
        vertices.close();
        indices.close();
    }
}
