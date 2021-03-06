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

package com.github.ykiselev.common.fps;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public class FrameInfoTest {

    @Test
    public void shouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> new FrameInfo(0));
    }

    @Test
    public void shouldSupportSmallWindow() {
        FrameInfo info = new FrameInfo(1);
        info.add(1);
        info.add(3);
        assertEquals(2, info.totalFrames());
        assertEquals(3.0, info.avg());
    }

    @Test
    public void shouldCaclulate() {
        FrameInfo info = new FrameInfo(3);
        info.add(100);
        info.add(20);
        info.add(16);
        assertEquals(16, info.min());
        assertEquals(100, info.max());
        assertEquals(45.3, info.avg(), 0.1);
        assertEquals(22.1, info.fps(), 0.1);

        info.add(15);
        info.add(12);
        assertEquals(12, info.min());
        assertEquals(16, info.max());
        assertEquals(14.3, info.avg(), 0.1);
        assertEquals(69.8, info.fps(), 0.1);
    }
}