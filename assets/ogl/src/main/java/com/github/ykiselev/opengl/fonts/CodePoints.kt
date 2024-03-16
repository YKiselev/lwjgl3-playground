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
package com.github.ykiselev.opengl.fonts

import java.nio.IntBuffer
import java.util.*
import java.util.stream.IntStream

/**
 * Represents a collection of Unicode code-point ranges.
 *
 * @author Yuriy Kiselev (uze@yandex.ru)
 * @since 06.04.2019
 */
class CodePoints private constructor(private val ranges: Array<Range>) {

    abstract class Range(val offset: Int) {

        abstract fun size(): Int
        abstract fun firstCodePoint(): Int
        abstract fun lastCodePoint(): Int
        abstract val isSparse: Boolean
        abstract fun copyTo(dest: IntBuffer)
        abstract fun indexOf(codePoint: Int): Int
        abstract fun codePoints(): IntStream
    }

    /**
     * Consecutive range of Unicode code points starting from `firstCodePoint` and ending with `firstCodePoint + count`.
     */
    class DenseRange(offset: Int, private val firstCodePoint: Int, private val lastCodePoint: Int) : Range(offset) {

        override fun firstCodePoint(): Int =
            firstCodePoint

        override fun lastCodePoint(): Int =
            lastCodePoint

        override fun size(): Int =
            lastCodePoint - firstCodePoint + 1

        override val isSparse: Boolean
            get() = false

        override fun copyTo(dest: IntBuffer) {
            // no-op
        }

        override fun codePoints(): IntStream =
            IntStream.range(firstCodePoint, lastCodePoint + 1)

        override fun indexOf(codePoint: Int): Int =
            offset + codePoint - firstCodePoint
    }

    /**
     * Sparse range of Unicode code points starting from `firstCodePoint` and ending with `firstCodePoint + count`.
     */
    internal class SparseRange(offset: Int, codePoints: IntArray) : Range(offset) {

        private val codePoints: IntArray

        override fun firstCodePoint(): Int =
            codePoints[0]

        override fun lastCodePoint(): Int =
            codePoints[codePoints.size - 1]

        override fun size(): Int =
            codePoints.size

        override val isSparse: Boolean
            get() = true

        override fun copyTo(dest: IntBuffer) {
            dest.put(codePoints)
        }

        override fun codePoints(): IntStream =
            Arrays.stream(codePoints)

        init {
            require(codePoints.isNotEmpty()) { "Range can't be empty!" }
            this.codePoints = codePoints.clone()
        }

        override fun indexOf(codePoint: Int): Int =
            offset + Arrays.binarySearch(codePoints, codePoint)
    }

    private var minCodePoint = 0
    private var maxCodePoint = 0
    private var numCodePoints = 0

    fun numCodePoints(): Int =
        numCodePoints

    fun numRanges(): Int =
        ranges.size

    fun range(index: Int): Range =
        ranges[index]

    init {
        if (ranges.isNotEmpty()) {
            minCodePoint = ranges[0].firstCodePoint()
            maxCodePoint = ranges[ranges.size - 1].lastCodePoint()
            numCodePoints = Arrays.stream(ranges).mapToInt {
                it.size()
            }.sum()
        } else {
            numCodePoints = 0
            maxCodePoint = 0
            minCodePoint = 0
        }
    }

    /**
     * Returns index of supplied code point if found.
     *
     * @param codePoint the code point to get index for
     * @return the index of code point or `-1` if not found
     */
    fun indexOf(codePoint: Int): Int {
        if (codePoint < minCodePoint || codePoint > maxCodePoint) {
            return -1
        }
        var left = 0
        var right = ranges.size - 1
        while (left <= right) {
            val c = (right + left) / 2
            val range = ranges[c]
            if (codePoint < range.firstCodePoint()) {
                right = c - 1
            } else if (codePoint > range.lastCodePoint()) {
                left = c + 1
            } else {
                return range.indexOf(codePoint)
            }
        }
        return -1
    }

    fun indexOf(codePoint: Int, defaultIndex: Int): Int {
        val result = indexOf(codePoint)
        return if (result > -1) {
            result
        } else defaultIndex
    }

    companion object {

        private fun refine(codePoints: IntStream): IntArray {
            val refined = codePoints
                .sorted()
                .distinct()
                .toArray()
            require(refined.isNotEmpty()) { "No code points!" }
            return refined
        }

        @JvmStatic
        fun collectDenseRanges(refined: IntArray): List<DenseRange> {
            if (refined.isEmpty()) {
                return emptyList()
            }
            val ranges: MutableList<DenseRange> = mutableListOf()
            var i = 1
            var prev = refined[0]
            var cp = 0
            var offset = 0
            while (i < refined.size) {
                cp = refined[i]
                if (cp > prev + 1) {
                    ranges.add(DenseRange(offset, refined[offset], prev))
                    offset = i
                }
                prev = cp
                i++
            }
            if (i > offset) {
                ranges.add(DenseRange(offset, refined[offset], cp))
            }
            return ranges
        }

        /**
         * Tries to convert consecutive degenerate dense ranges into sparse ranges. Dense range is degenerate if it has size of 1 or 2.
         * Input data may have degenerate dense ranges but two ranges should never intersect or touch each other.
         * Also, there should be no empty ranges.
         *
         * @param src preprocessed collection of dense ranges
         * @return collection where degenerate dense ranges are merged into sparse ranges where applicable.
         */
        @JvmStatic
        fun mergeDegenerates(src: List<DenseRange>): List<Range> {
            if (src.isEmpty()) {
                return emptyList()
            }
            var r: Range = src[0]
            val result: MutableList<Range> = ArrayList(src.size)
            for (i in 1 until src.size) {
                val r2 = src[i]
                val merged = merge(r, r2)
                r = if (merged != null) {
                    merged
                } else {
                    result.add(r)
                    r2
                }
            }
            result.add(r)
            return result
        }

        private fun isBadMergeCandidate(r: Range): Boolean {
            return !r.isSparse && r.size() > 2
        }

        private fun merge(a: Range, b: DenseRange): Range? {
            if (isBadMergeCandidate(a) || isBadMergeCandidate(b)) {
                return null
            }
            val merged = IntStream.concat(a.codePoints(), b.codePoints()).toArray()
            return SparseRange(a.offset, merged)
        }

        @JvmStatic
        fun of(codePoints: IntStream): CodePoints =
            CodePoints(
                mergeDegenerates(
                    collectDenseRanges(
                        refine(codePoints)
                    )
                ).toTypedArray()
            )
    }
}
