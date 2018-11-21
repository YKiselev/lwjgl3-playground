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

package com.github.ykiselev.memory.scrap;

import java.util.Arrays;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class IntArray {

    private final IntArrayScrap owner;

    private int array = Integer.MAX_VALUE;

    private IntArray(IntArrayScrap owner) {
        this.owner = requireNonNull(owner);
    }

    private static int ix(int offset, int index) {
        return offset + 1 + index;
    }

    private static int ix(int array) {
        return array;
    }

    public int size() {
        return owner.buffer[ix(array)];
    }

    /**
     * Resizes the array. Note that only the last array can be resized up.
     *
     * @param value the new length
     */
    public void size(int value) {
        if (value < 0) {
            throw new IllegalArgumentException("Array length cannot be negative: " + value);
        }
        owner.resize(array, value);
    }

    public void set(int index, int value) {
        owner.buffer[ix(array, index)] = value;
    }

    public int get(int index) {
        return owner.buffer[ix(array, index)];
    }

    public void set(int fromIndex, int[] src, int srcFromIndex, int length) {
        System.arraycopy(src, srcFromIndex, owner.buffer, ix(array, fromIndex), length);
    }

    public void get(int fromIndex, int[] dest, int destFromIndex, int length) {
        System.arraycopy(owner.buffer, ix(array, fromIndex), dest, destFromIndex, length);
    }

    public void fill(int fromIndex, int toIndex, int value) {
        Arrays.fill(owner.buffer, ix(array, fromIndex), ix(array, toIndex), value);
    }

    public void fill(int value) {
        fill(0, size(), value);
    }

    static Scrap<IntArray> createScrap(int size) {
        return new IntArrayScrap(size);
    }

    private static final class IntArrayScrap extends Scrap<IntArray> {

        private final int[] buffer;

        private int offset;

        IntArrayScrap(int size) {
            this.buffer = new int[size];
        }

        @Override
        IntArray allocate(IntArray reuse, int size) {
            final int reserved = reserve(size);
            final IntArray instance = Objects.requireNonNullElseGet(reuse, () -> new IntArray(this));
            instance.array = reserved;
            return instance;
        }

        private int reserve(int size) {
            final int newOffset = ix(offset, size);
            if (newOffset > buffer.length) {
                throw new IllegalArgumentException("Not enough space!");
            }
            final int result = offset;
            buffer[offset] = size;
            offset = newOffset;
            return result;
        }

        private void resize(int array, int value) {
            final int ix = ix(array);
            final int prevSize = buffer[ix];
            if (offset != ix(ix, prevSize)) {
                if (value > prevSize) {
                    throw new IllegalArgumentException("Only last array can be resized up!");
                }
            } else {
                offset = ix(offset, value);
            }
            buffer[ix] = value;
        }

        @Override
        void onPop(IntArray item) {
            final int ix = ix(item.array);
            if (ix > offset) {
                throw new IllegalStateException("Invalid array : ix=" + ix + ", offset=" + offset);
            }
            item.array = Integer.MAX_VALUE;
            offset = ix;
        }
    }
}
