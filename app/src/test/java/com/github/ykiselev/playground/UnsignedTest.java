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

package com.github.ykiselev.playground;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
final class UnsignedTest {

    @Test
    void shouldParse() {
        assertEquals(
                new Unsigned(new int[]{890123456, 901234567, 12345678}),
                Unsigned.valueOf("12345678901234567890123456")
        );
        assertEquals(
                new Unsigned(new int[]{890123456, 1234567}),
                Unsigned.valueOf("1234567890123456")
        );
        assertEquals(
                new Unsigned(new int[]{123456789}),
                Unsigned.valueOf("000000000000123456789")
        );

    }

    @Test
    void shouldCreate() {
        assertEquals(
                Unsigned.valueOf(123456789),
                Unsigned.valueOf("123456789")
        );
    }

    @Test
    void shouldMultiplyByInt() {
        // maxlong^2="85070591730234615847396907784232501249"
        //BigInteger c = BigInteger.valueOf(Integer.MAX_VALUE).multiply(BigInteger.valueOf(999999999));
        assertEquals(
                Unsigned.valueOf("15241578750190521"),
                Unsigned.valueOf(123456789).multiply(123456789)
        );
        assertEquals(
                Unsigned.valueOf("2147483644852516353"),
                Unsigned.valueOf(Integer.MAX_VALUE).multiply(999999999)
        );
        assertEquals(
                Unsigned.valueOf("9223372027631403770145224193"),
                Unsigned.valueOf(Long.MAX_VALUE).multiply(999999999)
        );
    }

    @Test
    void shouldMultiplyByLong() {
        //BigInteger c = BigInteger.valueOf(999999999).multiply(BigInteger.valueOf(12345678900000000L));
        //BigInteger c2 = BigInteger.valueOf(Long.MAX_VALUE).multiply(BigInteger.valueOf(Long.MAX_VALUE));
        assertEquals(
                Unsigned.valueOf("12345678900000000"),
                Unsigned.valueOf(1).multiply(12345678900000000L)
        );

        assertEquals(
                Unsigned.valueOf("12345678887654321100000000"),
                Unsigned.valueOf(999999999).multiply(12345678900000000L)
        );
        assertEquals(
                Unsigned.valueOf("85070591730234615847396907784232501249"),
                Unsigned.valueOf(Long.MAX_VALUE).multiply(Long.MAX_VALUE)
        );
    }

    @Test
    void shouldMultiplyByUnsigned() {
        assertEquals(
                Unsigned.valueOf("12345678900000000"),
                Unsigned.valueOf(1).multiply(Unsigned.valueOf(12345678900000000L))
        );

        assertEquals(
                Unsigned.valueOf("12345678887654321100000000"),
                Unsigned.valueOf(999999999).multiply(Unsigned.valueOf(12345678900000000L))
        );
        assertEquals(
                Unsigned.valueOf("85070591730234615847396907784232501249"),
                Unsigned.valueOf(Long.MAX_VALUE).multiply(Unsigned.valueOf(Long.MAX_VALUE))
        );
    }

}

/**
 * Unsigned integer with arbitrary precision.
 */
final class Unsigned {

    private static final long MASK = 0xffffffffL;

    public static final int BASE = 1000 * 1000 * 1000;

    private final int[] value;

    public Unsigned(int[] value) {
        this.value = value;
    }

    public static Unsigned valueOf(long value) {
        if (value <= 0) {
            throw new ArithmeticException("Value should be positive and greater than zero!");
        }
        final int[] tmp = new int[4];
        for (int i = 0; i < 4 && value > 0; i++) {
            tmp[i] = (int) (value % BASE);
            value /= BASE;
        }
        return new Unsigned(stripZeroes(tmp));
    }

    private static int[] stripZeroes(int[] src) {
        int zeroes = 0;
        for (int i = src.length - 1; i > 0; i--, zeroes++) {
            if (src[i] != 0) {
                break;
            }
        }
        if (zeroes > 0) {
            src = Arrays.copyOfRange(src, 0, src.length - zeroes);
        }
        return src;
    }

    public static Unsigned valueOf(String v) {
        final int[] tmp = new int[(v.length() + 8) / 9];
        for (int i = v.length(), k = 0; i > 0; i -= 9, k++) {
            if (i < 9) {
                tmp[k] = Integer.parseUnsignedInt(v, 0, i, 10);
            } else {
                tmp[k] = Integer.parseUnsignedInt(v, i - 9, i, 10);
            }
        }
        return new Unsigned(stripZeroes(tmp));
    }

