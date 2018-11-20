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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
class IntArrayScrapTest {

    private final IntArrayScrap scrap = new IntArrayScrap(1024);

    @BeforeEach
    void setUp() {
        scrap.push();
    }

    @AfterEach
    void tearDown() {
        scrap.pop();
    }

    @Test
    void shouldAllocate() {
        IntArray a1 = scrap.allocate(5);
        assertNotEquals(0, a1);
        assertEquals(5, a1.size());
        IntArray a2 = scrap.allocate(3);
        assertEquals(3, a2.size());
    }

    @Test
    void shouldFill() {
        IntArray a1 = scrap.allocate(1);
        IntArray a2 = scrap.allocate(2);
        IntArray a3 = scrap.allocate(1);
        a1.set(0, 1);
        a2.set(0, 2);
        a2.set(1, 3);
        a3.set(0, 4);

        assertEquals(2, a2.get(0));
        assertEquals(3, a2.get(1));

        a2.fill(9);

        assertEquals(1, a1.get(0));
        assertEquals(9, a2.get(0));
        assertEquals(9, a2.get(0));
        assertEquals(4, a3.get(0));
    }

    @Test
    void shouldResizeUp() {
        IntArray a = scrap.allocate(3);
        a.set(0, 1);
        a.set(1, 2);
        a.set(2, 3);
        assertEquals(3, a.size());

        a.size(5);
        assertEquals(5, a.size());

        a.set(3, 4);
        a.set(4, 5);

        assertEquals(1, a.get(0));
        assertEquals(2, a.get(1));
        assertEquals(3, a.get(2));
        assertEquals(4, a.get(3));
        assertEquals(5, a.get(4));
    }

    @Test
    void shouldResizeDown() {
        IntArray a = scrap.allocate(3);
        a.set(0, 1);
        a.set(1, 2);
        a.set(2, 3);
        assertEquals(3, a.size());

        IntArray a2 = scrap.allocate(1);
        a2.set(0, 7);

        a.size(2);
        assertEquals(2, a.size());

        assertEquals(1, a.get(0));
        assertEquals(2, a.get(1));
        assertEquals(3, a.get(2));
        assertEquals(7, a2.get(0));
    }
}