package com.github.ykiselev.assets.formats.obj;

import java.util.Arrays;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class ObjVertices extends ObjFloatArray implements Vertices {

    @Override
    protected int itemSize() {
        return 3;
    }

    public void add(float[] v) {
        if (v.length == 3) {
            add(v[0], v[1], v[2]);
        } else if (v.length == 4) {
            final float w = v[3];
            add(v[0] / w, v[1] / w, v[2] / w);
        } else {
            throw new IllegalArgumentException("Bad vertex: " + Arrays.toString(v));
        }
    }

    private void add(float x, float y, float z) {
        final int idx = idx(add());
        value(idx, x);
        value(idx + 1, y);
        value(idx + 2, z);
    }

    @Override
    public float x(int index) {
        return value(idx(index));
    }

    @Override
    public float y(int index) {
        return value(idx(index) + 1);
    }

    @Override
    public float z(int index) {
        return value(idx(index) + 2);
    }
}
