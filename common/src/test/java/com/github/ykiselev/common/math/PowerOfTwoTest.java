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

package com.github.ykiselev.common.math;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Yuriy Kiselev (uze@yandex.ru)
 * @since 05.04.2019
 */
public class PowerOfTwoTest {

    @Test
    public void shouldCalculateNext() {
        assertEquals(0, PowerOfTwo.next(0));
        assertEquals(1, PowerOfTwo.next(1));
        assertEquals(2, PowerOfTwo.next(2));
        assertEquals(4, PowerOfTwo.next(3));
        assertEquals(64, PowerOfTwo.next(33));
    }

    @Test
    public void nextShouldThrowIfNegative() {
        assertThrows(IllegalArgumentException.class, () -> PowerOfTwo.next(-1));
    }

}