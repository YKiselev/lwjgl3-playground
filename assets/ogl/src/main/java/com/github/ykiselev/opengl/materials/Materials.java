package com.github.ykiselev.opengl.materials;

import com.github.ykiselev.common.closeables.Closeables;

import java.util.List;

/**
 * Block materials
 */
public final class Materials implements AutoCloseable {

    private final List<MaterialAtlas> atlases;

    public Materials(List<MaterialAtlas> atlases) {
        this.atlases = List.copyOf(atlases);
    }

    public Material get(int index) {
        for (MaterialAtlas atlas : atlases) {
            var result = atlas.get(index);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    @Override
    public void close() {
        atlases.forEach(Closeables::close);
    }
}
