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
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
class ObjVertices : ObjFloatArray(3) {

    fun add(v: FloatArray) {
        when (v.size) {
            3 -> {
                add(v[0], v[1], v[2])
            }

            4 -> {
                val w = v[3]
                add(v[0] / w, v[1] / w, v[2] / w)
            }

            else -> throw IllegalArgumentException("Bad vertex: " + v.contentToString())
        }
    }

    private fun add(x: Float, y: Float, z: Float) {
        var idx = addFloats()
        this[idx++] = x
        this[idx++] = y
        this[idx] = z
    }
}
