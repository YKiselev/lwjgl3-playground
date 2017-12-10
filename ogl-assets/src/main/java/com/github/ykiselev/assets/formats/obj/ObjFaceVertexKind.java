package com.github.ykiselev.assets.formats.obj;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
enum ObjFaceVertexKind {
    V(1) {
        @Override
        public void emitVertices(int[] indices, Vertices vertices, TexCoords texCoords,
                                 Vertices normals, float[] target, int targetOffset) {
            for (int index : indices) {
                target[targetOffset] = vertices.x(index);
                target[targetOffset + 1] = vertices.y(index);
                target[targetOffset + 2] = vertices.z(index);
                target[targetOffset + 3] = 0;
                target[targetOffset + 4] = 0;
                target[targetOffset + 5] = 0;
                target[targetOffset + 6] = 0;
                target[targetOffset + 7] = 0;
                targetOffset += 8;
            }
        }
    }, VT(2) {
        @Override
        public void emitVertices(int[] indices, Vertices vertices, TexCoords texCoords, Vertices normals, float[] target, int targetOffset) {
            for (int i = 0; i < indices.length / 2; i++) {
                final int vi = indices[i * 2];
                final int ti = indices[i * 2 + 1];
                target[targetOffset] = vertices.x(vi);
                target[targetOffset + 1] = vertices.y(vi);
                target[targetOffset + 2] = vertices.z(vi);
                target[targetOffset + 3] = texCoords.s(ti);
                target[targetOffset + 4] = texCoords.t(ti);
                target[targetOffset + 5] = 0;
                target[targetOffset + 6] = 0;
                target[targetOffset + 7] = 0;
                targetOffset += 8;
            }
        }
    }, VN(2) {
        @Override
        public void emitVertices(int[] indices, Vertices vertices, TexCoords texCoords, Vertices normals, float[] target, int targetOffset) {
            for (int i = 0; i < indices.length / 2; i++) {
                final int vi = indices[i * 2];
                final int ni = indices[i * 2 + 1];
                target[targetOffset] = vertices.x(vi);
                target[targetOffset + 1] = vertices.y(vi);
                target[targetOffset + 2] = vertices.z(vi);
                target[targetOffset + 3] = 0;
                target[targetOffset + 4] = 0;
                target[targetOffset + 5] = normals.x(ni);
                target[targetOffset + 6] = normals.y(ni);
                target[targetOffset + 7] = normals.z(ni);
                targetOffset += 8;
            }
        }
    }, VTN(3) {
        @Override
        public void emitVertices(int[] indices, Vertices vertices, TexCoords texCoords, Vertices normals, float[] target, int targetOffset) {
            for (int i = 0; i < indices.length / 3; i++) {
                final int vi = indices[i * 3];
                final int ti = indices[i * 3 + 1];
                final int ni = indices[i * 3 + 2];
                target[targetOffset] = vertices.x(vi);
                target[targetOffset + 1] = vertices.y(vi);
                target[targetOffset + 2] = vertices.z(vi);
                target[targetOffset + 3] = texCoords.s(ti);
                target[targetOffset + 4] = texCoords.t(ti);
                target[targetOffset + 5] = normals.x(ni);
                target[targetOffset + 6] = normals.y(ni);
                target[targetOffset + 7] = normals.z(ni);
                targetOffset += 8;
            }
        }
    };

    private final int size;

    int size() {
        return size;
    }

    ObjFaceVertexKind(int size) {
        this.size = size;
    }

    public abstract void emitVertices(int[] indices, Vertices vertices, TexCoords texCoords,
                                      Vertices normals, float[] target, int targetOffset);
}
