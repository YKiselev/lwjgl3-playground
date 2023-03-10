package com.github.ykiselev.opengl.materials;

import com.github.ykiselev.common.closeables.Closeables;
import com.github.ykiselev.opengl.textures.Texture2d;

import java.util.List;

public final class MaterialAtlas implements AutoCloseable {

    private final Texture2d texture;

    private final List<Material> materials;

    private final float sScale, tScale;

    public Texture2d texture() {
        return texture;
    }

    public float sScale() {
        return sScale;
    }

    public float tScale() {
        return tScale;
    }

    public MaterialAtlas(Texture2d texture, List<Material> materials, float sScale, float tScale) {
        this.texture = texture;
        this.materials = List.copyOf(materials);
        this.sScale = sScale;
        this.tScale = tScale;
    }

    /**
     * @param index 1-based index of material
     * @return material or {@code null}
     */
    public Material get(int index) {
        if (index < 1) {
            return null;
        }
        return materials.get(index - 1);
    }

    @Override
    public void close() {
        Closeables.close(texture);
    }

    @Override
    public String toString() {
        return "MaterialAtlas{" +
                "texture=" + texture +
                ", sScale=" + sScale +
                ", tScale=" + tScale +
                '}';
    }
}
