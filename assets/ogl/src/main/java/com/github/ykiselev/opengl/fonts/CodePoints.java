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

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import static java.util.Objects.requireNonNull;

/**
 * Represents a collection of Unicode code-point ranges.
 *
 * @author Yuriy Kiselev (uze@yandex.ru)
 * @since 06.04.2019
 */
public final class CodePoints {

    public static abstract class Range {

        private int offset;

        public int offset() {
            return offset;
        }

        public abstract int size();

        public abstract int firstCodePoint();

        public abstract int lastCodePoint();

        public abstract boolean isSparse();

        public abstract void copyTo(IntBuffer dest);

        abstract int indexOf(int codePoint);

        abstract IntStream codePoints();

        protected Range(int offset) {
            this.offset = offset;
        }
    }

    /**
     * Consecutive range of Unicode code points starting from {@code firstCodePoint} and ending with {@code firstCodePoint + count}.
     */
    static final class DenseRange extends Range {

        private final int firstCodePoint;

        private final int lastCodePoint;

        @Override
        public int firstCodePoint() {
            return firstCodePoint;
        }

        @Override
        public int lastCodePoint() {
            return lastCodePoint;
        }

        @Override
        public int size() {
            return lastCodePoint - firstCodePoint + 1;
        }

        @Override
        public boolean isSparse() {
            return false;
        }

        @Override
        public void copyTo(IntBuffer dest) {
            // no-op
        }

        @Override
        IntStream codePoints() {
            return IntStream.range(firstCodePoint, lastCodePoint + 1);
        }

        DenseRange(int offset, int firstCodePoint, int lastCodePoint) {
            super(offset);
            this.firstCodePoint = firstCodePoint;
            this.lastCodePoint = lastCodePoint;
        }

        @Override
        int indexOf(int codePoint) {
            return codePoint - firstCodePoint;
        }
    }

    /**
     * Sparse range of Unicode code points starting from {@code firstCodePoint} and ending with {@code firstCodePoint + count}.
     */
    static final class SparseRange extends Range {

        private final int[] codePoints;

        @Override
        public int firstCodePoint() {
            return codePoints[0];
        }

        @Override
        public int lastCodePoint() {
            return codePoints[codePoints.length - 1];
        }

        @Override
        public int size() {
            return codePoints.length;
        }

        @Override
        public boolean isSparse() {
            return true;
        }

        @Override
        public void copyTo(IntBuffer dest) {
            dest.put(codePoints);
        }

        @Override
        IntStream codePoints() {
            return Arrays.stream(codePoints);
        }

        SparseRange(int offset, int[] codePoints) {
            super(offset);
            if (codePoints.length == 0) {
                throw new IllegalArgumentException("Range can't be empty!");
            }
            this.codePoints = codePoints.clone();
        }

        @Override
        int indexOf(int codePoint) {
            return Arrays.binarySearch(codePoints, codePoint);
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

    private CodePoints(Range[] ranges) {
        this.ranges = requireNonNull(ranges);
        if (ranges.length > 0) {
            this.minCodePoint = ranges[0].firstCodePoint();
            this.maxCodePoint = ranges[ranges.length - 1].lastCodePoint();
            this.numCodePoints = Arrays.stream(ranges).mapToInt(Range::size).sum();
        } else {
            this.minCodePoint = this.maxCodePoint = this.numCodePoints = 0;
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
                // Ranges are sorted so if current starts after supplied code point then we don't have
                // such code point at all
                return -1;
            }
            if (codePoint <= range.lastCodePoint()) {
                final int index = range.indexOf(codePoint);
                if (index < 0) {
                    return -1;
                }
                return offset + index;
            }
            offset += range.size();
        }
        return -1;
    }

