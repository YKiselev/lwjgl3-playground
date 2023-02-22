package com.github.ykiselev.opengl.materials;

import com.github.ykiselev.common.closeables.Closeables;
import com.github.ykiselev.opengl.textures.Texture2d;

import java.util.List;

public final class MaterialAtlas implements AutoCloseable {

    private final Texture2d texture;

    private final List<Material> materials;

    private final float sScale, tScale;

    private final int baseIndex;

    public Texture2d texture() {
        return texture;
    }

    public float sScale() {
        return sScale;
    }

    public float tScale() {
        return tScale;
    }

    public MaterialAtlas(Texture2d texture, int baseIndex, List<Material> materials, float sScale, float tScale) {
        this.texture = texture;
        this.baseIndex = baseIndex;
        this.materials = List.copyOf(materials);
        this.sScale = sScale;
        this.tScale = tScale;
    }

    public Material get(int index) {
        final int i = index - baseIndex;
        if (i < 0 || i > materials.size()) {
            return null;
        }
        return materials.get(i);
    }

    @Override
    public void close() {
        Closeables.close(texture);
    }
}
