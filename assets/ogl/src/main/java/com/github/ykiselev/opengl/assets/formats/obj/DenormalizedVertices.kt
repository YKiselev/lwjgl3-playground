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
 * Each de-normalized vertex occupies 8 floats (x,y,z,s,t,nx,ny,nz).
 *
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
class DenormalizedVertices : ObjFloatArray(8) {
    /**
     * Temporary storage for vertices
     */
    private val vertices = ObjVertices()

    /**
     * Temporary storage for texture coordinates
     */
    private val texCoords = ObjTexCoords()

    /**
     * Temporary storage for normals
     */
    private val normals = ObjVertices()
    private val map: MutableMap<Key, Int> = mutableMapOf()

    fun addVertex(v: FloatArray) =
        vertices.add(v)

    fun addTexCoord(coords: FloatArray) =
        texCoords.add(coords)

    fun addNormal(n: FloatArray) =
        normals.add(n)

    /**
     * Adds new de-normalized vertex to internal collection if not already present.
     *
     * @param vertex    the vertex index
     * @param texCoords the texture coordinates index
     * @param normal    the normal index
     * @return the index of first float element of de-normalized vertex (to get real index you need to divide this by vertex size in floats)
     */
    fun add(vertex: Int, texCoords: Int, normal: Int): Int {
        return map.computeIfAbsent(
            Key(
                vertex,
                texCoords,
                normal
            )
        ) { k: Key -> this.add(k) }
    }

    /**
     * Note: key indices are 1-based (as in obj-file) so we should treat 0 value as "undefined" and subtract 1 before getting any values by them.
     *
     * @param k the key with combination of indices.
     * @return index of de-normalized vertex corresponding to supplied key.
     */
    private fun add(k: Key): Int {
        val idx = addFloats()
        if (k.v > 0) {
            val v = vertices.idx(k.v - 1)

            this[idx] = vertices[v]
            this[idx + 1] = vertices[v + 1]
            this[idx + 2] = vertices[v + 2]
        } else {
            this[idx] = 0f
            this[idx + 1] = 0f
            this[idx + 2] = 0f
        }
        if (k.tc != 0) {
            val tc = texCoords.idx(k.tc) // why not tc - 1 ???
            this[idx + 3] = texCoords[tc]
            this[idx + 4] = texCoords[tc + 1]
        } else {
            this[idx + 3] = 0f
            this[idx + 4] = 0f
        }
        if (k.n != 0) {
            val n = normals.idx(k.n - 1)

            this[idx + 5] = normals[n]
            this[idx + 6] = normals[n + 1]
            this[idx + 7] = normals[n + 2]
        } else {
            this[idx + 5] = 0f
            this[idx + 6] = 0f
            this[idx + 7] = 0f
        }
        return idx
    }

    /**
     * Composite key consisting of three indices (vertex, texture coords and normal).
     */
    private data class Key(val v: Int, val tc: Int, val n: Int)
}
