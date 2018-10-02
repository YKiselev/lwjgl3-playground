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

package com.github.ykiselev.circular;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
class ArrayCircularBufferTest {

    @Nested
    class WhenSingleElementBuffer {

        CircularBuffer<String> buffer = new ArrayCircularBuffer<>(new String[1]);

        @Test
        void readShouldFailIfEmpty() {
            assertThrows(NoSuchElementException.class, buffer::read);
        }

        @Test
        void shouldBeEmptyIfZeroSize() {
            assertEquals(0, buffer.count());
            assertTrue(buffer.isEmpty());
        }

        @Test
        void shouldWriteAndRead() {
            buffer.write("abc");
            assertEquals(1, buffer.count());
            assertEquals("abc", buffer.read());
            assertEquals(0, buffer.count());
        }

        @Test
        void shouldWriteManyTimesAndRead() {
            buffer.write("a");
            assertEquals("a", buffer.read());
            buffer.write("b");
            assertEquals(1, buffer.count());
            buffer.write("c");
            assertEquals(1, buffer.count());
            assertEquals("c", buffer.read());
            assertTrue(buffer.isEmpty());
        }
    }

    @Nested
    class WhenThreeElementBuffer {

        CircularBuffer<String> buffer = new ArrayCircularBuffer<>(new String[3]);

        @Test
        void readShouldFailIfEmpty() {
            assertThrows(NoSuchElementException.class, buffer::read);
        }

        @Test
        void shouldBeEmptyIfZeroSize() {
            assertEquals(0, buffer.count());
            assertTrue(buffer.isEmpty());
        }

        @Test
        void shouldWriteAndRead() {
            buffer.write("a");
            buffer.write("b");
            buffer.write("c");
            buffer.write("d");

            assertEquals(3, buffer.count());
            assertEquals("b", buffer.read());
            assertEquals("c", buffer.read());
            assertEquals("d", buffer.read());
            assertTrue(buffer.isEmpty());
        }

        @Test
        void shouldClear() {
            buffer.write("a");
            buffer.write("b");
            buffer.write("c");
            buffer.write("d");
            buffer.clear();
            assertTrue(buffer.isEmpty());
        }

        @Test
        void shouldIterate() {
            buffer.write("a");
            buffer.write("b");
            buffer.write("c");
            buffer.write("d");
        }

        @Test
        void shouldCopyTo() {
            String[] dest = new String[3];
            buffer.write("a");
            assertEquals(1, buffer.copyTo(dest));
            assertArrayEquals(new String[]{"a", null, null}, dest);

            buffer.write("b");
            assertEquals(2, buffer.copyTo(dest));
            assertArrayEquals(new String[]{"a", "b", null}, dest);

            buffer.write("c");
            assertEquals(3, buffer.copyTo(dest));
            assertArrayEquals(new String[]{"a", "b", "c"}, dest);

            buffer.write("d");
            assertEquals(3, buffer.copyTo(dest));
            assertArrayEquals(new String[]{"b", "c", "d"}, dest);
        }
    }

}