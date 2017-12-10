package com.github.ykiselev.assets.formats.obj;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
final class ObjFace {

    private final ObjFaceVertexKind kind;

    private final int[] indices;

    public int[] indices() {
        return indices;
    }

    public int sizeInVertices() {
        return indices.length / kind.size();
    }

    ObjFace(ObjFaceVertexKind kind, int[] indices) {
        this.kind = kind;
        this.indices = indices;
    }

    void emitVertices(Vertices vertices, TexCoords texCoords, Vertices normals, float[] target, int targetOffset) {
        kind.emitVertices(
                indices,
                vertices,
                texCoords,
                normals,
                target,
                targetOffset
        );
    }
}