    public Unsigned multiply(int v) {
        if (v <= 0) {
            throw new ArithmeticException("Multiplier should be positive!");
        }
        if (v >= BASE) {
            throw new ArithmeticException("Multiplier should be smaller than base (" + BASE + ")");
        }
        long carry = 0;
        int[] tmp = Arrays.copyOf(value, value.length + 1);
        int t = 0;
        for (int i = 0; i < value.length; i++, t++) {
            final long cur = carry + (tmp[i] & MASK) * v;
            tmp[t] = (int) (cur % BASE);
            carry = cur / BASE;
        }
        tmp[t] = (int) carry;
        return new Unsigned(stripZeroes(tmp));
    }

    public Unsigned multiply(Unsigned b) {
        final int[] other = b.value;
        final int[] tmp = new int[value.length + other.length];
        for (int i = 0; i < value.length; i++) {
            for (int j = 0, carry = 0; j < other.length || carry > 0; j++) {
                final long cur = tmp[i + j] + (value[i] & MASK) * (j < other.length ? other[j] : 0) + carry;
                tmp[i + j] = (int) (cur % BASE);
                carry = (int) (cur / BASE);
            }
        }
        return new Unsigned(stripZeroes(tmp));
    }

    public Unsigned multiply(long v) {
        if (v <= 0) {
            throw new ArithmeticException("Multiplier should be positive!");
        }
        final int[] tmp = Arrays.copyOf(value, value.length + 4);
        int part = 0;
        while (v > 0) {
            int t = part;
            long carry = 0;
            final int lo = (int) (v % BASE);
            for (int i = 0; i < value.length; i++, t++) {
                final long cur = carry + (tmp[i] & MASK) * lo + (t > 0 ? (tmp[t] & MASK) : 0);
                tmp[t] = (int) (cur % BASE);
                carry = cur / BASE;
            }
            tmp[t] = (int) carry;
//            if (carry > 0) {
//                throw new ArithmeticException("Overflow!");
//            }
            //tmp[t] = (int) carry;
            v /= BASE;
            part++;
        }
        return new Unsigned(stripZeroes(tmp));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Unsigned unsigned = (Unsigned) o;

        return Arrays.equals(value, unsigned.value);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(value);
    }

    @Override
    public String toString() {
        return "Unsigned{" +
                "value=" + Arrays.toString(value) +
                '}';
    }
}

/**
 * 2^x = 5^-x * 10^x when x < 0
 * <p>
 * Positive integer. Can't be 0.
 */
final class BigInt {

    private static final long LONG_MASK = 0xffffffffL;

    private static final BigInt ONE = valueOf(1);

    private final int[] magnitude;

    BigInt(int[] magnitude) {
        this.magnitude = magnitude;
    }

    static BigInt valueOf(long value) {
        if (value <= 0) {
            throw new ArithmeticException("Value should be positive and greater than zero!");
        }
        final int highWord = (int) (value >>> 32);
        final int[] mag;
        if (highWord == 0) {
            mag = new int[1];
            mag[0] = (int) value;
        } else {
            mag = new int[2];
            mag[0] = highWord;
            mag[1] = (int) value;
        }
        return new BigInt(mag);
    }

    /**
     * @param v should be positive (can not be negative or zero).
     */
    public BigInt multiply(long v) {
        if (v <= 0) {
            throw new ArithmeticException("Value should be positive!");
        }
        final long dh = v >>> 32;      // higher order bits
        final long dl = v & LONG_MASK; // lower order bits
        final int xlen = magnitude.length;
        final int[] value = magnitude;
        int[] rmag = (dh == 0L) ? (new int[xlen + 1]) : (new int[xlen + 2]);
        long carry = 0;
        int rstart = rmag.length - 1;
        for (int i = xlen - 1; i >= 0; i--) {
            long product = (value[i] & LONG_MASK) * dl + carry;
            rmag[rstart--] = (int) product;
            carry = product >>> 32;
        }
        rmag[rstart] = (int) carry;
        if (dh != 0L) {
            carry = 0;
            rstart = rmag.length - 2;
            for (int i = xlen - 1; i >= 0; i--) {
                long product = (value[i] & LONG_MASK) * dh +
                        (rmag[rstart] & LONG_MASK) + carry;
                rmag[rstart--] = (int) product;
                carry = product >>> 32;
            }
            rmag[0] = (int) carry;
        }
        if (carry == 0L) {
            rmag = java.util.Arrays.copyOfRange(rmag, 1, rmag.length);
        }
        return new BigInt(rmag);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BigInt bigInt = (BigInt) o;

        return Arrays.equals(magnitude, bigInt.magnitude);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(magnitude);
    }

    @Override
    public String toString() {
        return "BigInt{" +
                "magnitude=" + Arrays.toString(magnitude) +
                '}';
    }
}