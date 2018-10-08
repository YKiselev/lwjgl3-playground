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

package com.github.ykiselev.iterators;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
class EndlessIteratorTest {

    @Test
    void shouldWorkWithEmptyCollection() {
        assertFalse(new EndlessIterator<>(Collections.emptyList()).hasNext());
    }

    @Test
    void shouldWorkWithSingleItemCollection() {
        EndlessIterator<Object> it = new EndlessIterator<>(Collections.singletonList("a"));
        assertEquals("a", it.next());
        assertEquals("a", it.next());
        assertEquals("a", it.next());
    }

    @Test
    void shouldWorkWithMultiItemCollection() {
        EndlessIterator<Object> it = new EndlessIterator<>(Arrays.asList("a", "b", "c"));
        assertEquals("a", it.next());
        assertEquals("b", it.next());
        assertEquals("c", it.next());

        assertEquals("a", it.next());
        assertEquals("b", it.next());
        assertEquals("c", it.next());
    }
}