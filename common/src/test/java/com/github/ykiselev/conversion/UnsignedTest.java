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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.charset.StandardCharsets;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
class UnsignedTest {

    private final MemoryStack stack = MemoryStack.stackPush();

    @AfterEach
    void tearDown() {
        stack.close();
    }


    private void assertEquals(IntBuffer a, IntBuffer b) {
        Assertions.assertEquals(Unsigned.toString(a), Unsigned.toString(b));
    }

    @ParameterizedTest
    @ValueSource(strings = {"390625", "1220703125"})
    void shouldConvertToString(String expected) {
        Assertions.assertEquals(
                expected,
                Unsigned.toString(
                        Unsigned.valueOf(expected, stack)
                )
        );
    }

    private String toString(IntBuffer v) {
        final StringBuilder sb = new StringBuilder();
        Unsigned.append(v, sb, stack);
        return sb.toString();
    }

    @ParameterizedTest
    @ValueSource(strings = {"390625", "1220703125", "123000456000789000112233000000445566000000778899000000"})
    void shouldAppend(String expected) {
        Assertions.assertEquals(
                expected,
                toString(Unsigned.valueOf(expected, stack))
        );
    }

    private String toDigits(IntBuffer v) {
        final ByteBuffer buffer = Unsigned.toDigits(v, stack);
        final byte[] data = new byte[buffer.remaining()];
        buffer.get(data);
        return new String(data, StandardCharsets.US_ASCII);
    }

    @ParameterizedTest
    @ValueSource(strings = {"390625", "1220703125", "123000456000789000112233000000445566000000778899000000"})
    void shouldConvertToDigits(String expected) {
        Assertions.assertEquals(
                expected,
                toDigits(Unsigned.valueOf(expected, stack))
        );
    }

    @Test
    void shouldParse() {
        assertEquals(
                Unsigned.valueOf(new int[]{890123456, 901234567, 12345678}, stack),
                Unsigned.valueOf("12345678901234567890123456", stack)
        );
        assertEquals(
                Unsigned.valueOf(new int[]{890123456, 1234567}, stack),
                Unsigned.valueOf("1234567890123456", stack)
        );
        assertEquals(
                Unsigned.valueOf(new int[]{123456789}, stack),
                Unsigned.valueOf("000000000000123456789", stack)
        );
    }

    @Test
    void shouldCreate() {
        assertEquals(
                Unsigned.valueOf(123456789, stack),
                Unsigned.valueOf("123456789", stack)
        );
    }

    @Test
    void shouldMultiplyByInt() {
        assertEquals(
                Unsigned.valueOf("15241578750190521", stack),
                Unsigned.multiply(Unsigned.valueOf(123456789, stack), 123456789, stack)
        );
        assertEquals(
                Unsigned.valueOf("2147483644852516353", stack),
                Unsigned.multiply(Unsigned.valueOf(Integer.MAX_VALUE, stack), 999999999, stack)
        );
        assertEquals(
                Unsigned.valueOf("9223372027631403770145224193", stack),
                Unsigned.multiply(Unsigned.valueOf(Long.MAX_VALUE, stack), 999999999, stack)
        );
    }

    @Test
    void shouldMultiplyByLong() {
        assertEquals(
                Unsigned.valueOf("12345678900000000", stack),
                Unsigned.multiply(Unsigned.valueOf(1, stack), 12345678900000000L, stack)
        );
        assertEquals(
                Unsigned.valueOf("12345678887654321100000000", stack),
                Unsigned.multiply(Unsigned.valueOf(999999999, stack), 12345678900000000L, stack)
        );
        assertEquals(
                Unsigned.valueOf("85070591730234615847396907784232501249", stack),
                Unsigned.multiply(Unsigned.valueOf(Long.MAX_VALUE, stack), Long.MAX_VALUE, stack)
        );
    }

    @Test
    void shouldDivideByInt() {
        assertEquals(
                Unsigned.valueOf("123456789", stack),
                Unsigned.divide(Unsigned.valueOf(123456789L, stack), 1)
        );
        assertEquals(
                Unsigned.valueOf("123456789", stack),
                Unsigned.divide(Unsigned.valueOf(123456789000L, stack), 1_000)
        );
        assertEquals(
                Unsigned.valueOf("246913577753", stack),
                Unsigned.divide(Unsigned.valueOf("1234567888765432110", stack), 5_000_000)
        );
        assertEquals(
                Unsigned.valueOf("255467242433136984526717440793490994", stack),
                Unsigned.divide(Unsigned.valueOf("85070591730234615847396907784232501249", stack), 333)
        );
    }
}