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
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
class ByteArrayScrapTest {

    private final ByteArrayScrap scrap = new ByteArrayScrap(1024);

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
        ByteArray a1 = scrap.allocate(5);
        assertNotEquals(0, a1);
        assertEquals(5, a1.size());
        ByteArray a2 = scrap.allocate(3);
        assertEquals(3, a2.size());
    }

    @Test
    void shouldPop() {
        assertEquals(1, scrap.depth());
        assertEquals(0, scrap.free());
        assertEquals(0, scrap.allocated());
        scrap.push();
        ByteArray a1 = scrap.allocate(2);
        ByteArray a2 = scrap.allocate(2);
        ByteArray a3 = scrap.allocate(2);
        assertEquals(2, scrap.depth());
        assertEquals(0, scrap.free());
        assertEquals(3, scrap.allocated());
        scrap.pop();
        assertEquals(1, scrap.depth());
        assertEquals(3, scrap.free());
        assertEquals(0, scrap.allocated());
    }

    @Test
    void shouldFill() {
        ByteArray a1 = scrap.allocate(1);
        ByteArray a2 = scrap.allocate(2);
        ByteArray a3 = scrap.allocate(1);
        a1.set(0, (byte) 1);
        a2.set(0, (byte) 2);
        a2.set(1, (byte) 3);
        a3.set(0, (byte) 4);

        assertEquals(2, a2.get(0));
        assertEquals(3, a2.get(1));

        a2.fill((byte) 9);

        assertEquals(1, a1.get(0));
        assertEquals(9, a2.get(0));
        assertEquals(9, a2.get(0));
        assertEquals(4, a3.get(0));
    }

    @Test
    void shouldResizeUp() {
        ByteArray a = scrap.allocate(3);
        a.set(0, (byte) 1);
        a.set(1, (byte) 2);
        a.set(2, (byte) 3);
        assertEquals(3, a.size());

        a.size(5);
        assertEquals(5, a.size());

        a.set(3, (byte) 4);
        a.set(4, (byte) 5);

        assertEquals(1, a.get(0));
        assertEquals(2, a.get(1));
        assertEquals(3, a.get(2));
        assertEquals(4, a.get(3));
        assertEquals(5, a.get(4));
    }

    @Test
    void shouldResizeDown() {
        ByteArray a = scrap.allocate(3);
        a.set(0, (byte) 1);
        a.set(1, (byte) 2);
        a.set(2, (byte) 3);
        assertEquals(3, a.size());

        ByteArray a2 = scrap.allocate(1);
        a2.set(0, (byte) 7);

        a.size(2);
        assertEquals(2, a.size());

        assertEquals(1, a.get(0));
        assertEquals(2, a.get(1));
        assertEquals(3, a.get(2));
        assertEquals(7, a2.get(0));
    }

    @Test
    @Disabled
    void performance() {
        final int elements = 8;
        final int[] indices = new int[elements];
        for (int i = 0; i < elements; i++) {
            indices[i] = ThreadLocalRandom.current().nextInt(0, elements);
        }
        long hash = 0;
        long t0 = System.nanoTime();
        for (long c = 0; c < 100_000_000; c++) {
            scrap.push();
            ByteArray a = scrap.allocate(elements);
            for (int i = 0; i < elements; i++) {
                a.set(i, (byte) ((c ^ i) & 0xffL));
            }
            int idx = indices[(int) (c % elements)];
            hash += a.get(idx);
            scrap.pop();
        }
        long t1 = System.nanoTime();
        System.out.println(TimeUnit.NANOSECONDS.toMillis(t1 - t0) + " ms");
        System.out.println(hash);
    }
}