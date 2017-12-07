package com.github.ykiselev.assets.formats.obj;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
enum ObjFaceVertexKind {
    V(1) {
        @Override
        public Collection<float[]> toVertices(int[] indices, Vertices vertices, TexCoords texCoords, Vertices normals) {
            final List<float[]> result = new ArrayList<>();
            for (int index : indices) {
                result.add(
                        new float[]{
                                vertices.x(index),
                                vertices.y(index),
                                vertices.z(index),
                                0, 0,
                                0, 0, 0
                        }
                );
            }
            return result;
        }
    }, VT(2) {
        @Override
        public Collection<float[]> toVertices(int[] indices, Vertices vertices, TexCoords texCoords, Vertices normals) {
            final List<float[]> result = new ArrayList<>();
            for (int i = 0; i < indices.length / 2; i++) {
                final int vi = indices[i * 2];
                final int ti = indices[i * 2 + 1];
                result.add(
                        new float[]{
                                vertices.x(vi),
                                vertices.y(vi),
                                vertices.z(vi),
                                texCoords.s(ti),
                                texCoords.t(ti),
                                0, 0, 0
                        }
                );
            }
            return result;
        }
    }, VN(2) {
        @Override
        public Collection<float[]> toVertices(int[] indices, Vertices vertices, TexCoords texCoords, Vertices normals) {
            final List<float[]> result = new ArrayList<>();
            for (int i = 0; i < indices.length / 2; i++) {
                final int vi = indices[i * 2];
                final int ni = indices[i * 2 + 1];
                result.add(
                        new float[]{
                                vertices.x(vi),
                                vertices.y(vi),
                                vertices.z(vi),
                                0, 0,
                                normals.x(ni),
                                normals.y(ni),
                                normals.z(ni)
                        }
                );
            }
            return result;
        }
    }, VTN(3) {
        @Override
        public Collection<float[]> toVertices(int[] indices, Vertices vertices, TexCoords texCoords, Vertices normals) {
            final List<float[]> result = new ArrayList<>();
            for (int i = 0; i < indices.length / 3; i++) {
                final int vi = indices[i * 3];
                final int ti = indices[i * 3 + 1];
                final int ni = indices[i * 3 + 2];
                result.add(
                        new float[]{
                                vertices.x(vi),
                                vertices.y(vi),
                                vertices.z(vi),
                                texCoords.s(ti),
                                texCoords.t(ti),
                                normals.x(ni),
                                normals.y(ni),
                                normals.z(ni)
                        }
                );
            }
            return result;
        }
    };

    private final int size;

    int size() {
        return size;
    }

    ObjFaceVertexKind(int size) {
        this.size = size;
    }

    public abstract Collection<float[]> toVertices(int[] indices, Vertices vertices, TexCoords texCoords, Vertices normals);
}
