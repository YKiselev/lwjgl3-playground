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

package com.github.ykiselev.opengl.fonts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Represents a collection of Unicode code-point ranges.
 *
 * @author Yuriy Kiselev (uze@yandex.ru)
 * @since 06.04.2019
 */
public final class CodePoints {

    /**
     * Consecutive range of Unicode code points starting from {@code firstCodePoint} and ending with {@code firstCodePoint + count}.
     */
    public static final class Range {

        private final int firstCodePoint;

        private final int lastCodePoint;

        public int firstCodePoint() {
            return firstCodePoint;
        }

        public int lastCodePoint() {
            return lastCodePoint;
        }

        public int size() {
            return lastCodePoint - firstCodePoint + 1;
        }

        public Range(int firstCodePoint, int lastCodePoint) {
            this.firstCodePoint = firstCodePoint;
            this.lastCodePoint = lastCodePoint;
        }

        @Override
        public String toString() {
            return "Range{" +
                    "firstCodePoint=" + firstCodePoint +
                    ", lastCodePoint=" + lastCodePoint +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Range range = (Range) o;
            return firstCodePoint == range.firstCodePoint &&
                    lastCodePoint == range.lastCodePoint;
        }

        @Override
        public int hashCode() {
            return Objects.hash(firstCodePoint, lastCodePoint);
        }

        /**
         * Tries to merge this range with {@code other} if second range intersects with this one.
         *
         * @param other the second range to merge
         * @return the merged range or {@code null} if {@code other.firstCodePoint > this.firstCodePoint + this.count + 1}
         */
        public Range tryMerge(Range other) {
            if (other.firstCodePoint - 1 > lastCodePoint()) {
                return null;
            }
            if (firstCodePoint - 1 > other.lastCodePoint()) {
                return null;
            }
            return new Range(
                    Math.min(firstCodePoint, other.firstCodePoint),
                    Math.max(lastCodePoint, other.lastCodePoint)
            );
        }
    }

    /**
     * Sorted array of ranges
     */
    private final Range[] ranges;

    private final int minCodePoint;

    private final int maxCodePoint;

    private final int numCodePoints;

    public int numCodePoints() {
        return numCodePoints;
    }

    public int numRanges() {
        return ranges.length;
    }

    public Range range(int index) {
        return ranges[index];
    }

    /**
     * @param ranges the collection of ranges sorted in ascending order
     */
    public CodePoints(Collection<Range> ranges) {
        this.ranges = ranges.toArray(Range[]::new);
        if (!ranges.isEmpty()) {
            minCodePoint = this.ranges[0].firstCodePoint();
            maxCodePoint = this.ranges[this.ranges.length - 1].lastCodePoint();
            numCodePoints = ranges.stream().mapToInt(Range::size).sum();
        } else {
            minCodePoint = maxCodePoint = numCodePoints = 0;
        }

    }

    /**
     * Returns index of supplied code point if found.
     *
     * @param codePoint the code point to get index for
     * @return the index of code point or {@code -1} if not found
     */
    public int indexOf(int codePoint) {
        if (codePoint < minCodePoint || codePoint > maxCodePoint) {
            return -1;
        }
        int offset = 0;
        for (Range range : ranges) {
            if (codePoint < range.firstCodePoint()) {
                // Ranges are sorted so if current range starts after supplied code point then we don't have
                // such code point at all
                return -1;
            }
            if (codePoint <= range.lastCodePoint()) {
                return offset + codePoint - range.firstCodePoint();
            }
            offset += range.size();
        }
        return -1;
    }

    static List<Range> refine(Range... ranges) {
        final List<Range> list = new ArrayList<>(Arrays.asList(ranges));
        list.sort(Comparator.comparing(Range::firstCodePoint));
        Range previous = list.get(0);
        for (int i = 1; i < list.size(); ) {
            final Range range = list.get(i);
            final Range merged = previous.tryMerge(range);
            if (merged != null) {
                list.remove(i);
                list.set(i - 1, merged);
                previous = merged;
                continue;
            }
            i++;
            previous = range;
        }
        return list;
    }

    public static CodePoints of(Range... ranges) {
        return new CodePoints(refine(ranges));
    }
}
