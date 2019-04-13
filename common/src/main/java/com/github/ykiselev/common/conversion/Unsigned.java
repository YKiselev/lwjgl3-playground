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

package com.github.ykiselev.common.conversion;

import com.github.ykiselev.common.memory.scrap.ByteArray;
import com.github.ykiselev.common.memory.scrap.IntArray;
import com.github.ykiselev.common.memory.scrap.ScrapMemory;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.Buffer;
import java.nio.IntBuffer;

/**
 * Unsigned positive integer with arbitrary precision. Zero or negative values are not supported.
 * All methods are expected to be GC-free unless stated otherwise. Values are stored as an arrays of integers. Each integer
 * in array holds a 9-digit number. Integers are stored from least significant to most.
 *
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
final class Unsigned {

    private static final long MASK = 0xffffffffL;

    private static final int BASE = 1000 * 1000 * 1000;

    private static final int WORDS_PER_LONG = (int) Math.ceil(Math.log10(Long.MAX_VALUE) / Math.log10(BASE));

    /**
     * @param value the value to store
     * @param scrap to use for allocation
     * @return the buffer with stored value
     */
    static IntArray valueOf(int[] value, ScrapMemory scrap) {
        int i = value.length - 1;
        while (i > 0 && value[i] == 0) {
            i--;
        }
        final IntArray result = scrap.allocateInts(i + 1);
        result.set(0, value, 0, i + 1);
        return result;
    }

    /**
     * @param value the value to store.
     * @param scrap to use for allocation.
     * @return the buffer with stored value
     */
    static IntArray valueOf(long value, ScrapMemory scrap) {
        if (value <= 0) {
            throw new ArithmeticException("Value should be positive and greater than zero!");
        }
        final IntArray a = scrap.allocateInts(WORDS_PER_LONG);
        int i = 0;
        for (; i < WORDS_PER_LONG && value > 0; i++) {
            a.set(i, (int) (value % BASE));
            value /= BASE;
        }
        a.size(i);
        return a;
    }

    /**
     * Converts supplied decimal string into big integer and stores it in {@link IntBuffer} allocated using supplied {@link MemoryStack}.
     *
     * @param v     the source decimal string (digits only, without extra characters).
     * @param scrap to use for allocation.
     * @return the buffer with stored value.
     */
    static IntArray valueOf(String v, ScrapMemory scrap) {
        final IntArray a = scrap.allocateInts((v.length() + 8) / 9);
        for (int i = v.length(), j = 0; i > 0; i -= 9) {
            if (i < 9) {
                a.set(j++, parseInt(v, 0, i));
            } else {
                a.set(j++, parseInt(v, i - 9, i));
            }
        }
        stripExtraZeroes(a);
        return a;
    }

    /**
     * Strips extra zeroes starting from {@link Buffer#position()}. Note that passed buffer should be just filled, before calling {@link Buffer#flip()}
     *
     * @param a the buffer to strip zeroes from
     */
    private static void stripExtraZeroes(IntArray a) {
        int i = a.size() - 1;
        while (i > 0 && a.get(i) == 0) {
            i--;
        }
        a.size(i + 1);
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
     * @param a     the unsigned integer value in array.
     * @param b     the short integer value greater than zero and less than {@link Unsigned#BASE}
     * @param scrap to use for allocation.
     * @return the buffer with sored result.
     */
    static IntArray multiply(IntArray a, int b, ScrapMemory scrap) {
        if (b <= 0) {
            throw new ArithmeticException("Multiplier should be positive!");
        }
        if (b >= BASE) {
            throw new ArithmeticException("Multiplier should be smaller than base (" + BASE + ")");
        }
        final int len = a.size();
        final IntArray buffer = scrap.allocateInts(len + 1);
        long carry = 0;
        int i = 0;
        for (; i < len; i++) {
            final long cur = carry + (a.get(i) & MASK) * b;
            buffer.set(i, (int) (cur % BASE));
            carry = cur / BASE;
        }
        buffer.set(i, (int) carry);
        stripExtraZeroes(buffer);
        return buffer;
    }

    /**
     * Multiplies supplied big integer by long value.
     *
     * @param a     the first operand (big integer).
     * @param v     the second operand.
     * @param scrap to use for allocation.
     * @return the buffer with sored result.
     */
    static IntArray multiply(IntArray a, long v, ScrapMemory scrap) {
        if (v <= 0) {
            throw new ArithmeticException("Multiplier should be positive!");
        }
        final int len = a.size();
        final IntArray buffer = scrap.allocateInts(len + WORDS_PER_LONG);
        buffer.fill(0);
        for (int i = 0; i < len; i++) {
            final long mine = a.get(i) & MASK;
            int carry = 0, j = 0;
            long vj = v;
            for (; vj > 0; j++) {
                final long cur = buffer.get(i + j) + mine * (vj % BASE) + carry;
                buffer.set(i + j, (int) (cur % BASE));
                carry = (int) (cur / BASE);
                vj /= BASE;
            }
            if (carry > 0) {
                final long cur = buffer.get(i + j) + carry;
                buffer.set(i + j, (int) (cur % BASE));
            }
        }
        stripExtraZeroes(buffer);
        return buffer;
    }

    /**
     * Divides supplied big integer {@code a} in-place by short integer {@code b} (b > 0 and b < {@link Unsigned#BASE})
     *
     * @param a the dividend.
     * @param b the divisor.
     * @return the modified {@code a}
     */
    static IntArray divide(IntArray a, int b) {
        if (b <= 0) {
            throw new ArithmeticException("Divisor should be positive!");
        }
        if (b >= BASE) {
            throw new ArithmeticException("Divisor should be smaller than base (" + BASE + ")");
        }
        final int len = a.size();
        long carry = 0;
        for (int i = len - 1; i >= 0; i--) {
            final long cur = carry * BASE + (a.get(i) & MASK);
            a.set(i, (int) (cur / b));
            carry = cur % b;
        }
        stripExtraZeroes(a);
        return a;
    }

    /**
     * Returns the maximum number of digits for array of such length. Note that actual number of digits in supplied array may be less.
     *
     * @param v the array
     * @return the maximum number of digits for array of such length.
     */
    static int digits(IntArray v) {
        // 32 bits per integer, 30 bit is needed for 9 nines number.
        final int words = (v.size() * 32 + 29) / 30;
        return words * 9;
    }

    /**
     * Converts supplied big integer value into sequence of digits. Each digit occupies one byte.
     * todo - see optimization in java.text.DecimalFormat#collectIntegralDigits(int, char[], int)
     *
     * @param v     the big integer.
     * @param scrap to use for memory allocations.
     * @return the number of digits written to {@code dest}
     */
    static ByteArray toDigits(IntArray v, ScrapMemory scrap) {
        final int last = v.size() - 1;
        final ByteArray buffer = scrap.allocate(digits(v));
        final IntArray tmp = scrap.allocateInts(9);
        int j = 0;
        for (int i = last; i >= 0; i--) {
            int part = v.get(i);
            int digits = 0;
            for (int k = 0; k < 9; k++) {
                final int digit = part % 10;
                if (digit > 0 || part > 0 || i < last) {
                    tmp.set(k, '0' + digit);
                    digits++;
                }
                part /= 10;
            }
            for (; digits > 0; digits--) {
                buffer.set(j++, (byte) tmp.get(digits - 1));
            }
        }
        buffer.size(j);
        return buffer;
    }

    /**
     * Appends decimal string representation to the given {@link Appendable}.
     *
     * @param v          the big integer value to convet to string.
     * @param appendable the appendable to append string to.
     * @param scrap      the array mechanic
     */
    static void append(IntArray v, Appendable appendable, ScrapMemory scrap) {
        final int last = v.size() - 1;
        final IntArray tmp = scrap.allocateInts(9);
        for (int i = last; i >= 0; i--) {
            int part = v.get(i);
            int digits = 0;
            for (int k = 0; k < 9; k++) {
                final int digit = part % 10;
                if (digit > 0 || part > 0 || i < last) {
                    tmp.set(k, '0' + digit);
                    digits++;
                }
                part /= 10;
            }
            for (; digits > 0; digits--) {
                try {
                    appendable.append((char) tmp.get(digits - 1));
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }
        }
    }

    /**
     * For debug purposes only.
     * Note: this method is not gc-free!
     *
     * @param v the big integer
     * @return the string representation
     */
    static String toString(IntArray v, ScrapMemory scrap) {
        final StringBuilder sb = new StringBuilder();
        append(v, sb, scrap);
        return sb.toString();
    }

}
