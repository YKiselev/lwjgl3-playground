package com.github.ykiselev.assets.formats.obj;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
final class ObjFace {

    private final String material;

    private final int[] indices;

    public int indexAt(int index) {
        return indices[index];
    }

    public int size() {
        return indices.length;
    }

    public String material() {
        return material;
    }

    ObjFace(String material, int[] indices) {
        this.material = material;
        this.indices = indices;
    }
}
