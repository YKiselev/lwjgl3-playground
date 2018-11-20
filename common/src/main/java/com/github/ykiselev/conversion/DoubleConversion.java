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

import com.github.ykiselev.memory.scrap.ByteArray;
import com.github.ykiselev.memory.scrap.IntArray;
import com.github.ykiselev.memory.scrap.ScrapMemory;
import com.github.ykiselev.memory.scrap.ThreadScrapMemory;

import java.io.IOException;
import java.io.UncheckedIOException;

/**
 * gc-free double to string conversion.
 *
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class DoubleConversion {

    private static final int EXP_MASK = 0x7ff;

    public static void append(double value, int precision, Appendable appendable) {
        try (ScrapMemory scrap = ThreadScrapMemory.push()) {
            append(value, precision, appendable, scrap);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static void append(double value, int precision, Appendable appendable, ScrapMemory scrap) throws IOException {
        final long bits = Double.doubleToRawLongBits(value);
        final int sign = (bits & 0x8000000000000000L) != 0 ? 1 : 0;
        final int biasedExponent = (int) ((bits >>> 52) & EXP_MASK);
        final long fraction = bits & 0xFFFFFFFFFFFFFL;
        if (bits == 0L) {
            // +0.0
            appendable.append("0.0");
        } else if (bits == 0x8000000000000000L) {
            // -0.0
            appendable.append("-0.0");
        } else if (biasedExponent == EXP_MASK) {
            if (fraction == 0) {
                // infinity
                if (sign == 1) {
                    appendable.append('-');
                }
                appendable.append("Infinity");
            } else {
                // NaN
                appendable.append("NaN");
            }
        } else {
            // Valid exponent range −1022 to +1023
            final int exp = biasedExponent - 1023;
            final long f = fraction + (biasedExponent != 0 ? (1L << 52) : 0); // add implicit leading binary 1 if number isn't denormalized
            final int powerOfTen;
            if (exp < 0) {
                // 2^e == 5^(-e) * 10^e when e < 0
                powerOfTen = exp - 52;
                final IntArray tmp = PowersOfFive.valueOf(52 - exp, scrap);
                final IntArray result = Unsigned.multiply(tmp, f, scrap);
                final ByteArray digits = Unsigned.toDigits(result, scrap);
                int decimalPlaces = digits.size() + powerOfTen;
                if (decimalPlaces <= 0) {
                    appendable.append("0.");
                }
                int p = precision;
                while (p > 0 && decimalPlaces < 0) {
                    appendable.append('0');
                    decimalPlaces++;
                    p--;
                }
                if (p > digits.size()) {
                    p = digits.size();
                }
                while (p >= 1 && digits.get(p - 1) == '0') {
                    p--;
                }
                for (int i = 0; p > 0 && i < digits.size(); i++, p--) {
                    appendable.append((char) digits.get(i));
                }
            } else {
                //final long f = fraction + 1 << 53;
            }
        }
    }
}
