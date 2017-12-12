package com.github.ykiselev.assets.formats.obj;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
final class ObjFace {

    private final String material;

    private final ObjFaceVertexKind kind;

    private final int[] indices;

    public int[] indices() {
        return indices;
    }

    public int sizeInVertices() {
        return indices.length / kind.size();
    }

    ObjFace(String material, ObjFaceVertexKind kind, int[] indices) {
        this.material = material;
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