    static Range[] ranges(IntStream codePoints) {
        final int[] refined = refine(codePoints);
        final List<Range> ranges = new ArrayList<>();
        boolean dense = true;
        int i = 1, prev = refined[0], cp = 0, offset = 0;
        for (; i < refined.length; i++) {
            cp = refined[i];
            if (dense) {
                if (cp > prev + 1) {
                    if (i - offset > 1) {
                        ranges.add(new DenseRange(offset, refined[offset], prev));
                        offset = i;
                    }
                    dense = false;
                }
            } else {
                if (cp <= prev + 1) {
                    if (i - offset > 1) {
                        ranges.add(new SparseRange(offset, Arrays.copyOfRange(refined, offset, i)));
                        offset = i;
                    }
                    dense = true;
                }
            }
            prev = cp;
        }
        if (i > offset) {
            if (dense) {
                ranges.add(new DenseRange(offset, refined[offset], cp));
            } else {
                ranges.add(new SparseRange(offset, Arrays.copyOfRange(refined, offset, i)));
            }
        }
        return mergeSparse(ranges).toArray(Range[]::new);
    }

    static int[] refine(IntStream codePoints) {
        final int[] refined = codePoints
                .sorted()
                .distinct()
                .toArray();
        if (refined.length == 0) {
            throw new IllegalArgumentException("No code points!");
        }
        return refined;
    }

    static List<DenseRange> collectDenseRanges(int[] refined) {
        if (refined.length == 0) {
            return Collections.emptyList();
        }
        final List<DenseRange> ranges = new ArrayList<>();
        int i = 1, prev = refined[0], cp = 0, offset = 0;
        for (; i < refined.length; i++) {
            cp = refined[i];
            if (cp > prev + 1) {
                ranges.add(new DenseRange(offset, refined[offset], prev));
                offset = i;
            }
            prev = cp;
        }
        if (i > offset) {
            ranges.add(new DenseRange(offset, refined[offset], cp));
        }
        return ranges;
    }

    /**
     * Tries to convert consecutive degenerate dense ranges into sparse ranges. Dense range is degenerate if it has size of 1 or 2.
     * Input data may have degenerate dense ranges but two ranges should never intersect or touch each other.
     * Also there should be no empty ranges.
     *
     * @param src preprocessed collection of dense ranges
     * @return collection where degenerate dense ranges are merged into sparse ranges where applicable.
     */
    static List<Range> mergeDegenerates(List<DenseRange> src) {
        if (src.isEmpty()) {
            return Collections.emptyList();
        }
        Range r = src.get(0);
        final List<Range> result = new ArrayList<>(src.size());
        for (int i = 1; i < src.size(); i++) {
            final DenseRange r2 = src.get(i);
            final Range merged = merge(r, r2);
            if (merged != null) {
                r = merged;
            } else {
                result.add(r);
                r = r2;
            }
        }
        result.add(r);
        return result;
    }

    private static boolean isBadMergeCandidate(Range r) {
        return !r.isSparse() && r.size() > 2;
    }

    private static Range merge(Range a, DenseRange b) {
        if (isBadMergeCandidate(a) || isBadMergeCandidate(b)) {
            return null;
        }
        final int[] merged = IntStream.concat(a.codePoints(), b.codePoints()).toArray();
        return new SparseRange(a.offset(), merged);
    }

    private static List<Range> mergeSparse(List<Range> src) {
        if (src.isEmpty()) {
            return src;
        }
        Range r = src.get(0);
        final List<Range> result = new ArrayList<>(src.size());
        for (int i = 1; i < src.size(); i++) {
            final Range r2 = src.get(i);
            if (!r.isSparse() || !r2.isSparse()) {
                result.add(r);
                r = r2;
            } else {
                final int leftRangeSize = r.size();
                final int[] ints = Arrays.copyOf(((SparseRange) r).codePoints, leftRangeSize + r2.size());
                System.arraycopy(((SparseRange) r2).codePoints, 0, ints, leftRangeSize, r2.size());
                r = new SparseRange(r.offset, ints);
            }
        }
        result.add(r);
        return result;
    }

    public static CodePoints of(IntStream codePoints) {
        return new CodePoints(
                mergeDegenerates(
                        collectDenseRanges(
                                refine(codePoints)
                        )
                ).toArray(Range[]::new)
        );
    }
}
