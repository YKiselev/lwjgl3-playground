package com.github.ykiselev.assets.formats.obj;

import com.github.ykiselev.common.Wrap;
import com.github.ykiselev.memory.MemAlloc;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Stateful obj model builder.
 * Note: this is a single-use object. You need to supply new instance each time new obj file is parsed.
 *
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class ObjModelBuilder {

    private final ObjVertices vertices = new ObjVertices();

    private final ObjTexCoords texCoords = new ObjTexCoords();

    private final ObjVertices normals = new ObjVertices();

    private final List<ObjName> objects = new ArrayList<>();

    private ObjName object;

    private String materialLibrary;

    private String material;

    public void parseLine(String s) {
        if (s == null || s.isEmpty() || s.startsWith("#")) {
            return;
        }
        final String[] row = s.split("\\s");
        if (row.length == 0) {
            return;
        }
        switch (row[0]) {
            case "v":
                vertices.add(floats(row));
                break;

            case "vt":
                texCoords.add(floats(row));
                break;

            case "vn":
                normals.add(floats(row));
                break;

            case "f":
                addFace(face(row));
                break;

            case "o":
                object(row);
                break;

            case "g":
                break;

            case "s":
                break;

            case "mtllib":
                materialLibrary = mtllib(row);
                material = null;
                break;

            case "usemtl":
                material = usemtl(row);
                break;
        }
    }

    private void object(String[] row) {
        if (row.length != 2) {
            throw new IllegalStateException("Bad object: " + Arrays.toString(row));
        }
        object = new ObjName(row[1]);
        objects.add(object);
    }

    private void addFace(ObjFace face) {
        if (object == null) {
            throw new IllegalStateException("No object defined!");
        }
        object.addFace(face);
    }

    private String usemtl(String[] row) {
        if (row.length < 2) {
            throw new IllegalArgumentException("Bad material: " + Arrays.toString(row));
        }
        return row[1];
    }

    private String mtllib(String[] row) {
        if (row.length < 2) {
            throw new IllegalArgumentException("Bad material library: " + Arrays.toString(row));
        }
        return row[1];
    }

    /**
     * Converts rest of string parts into floats. First one (the command - v/vt/vn) is skipped.
     *
     * @param v the input sub-strings
     * @return the array of float values
     */
    private float[] floats(String[] v) {
        final float[] result = new float[v.length - 1];
        for (int i = 0; i < result.length; i++) {
            result[i] = Float.parseFloat(v[i + 1]);
        }
        return result;
    }

    /**
     * Parses f command.
     * Note: OBJ file indices are one-based!
     *
     * @param v the command with arguments
     * @return parsed face
     */
    private ObjFace face(String[] v) {
        final int[] indices = new int[3 * v.length];
        int i = 0, prev = -1;
        ObjFaceVertexKind kind = null;
        for (int k = 1; k < v.length; k++) {
            // Each vertex may be either v or v/vt or v//vn or v/vt/vn
            final String[] vtn = v[k].split("/");
            if (prev != -1 && vtn.length != prev) {
                throw new IllegalStateException("Invalid face command: " + Arrays.toString(v));
            }
            if (vtn.length == 0) {
                throw new IllegalStateException("Invalid face command: " + Arrays.toString(v));
            }
            ObjFaceVertexKind curKind = ObjFaceVertexKind.V;
            // v
            indices[i] = Integer.parseInt(vtn[0]) - 1;
            i++;
            if (vtn.length > 1 && vtn[1] != null && !vtn[1].isEmpty()) {
                // vt
                indices[i] = Integer.parseInt(vtn[1]) - 1;
                i++;
                curKind = ObjFaceVertexKind.VT;
            }
            if (vtn.length > 2) {
                // vn
                indices[i] = Integer.parseInt(vtn[2]) - 1;
                i++;
                if (curKind == ObjFaceVertexKind.VT) {
                    curKind = ObjFaceVertexKind.VTN;
                } else {
                    curKind = ObjFaceVertexKind.VN;
                }
            }
            prev = vtn.length;
            if (kind == null) {
                kind = curKind;
            } else if (kind != curKind) {
                throw new IllegalStateException(
                        "Invalid face command: " + Arrays.toString(v) + ", expected " + kind + " got " + curKind
                );
            }
        }
        if (kind == null) {
            throw new NullPointerException("Kind not set!");
        }
        return new ObjFace(
                material,
                kind,
                Arrays.copyOf(
                        indices,
                        kind.size() * v.length
                )
        );
    }

    public ObjModel build() {
        throw new UnsupportedOperationException();
//        final int vertexSizeInFloats = 3 + 2 + 3;
//        final int totalVertices = faces.stream()
//                .mapToInt(ObjFace::sizeInVertices)
//                .sum();
//        final Wrap<ByteBuffer> vBufWrap = new MemAlloc(Float.SIZE * totalVertices * vertexSizeInFloats);
//        final ByteBuffer vbuf = vBufWrap.value();
//        final List<int[]> idxList = new ArrayList<>();
//        int vertexCount = 0;
//        for (ObjFace face : faces) {
//            final int indexOffset = vertexCount;
//            face.emitVertices(
//                    vertices,
//                    texCoords,
//                    normals,
//                    vbuf,
//                    vertexCount * vertexSizeInFloats
//            );
//            vertexCount += face.sizeInVertices();
//            final int[] faceIndices = face.indices();
//            final int[] offsetIndices = new int[faceIndices.length];
//            for (int i = 0; i < faceIndices.length; i++) {
//                offsetIndices[i] = indexOffset + faceIndices[i];
//            }
//            idxList.add(offsetIndices);
//        }
//        if (vbuf.length != totalVertices * vertexSizeInFloats) {
//            throw new IllegalStateException("Vertex number mismatch!");
//        }
//        return new ObjModel(vbuf, idxList);
    }
}
