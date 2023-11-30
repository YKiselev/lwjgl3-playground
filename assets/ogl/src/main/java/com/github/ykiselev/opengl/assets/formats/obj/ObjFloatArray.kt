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

import kotlin.math.max

/**
 * @param itemSize the size of this array's item in floats
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
open class ObjFloatArray(val itemSize: Int = 1) {

    private var floats = FloatArray(0)
    var count = 0
        private set

    operator fun get(floatIndex: Int): Float =
        floats[floatIndex]

    operator fun set(floatIndex: Int, value: Float) {
        floats[floatIndex] = value
    }

    /**
     * Ensures there is a space for new item of size [ObjFloatArray.itemSize].
     *
     * @return the logical index of item
     */
    fun add(): Int {
        ensureSize(count + 1)
        count++
        return count - 1
    }

    /**
     * Ensures there is a space for new item of size [ObjFloatArray.itemSize].
     * @return the absolute index of item's first float element
     */
    fun addFloats(): Int = idx(add())

    private fun ensureSize(requiredSizeInItems: Int) {
        val sizeInFloats = requiredSizeInItems * itemSize
        if (sizeInFloats > floats.size) {
            floats = floats.copyOf((max(1, requiredSizeInItems) * itemSize * 3 / 2))
        }
    }

    /**
     * Converts item index into index of item's first float component
     *
     * @param index the index of item
     * @return the index of first float in item
     */
    fun idx(index: Int): Int =
        index * itemSize

    fun toArray(): FloatArray =
        floats.copyOf(count * itemSize)
}
