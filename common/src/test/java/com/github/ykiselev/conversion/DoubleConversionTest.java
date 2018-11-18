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

package com.github.ykiselev.conversion;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.lwjgl.system.MemoryStack;

import java.math.BigDecimal;
import java.nio.IntBuffer;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
class DoubleConversionTest {

    private void print(double value) {
        final long lv = Double.doubleToLongBits(value);
        final long exp = (lv >> 52L) & 0x7FFL;
        final long fraction = (lv & 0xFFFFFFFFFFFFFL);
        final double e = exp - 1023;
        double k = (exp == 0 ? 0 : 1);
        for (int i = 1; i <= 52; i++) {
            final long bit = 1 & (fraction >> (52 - i));
            k += bit * Math.pow(2, -i);
        }
        final double result, twoRaised, r2;
        if (exp != 0) {
            twoRaised = Math.pow(2, e);
        } else {
            twoRaised = Math.pow(2, -1022);
        }
        result = k * twoRaised;
        final long f2 = fraction + (exp == 0 ? 0 : 0x10000000000000L); // +2^52 if needed
        //
        r2 = f2 * Math.pow(5, 52 - e) * Math.pow(10, e - 52); // * Math.pow(2, -52)
        //
        final String equals1 = (value == result ? " == " : " != ");
        final String equals2 = (value == r2 ? " == " : " != ");
        System.out.println(value + equals1 + result + equals2 + r2 + " (exp=" + exp +
                ", e=" + e + ", fraction=" + fraction + ", f2=" + f2 + ", k=" + k + ")");
    }

    @Test
    @Disabled
    void should() {
        print(0.2);
        print(0.333);
        print(1.0);
        print(1.5);
        print(Math.PI);
        print(Double.longBitsToDouble(0xFFFFFFFFFFFFFL));

    }

    @ParameterizedTest
    @ValueSource(doubles = {Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, 0, -0.0, 0.1, 0.001, 0.2, 0.333, 1e-5, 1e-123, Double.MIN_VALUE})
    void shouldConvert(double value) {
        final StringBuilder sb = new StringBuilder();
        DoubleConversion.append(value, 6, sb);
        Assertions.assertEquals(Double.toString(value), sb.toString());
    }

    @Test
    @Disabled
    void generatePowersOfFive() {
        final StringBuilder sb = new StringBuilder();
        final BigDecimal five = BigDecimal.valueOf(5);
        BigDecimal num = BigDecimal.ONE;
        for (int i = 1; i < 1075; i++) {
            num = num.multiply(five);
            if (i % 10 == 0) {
                final String s = num.toPlainString();
                try (MemoryStack stack = MemoryStack.stackPush()) {
                    final IntBuffer buffer = Unsigned.valueOf(s, stack);
                    Assertions.assertEquals(s, Unsigned.toString(buffer));
                    sb.append('{');
                    for (int k = 0; k < buffer.limit(); k++) {
                        sb.append(buffer.get(k));
                        if (k < buffer.limit() - 1) {
                            sb.append(',');
                        }
                    }
                    sb.append("}, // ").append(i).append("\n");

                }
            }
        }
        System.out.println(sb);
    }
}