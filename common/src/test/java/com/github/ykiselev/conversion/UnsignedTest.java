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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.charset.StandardCharsets;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public class UnsignedTest {

    private ScrapMemory scrap;

    @BeforeEach
    public void setUp() {
        scrap = new ScrapMemory(64, 64).push();
    }

    @AfterEach
    public void tearDown() {
        scrap.pop();
    }

    private void assertEquals(IntArray a, IntArray b) {
        Assertions.assertEquals(Unsigned.toString(a, scrap), Unsigned.toString(b, scrap));
    }

    @ParameterizedTest
    @ValueSource(strings = {"390625", "1220703125"})
    public void shouldConvertToString(String expected) {
        Assertions.assertEquals(
                expected,
                Unsigned.toString(
                        Unsigned.valueOf(expected, scrap),
                        scrap
                )
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"390625", "1220703125", "123000456000789000112233000000445566000000778899000000"})
    public void shouldAppend(String expected) {
        Assertions.assertEquals(
                expected,
                Unsigned.toString(
                        Unsigned.valueOf(expected, scrap),
                        scrap
                )
        );
    }

    private String toDigits(IntArray v) {
        final ByteArray buffer = Unsigned.toDigits(v, scrap);
        final byte[] data = new byte[buffer.size()];
        buffer.get(0, data, 0, data.length);
        return new String(data, StandardCharsets.US_ASCII);
    }

    @ParameterizedTest
    @ValueSource(strings = {"390625", "1220703125", "123000456000789000112233000000445566000000778899000000"})
    public void shouldConvertToDigits(String expected) {
        Assertions.assertEquals(
                expected,
                toDigits(Unsigned.valueOf(expected, scrap))
        );
    }

    @Test
    public void shouldParse() {
        assertEquals(
                Unsigned.valueOf(new int[]{890123456, 901234567, 12345678}, scrap),
                Unsigned.valueOf("12345678901234567890123456", scrap)
        );
        assertEquals(
                Unsigned.valueOf(new int[]{890123456, 1234567}, scrap),
                Unsigned.valueOf("1234567890123456", scrap)
        );
        assertEquals(
                Unsigned.valueOf(new int[]{123456789}, scrap),
                Unsigned.valueOf("000000000000123456789", scrap)
        );
    }

    @Test
    public void shouldCreate() {
        assertEquals(
                Unsigned.valueOf(123456789, scrap),
                Unsigned.valueOf("123456789", scrap)
        );
    }

    @Test
    public void shouldMultiplyByInt() {
        assertEquals(
                Unsigned.valueOf("15241578750190521", scrap),
                Unsigned.multiply(Unsigned.valueOf(123456789, scrap), 123456789, scrap)
        );
        assertEquals(
                Unsigned.valueOf("2147483644852516353", scrap),
                Unsigned.multiply(Unsigned.valueOf(Integer.MAX_VALUE, scrap), 999999999, scrap)
        );
        assertEquals(
                Unsigned.valueOf("9223372027631403770145224193", scrap),
                Unsigned.multiply(Unsigned.valueOf(Long.MAX_VALUE, scrap), 999999999, scrap)
        );
    }

    @Test
    public void shouldMultiplyByLong() {
        assertEquals(
                Unsigned.valueOf("12345678900000000", scrap),
                Unsigned.multiply(Unsigned.valueOf(1, scrap), 12345678900000000L, scrap)
        );
        assertEquals(
                Unsigned.valueOf("12345678887654321100000000", scrap),
                Unsigned.multiply(Unsigned.valueOf(999999999, scrap), 12345678900000000L, scrap)
        );
        assertEquals(
                Unsigned.valueOf("85070591730234615847396907784232501249", scrap),
                Unsigned.multiply(Unsigned.valueOf(Long.MAX_VALUE, scrap), Long.MAX_VALUE, scrap)
        );
    }

    @Test
    public void shouldDivideByInt() {
        assertEquals(
                Unsigned.valueOf("123456789", scrap),
                Unsigned.divide(Unsigned.valueOf(123456789L, scrap), 1)
        );
        assertEquals(
                Unsigned.valueOf("123456789", scrap),
                Unsigned.divide(Unsigned.valueOf(123456789000L, scrap), 1_000)
        );
        assertEquals(
                Unsigned.valueOf("246913577753", scrap),
                Unsigned.divide(Unsigned.valueOf("1234567888765432110", scrap), 5_000_000)
        );
        assertEquals(
                Unsigned.valueOf("255467242433136984526717440793490994", scrap),
                Unsigned.divide(Unsigned.valueOf("85070591730234615847396907784232501249", scrap), 333)
        );
    }
}