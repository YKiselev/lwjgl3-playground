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

package com.github.ykiselev.math;

import org.lwjgl.system.MemoryStack;

import java.nio.Buffer;
import java.nio.IntBuffer;

/**
 * Unsigned positive integer with arbitrary precision. Zero or negative values are not supported.
 * All methods are expected to be GC-free.
 * Values are stored in {@link IntBuffer} allocated using {@link MemoryStack} class. Each int in buffer holds a 9-digit number.
 * Integers are stored from least significant to most (i.e. {@code value.get(0)} will return least significant part).
 *
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
final class Unsigned {

    private static final long MASK = 0xffffffffL;

    private static final int BASE = 1000 * 1000 * 1000;

    private static final int INTS_PER_LONG = (int) Math.ceil(Math.log10(Long.MAX_VALUE) / Math.log10(BASE));

    /**
     * @param value the value to store
     * @param stack the stack to use for allocation
     * @return the buffer with stored value
     */
    static IntBuffer valueOf(int[] value, MemoryStack stack) {
        int i = value.length - 1;
        while (i > 0 && value[i] == 0) {
            i--;
        }
        final IntBuffer buffer = stack.mallocInt(i + 1);
        buffer.put(value, 0, i + 1);
        buffer.flip();
        return buffer;
    }

    /**
     * @param value the value to store.
     * @param stack the memory stack to use for allocation.
     * @return the buffer with stored value
     */
    static IntBuffer valueOf(long value, MemoryStack stack) {
        if (value <= 0) {
            throw new ArithmeticException("Value should be positive and greater than zero!");
        }
        final IntBuffer buffer = stack.mallocInt(INTS_PER_LONG);
        int i = 0;
        for (; i < 4 && value > 0; i++) {
            buffer.put((int) (value % BASE));
            value /= BASE;
        }
        buffer.flip();
        return buffer;
    }

    /**
     * Converts supplied decimal string into big integer and stores it in {@link IntBuffer} allocated using supplied {@link MemoryStack}.
     *
     * @param v     the source decimal string (digits only, without extra characters).
     * @param stack the memory stack to use for allocation.
     * @return the buffer with stored value.
     */
    static IntBuffer valueOf(String v, MemoryStack stack) {
        final IntBuffer buffer = stack.mallocInt((v.length() + 8) / 9);
        for (int i = v.length(); i > 0; i -= 9) {
            if (i < 9) {
                buffer.put(parseInt(v, 0, i));
            } else {
                buffer.put(parseInt(v, i - 9, i));
            }
        }
        stripExtraZeroes(buffer);
        buffer.flip();
        return buffer;
    }

    /**
     * Strips extra zeroes starting from {@link Buffer#position()}. Note that passed buffer should be just filled, before calling {@link Buffer#flip()}
     *
     * @param b the buffer to strip zeroes from
     */
    private static void stripExtraZeroes(IntBuffer b) {
        int i = b.position() - 1;
        while (i > 0 && b.get(i) == 0) {
            i--;
        }
        b.position(i + 1);
    }

    /**
     * Powers of ten for {@link Unsigned#parseInt(java.lang.CharSequence, int, int)}
     */
    private static final int[] POW10 = {
            1, 10, 100, 1_000, 10_000, 100_000, 1_000_000, 10_000_000, 100_000_000
    };

    /**
     * Parses supplied fragment into positive integer value (max 9 digits).
     *
     * @param seq       the sequence to get fragment from.
     * @param fromIndex the start index (inclusive)
     * @param toIndex   the to index (exclusive)
     * @return the parsed integer value
     */
    private static int parseInt(CharSequence seq, int fromIndex, int toIndex) {
        if (fromIndex >= toIndex || toIndex - fromIndex > 9) {
            throw new IndexOutOfBoundsException(fromIndex + ".." + toIndex);
        }
        int result = 0;
        for (int i = toIndex - 1, j = 0; i >= fromIndex; i--, j++) {
            result += Character.digit(seq.charAt(i), 10) * POW10[j];
        }
        return result;
    }

    /**
     * Multiplies supplied big integer value a by integer b.
     *
     * @param a     the unsigned integer value.
     * @param b     the integer value gretare than zero and less than {@link Unsigned#BASE}
     * @param stack the stack to use for allocation.
     * @return the buffer with sored result.
     */
    static IntBuffer multiply(IntBuffer a, int b, MemoryStack stack) {
        if (b <= 0) {
            throw new ArithmeticException("Multiplier should be positive!");
        }
        if (b >= BASE) {
            throw new ArithmeticException("Multiplier should be smaller than base (" + BASE + ")");
        }
        final int len = a.remaining();
        final IntBuffer buffer = stack.callocInt(len + 1);
        long carry = 0;
        for (int i = 0; i < len; i++) {
            final long cur = carry + (a.get(i) & MASK) * b;
            buffer.put((int) (cur % BASE));
            carry = cur / BASE;
        }
        buffer.put((int) carry);
        stripExtraZeroes(buffer);
        buffer.flip();
        return buffer;
    }

    /**
     * Multiplies supplied big integer by long value.
     *
     * @param a     the first operand (big integer).
     * @param v     the second operand.
     * @param stack the stack to use for allocation.
     * @return the buffer with sored result.
     */
    static IntBuffer multiply(IntBuffer a, long v, MemoryStack stack) {
        if (v <= 0) {
            throw new ArithmeticException("Multiplier should be positive!");
        }
        final int len = a.limit();
        final IntBuffer buffer = stack.callocInt(len + INTS_PER_LONG);
        for (int i = 0; i < len; i++) {
            final long mine = a.get(i) & MASK;
            int carry = 0, j = 0;
            long vj = v;
            for (; vj > 0; j++) {
                final long cur = buffer.get(i + j) + mine * (vj % BASE) + carry;
                buffer.put(i + j, (int) (cur % BASE));
                carry = (int) (cur / BASE);
                vj /= BASE;
            }
            if (carry > 0) {
                final long cur = buffer.get(i + j) + carry;
                buffer.put(i + j, (int) (cur % BASE));
            }
        }
        buffer.position(len + INTS_PER_LONG);
        stripExtraZeroes(buffer);
        buffer.flip();
        return buffer;
    }
}
