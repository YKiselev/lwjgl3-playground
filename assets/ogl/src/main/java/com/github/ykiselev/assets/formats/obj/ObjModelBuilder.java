/*
 * Copyright 2017 Yuriy Kiselev (uze@yandex.ru)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.ykiselev.assets.formats.obj;

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

    private final DenormalizedVertices denormalizedVertices = new DenormalizedVertices();

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
                denormalizedVertices.addVertex(
                        floats(row)
                );
                break;

            case "vt":
                denormalizedVertices.addTexCoord(
                        floats(row)
                );
                break;

            case "vn":
                denormalizedVertices.addNormal(
                        floats(row)
                );
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
        // We don't need 'f' (command itself)
        final int[] indices = new int[v.length - 1];
        int i = 0, prev = -1;
        for (int k = 1; k < v.length; k++) {
            // Each vertex may be either v or v/vt or v//vn or v/vt/vn
            final String[] vtn = v[k].split("/");
            if (prev != -1 && vtn.length != prev) {
                throw new IllegalStateException("Invalid face command: " + Arrays.toString(v));
            }
            if (vtn.length == 0) {
                throw new IllegalStateException("Invalid face command: " + Arrays.toString(v));
            }
            // v
            final int vertexIndex = Integer.parseInt(vtn[0]);
            // Note: Index in OBJ file is always 1-based, so value of 0 here means "index is undefined".
            // (according to obj reference negative values are valid cases so we can't use -1 for example)
            int texCoordIndex = 0, normalIndex = 0;
            if (vtn.length > 1 && vtn[1] != null && !vtn[1].isEmpty()) {
                // vt
                texCoordIndex = Integer.parseInt(vtn[1]);
            }
            if (vtn.length > 2) {
                // vn
                normalIndex = Integer.parseInt(vtn[2]);
            }
            prev = vtn.length;
            // de-normalized vertices is a float array so it return index of first float in item (where item is our vertex)
            final int indexOfFirstFloat = denormalizedVertices.add(
                    vertexIndex,
                    texCoordIndex,
                    normalIndex
            );
            // convert index of first float in vertex into actual vertex index
            indices[i] = indexOfFirstFloat / denormalizedVertices.itemSize();
            i++;
        }
        return new ObjFace(
                material,
                indices
        );
    }

    public ObjModel build() {
        return new ObjModel(denormalizedVertices.toArray(), objects);
    }
}
