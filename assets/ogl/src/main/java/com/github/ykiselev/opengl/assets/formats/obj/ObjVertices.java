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
