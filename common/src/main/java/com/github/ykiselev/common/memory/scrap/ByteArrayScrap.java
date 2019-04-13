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

package com.github.ykiselev.common.memory.scrap;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.nio.ByteOrder;
import java.util.Arrays;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
final class ByteArrayScrap extends Scrap<ByteArray> {

    private final byte[] buffer;

    private static final VarHandle VHI;

    private int offset;

    static {
        VHI = MethodHandles.byteArrayViewVarHandle(int[].class, ByteOrder.nativeOrder());
    }

    ByteArrayScrap(int size) {
        this.buffer = new byte[size];
    }

    @Override
    ByteArray allocate(ByteArray reuse, int size) {
        final int reserved = reserve(size);
        final ReusableArray instance;
        if (reuse != null) {
            instance = (ReusableArray) reuse;
        } else {
            instance = new ReusableArray();
        }
        instance.array = reserved;
        return instance;
    }

    private int reserve(int size) {
        final int newOffset = ix(offset, size);
        if (newOffset > buffer.length) {
            throw new IllegalArgumentException("Not enough space!");
        }
        final int result = offset;
        VHI.set(buffer, result, size);
        offset = newOffset;
        return result;
    }

    private static int ix(int array) {
        return array;
    }

    private static int ix(int offset, int index) {
        return offset + Integer.BYTES + index;
    }

    @Override
    void onPop(ByteArray item) {
        final ReusableArray array = (ReusableArray) item;
        final int ix = ix(array.array);
        if (ix > offset) {
            throw new IllegalStateException("Invalid array : ix=" + ix + ", offset=" + offset);
        }
        array.array = Integer.MAX_VALUE;
        offset = ix;
    }

    final class ReusableArray implements ByteArray {

        private int array = Integer.MAX_VALUE;

        @Override
        public int size() {
            return (int) VHI.get(buffer, ix(array));
        }

        @Override
        public void size(int value) {
            if (value < 0) {
                throw new IllegalArgumentException("Array length cannot be negative: " + value);
            }
            final int ix = ix(array);
            final int prevSize = size();
            if (offset != ix(ix, prevSize)) {
                if (value > prevSize) {
                    throw new IllegalArgumentException("Only last array can be resized up!");
                }
            } else {
                offset = ix(ix, value);
            }
            VHI.set(buffer, ix, value);
        }

        @Override
        public void set(int index, byte value) {
            buffer[ix(array, index)] = value;
        }

        @Override
        public byte get(int index) {
            return buffer[ix(array, index)];
        }

        @Override
        public void set(int fromIndex, byte[] src, int srcFromIndex, int length) {
            System.arraycopy(src, srcFromIndex, buffer, ix(array, fromIndex), length);
        }

        @Override
        public void get(int fromIndex, byte[] dest, int destFromIndex, int length) {
            System.arraycopy(buffer, ix(array, fromIndex), dest, destFromIndex, length);
        }

        @Override
        public void fill(int fromIndex, int toIndex, byte value) {
            Arrays.fill(buffer, ix(array, fromIndex), ix(array, toIndex), value);
        }
    }
}
