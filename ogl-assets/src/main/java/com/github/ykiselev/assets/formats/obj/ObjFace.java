package com.github.ykiselev.assets.formats.obj;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
final class ObjFace {

    private final String material;

    private final int[] indices;

    public int[] indices() {
        return indices;
    }

    ObjFace(String material, int[] indices) {
        this.material = material;
        this.indices = indices;
    }
}
