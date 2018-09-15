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

import java.util.Arrays;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public abstract class ObjFloatArray {

    private float[] floats = new float[0];

    private int count;

    public int count() {
        return count;
    }

    final float value(int floatIndex) {
        return floats[floatIndex];
    }

    final void value(int floatIndex, float value) {
        floats[floatIndex] = value;
    }

    /**
     * @return the size of item in floats
     */
    protected abstract int itemSize();

    /**
     * Adds new item of size {@link ObjFloatArray#itemSize()} into array.
     *
     * @return the logical index of item
     */
    int add() {
        ensureSize(count + 1);
        count++;
        return count - 1;
    }

    private void ensureSize(int sizeInItems) {
        final int sizeInFloats = sizeInItems * itemSize();
        if (sizeInFloats > floats.length) {
            floats = Arrays.copyOf(floats, Math.max(1, sizeInItems) * itemSize() * 3 / 2);
        }
    }

    /**
     * Converts item index into index of first item's float component
     *
     * @param index the index of item
     * @return the index of first float in item
     */
    int idx(int index) {
        return index * itemSize();
    }

    public float[] toArray() {
        return Arrays.copyOf(floats, count * itemSize());
    }
}
