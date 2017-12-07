package com.github.ykiselev.assets.formats.obj;

import java.util.Arrays;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class ObjTexCoords extends ObjFloatArray implements TexCoords {

    @Override
    protected int itemSize() {
        return 2;
    }

    public void add(float[] coords) {
        if (coords.length == 2) {
            add(coords[0], coords[1]);
        } else if (coords.length == 3) {
            final float w = coords[2];
            add(coords[0] / w, coords[1] / w);
        } else {
            throw new IllegalArgumentException("Bad texture coordinates: " + Arrays.toString(coords));
        }
    }

    private void add(float s, float t) {
        final int idx = idx(add());
        value(idx, s);
        value(idx + 1, t);
    }

    @Override
    public float s(int index) {
        return value(idx(index));
    }

    @Override
    public float t(int index) {
        return value(idx(index) + 1);
    }
}
