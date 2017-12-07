package com.github.ykiselev.assets.formats.obj;

import java.util.Collection;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
final class ObjFace {

    private final ObjFaceVertexKind kind;

    private final int[] indices;

    public int[] indices() {
        return indices;
    }

    ObjFace(ObjFaceVertexKind kind, int[] indices) {
        this.kind = kind;
        this.indices = indices;
    }

    Collection<float[]> toVertices(Vertices vertices, TexCoords texCoords, Vertices normals) {
        return kind.toVertices(indices, vertices, texCoords, normals);
    }
}
