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

package com.github.ykiselev.assets.formats.obj;

import java.util.HashMap;
import java.util.Map;

/**
 * Each de-normalized vertex occupies 8 floats (x,y,z,s,t,nx,ny,nz).
 *
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
     * @return the index of first float element of de-normalized vertex (to get real index you need to divide this by vertex size in floats)
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

    /**
     * Note: key indices are 1-based (as in obj-file) so we should treat 0 value as "undefined" and extract 1 before getting any values by them.
     *
     * @param k the key with combination of indices.
     * @return index of de-normalized vertex corresponding to supplied key.
     */
    private int add(Key k) {
        final int idx = idx(add());
        if (k.v > 0) {
            final int v = k.v - 1;
            value(idx, vertices.x(v));
            value(idx + 1, vertices.y(v));
            value(idx + 2, vertices.z(v));
        } else {
            value(idx, 0);
            value(idx + 1, 0);
            value(idx + 2, 0);
        }
        if (k.tc != 0) {
            final int tc = k.tc;
            value(idx + 3, texCoords.s(tc));
            value(idx + 4, texCoords.t(tc));
        } else {
            value(idx + 3, 0);
            value(idx + 4, 0);
        }
        if (k.n != 0) {
            final int n = k.n - 1;
            value(idx + 5, normals.x(n));
            value(idx + 6, normals.y(n));
            value(idx + 7, normals.z(n));
        } else {
            value(idx + 5, 0);
            value(idx + 6, 0);
            value(idx + 7, 0);
        }
        return idx;
    }

    /**
     * Composite key consisting of three indices (vertex, texture coords and normal).
     */
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
