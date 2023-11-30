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
package com.github.ykiselev.opengl.assets.formats.obj

/**
 * Stateful obj model builder.
 * Note: this is a single-use object. You need to supply new instance each time new obj file is parsed.
 *
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
class ObjModelBuilder {

    private val vertices = DenormalizedVertices()
    private val objects: MutableList<NamedObject> = ArrayList()
    private var namedObject: NamedObject? = null
    private var materialLibrary: String? = null
    private var material: String? = null

    fun parseLine(s: String?) {
        if (s.isNullOrEmpty() || s.startsWith("#")) {
            return
        }
        val row = s.split("\\s".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        if (row.isEmpty()) {
            return
        }
        when (row[0]) {
            "v" -> vertices.addVertex(floats(row))
            "vt" -> vertices.addTexCoord(floats(row))
            "vn" -> vertices.addNormal(floats(row))
            "f" -> addFace(face(row))
            "o" -> namedObject(row)
            "g" -> {}
            "s" -> {}
            "mtllib" -> {
                materialLibrary = materialLibrary(row)
                material = null
            }

            "usemtl" -> material = useMaterial(row)
        }
    }

    private fun namedObject(row: Array<String>) {
        check(row.size == 2) { "Bad object: " + row.contentToString() }
        namedObject = NamedObject(row[1])
        objects.add(namedObject!!)
    }

    private fun addFace(face: ObjFace) {
        checkNotNull(namedObject) { "No object defined!" }
        namedObject!!.addFace(face)
    }

    private fun useMaterial(row: Array<String>): String {
        require(row.size >= 2) { "Bad material: " + row.contentToString() }
        return row[1]
    }

    private fun materialLibrary(row: Array<String>): String {
        if (row.size < 2) {
            throw IllegalArgumentException("Bad material library: " + row.contentToString())
        }
        return row[1]
    }

    /**
     * Converts rest of string parts into floats. First one (the command - v/vt/vn) is skipped.
     *
     * @param v the input sub-strings
     * @return the array of float values
     */
    private fun floats(v: Array<String>): FloatArray {
        val result = FloatArray(v.size - 1)
        for (i in result.indices) {
            result[i] = v[i + 1].toFloat()
        }
        return result
    }

    /**
     * Parses f command.
     * Note: OBJ file indices are one-based!
     *
     * @param v the command with arguments
     * @return parsed face
     */
    private fun face(v: Array<String>): ObjFace {
        // We don't need 'f' (command itself)
        val indices = IntArray(v.size - 1)
        var prev = -1
        for ((i, k) in (1 until v.size).withIndex()) {
            // Each vertex may be either v or v/vt or v//vn or v/vt/vn
            val vtn: Array<String> = v[k].split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            if (prev != -1 && vtn.size != prev) {
                throw IllegalStateException("Invalid face command: " + v.contentToString())
            }
            if (vtn.isEmpty()) {
                throw IllegalStateException("Invalid face command: " + v.contentToString())
            }
            // v
            val vertexIndex = vtn[0].toInt()
            // Note: Index in OBJ file is always 1-based, so value of 0 here means "index is undefined".
            // (according to obj reference negative values are valid case, so we can't use -1 for example)
            var texCoordIndex = 0
            var normalIndex = 0
            if (vtn.size > 1 && vtn[1].isNotEmpty()) {
                // vt
                texCoordIndex = vtn[1].toInt()
            }
            if (vtn.size > 2) {
                // vn
                normalIndex = vtn[2].toInt()
            }
            prev = vtn.size
            // de-normalized vertices is a float array, so we get index of first float element
            val indexOfFirstFloat = vertices.add(
                vertexIndex,
                texCoordIndex,
                normalIndex
            )
            // convert index of first float in vertex into actual vertex index
            indices[i] = indexOfFirstFloat / vertices.itemSize
        }
        return ObjFace(
            material,
            indices
        )
    }

    fun build(): ObjModel {
        return ObjModel(vertices.toArray(), objects)
    }
}
