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

}
