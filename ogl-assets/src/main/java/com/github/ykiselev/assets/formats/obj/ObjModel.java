package com.github.ykiselev.assets.formats.obj;

import java.util.Collection;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class ObjModel {

    private final float[] vertices;

    private final Collection<int[]> faces;

    public ObjModel(float[] vertices, Collection<int[]> faces) {
        this.vertices = requireNonNull(vertices);
        this.faces = requireNonNull(faces);
    }

    public IndexedGeometry toIndexedGeometry(){
        throw new UnsupportedOperationException();
    }
}
