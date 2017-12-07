package com.github.ykiselev.assets.formats.obj;

import com.github.ykiselev.opengl.models.ObjModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class ObjModelBuilder implements Consumer<String> {

    private final ObjVertices vertices = new ObjVertices();

    private final ObjTexCoords texCoords = new ObjTexCoords();

    private final ObjVertices normals = new ObjVertices();

    private final List<ObjFace> faces = new ArrayList<>();

    @Override
    public void accept(String s) {
        if (s == null || s.isEmpty() || s.startsWith("#")) {
            return;
        }
        final String[] row = s.split(" ");
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
                faces.add(face(row));
                break;

            case "o":
                break;

            case "g":
                break;

            case "s":
                break;

            case "mtllib":
                break;

            case "usemtl":
                break;
        }
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
                kind,
                Arrays.copyOf(
                        indices,
                        kind.size() * v.length
                )
        );
    }

    public ObjModel build() {
        final List<float[]> buf = new ArrayList<>();
        for (ObjFace face : faces) {
            final int indexOffset = buf.size();
            buf.addAll(
                    face.toVertices(
                            vertices,
                            texCoords,
                            normals
                    )
            );
        }
        return new ObjModel();
    }
}
