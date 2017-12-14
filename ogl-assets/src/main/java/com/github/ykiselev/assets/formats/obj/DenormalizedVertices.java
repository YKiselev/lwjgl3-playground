package com.github.ykiselev.assets.formats.obj;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class DenormalizedVertices extends ObjFloatArray {

    private final ObjVertices vertices = new ObjVertices();

    private final ObjTexCoords texCoords = new ObjTexCoords();

    private final ObjVertices normals = new ObjVertices();

    private final Map<Key, Integer> map = new HashMap<>();

    @Override
    protected int itemSize() {
        return 8;
    }

    public void addVertex(float[] v) {
        vertices.add(v);
    }

    public void addTexCoord(float[] coords) {
        texCoords.add(coords);
    }

    public void addNormal(float[] n) {
        normals.add(n);
    }

    /**
     * Adds new de-normalized vertex to internal collection if not already present.
     *
     * @param vertex    the vertex index
     * @param texCoords the texture coordinates index
     * @param normal    the normal index
     * @return the index of de-normalized vertex
     */
    public int add(int vertex, int texCoords, int normal) {
        return map.computeIfAbsent(
                new Key(
                        vertex,
                        texCoords,
                        normal
                ),
                this::add
        );
    }

    private int add(Key k) {
        final int idx = idx(add());
        if (k.v > 0) {
            value(idx, vertices.x(k.v));
            value(idx + 1, vertices.y(k.v));
            value(idx + 2, vertices.z(k.v));
        } else {
            value(idx, 0);
            value(idx + 1, 0);
            value(idx + 2, 0);
        }
        if (k.tc != 0) {
            value(idx + 3, texCoords.s(k.tc));
            value(idx + 4, texCoords.t(k.tc));
        } else {
            value(idx + 3, 0);
            value(idx + 4, 0);
        }
        if (k.n != 0) {
            value(idx + 5, normals.x(k.n));
            value(idx + 6, normals.y(k.n));
            value(idx + 7, normals.z(k.n));
        } else {
            value(idx + 5, 0);
            value(idx + 6, 0);
            value(idx + 7, 0);
        }
        return idx;
    }

    private static final class Key {

        final int v;

        final int tc;

        final int n;

        Key(int v, int tc, int n) {
            this.v = v;
            this.tc = tc;
            this.n = n;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            final Key key = (Key) o;
            return v == key.v && tc == key.tc && n == key.n;
        }

        @Override
        public int hashCode() {
            int result = v;
            result = 31 * result + tc;
            result = 31 * result + n;
            return result;
        }
    }
}
